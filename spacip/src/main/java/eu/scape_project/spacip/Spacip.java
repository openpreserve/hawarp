/*
 * Copyright 2012 The SCAPE Project Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package eu.scape_project.spacip;

import eu.scape_project.spacip.cli.CliConfig;
import eu.scape_project.spacip.cli.Options;
import eu.scape_project.spacip.utils.PropertyUtil;
import eu.scape_project.spacip.utils.StringUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spacip: Scape PlAtform Container Input Preparation
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class Spacip {

    private static CliConfig config;
    // Logger instance
    private static Logger logger = LoggerFactory.getLogger(Spacip.class.getName());
    
    private static PropertyUtil pu;

    /**
     * Mapper class.
     */
    public static class ContainerProcessingMapper
            extends Mapper<LongWritable, Text, Text, ObjectWritable> {

        private MultipleOutputs mos;

        @Override
        public void setup(Context context) {
            mos = new MultipleOutputs(context);
        }

        @Override
        public void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }

        @Override
        public void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException, FileNotFoundException {
            Path pt = new Path("hdfs://" + value);
            Configuration conf = context.getConfiguration();
            
            String containerFileName = pt.getName();
            if (containerFileName.endsWith(conf.get("containerfilesuffix", ".arc.gz"))) {
                ContainerProcessing contProc = new ContainerProcessing(mos, context, conf);
                contProc.prepareInput(pt);
            } else {
                throw new IllegalArgumentException("Unsupported input format");
            }
        }
    }

    /**
     * Constructor
     */
    public Spacip() {
    }

    /**
     * Get CLI configuration
     *
     * @return CLI configuration
     */
    public static CliConfig getConfig() {
        return config;
    }

    /**
     * Main entry point.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
      
        // configuration properties
        pu = new PropertyUtil("/eu/scape_project/spacip/config.properties");
        
        // hadoop configuration
        Configuration hadoopConf = new Configuration();
        // Command line interface
        config = new CliConfig();
        CommandLineParser cmdParser = new PosixParser();
        GenericOptionsParser gop = new GenericOptionsParser(hadoopConf, args);
        CommandLine cmd = cmdParser.parse(Options.OPTIONS, gop.getRemainingArgs());
        if ((args.length == 0) || (cmd.hasOption(Options.HELP_OPT))) {
            Options.exit("Usage", 0);
        } else {
            Options.initOptions(cmd, config);
        }
        // cli parameter has priority over default configuration
        int cliParamNumPerInv = config.getNumItemsPerInvokation();
        int defaultNumPerInv = Integer.parseInt(pu.getProp("default.itemsperinvokation"));
        int numPerInv = (cliParamNumPerInv != 0)?cliParamNumPerInv:defaultNumPerInv;
        // setting hadoop configuration parameters so that they can be used
        // during MapReduce
        hadoopConf.setInt("num_items_per_task", numPerInv);
        hadoopConf.set("output_file_suffix", pu.getProp("default.outputfilesuffix"));
        hadoopConf.set("scape_platform_invoke", pu.getProp("tomar.invoke.command"));
        hadoopConf.set("unpack_hdfs_path", pu.getProp("default.hdfsdir.unpacked"));
        hadoopConf.set("joboutput_hdfs_path", pu.getProp("default.hdfsdir.joboutput"));
        hadoopConf.set("tooloutput_hdfs_path", pu.getProp("default.hdfsdir.toolout"));
        hadoopConf.set("container_file_suffix", pu.getProp("containerfilesuffix"));
        hadoopConf.set("tomar_param_pattern", pu.getProp("tomar.param.pattern"));
        startHadoopJob(hadoopConf);

    }

    /**
     * Start Hadoop job
     *
     * @param conf Hadoop job configuration
     */
    public static void startHadoopJob(Configuration conf) {
        try {
            Job job = new Job(conf, "spacip_"+conf.getInt("num_items_per_task", 0));

            // local debugging (pseudo-distributed)
//             job.getConfiguration().set("mapred.job.tracker", "local");
//             job.getConfiguration().set("fs.default.name", "file:///");

            job.setJarByClass(Spacip.class);

            job.setMapperClass(Spacip.ContainerProcessingMapper.class);
            // No reducer needed
            job.setNumReduceTasks(0);

            job.setInputFormatClass(TextInputFormat.class);
            
            MultipleOutputs.addNamedOutput(job, "keyfilmapping", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job, "tomarinput", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job, "error", TextOutputFormat.class, Text.class, Text.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(ObjectWritable.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(ObjectWritable.class);

            TextInputFormat.addInputPath(job, new Path(config.getDirStr()));
            String outpath = StringUtils.normdir(conf.get("joboutput_hdfs_path","spacip_joboutput")) + System.currentTimeMillis();
            FileOutputFormat.setOutputPath(job, new Path(outpath));
            LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
            job.waitForCompletion(true);
            // print output path (taverna integration)
            System.out.print(outpath);
            System.exit(0);
        } catch (Exception e) {
            logger.error("I/O error", e);
        }
    }
}