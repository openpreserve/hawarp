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

import eu.scape_project.hawarp.mapreduce.HadoopWebArchiveRecord;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.CompressorStream;
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
public class WarcOutputFormat extends FileOutputFormat<Text, HadoopWebArchiveRecord> {

    private static final Log LOG = LogFactory.getLog(WarcOutputFormat.class);

    @Override
    public RecordWriter<Text, HadoopWebArchiveRecord> getRecordWriter(TaskAttemptContext tac) throws IOException, InterruptedException {
        //create our record writer with the new file
        return new WarcOutputFormat.WarcRecordWriter(tac);
    }

    public class WarcRecordWriter extends RecordWriter<Text, HadoopWebArchiveRecord> {

        private WarcWriter writer = null;

        private OutputStream outputStream;

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
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                LOG.error("I/O Exception",e);
            }
        }

        @Override
        public void write(Text k, HadoopWebArchiveRecord arcRecord) throws IOException, InterruptedException {
            // writer is initialised at the first record because only at this
            // point the input filename is known by the hadoop record key
            if (writer == null) {
                initialiseWriter(k);
            }
            warcCreator.createContentRecord(arcRecord);
        }

        private void initialiseWriter(Text k) throws IOException {
            Path path = FileOutputFormat.getOutputPath(tac);
            String inputFilePath = k.toString();
            String inputFileName = inputFilePath.substring(inputFilePath.lastIndexOf(File.separatorChar) + 1);
            boolean createCompressedWarc = tac.getConfiguration().getBoolean("warc_compressed", false);
            String warcExt = createCompressedWarc ? ".warc.gz" : ".warc";
            String warcFileName = inputFileName + warcExt;
            Path fullPath = new Path(path, warcFileName);
            FileSystem fs = path.getFileSystem(tac.getConfiguration());
            FSDataOutputStream fsDataOutputStream = fs.create(fullPath, true);
            outputStream = fsDataOutputStream;
            writer = WarcWriterFactory.getWriter(outputStream, createCompressedWarc);
            warcCreator = new WarcCreator(writer, warcFileName);
            warcCreator.createWarcInfoRecord();
        }

    }

}
