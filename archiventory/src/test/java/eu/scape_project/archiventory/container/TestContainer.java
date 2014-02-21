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
package eu.scape_project.archiventory.container;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.hadoop.thirdparty.guava.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test container class
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TestContainer extends DualHashBidiMap implements Container {

    public static String ODT_TESTFILE = "test.odt";
    public static String TXT_TESTFILE = "test.txt";
    public static String PDF_TESTFILE = "test.pdf";
    private String extractDirectoryName;
    
    private List<String> testfiles;

    /**
     * Get the list of testfiles
     * @return 
     */
    public List<String> getTestfiles() {
        
        return testfiles;
    }

    /**
     * Set the bidirectional
     * @return extract directory name
     */
    @Override
    public DualHashBidiMap getBidiIdentifierFilenameMap() {
        return this;
    }

    /**
     * Get the extract directory name
     * @return extract directory name
     */
    @Override
    public String getExtractDirectoryName() {
        return this.extractDirectoryName;
    }

    /**
     * Initialise test container
     *
     * @param containerFileName Name of the container file
     * @param containerFileStream Container file stream
     * @throws IOException
     */
    @Override
    public void init(String containerFileName, InputStream containerFileStream) throws IOException {
        File tmpDir = Files.createTempDir();
        testfiles = new ArrayList<String>();
        extractDirectoryName = tmpDir.getAbsolutePath();
        File odtTempFile = createTempTestFile(ODT_TESTFILE);
        testfiles.add(ODT_TESTFILE);
        this.put(ODT_TESTFILE, odtTempFile.getAbsolutePath());
        File txtTempFile = createTempTestFile(TXT_TESTFILE);
        testfiles.add(TXT_TESTFILE);
        this.put(TXT_TESTFILE, txtTempFile.getAbsolutePath());
        File pdfTempFile = createTempTestFile(PDF_TESTFILE);
        testfiles.add(PDF_TESTFILE);
        this.put(PDF_TESTFILE, pdfTempFile.getAbsolutePath());
    }

    /**
     * Create temporary files for the test container
     *
     * @param resourcePath Resource path
     * @return temporary file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private File createTempTestFile(String resourcePath) throws FileNotFoundException, IOException {
        InputStream testFileStream = TestContainer.class.getResourceAsStream(resourcePath);
        if (testFileStream == null) {
            throw new FileNotFoundException(resourcePath);
        }
        String filePath = extractDirectoryName + File.separator + resourcePath;
        File tmpTestFile = new File(filePath);
        Files.createParentDirs(tmpTestFile);
        tmpTestFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tmpTestFile);
        IOUtils.copy(testFileStream, fos);
        fos.close();
        return tmpTestFile;
    }
}
