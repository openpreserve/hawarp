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

import java.io.*;
import java.util.Set;
import java.util.zip.ZipEntry;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import java.util.zip.ZipInputStream;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ZIP files map. Different Key-value pair maps for managing ZIP file records.
 * K: Record key V: temporary file
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class ZipContainer extends DualHashBidiMap implements Container {

    private static final Log LOG = LogFactory.getLog(ZipContainer.class);
    
    private static final int BUFFER_SIZE = 4096;
    
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
    public ZipContainer() {
    }

    @Override
    public void init(String containerFileName, InputStream containerFileStream) throws IOException {
        unzip(containerFileName,containerFileStream);
    }

    @Override
    public DualHashBidiMap getBidiIdentifierFilenameMap() {
        return this;
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param containerFileName
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(String containerFileName, InputStream containerFileStream) throws IOException {
        extractDirectoryName = "/tmp/up2ti_"+RandomStringUtils.randomAlphabetic(10)+"/";
        File destDir = new File(extractDirectoryName);
        destDir.mkdir();
        String subDestDirStr = extractDirectoryName+containerFileName+"/";
        File subDestDir = new File(subDestDirStr);
        subDestDir.mkdir();
        ZipInputStream zipIn = new ZipInputStream(containerFileStream);
        ZipEntry entry = zipIn.getNextEntry();
        while (entry != null) {
            String filePath = subDestDirStr + entry.getName();
            if (!entry.isDirectory()) {
                extractFile(zipIn, filePath);
            } else {
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
        if((new File(filePath).exists())) {
            String id = filePath.replaceFirst("[0-9]{10,30}", "");
            //id = id.replace(this.tmpDirName"/tmp", "");
            // key-value pair
            this.put(filePath, filePath);
        }
    }
    
}
