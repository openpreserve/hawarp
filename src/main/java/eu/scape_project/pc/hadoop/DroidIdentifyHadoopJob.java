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

package eu.scape_project.pc.hadoop;

import eu.scape_project.pc.droid.DroidIdentification;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Iterator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;

public class DroidIdentifyHadoopJob {
    
    // Logger instance
    private static Logger logger = LoggerFactory.getLogger(DroidIdentifyHadoopJob.class.getName());

    public static class DroidIdentifyReducer
            extends Reducer<Text, LongWritable, Text, LongWritable> {

        @Override
        public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            String keyStr = key.toString();
            if (keyStr != null && !keyStr.equals("")) {
                Iterator<LongWritable> it = values.iterator();
                Long sum = 0L;
                while (it.hasNext()) {
                    LongWritable value = it.next();
                    sum += value.get();
                    String strVal = value.toString();
                }
                context.write(key, new LongWritable(sum));
            }
        }
    }

    /**
     * The map class of HocrParser.
     */
    public static class DroidIdentifyMapper
            extends Mapper<Text, BytesWritable, Text, LongWritable> {

        @Override
        public void map(Text key, BytesWritable value, Mapper.Context context)
                throws IOException, InterruptedException {
            
            DroidIdentification dihj = null;
            
            File dummy = new File("/tmp/dummy.tmp");
            
            // Attention: Hadoop versions < 0.22.0 return a padded byte array
            // with arbitrary data chunks and zero bytes using BytesWritable.getBytes.
            // BytesWritable.getBytes.getLength() returns the real size of the
            // BytesWritable content. The name of BytesWritable.getBytes is
            // misleading, which has been fixed in Hadoop version 0.22.0.
            // See https://issues.apache.org/jira/browse/HADOOP-6298
            byte[] bytes = value.getBytes();
            int bytesLen = value.getLength();
            byte[] slicedBytes = new byte[bytesLen];
            System.arraycopy(bytes, 0, slicedBytes, 0, bytesLen);
            InputStream valueInputStream = new ByteArrayInputStream(slicedBytes);
            try {
                dihj = DroidIdentification.getInstance();
                if(dihj != null) {
                    IdentificationResult result = dihj.identify(valueInputStream , new Long(bytesLen));
                    context.write(new Text(result.getMimeType()), new LongWritable(1L) );
                } else {
                    logger.error("Droid identifier not available");
                }
            } catch (SignatureParseException ex) {
                logger.error("Error parsing signature file", ex);
            } catch (FileNotFoundException ex) {
                logger.error("File not found error", ex);
            } catch (URISyntaxException ex) {
                logger.error("URI syntax error", ex);
            }
        }
    }

    /**
     * The main entry point.
     */
    public static void main(String[] args) throws ParseException {
        Configuration conf = new Configuration();
        GenericOptionsParser gop = new GenericOptionsParser(conf, args);
        HadoopJobCliConfig pc = new HadoopJobCliConfig();
        CommandLineParser cmdParser = new PosixParser();
        CommandLine cmd = cmdParser.parse(HadoopJobOptions.OPTIONS, gop.getRemainingArgs());
        if ((args.length == 0) || (cmd.hasOption(HadoopJobOptions.HELP_OPT))) {
            HadoopJobOptions.exit("Usage", 0);
        } else {
            HadoopJobOptions.initOptions(cmd, pc);
        }
        String dir = pc.getDirStr();

        String name = pc.getHadoopJobName();
        if (name == null || name.equals("")) {
            name = "droid_identification";
        }

        try {
            Job job = new Job(conf, name);
            
              // local debugging
            job.getConfiguration().set("mapred.job.tracker", "local");
            job.getConfiguration().set("fs.default.name", "file:///");
            
            job.setJarByClass(DroidIdentifyHadoopJob.class);

            job.setMapperClass(DroidIdentifyMapper.class);
            //job.setCombinerClass(DroidIdentifyReducer.class);
            job.setReducerClass(DroidIdentifyReducer.class);

            job.setInputFormatClass(SequenceFileInputFormat.class);

            job.setOutputFormatClass(TextOutputFormat.class);
            //SequenceFileOutputFormat.setOutputCompressionType(job, SequenceFile.CompressionType.NONE);

            //conf.setMapOutputKeyClass(Text.class);
            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(LongWritable.class);

            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(LongWritable.class);

            SequenceFileInputFormat.addInputPath(job, new Path(dir));
            String outpath = "output/" + System.currentTimeMillis() + "dri";
            FileOutputFormat.setOutputPath(job, new Path(outpath));
            job.waitForCompletion(true);
            System.out.print(outpath);
            System.exit(0);
        } catch (Exception e) {
            logger.error("I/O error", e);
        }
    }
}
