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
package eu.scape_project.up2ti.container;

import eu.scape_project.hawarp.utils.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;

/**
 * ARC files map. Different Key-value pair maps for managing ARC file records.
 * K: Record key V: temporary file
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class ArcContainer extends DualHashBidiMap implements Container {

    private static final Log LOG = LogFactory.getLog(ArcContainer.class);
    private ArrayList<ArcRecordBase> archiveRecords;

    private String extractDirectoryName;

    private ArcReader reader;
    
    private String containerFileName;

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
        this.containerFileName = containerFileName;
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
        archiveRecords = new ArrayList<ArcRecordBase>();
        // initialise object by create temporary files and the bidirectional 
        // file-record map.
        arcRecContentsToTempFiles();
    }

    @Override
    public DualHashBidiMap getBidiIdentifierFilenameMap() {
        return this;
    }

    public ArrayList<ArcRecordBase> getArchiveRecords() {
        return archiveRecords;
    }

    public void setArchiveRecords(ArrayList<ArcRecordBase> archiveRecords) {
        this.archiveRecords = archiveRecords;
    }

    /**
     * Create temporary files and create bidirectional file-record map.
     *
     * @throws IOException IO Error
     */
    private void arcRecContentsToTempFiles() throws IOException {
        extractDirectoryName = "/tmp/up2ti_" + RandomStringUtils.randomAlphabetic(10) + "/";
        File destDir = new File(extractDirectoryName);
        destDir.mkdir();
        Iterator<ArcRecordBase> recordIterator = reader.iterator();
        try {
            // K: Record key V: Temporary file
            while (recordIterator.hasNext()) {
                ArcRecordBase arcRecord = recordIterator.next();
                String archiveDateStr = arcRecord.getArchiveDateStr();
                archiveRecords.add(arcRecord);
                String recordIdentifier = arcRecord.getUrlStr();
                String recordKey = containerFileName + "/"+archiveDateStr+ "/" + recordIdentifier;
                if (arcRecord.hasPayload() && arcRecord.getPayload().getRemaining()< Integer.MAX_VALUE) {
                    File tmpFile = IOUtils.copyStreamToTempFileInDir(arcRecord.getPayloadContent(), extractDirectoryName, ".tmp");
                    this.put(recordKey, tmpFile.getAbsolutePath());
                }
            }
        } catch (RuntimeException ex) {
            LOG.error("ARC reader error, skipped.", ex);
        }
    }

}
