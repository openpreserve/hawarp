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
package eu.scape_project.tb.cipex.container;

import eu.scape_project.tb.cipex.identifiers.ArcFilesTestMap;
import java.io.*;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.*;
import static org.junit.Assert.*;
import eu.scape_project.tb.cipex.container.ZipContainer;

/**
 * Test class for the ZipContainer class
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class ZipContainerTest {
    
    public ZipContainerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
   

    /**
     * Test of init method, of class ZipContainer.
     */
    @Test
    public void testInit() throws Exception {
        InputStream testFileStream = ZipContainer.class.getResourceAsStream("test.zip");
        if(testFileStream == null)
            fail();
        File tmpTestFile = File.createTempFile("test", ".zip");
        FileOutputStream fos = new FileOutputStream(tmpTestFile);
        IOUtils.copy(testFileStream, fos);
        fos.close();
        String containerFileName = "test.zip";
        InputStream containerFileStream = new FileInputStream(tmpTestFile);
        ZipContainer instance = new ZipContainer();
        instance.init(containerFileName, containerFileStream);
        DualHashBidiMap bidiIdentifierFilenameMap = instance.getBidiIdentifierFilenameMap();
        String key = instance.getExtractDirectoryName() + "test.zip/test.doc";
        assertTrue(bidiIdentifierFilenameMap.containsKey(key));
        String value = (String) bidiIdentifierFilenameMap.get(key);
        assertNotNull(value);
        File tmpFile = new File(value);
        assertTrue("File does not exist: "+tmpFile.getAbsolutePath(),tmpFile.exists());
    }
    
    

    /**
     * Test of init method, of class ZipContainer.
     */
    @Test
    public void testInitWithSubfolder() throws Exception {
        InputStream testFileStream = ZipContainer.class.getResourceAsStream("testsub.zip");
        if(testFileStream == null)
            fail();
        File tmpTestFile = File.createTempFile("testsub", ".zip");
        FileOutputStream fos = new FileOutputStream(tmpTestFile);
        IOUtils.copy(testFileStream, fos);
        fos.close();
        String containerFileName = "testsub.zip";
        InputStream containerFileStream = new FileInputStream(tmpTestFile);
        ZipContainer instance = new ZipContainer();
        instance.init(containerFileName, containerFileStream);
        DualHashBidiMap bidiIdentifierFilenameMap = instance.getBidiIdentifierFilenameMap();
        String key = instance.getExtractDirectoryName() + "testsub.zip/test/sub/test.doc";
        //String key = "/testsub.zip/test/sub/test.doc";
        assertTrue(bidiIdentifierFilenameMap.containsKey(key));
        String value = (String) bidiIdentifierFilenameMap.get(key);
        assertNotNull(value);
        File tmpFile = new File(value);
        assertTrue("File does not exist: "+tmpFile.getAbsolutePath(),tmpFile.exists());
    }
   
}
