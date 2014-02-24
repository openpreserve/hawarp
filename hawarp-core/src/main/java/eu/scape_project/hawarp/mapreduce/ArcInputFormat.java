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
package eu.scape_project.hawarp.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;

/**
 * Hadoop input format for ARC files. Hadoop reader based on JWAT ARC record
 * reader, see https://sbforge.org/display/JWAT/JWAT-Tools.
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class ArcInputFormat extends FileInputFormat<LongWritable, ArcRecordBase> {

    /**
     * Create ARC record reader.
     *
     * @param split Input split (one ARC file corresponds to a split)
     * @param context Job configuration context
     * @return Record reader (defined as inner class)
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public RecordReader<LongWritable, ArcRecordBase> createRecordReader(InputSplit split,
            TaskAttemptContext context) throws IOException, InterruptedException {
        return new ArcRecordReader();
    }

    /**
     * Set the 'splittable' property. By defining the input files as
     * "non-splittable", each ARC file is one split.
     *
     * @param context Job configuration context
     * @param filename ARC file path
     * @return
     */
    @Override
    protected boolean isSplitable(JobContext context, Path filename) {
        return false;
    }

    /**
     * ARC Record reader class. The ARC record reader is based on the JWAT
     * record reader. By using the 'compressed' reader, it is assumed that the
     * ARC input files are gzip-compressed.
     */
    public class ArcRecordReader extends RecordReader<LongWritable, ArcRecordBase> {

        private ArcReader reader;
        private long start;
        private long pos;
        private long end;
        private LongWritable key = null;
        private ArcRecordBase value = null;
        private Seekable filePosition;

        /**
         * Initialise the JWAT record reader
         *
         * @param genericSplit Input split (corresponds to one ARC file)
         * @param context Context
         * @throws IOException
         */
        @Override
        public void initialize(InputSplit genericSplit, TaskAttemptContext context) throws IOException {
            FileSplit split = (FileSplit) genericSplit;
            Configuration job = context.getConfiguration();
            
            final Path file = split.getPath();

            FileSystem fs = file.getFileSystem(job);
            FSDataInputStream fileIn = fs.open(split.getPath());

            reader = ArcReaderFactory.getReaderCompressed(fileIn);

            start = split.getStart();
            end = start + split.getLength();
            filePosition = fileIn;
            this.pos = start;
        }

        /**
         * Get next key value pair.
         *
         * @return Next key value pair.
         * @throws IOException
         */
        @Override
        public boolean nextKeyValue() throws IOException {

            ArcRecordBase arcRecord = reader.getNextRecord();
            if (arcRecord == null) {
                return false;
            }

            key = new LongWritable(reader.getOffset());
            value = arcRecord;
            return true;
        }

        /**
         * Get current key
         *
         * @return Current key
         */
        @Override
        public LongWritable getCurrentKey() {
            return key;
        }

        /**
         * Get current value
         *
         * @return Current value
         */
        @Override
        public ArcRecordBase getCurrentValue() {
            return value;
        }

        /**
         * Get the progress within the split
         *
         * @return Progress as floating point number
         * @throws java.io.IOException
         */
        @Override
        public float getProgress() throws IOException {
            if (start == end) {
                return 0.0f;
            } else {
                return Math.min(1.0f, (filePosition.getPos() - start) / (float) (end - start));
            }
        }

        /**
         * Close the reader
         *
         * @throws IOException
         */
        @Override
        public synchronized void close() throws IOException {

            if (reader != null) {
                reader.close();
            }

        }
    }
}
