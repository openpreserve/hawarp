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
package eu.scape_project.arc2warc.warc;

import eu.scape_project.hawarp.mapreduce.FlatListArcRecord;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;

/**
 * Hadoop WARC output format. Custom Hadoop Output format based on the JWAT
 * record writer.
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class WarcOutputFormat extends FileOutputFormat<LongWritable, FlatListArcRecord> {
    
    private static final Log LOG = LogFactory.getLog(WarcOutputFormat.class);

    @Override
    public RecordWriter<LongWritable, FlatListArcRecord> getRecordWriter(TaskAttemptContext tac) throws IOException, InterruptedException {

        LOG.info(tac.getConfiguration().get("map.input.file"));
        
        //get the current path
        Path path = FileOutputFormat.getOutputPath(tac);

        //create the full path with the output directory plus our filename
        String filename = "result" + System.currentTimeMillis() + ".warc";
        Path fullPath = new Path(path, filename);

        //create the file in the file system
        FileSystem fs = path.getFileSystem(tac.getConfiguration());

        FSDataOutputStream fileOut = fs.create(fullPath, true);

        //create our record writer with the new file
        return new WarcOutputFormat.WarcRecordWriter(filename, fileOut);
    }

    public class WarcRecordWriter extends RecordWriter<LongWritable, FlatListArcRecord> {

        private final WarcWriter writer;

        WarcCreator warcCreator;

        public WarcRecordWriter(String filename, DataOutputStream stream) throws IOException {
            writer = WarcWriterFactory.getWriter(stream, false);
            warcCreator = new WarcCreator(writer, filename);
            warcCreator.createWarcInfoRecord();
        }

        @Override
        public void close(TaskAttemptContext tac) throws IOException, InterruptedException {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }

        @Override
        public void write(LongWritable k, FlatListArcRecord arcRecord) throws IOException, InterruptedException {
            warcCreator.createContentRecord(arcRecord);
        }

    }

}
