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
package eu.scape_project.tika_identify.tika;

import com.google.common.io.Resources;
import eu.scape_project.tika_identify.tika.TikaIdentification;
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
        
        tikaid = TikaIdentification.getInstance();
      
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
    @Test
    public void testIdentifyOdt() throws IOException {
        InputStream odtFileInputStream = Resources.getResource("testfile.odt").openStream();
        assertNotNull(odtFileInputStream);
        String odtResult = tikaid.identify(odtFileInputStream);
        assertEquals("application/zip",odtResult);
        odtFileInputStream.close();
        
        InputStream pdfFileInputStream = Resources.getResource("testfile.pdf").openStream();
        assertNotNull(pdfFileInputStream);
        String pdfResult = tikaid.identify(pdfFileInputStream);
        assertEquals("application/pdf",pdfResult);
        pdfFileInputStream.close();
    }
    
}