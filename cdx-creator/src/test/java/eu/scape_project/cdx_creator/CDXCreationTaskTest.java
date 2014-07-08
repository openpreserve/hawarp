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
                    assertEquals(" CDX N b a m s k r M V g", line);
                    break;
                case 1:
                    assertEquals("filedesc://3-2-20130522085320-00000-prepc2.arc 20130522085320 filedesc://3-2-20130522085320-00000-prepc2.arc text/plain -1 066A950FB7D49644689B371BD6FDDC8A1FBEA3F3 - - 0 example.arc.gz", line);
                    break;
                case 2:
                    assertEquals("dns:fue.onb.ac.at 20130522085319 dns:fue.onb.ac.at text/dns -1 7E08F640546ECB9538E973D698FD918645EF147B - - 681 example.arc.gz", line);
                    break;
                case 3:
                    assertEquals("fue.onb.ac.at/robots.txt 20130522085320 http://fue.onb.ac.at/robots.txt text/html 404 881AE766338CFFDA0DD0DDFCA9D555A16995C87C - - 790 example.arc.gz", line);
                    break;
                case 4:
                    assertEquals("fue.onb.ac.at/test/ 20130522085321 http://fue.onb.ac.at/test/ text/html 200 5304803976FF20E88B1BC47DF439F011D781FB20 - - 1188 example.arc.gz", line);
                    break;
                case 5:
                    assertEquals("fue.onb.ac.at/test/image.png 20130522085321 http://fue.onb.ac.at/test/image.png image/png 200 FBA570FCDB1D8621BD331F44E32C29FB4F7B0191 - - 1559 example.arc.gz", line);
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
                    assertEquals(" CDX N b a m s k r M V g", line);
                    break;
                case 1:
                    assertEquals(" 20140626075501  application/warc-fields -1 - - - 0 example.warc", line);
                    break;
                case 2:
                    assertEquals("filedesc://3-2-20130522085320-00000-prepc2.arc 20130522085320 filedesc://3-2-20130522085320-00000-prepc2.arc text/plain -1 - - - 357 example.warc", line);
                    break;
                case 3:
                    assertEquals("dns:fue.onb.ac.at 20130522085319 dns:fue.onb.ac.at text/dns -1 - - - 1958 example.warc", line);
                    break;
                case 4:
                    assertEquals("fue.onb.ac.at/robots.txt 20130522085320 http://fue.onb.ac.at/robots.txt text/html 404 - - - 2322 example.warc", line);
                    break;
                case 5:
                    assertEquals("fue.onb.ac.at/test/ 20130522085321 http://fue.onb.ac.at/test/ text/html 200 - - - 3142 example.warc", line);
                    break;
                case 6:
                    assertEquals("fue.onb.ac.at/test/image.png 20130522085321 http://fue.onb.ac.at/test/image.png image/png 200 - - - 3908 example.warc", line);
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
