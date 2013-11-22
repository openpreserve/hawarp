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
import eu.scape_project.spacip.unpacker.ContainerItemPreparation;
import eu.scape_project.spacip.utils.StrUt;
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
    
    // Default parameters
    public static final int DEFAULT_ITEMS_PER_INVOCATION = 50;
    public static final String DEFAULT_SCAPE_PLATFORM_INVOKE = "tool operation";
    public static final String DEFAULT_OUTPUT_FILE_SUFFIX = ".fits.xml";
    public static final String DEFAULT_UNPACK_HDFS_PATH = "spacip_unpacked";
    public static final String DEFAULT_TOOLOUTPUT_HDFS_PATH = "spacip_tooloutput";
    public static final String DEFAULT_JOBOUTPUT_HDFS_PATH = "spacip_joboutput";
    public static final String CONTAINER_FILE_SUFFIX = ".arc.gz";

    /**
     * Reducer class.
     */
    public static class InputPreparationReducer
            extends Reducer<Text, Text, Text, ObjectWritable> {

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
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text val : values) {
                mos.write("keyfilmapping", key, val);
                mos.write("ptmapredinput", key, val);
            }
        }
    }

    /**
     * Mapper class.
     */
    public static class InputPreparationMapper
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
            if (containerFileName.endsWith(CONTAINER_FILE_SUFFIX)) {
                ContainerItemPreparation hau = new ContainerItemPreparation(mos, context, conf);
                
                hau.prepareInput(pt);
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
        Configuration conf = new Configuration();
        // Command line interface
        config = new CliConfig();
        CommandLineParser cmdParser = new PosixParser();
        GenericOptionsParser gop = new GenericOptionsParser(conf, args);
        CommandLine cmd = cmdParser.parse(Options.OPTIONS, gop.getRemainingArgs());
        if ((args.length == 0) || (cmd.hasOption(Options.HELP_OPT))) {
            Options.exit("Usage", 0);
        } else {
            Options.initOptions(cmd, config);
        }
        conf.setInt("num_items_per_task", config.getNumItemsPerInvokation());
        conf.set("scape_platform_invoke", config.getScapePlatformInvoke());
        conf.set("output_file_suffix", config.getOutputFileSuffix());
        conf.set("unpack_hdfs_path", config.getUnpackHdfsPath());
        conf.set("joboutput_hdfs_path", config.getJoboutputHdfsPath());
        conf.set("tooloutput_hdfs_path", config.getTooloputHdfsPath());
        startHadoopJob(conf);

    }

    /**
     * Start Hadoop job
     *
     * @param conf Hadoop job configuration
     */
    public static void startHadoopJob(Configuration conf) {
        try {
            Job job = new Job(conf, "spacip");

            // local debugging (pseudo-distributed)
//             job.getConfiguration().set("mapred.job.tracker", "local");
//             job.getConfiguration().set("fs.default.name", "file:///");

            job.setJarByClass(Spacip.class);

            job.setMapperClass(Spacip.InputPreparationMapper.class);
            job.setReducerClass(Spacip.InputPreparationReducer.class);

            job.setInputFormatClass(TextInputFormat.class);

            // tabular output of identification results
            MultipleOutputs.addNamedOutput(job, "keyfilmapping", TextOutputFormat.class, Text.class, Text.class);
            MultipleOutputs.addNamedOutput(job, "ptmapredinput", TextOutputFormat.class, Text.class, Text.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(ObjectWritable.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(ObjectWritable.class);

            TextInputFormat.addInputPath(job, new Path(config.getDirStr()));
            String outpath = StrUt.normdir(config.getJoboutputHdfsPath()) + System.currentTimeMillis();
            FileOutputFormat.setOutputPath(job, new Path(outpath));
            job.waitForCompletion(true);
            System.out.print(outpath);
            System.exit(0);
        } catch (Exception e) {
            logger.error("I/O error", e);
        }
    }
}
