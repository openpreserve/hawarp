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
import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
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
public class WarcOutputFormat extends FileOutputFormat<Text, FlatListArcRecord> {

    private static final Log LOG = LogFactory.getLog(WarcOutputFormat.class);

    @Override
    public RecordWriter<Text, FlatListArcRecord> getRecordWriter(TaskAttemptContext tac) throws IOException, InterruptedException {
        //create our record writer with the new file
        return new WarcOutputFormat.WarcRecordWriter(tac);
    }

    public class WarcRecordWriter extends RecordWriter<Text, FlatListArcRecord> {

        private WarcWriter writer = null;

        private final TaskAttemptContext tac;

        WarcCreator warcCreator;

        public WarcRecordWriter(TaskAttemptContext tac) {
            this.tac = tac;
            // writer is initialised when writing first record
            writer = null;
        }

        @Override
        public void close(TaskAttemptContext tac) throws IOException, InterruptedException {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }

        @Override
        public void write(Text k, FlatListArcRecord arcRecord) throws IOException, InterruptedException {
            // writer is initialised at the first record because only at this
            // point the input filename is known by the hadoop record key
            if (writer == null) {
                initialiseWriter(k);
            }
            warcCreator.createContentRecord(arcRecord);
        }

        private void initialiseWriter(Text k) throws IOException {
            //get the current path
            Path path = FileOutputFormat.getOutputPath(tac);
            String inputFilePath = k.toString();
            String inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf(File.separatorChar) + 1);
            
            // create compressed warc?
            boolean createCompressedWarc = tac.getConfiguration().getBoolean("warc_compressed", false);

            //create the full path with the output directory plus filename
            
            String warcExt = createCompressedWarc?".warc.gz":".warc";
            String warcFileName = inputFileName + warcExt;
            Path fullPath = new Path(path, warcFileName);

            //create the file in the file system
            FileSystem fs = path.getFileSystem(tac.getConfiguration());

            FSDataOutputStream outputStream = fs.create(fullPath, true);

            writer = WarcWriterFactory.getWriter(outputStream, createCompressedWarc);
            warcCreator = new WarcCreator(writer, warcFileName);
            warcCreator.createWarcInfoRecord();
        }

    }

}
