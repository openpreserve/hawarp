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
package eu.scape_project.mup2ti.container;

import eu.scape_project.mup2ti.utils.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCRecord;

/**
 * ARC files map. Different Key-value pair maps for managing ARC file records.
 * K: Record key V: temporary file
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class ArcContainer extends DualHashBidiMap implements Container {

    private static final Log LOG = LogFactory.getLog(ArcContainer.class);
    private static ArchiveReader reader;
    private ArrayList<ArchiveRecord> archiveRecords;
    
    private String extractDirectoryName;

    @Override
    public String getExtractDirectoryName() {
        return extractDirectoryName;
    }

    public void setExtractDirectoryName(String extractDirectoryName) {
        this.extractDirectoryName = extractDirectoryName;
    }

    /**
     * Delete temporary files.
     */
    public void deleteFiles() {
        if (!this.isEmpty()) {
            Set<String> tmpFileSet = this.keySet();
            for (Object tmp : tmpFileSet) {
                File tmpFile = new File((String) this.get(tmp));
                tmpFile.delete();
            }
        }
    }

    /**
     * Constructor
     */
    public ArcContainer() {
    }
    
    @Override
    public void init(String containerFileName, InputStream containerFileStream) throws IOException {
        reader = ArchiveReaderFactory.get(containerFileName, containerFileStream, true);
        //ArcFilesMap.reader = reader;
        archiveRecords = new ArrayList<ArchiveRecord>();
        // initialise object by create temporary files and the bidirectional 
        // file-record map.
        arcRecContentsToTempFiles();
    }
    
    @Override
    public DualHashBidiMap getBidiIdentifierFilenameMap() {
        return this;
    }

    public ArrayList<ArchiveRecord> getArchiveRecords() {
        return archiveRecords;
    }

    public void setArchiveRecords(ArrayList<ArchiveRecord> archiveRecords) {
        this.archiveRecords = archiveRecords;
    }
    
    

    /**
     * Read the ARC record content into a byte array. Note that the record
     * content can be only read once, it is "consumed" afterwards.
     *
     * @param arcRecord ARC record.
     * @return Content byte array.
     * @throws IOException If content is too large to be stored in a byte array.
     */
    private static byte[] readArcRecContentToByteArr(ARCRecord arcRecord) throws IOException {

        if (arcRecord.getMetaData().getLength() > Integer.MAX_VALUE) {
            throw new IOException("ARC Record content is too large");
        }
        // Length of the complete ARC record
        int dataLength = (int) arcRecord.getMetaData().getLength();
        // Byte point where the content of the ARC record begins
        int contentBegin = (int) arcRecord.getMetaData().getContentBegin();
        byte[] recordBuffer = new byte[dataLength];
        byte[] tempBuffer = new byte[4096];
        int bytesRead;
        int totalBytes = 0;
        while ((totalBytes < dataLength) && ((bytesRead = arcRecord.read(tempBuffer)) != -1)) {
            System.arraycopy(tempBuffer, 0, recordBuffer, totalBytes, bytesRead);
            totalBytes += bytesRead;
        }
        // Cutting off the record section at the beginning of the ARC record
        byte[] contentByteArr = new byte[totalBytes - contentBegin];
        System.arraycopy(recordBuffer, contentBegin, contentByteArr, 0, totalBytes - contentBegin);
        return contentByteArr;
    }

    /**
     * Create temporary files and create bidirectional file-record map.
     *
     * @throws IOException IO Error
     */
    private void arcRecContentsToTempFiles() throws IOException {
        extractDirectoryName = "/tmp/archiventory_"+RandomStringUtils.randomAlphabetic(10)+"/";
        File destDir = new File(extractDirectoryName);
        destDir.mkdir();
        Iterator<ArchiveRecord> recordIterator = reader.iterator();
        try {
            // K: Record key V: Temporary file
            while (recordIterator.hasNext()) {
                ArchiveRecord nativeArchiveRecord = recordIterator.next();
                archiveRecords.add(nativeArchiveRecord);
                String readerIdentifier = nativeArchiveRecord.getHeader().getReaderIdentifier();
                String recordIdentifier = nativeArchiveRecord.getHeader().getRecordIdentifier();
                ARCRecord arcRecord = (ARCRecord)nativeArchiveRecord;
                byte[] content = ArcContainer.readArcRecContentToByteArr(arcRecord);
                String recordKey = readerIdentifier+"/"+recordIdentifier;
                if (nativeArchiveRecord.getHeader().getLength() < Integer.MAX_VALUE) {
                    File tmpFile = IOUtils.copyByteArrayToTempFileInDir(content, extractDirectoryName, ".tmp");
//                    tmpFile.deleteOnExit();
                    this.put(recordKey, tmpFile.getAbsolutePath());
                }
            }
        } catch (RuntimeException ex) {
            LOG.error("ARC reader error, skipped.", ex);
        }
    }

}
