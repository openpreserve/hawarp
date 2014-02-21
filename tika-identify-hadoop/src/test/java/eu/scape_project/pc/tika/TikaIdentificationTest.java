/*
 *  Copyright 2012 The SCAPE Project Consortium.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package eu.scape_project.pc.tika;

import eu.scape_project.pc.tika.TikaIdentification;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test class for the Droid File Format Identification.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TikaIdentificationTest {

    private static TikaIdentification tikaid;

    /**
     * Set up.
     * @throws Exception 
     */
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
     * Test ODT file format identification
     * @throws IOException 
     */
//    @Test
//    public void testIdentifyOdt() throws IOException {
//        InputStream odtTestFileStream = TikaIdentificationTest.class.getResourceAsStream("testfile.odt");
//        File tmpOdtTestFile = File.createTempFile("odttestfile", ".odt");
//        FileOutputStream fos = new FileOutputStream(tmpOdtTestFile);
//        IOUtils.copy(odtTestFileStream, fos);
//        fos.close();
//        List<IdentificationResult> result = dihj.identify(tmpOdtTestFile.getAbsolutePath());
//        if(result.isEmpty()) {
//            fail("No identification result");
//        }
//        IdentificationResult res = result.get(0);
//        assertEquals("fmt/290",res.getPuid());
//        assertEquals("application/vnd.oasis.opendocument.text",res.getMimeType()); 
//        assertEquals("OpenDocument Text",res.getName()); 
//    }
    
    /**
     * Test PDF file format identification
     * @throws IOException 
     */
//    @Test
//    public void testIdentifyPdf() throws IOException {
//        InputStream odtTestFileStream = TikaIdentificationTest.class.getResourceAsStream("testfile.pdf");
//        File tmpOdtTestFile = File.createTempFile("pdftestfile", ".pdf");
//        FileOutputStream fos = new FileOutputStream(tmpOdtTestFile);
//        IOUtils.copy(odtTestFileStream, fos);
//        fos.close();
//        List<IdentificationResult> result = dihj.identify(tmpOdtTestFile.getAbsolutePath());
//        if(result.isEmpty()) {
//            fail("No identification result");
//        }
//        IdentificationResult res = result.get(0);
//        assertEquals("fmt/18",res.getPuid());
//        assertEquals("application/pdf",res.getMimeType()); 
//        assertEquals("Acrobat PDF 1.4 - Portable Document Format",res.getName()); 
//    }
    
    /**
     * Test PDF file format identification using an input stream of known length
     * @throws IOException 
     */
//    @Test
//    public void testPdfInputStreamIdentify() throws IOException, FileNotFoundException, URISyntaxException {
//       
//    }
    
}