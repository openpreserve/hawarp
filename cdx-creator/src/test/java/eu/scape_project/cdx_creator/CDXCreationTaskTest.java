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
import eu.scape_project.hawarp.utils.PropertyUtil;
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

    private CDXCreatorConfig conf;
    private PropertyUtil pu;

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
        conf = new CDXCreatorConfig();
        pu = new PropertyUtil("/eu/scape_project/cdx_creator/config.properties", false);
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
        conf.setInputStr(tmpArcFile.getAbsolutePath());
        conf.setOutputStr(outputFileName);
        conf.setDirectoryInput(false);
        conf.setCdxfileCsColumns(pu.getProp("cdxfile.cscolumns"));
        conf.setCdxfileCsHeader(pu.getProp("cdxfile.csheader"));
        CDXCreationTask cdxCreator = new CDXCreationTask(conf, arcFile, arcFileName);
        cdxCreator.createIndex();
        assertTrue("File does not exist: " + outputFileName, (new File(outputFileName)).exists());
        // check WARC CDX file 
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
        conf.setInputStr(tmpWarcFile.getAbsolutePath());
        conf.setOutputStr(outputFileName);
        conf.setDirectoryInput(false);
        conf.setCdxfileCsColumns(pu.getProp("cdxfile.cscolumns"));
        conf.setCdxfileCsHeader(pu.getProp("cdxfile.csheader"));
        CDXCreationTask cdxCreator = new CDXCreationTask(conf, warcFile, warcFileName);
        cdxCreator.createIndex();
        assertTrue("File does not exist: " + outputFileName, (new File(outputFileName)).exists());
        // check WARC CDX file
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
                    assertEquals("CDX A b e a m s c k r V v D d g M n", line);
                    break;
                case 1:
                    assertEquals("filedesc://3-2-20130522085320-00000-prepc2.arc\t20130522085320\t0.0.0.0\tfiledesc://3-2-20130522085320-00000-prepc2.arc\ttext/plain\t-1\t-\t-\t-\t0\t-\t-\t-\texample.arc.gz\t-\t2035", line);
                    break;
                case 2:
                    assertEquals("dns:fue.onb.ac.at\t20130522085319\t172.16.4.1\tdns:fue.onb.ac.at\ttext/dns\t-1\t-\t-\t-\t1353\t-\t-\t-\texample.arc.gz\t-\t2035", line);
                    break;
                case 3:
                    assertEquals("http://fue.onb.ac.at/robots.txt\t20130522085320\t172.16.14.151\thttp://fue.onb.ac.at/robots.txt\ttext/html\t404\t-\t-\t-\t1467\t-\t-\t-\texample.arc.gz\t-\t2035", line);
                    break;
                case 4:
                    assertEquals("http://fue.onb.ac.at/test/\t20130522085321\t172.16.14.151\thttp://fue.onb.ac.at/test/\ttext/html\t200\t-\t-\t-\t2033\t-\t-\t-\texample.arc.gz\t-\t2035", line);
                    break;
                case 5:
                    assertEquals("http://fue.onb.ac.at/test/image.png\t20130522085321\t172.16.14.151\thttp://fue.onb.ac.at/test/image.png\timage/png\t200\t-\t-\t-\t2545\t-\t-\t-\texample.arc.gz\t-\t2035", line);
                    break;
                default:
                    break;
            }
            i++;
        }
        assertEquals(6, i);
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
                    assertEquals("CDX A b e a m s c k r V v D d g M n", line);
                    break;
                case 1:
                    assertEquals("\t20140626075501\t\t\tapplication/warc-fields\t-1\t-\t-\t-\t0\t-\t-\t-\texample.warc\t-\t5104", line);
                    break;
                case 2:
                    assertEquals("filedesc://3-2-20130522085320-00000-prepc2.arc\t20130522085320\t0.0.0.0\tfiledesc://3-2-20130522085320-00000-prepc2.arc\ttext/plain\t-1\t-\t-\t-\t357\t-\t-\t-\texample.warc\t-\t5104", line);
                    break;
                case 3:
                    assertEquals("dns:fue.onb.ac.at\t20130522085319\t172.16.4.1\tdns:fue.onb.ac.at\ttext/dns\t-1\t-\t-\t-\t1958\t-\t-\t-\texample.warc\t-\t5104", line);
                    break;
                case 4:
                    assertEquals("http://fue.onb.ac.at/robots.txt\t20130522085320\t172.16.14.151\thttp://fue.onb.ac.at/robots.txt\ttext/html\t404\t-\t-\t-\t2322\t-\t-\t-\texample.warc\t-\t5104", line);
                    break;
                case 5:
                    assertEquals("http://fue.onb.ac.at/test/\t20130522085321\t172.16.14.151\thttp://fue.onb.ac.at/test/\ttext/html\t200\t-\t-\t-\t3142\t-\t-\t-\texample.warc\t-\t5104", line);
                    break;
                case 6:
                    assertEquals("http://fue.onb.ac.at/test/image.png\t20130522085321\t172.16.14.151\thttp://fue.onb.ac.at/test/image.png\timage/png\t200\t-\t-\t-\t3908\t-\t-\t-\texample.warc\t-\t5104", line);
                    break;
                default:
                    break;
            }
            i++;
        }
        assertEquals(7, i);
        input.close();
    }

}
