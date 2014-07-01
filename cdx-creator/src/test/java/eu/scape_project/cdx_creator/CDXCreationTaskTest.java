/*
 * Copyright 2014 onbscs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.scape_project.cdx_creator;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import eu.scape_project.cdx_creator.cli.CDXCreatorConfig;
import eu.scape_project.hawarp.utils.IOUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * CDXCreationTask test class
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class CDXCreationTaskTest {

    private static final Log LOG = LogFactory.getLog(CDXCreationTaskTest.class);

    private static File tempDir;

    public CDXCreationTaskTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        tempDir = Files.createTempDir();
        LOG.info("Temporary directory: " + tempDir);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createIndex method, of class CDXCreationTask.
     */
    @Test
    public void testCdxCreatorArc() throws Exception {
        String arcFileName = "example.arc.gz";
        InputStream arcInputStream = Resources.getResource("arc/" + arcFileName).openStream();
        File arcFile = IOUtils.copyStreamToTempFileInDir(arcInputStream, tempDir.getAbsolutePath(), ".arc.gz");
        assertNotNull(arcInputStream);
        File tmpArcFile = new File(tempDir.getAbsolutePath() + "/" + arcFileName);
        String outputFileName = tmpArcFile + ".cdx";
        CDXCreatorConfig conf = new CDXCreatorConfig();
        conf.setInputStr(tmpArcFile.getAbsolutePath());
        conf.setOutputStr(outputFileName);
        conf.setDirectoryInput(false);
        CDXCreationTask cdxCreator = new CDXCreationTask(conf, arcFile);
        cdxCreator.createIndex();
        assertTrue("File does not exist: " + outputFileName, (new File(outputFileName)).exists());
        // check WARC CDX file (0: no lines skipped)
        checkCdxArcFile(outputFileName);
    }

    /**
     * Test of createIndex method, of class CDXCreationTask.
     */
    @Test
    public void testCdxCreatorWarc() throws Exception {
        String warcFileName = "example.warc";
        InputStream arcInputStream = Resources.getResource("warc/" + warcFileName).openStream();
        File warcFile = IOUtils.copyStreamToTempFileInDir(arcInputStream, tempDir.getAbsolutePath(), ".warc");
        assertNotNull(arcInputStream);
        File tmpWarcFile = new File(tempDir.getAbsolutePath() + "/" + warcFileName);
        String outputFileName = tmpWarcFile + ".cdx";
        CDXCreatorConfig conf = new CDXCreatorConfig();
        conf.setInputStr(tmpWarcFile.getAbsolutePath());
        conf.setOutputStr(outputFileName);
        conf.setDirectoryInput(false);
        CDXCreationTask cdxCreator = new CDXCreationTask(conf, warcFile);
        cdxCreator.createIndex();
        assertTrue("File does not exist: " + outputFileName, (new File(outputFileName)).exists());
        // check WARC CDX file (1: skipping 1 line)
        checkCdxWarcFile(outputFileName);
    }

    /**
     * Check CDX ARC index file
     *
     * @param fileName Name of the test file
     * @param s Number of records to be skipped
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void checkCdxArcFile(String fileName) throws FileNotFoundException, IOException {
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String line = "";
        int i = 0;
        while ((line = input.readLine()) != null) {
            switch (i) {
                case 0:
                    assertEquals("filedesc://3-2-20130522085320-00000-prepc2.arc\ttext/plain\t20130522105320\t-1\t0.0.0.0\t0", line);
                    break;
                case 1:
                    assertEquals("dns:fue.onb.ac.at\ttext/dns\t20130522105319\t-1\t172.16.4.1\t1353", line);
                    break;
                case 2:
                    assertEquals("http://fue.onb.ac.at/robots.txt\ttext/html\t20130522105320\t404\t172.16.14.151\t1467", line);
                    break;
                case 3:
                    assertEquals("http://fue.onb.ac.at/test/\ttext/html\t20130522105321\t200\t172.16.14.151\t2033", line);
                    break;
                case 4:
                    assertEquals("http://fue.onb.ac.at/test/image.png\timage/png\t20130522105321\t200\t172.16.14.151\t2545", line);
                    break;
                default:
                    break;
            }
            i++;
        }
        assertEquals(5, i);
        input.close();
    }
    
    /**
     * Check CDX WARC index file
     *
     * @param fileName Name of the test file
     * @param s Number of records to be skipped
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void checkCdxWarcFile(String fileName) throws FileNotFoundException, IOException {
        BufferedReader input = new BufferedReader(new FileReader(fileName));
        String line = "";
        int i = 0;
        while ((line = input.readLine()) != null) {
            switch (i) {
                case 0:
                    assertEquals("\tapplication/warc-fields\t20140626095501\t-1\t\t0", line);
                    break;
                case 1:
                    assertEquals("filedesc://3-2-20130522085320-00000-prepc2.arc\ttext/plain\t20130522105320\t-1\t0.0.0.0\t357", line);
                    break;
                case 2:
                    assertEquals("dns:fue.onb.ac.at\ttext/dns\t20130522105319\t-1\t172.16.4.1\t1958", line);
                    break;
                case 3:
                    assertEquals("http://fue.onb.ac.at/robots.txt\ttext/html\t20130522105320\t404\t172.16.14.151\t2322", line);
                    break;
                case 4:
                    assertEquals("http://fue.onb.ac.at/test/\ttext/html\t20130522105321\t200\t172.16.14.151\t3142", line);
                    break;
                case 5:
                    assertEquals("http://fue.onb.ac.at/test/image.png\timage/png\t20130522105321\t200\t172.16.14.151\t3908", line);
                    break;
                default:
                    break;
            }
            i++;
        }
        assertEquals(6, i);
        input.close();
    }

}
