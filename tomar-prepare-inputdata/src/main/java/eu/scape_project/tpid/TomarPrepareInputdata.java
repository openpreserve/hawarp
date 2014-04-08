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
package eu.scape_project.tpid;

import eu.scape_project.hawarp.utils.StringUtils;
import eu.scape_project.tpid.cli.TpidCliConfig;
import eu.scape_project.tpid.cli.TpidOptions;
import eu.scape_project.hawarp.utils.PropertyUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * TomarPrepareInputdata. Hadoop job to prepare input data for ToMaR.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TomarPrepareInputdata {

    private static TpidCliConfig config;

    private static final Log LOG = LogFactory.getLog(TomarPrepareInputdata.class);

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

            Configuration conf = context.getConfiguration();
            String fileSystemPrefix;
            if (conf.getBoolean("pseudo_distributed", false)) {
                fileSystemPrefix = "file://";
            } else {
                fileSystemPrefix = "hdfs://";
            }
            Path pt = new Path(fileSystemPrefix + value);
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
    public TomarPrepareInputdata() {
    }

    /**
     * Get CLI configuration
     *
     * @return CLI configuration
     */
    public static TpidCliConfig getConfig() {
        return config;
    }

    /**
     * Main entry point.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        start(args);
    }

    /**
     * Start.
     *
     * @param args Command line arguments
     * @throws IOException
     * @throws ParseException
     */
    private static void start(String[] args) throws IOException, ParseException {

        // hadoop configuration
        Configuration hadoopConf = new Configuration();
        // Command line interface
        config = new TpidCliConfig();
        
        CommandLineParser cmdParser = new PosixParser();
        GenericOptionsParser gop = new GenericOptionsParser(hadoopConf, args);
        
        TpidOptions tpidOptions = new TpidOptions();
        CommandLine cmd = cmdParser.parse(tpidOptions.options, gop.getRemainingArgs());
        if ((args.length == 0) || (cmd.hasOption(tpidOptions.HELP_OPT))) {
            tpidOptions.exit("Help", 0);
        } else {
            tpidOptions.initOptions(cmd, config);
        }

        // configuration properties
        if (config.getPropertiesFilePath() != null) {
            pu = new PropertyUtil(config.getPropertiesFilePath(), true);
        } else {
            pu = new PropertyUtil("/eu/scape_project/tpid/config.properties", false);
        }

        // cli parameter has priority over default configuration
        int cliParamNumPerInv = config.getNumItemsPerInvokation();
        int defaultNumPerInv = Integer.parseInt(pu.getProp("default.itemsperinvokation"));
        int numPerInv = (cliParamNumPerInv != 0) ? cliParamNumPerInv : defaultNumPerInv;
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
        hadoopConf.setBoolean("pseudo_distributed", config.isPseudoDistributed());
        startHadoopJob(hadoopConf);
    }

    /**
     * Start Hadoop job
     *
     * @param conf Hadoop job configuration
     */
    public static void startHadoopJob(Configuration conf) {
        try {
            Job job = new Job(conf, "tpid_" + conf.getInt("num_items_per_task", 0));
            if (conf.getBoolean("pseudo_distributed", false)) {
                job.getConfiguration().set("mapred.job.tracker", "local");
                job.getConfiguration().set("fs.default.name", "file:///");
            }
            job.setJarByClass(TomarPrepareInputdata.class);

            job.setMapperClass(TomarPrepareInputdata.ContainerProcessingMapper.class);
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

            TextInputFormat.addInputPath(job, new Path(config.getInputStr()));
            String outpath = StringUtils.normdir(conf.get("joboutput_hdfs_path", "tpid_joboutput")) + System.currentTimeMillis();
            FileOutputFormat.setOutputPath(job, new Path(outpath));
            LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
            job.waitForCompletion(true);
            // print output path (taverna integration)
            System.out.print(outpath);
            System.exit(0);
        } catch (Exception e) {
            LOG.error("I/O error", e);
        }
    }
}
