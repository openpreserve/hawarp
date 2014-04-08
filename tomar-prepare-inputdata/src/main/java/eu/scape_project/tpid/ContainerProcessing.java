/*
 * Copyright 2012 The SCAPE Project Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. under the License.
 */
package eu.scape_project.tpid;

import eu.scape_project.hawarp.mapreduce.JwatArcReaderFactory;
import eu.scape_project.hawarp.utils.ArcUtils;
import eu.scape_project.hawarp.utils.StringUtils;
import java.io.*;
import java.util.Iterator;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;

/**
 * ContainerItemPreparation
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class ContainerProcessing {

    public static final int BUFFER_SIZE = 8192;

    private MultipleOutputs mos;
    private Mapper.Context context;
    private Configuration conf;

    /**
     * Constructor
     *
     * @param mos
     * @param context
     */
    public ContainerProcessing(MultipleOutputs mos, Mapper.Context context, Configuration conf) {
        this.mos = mos;
        this.context = context;
        this.conf = conf;
    }

    /**
     * Constructor
     */
    private ContainerProcessing() {

    }

    /**
     * Prepare input
     *
     * @param pt
     * @throws IOException IO Error
     * @throws java.lang.InterruptedException
     */
    public void prepareInput(Path pt) throws InterruptedException, IOException {
        FileSystem fs = FileSystem.get(context.getConfiguration());
        InputStream containerFileStream = fs.open(pt);
        String containerFileName = pt.getName();

        ArcReader reader;
        // Read first two bytes to check if we have a gzipped input stream
        PushbackInputStream pb = new PushbackInputStream(containerFileStream, 2);
        byte[] signature = new byte[2];
        pb.read(signature);
        pb.unread(signature);
        // use compressed reader if gzip magic number is matched
        if (signature[ 0] == (byte) 0x1f && signature[ 1] == (byte) 0x8b) {
            reader = ArcReaderFactory.getReaderCompressed(pb);
        } else {
            reader = ArcReaderFactory.getReaderUncompressed(pb);
        }
        long currTM = System.currentTimeMillis();
        String unpackHdfsPath = conf.get("unpack_hdfs_path", "tpid_unpacked");
        String hdfsUnpackDirStr = StringUtils.normdir(unpackHdfsPath, Long.toString(currTM));
        String hdfsJoboutputPath = conf.get("tooloutput_hdfs_path", "tpid_tooloutput");
        String hdfsOutputDirStr = StringUtils.normdir(hdfsJoboutputPath, Long.toString(currTM));

        Iterator<ArcRecordBase> arcIterator = reader.iterator();

        // Number of files which should be processed per invokation
        int numItemsPerInvocation = conf.getInt("num_items_per_task", 50);
        int numItemCounter = numItemsPerInvocation;
        // List of input files to be processed
        String inliststr = "";
        // List of output files to be generated
        String outliststr = "";
        try {
            while (arcIterator.hasNext()) {
                ArcRecordBase arcRecord = arcIterator.next();
                String recordKey = getRecordKey(arcRecord, containerFileName);
                String outFileName = RandomStringUtils.randomAlphabetic(25);
                String hdfsPathStr = hdfsUnpackDirStr + outFileName;
                Path hdfsPath = new Path(hdfsPathStr);
                String outputFileSuffix = conf.get("output_file_suffix", ".fits.xml");
                String hdfsOutPathStr = hdfsOutputDirStr + outFileName + outputFileSuffix;
                FSDataOutputStream hdfsOutStream = fs.create(hdfsPath);
                ArcUtils.recordToOutputStream(arcRecord, hdfsOutStream);
                Text key = new Text(recordKey);
                Text value = new Text(fs.getHomeDirectory() + File.separator + hdfsOutPathStr);
                mos.write("keyfilmapping", key, value);
                String scapePlatformInvoke = conf.get("scape_platform_invoke", "fits dirxml");
                Text ptmrkey = new Text(scapePlatformInvoke);
                // for the configured number of items per invokation, add the 
                // files to the input and output list of the command.
                inliststr += "," + fs.getHomeDirectory() + File.separator + hdfsPathStr;
                outliststr += "," + fs.getHomeDirectory() + File.separator + hdfsOutPathStr;
                if (numItemCounter > 1 && arcIterator.hasNext()) {
                    numItemCounter--;
                } else if (numItemCounter == 1 || !arcIterator.hasNext()) {
                    inliststr = inliststr.substring(1); // cut off leading comma 
                    outliststr = outliststr.substring(1); // cut off leading comma 
                    String pattern = conf.get("tomar_param_pattern", "%1$s %2$s");
                    String ptMrStr = StringUtils.formatCommandOutput(pattern, inliststr, outliststr);
                    Text ptmrvalue = new Text(ptMrStr);
                    // emit tomar input line where the key is the tool invokation
                    // (tool + operation) and the value is the parameter list
                    // where input and output strings contain file lists.
                    mos.write("tomarinput", ptmrkey, ptmrvalue);
                    numItemCounter = numItemsPerInvocation;
                    inliststr = "";
                    outliststr = "";
                }
            }
        } catch (Exception ex) {
            mos.write("error", new Text("Error"), new Text(pt.toString()));
        }

    }

    public static int iterateOverContainerRecords(String path) throws IOException, InterruptedException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        InputStream containerFileStream = fis;
        String containerFileName = path;
        ArcReader reader = JwatArcReaderFactory.getReader(containerFileStream);
        Iterator<ArcRecordBase> arcIterator = reader.iterator();
        arcIterator.next(); // skip filedesc record (arc filedesc)
        int i = 0;
        // K: Record key V: Temporary file
        while (arcIterator.hasNext()) {
            ArcRecordBase arcRecord = arcIterator.next();
            String archiveDateStr = arcRecord.getArchiveDateStr();
            String recordIdentifier = arcRecord.getUrlStr();
            String recordKey = containerFileName + "/" + archiveDateStr + "/" + recordIdentifier;
            i++;
            System.out.println("Nr. " + i + ": " + recordKey);
        }
        return i; // number of records without filedesc record
    }

    private String getRecordKey(ArcRecordBase arcRecord, String containerFileName) {
        String archiveDateStr = arcRecord.getArchiveDateStr();
        String recordIdentifier = arcRecord.getUrlStr();
        String recordKey = containerFileName + "/" + archiveDateStr + "/" + recordIdentifier;
        return recordKey;
    }

}
