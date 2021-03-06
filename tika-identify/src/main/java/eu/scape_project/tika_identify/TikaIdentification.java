/*
 *  Copyright 2011 The SCAPE Project Consortium.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package eu.scape_project.tika_identify;

import eu.scape_project.tika_identify.cli.TikaCliConfig;
import eu.scape_project.tika_identify.cli.TikaCliOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import eu.scape_project.tika_identify.tika.TikaIdentificationTask;
import java.io.FileOutputStream;
import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * Tika command line application
 *
 * @author shsdev https://github.com/shsdev
 * @version 0.2
 */
public class TikaIdentification {

    private static TikaCliConfig appConfig;

    private static final Log LOG = LogFactory.getLog(TikaIdentification.class);

    private TikaIdentificationTask tihj;

    public static class TikaIdentifyReducer
            extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String keyStr = key.toString();
            if (keyStr != null && !keyStr.isEmpty()) {
                for(Text val : values) {
                    context.write(key, val);
                }
            }
        }
    }

    /**
     * The map class of HocrParser.
     */
    public static class TikaIdentifyMapper
            extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Mapper.Context context)
                throws IOException, InterruptedException {
            TikaIdentificationTask dihj = null;
            try {
                dihj = TikaIdentificationTask.getInstance();
                if (dihj != null) {
                    String mime = dihj.identify(value.toString());
                    context.write(key, new Text(mime));
                } else {
                    LOG.error("Tika identifier not available");
                }
            
            } catch (FileNotFoundException ex) {
                LOG.error("File not found error", ex);
            }
        }
    }

    public TikaIdentification() {
        try {
            tihj = TikaIdentificationTask.getInstance();
        } catch (IOException ex) {
            LOG.error("File not found error", ex);
        }
    }

    public void startHadoopJob() throws IOException, InterruptedException, ClassNotFoundException {
        Configuration hadoopConf = new Configuration();
        String name = "tika_identification";

        Job job = new Job(hadoopConf, name);
        if (appConfig.isPseudoDistributed()) {
            // local debugging (pseudo-distributed)
            job.getConfiguration().set("mapred.job.tracker", "local");
            job.getConfiguration().set("fs.default.name", "file:///");
        }
        job.setJarByClass(TikaIdentification.class);

        job.setMapperClass(TikaIdentification.TikaIdentifyMapper.class);
        job.setReducerClass(TikaIdentification.TikaIdentifyReducer.class);

        job.setInputFormatClass(TextInputFormat.class);

        job.setOutputFormatClass(TextOutputFormat.class);
        
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        SequenceFileInputFormat.addInputPath(job, new Path(appConfig.getInputStr()));
        String outpath = appConfig.getOutputStr();
        if (outpath == null || outpath.isEmpty()) {
            outpath = "tika_identification/" + System.currentTimeMillis();
        }
        FileOutputFormat.setOutputPath(job, new Path(outpath));
        job.waitForCompletion(true);
        System.exit(0);

    }

    public void startApplication() throws IOException {
        long startClock = System.currentTimeMillis();
        File dir = new File(appConfig.getInputStr());
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Input is not a directory: " + appConfig.getInputStr());
        }
        PrintStream pout = null;
        String outputPathStr = appConfig.getOutputStr();
        if (outputPathStr != null) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(outputPathStr, true);
                pout = new PrintStream(fos);
                System.setOut(pout);
            } catch (FileNotFoundException ex) {
                LOG.error("File not found error", ex);
            }
        }
        this.processFiles(new File(appConfig.getInputStr()));
        if (pout != null) {
            pout.close();
        }
        long elapsedTimeMillis = System.currentTimeMillis() - startClock;
        LOG.info("Identification finished after " + elapsedTimeMillis + " milliseconds");
    }

    public static void main(String[] args) throws ParseException, IOException, InterruptedException, ClassNotFoundException {
        CommandLineParser cmdParser = new PosixParser();

        TikaCliOptions tikaCliOpts = new TikaCliOptions();
        appConfig = new TikaCliConfig();

        CommandLine cmd = cmdParser.parse(tikaCliOpts.options, args);
        if ((args.length == 0) || (cmd.hasOption(tikaCliOpts.HELP_OPT))) {
            tikaCliOpts.exit("Help", 0);
        } else {
            tikaCliOpts.initOptions(cmd, appConfig);
            TikaIdentification tc = new TikaIdentification();
            if (appConfig.isLocal()) {
                tc.startApplication();
            } else {
                tc.startHadoopJob();
            }
        }
    }

    private void processFiles(File path) throws FileNotFoundException, IOException {
        if (path.isDirectory()) {
            String[] children = path.list();
            for (int i = 0; i < children.length; i++) {
                processFiles(new File(path, children[i]));
            }
        } else {
            processFile(path);
        }
    }

    private synchronized void processFile(File path) throws FileNotFoundException, IOException {
        if (tihj != null) {
            String mime = tihj.identify(path.toString());
            System.out.println(path.toString() + "\t" + mime);
        } else {
            LOG.error("Tika identifier not available");
        }

    }
}
