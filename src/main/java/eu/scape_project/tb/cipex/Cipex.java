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
package eu.scape_project.tb.cipex;

import eu.scape_project.tb.cipex.cli.CliConfig;
import eu.scape_project.tb.cipex.cli.Options;
import eu.scape_project.tb.cipex.container.ArcContainer;
import eu.scape_project.tb.cipex.container.Container;
import eu.scape_project.tb.cipex.container.ZipContainer;
import eu.scape_project.tb.cipex.identifiers.Identification;
import eu.scape_project.tb.cipex.identifiers.IdentifierCollection;
import eu.scape_project.tb.cipex.reporters.Reportable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.FileNotFoundException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

/**
 * Cipex - Container Item Property EXtraction.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class Cipex {

    public static final String SPRING_CONFIG_RESOURCE_PATH = "eu/scape_project/tb/cipex/spring-config.xml";
    private static ApplicationContext ctx;
    private static CliConfig config;
    // Logger instance
    private static Logger logger = LoggerFactory.getLogger(Cipex.class.getName());

    /**
     * Reducer class.
     */
    public static class ContainerItemIdentificationReducer
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
                mos.write("idtab",key, val);
                mos.write("fitsc",key, val);
            }
        }
    }

    /**
     * Mapper class.
     */
    public static class ContainerItemIdentificationMapper
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
            Cipex wai = new Cipex();
            Path pt = new Path("hdfs://" + value);
            FileSystem fs = FileSystem.get(new Configuration());
            wai.executeIdentificationStack(fs.open(pt), pt.getName(), mos);
        }
    }

    public Cipex() {
    }

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
        // Trying to load spring configuration from local file system (same
        // directory where the application is executed). If the spring 
        // configuration is not given as a parameter, it is loaded as a 
        // resource from the class path. 
        if (config.getSpringConfig() != null && (new File(config.getSpringConfig()).exists())) {
            ctx = new FileSystemXmlApplicationContext("file://" + config.getSpringConfig());
        } else {
            ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_RESOURCE_PATH);
        }
        if (config.isMapReduceJob()) {
            startHadoopJob(conf);
        } else {
            startApplication();
        }
    }

    public static void startApplication() throws FileNotFoundException, IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        Cipex wai = new Cipex();
        wai.traverseDir(new File(config.getDirStr()));
        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.debug("Processing time (sec): " + elapsedTime / 1000F);
        System.exit(0);
    }

    public static void startHadoopJob(Configuration conf) {
        try {
            Job job = new Job(conf, "cipex");

            // local debugging (pseudo-distributed)
//             job.getConfiguration().set("mapred.job.tracker", "local");
//             job.getConfiguration().set("fs.default.name", "file:///");

            job.setJarByClass(Cipex.class);

            job.setMapperClass(Cipex.ContainerItemIdentificationMapper.class);
            job.setReducerClass(Cipex.ContainerItemIdentificationReducer.class);

            job.setInputFormatClass(TextInputFormat.class);
            
            // tabular output of identification results
            MultipleOutputs.addNamedOutput(job, "idtab", TextOutputFormat.class, Text.class, Text.class);
            // fits output aggregated in sequence file
            MultipleOutputs.addNamedOutput(job, "fitsc", SequenceFileOutputFormat.class, Text.class, BytesWritable.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(ObjectWritable.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(ObjectWritable.class);

            TextInputFormat.addInputPath(job, new Path(config.getDirStr()));
            String outpath = "output/" + System.currentTimeMillis();
            FileOutputFormat.setOutputPath(job, new Path(outpath));
            job.waitForCompletion(true);
            System.out.print(outpath);
            System.exit(0);
        } catch (Exception e) {
            logger.error("I/O error", e);
        }
    }

    /**
     * Apply identification stack
     *
     * @param containerFileStream Content of the container file
     * @param containerFileName File name
     * @param context Hadoop context (only for Hadoop job execution)
     * @throws FileNotFoundException Exception if the container file cannot be
     * found
     * @throws IOException I/O Exception
     */
    private void executeIdentificationStack(InputStream containerFileStream, String containerFileName, MultipleOutputs mos) throws FileNotFoundException, IOException, InterruptedException {
        Container container; //= (Container) ctx.getBean("containerBean");
        if (containerFileName.endsWith(".arc.gz")) {
            container = new ArcContainer();
        } else if (containerFileName.endsWith(".zip")) {
            container = new ZipContainer();
        } else {
            logger.warn("Unsupported file skipped: " + containerFileName);
            return;
        }
        container.init(containerFileName, containerFileStream);
        
        
        String extrDir = container.getExtractDirectoryName();
        // TODO: fits
        BytesWritable b = new BytesWritable(extrDir.getBytes());
        mos.write("fitsc", new Text(extrDir), b);
        
        if (ctx == null) {
            ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_RESOURCE_PATH);
        }
        IdentifierCollection identificationStack = (IdentifierCollection) ctx.getBean("identificationStack");
        for (Identification identifierItem : identificationStack.getIdentifiers()) {
            Identification fli = (Identification) identifierItem;
            Reportable rep = (Reportable) ctx.getBean("reporterBean");
            if (mos != null) {
                rep.report(fli.identifyFileList(container.getBidiIdentifierFilenameMap()), mos);
            } else {
                rep.report(fli.identifyFileList(container.getBidiIdentifierFilenameMap()));
            }
        }
    }

    /**
     * Traverse the root directory recursively
     *
     * @param dir Root directory
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void traverseDir(File dirStructItem) throws FileNotFoundException, IOException, InterruptedException {
        if (dirStructItem.isDirectory()) {
            String[] children = dirStructItem.list();
            for (String child : children) {
                traverseDir(new File(dirStructItem, child));
            }
        } else if (!dirStructItem.isDirectory()) {
            File arcFile = new File(dirStructItem.getAbsolutePath());
            FileInputStream fileInputStream = new FileInputStream(arcFile);

            executeIdentificationStack(fileInputStream, arcFile.getName(), null);

        }
    }
}
