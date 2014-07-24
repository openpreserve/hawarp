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
package eu.scape_project.arc2warc;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import eu.scape_project.arc2warc.cli.Arc2WarcMigrationConfig;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ARC Migrator test class
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class ArcMigratorTest {

    private static final Log LOG = LogFactory.getLog(ArcMigratorTest.class);
    protected static final Charset CHARSET = Charset.forName("UTF-8");
    private File tempDir;

    public ArcMigratorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        tempDir = Files.createTempDir();
        LOG.info("Temporary directory: " + tempDir);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(tempDir);
    }

    @Test
    public void testWarcCreator() throws Exception {
        String warcFileName = "example.warc";
        File arcFile = new File(Thread.currentThread().getContextClassLoader().getResource("arc/example.arc.gz").toURI());
        File tmpWarcFile = new File(tempDir.getAbsolutePath() + "/" + warcFileName);
        Arc2WarcMigrationConfig conf = new Arc2WarcMigrationConfig();
        ArcMigrator warcCreator = new ArcMigrator(conf, arcFile, tmpWarcFile);
        warcCreator.migrateArcFile();
        validateExampleArcGzMigrated(tmpWarcFile);
    }

    public static void validateExampleArcGzMigrated(File tmpWarcFile) throws FileNotFoundException, IOException {
        // Validate warc records using jwat
        InputStream is = new FileInputStream(tmpWarcFile);
        ByteCountingPushBackInputStream pbin = new ByteCountingPushBackInputStream(new BufferedInputStream(is), 16);
        org.jwat.warc.WarcReader warcReader = WarcReaderFactory.getReader(pbin);
        Iterator<org.jwat.warc.WarcRecord> warcIterator = warcReader.iterator();
        int recordCounter = 0;
        while (warcIterator.hasNext()) {
            recordCounter++;
            org.jwat.warc.WarcRecord warcRecord = warcIterator.next();
            InputStream payloadIs = warcRecord.getPayloadContent();
            switch (recordCounter) {
                case 1:
                    // header
                    assertEquals("warcinfo", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("application/warc-fields", warcRecord.getHeader("Content-Type").value);
                    assertEquals("133", warcRecord.getHeader("Content-Length").value);
                    // payload
                    String arcHeader = new String(IOUtils.toByteArray(payloadIs), CHARSET);
                    assertTrue("header start not as expected",arcHeader.startsWith("software: JWAT Version 1.0.0 https://sbforge.org/display/JWAT/JWAT-Tools\n"));
                    assertTrue("header end not as expected",arcHeader.endsWith("description: migrated from ARC format: WARC file version 1.0"));
                    break;
                case 2:
                    // header
                    assertEquals("metadata", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("1190", warcRecord.getHeader("Content-Length").value);
                    assertEquals("text/plain", warcRecord.getHeader("Content-Type").value);
                    // payload
                    String oldArcInfoRecord = IOUtils.toString(payloadIs);
                    assertTrue(oldArcInfoRecord.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
                    assertTrue(oldArcInfoRecord.endsWith("</arcmetadata>\n"));
                    break;
                case 3:
                    // header
                    assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("text/dns", warcRecord.getHeader("Content-Type").value);
                    assertEquals("57", warcRecord.getHeader("Content-Length").value);
                    // payload
                    String dns = IOUtils.toString(payloadIs);
                    assertTrue(dns.startsWith("20130522085319"));
                    assertTrue(dns.endsWith("fue-l.onb1.ac.at.\t3600\tIN\tA\t172.16.14.151\n"));
                    break;
                case 4:
                    // header
                    assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("text/html", warcRecord.getHeader("Content-Type").value);
                    assertEquals("490", warcRecord.getHeader("Content-Length").value);
                    // payload
                    String robots = IOUtils.toString(payloadIs);
                    assertTrue(robots.startsWith("HTTP/1.1 404 Not Found"));
                    assertTrue(robots.endsWith("</body></html>\n"));
                    break;
                case 5:
                    // header
                    assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("text/html", warcRecord.getHeader("Content-Type").value);
                    assertEquals("441", warcRecord.getHeader("Content-Length").value);
                    // payload
                    String html = IOUtils.toString(payloadIs);
                    assertTrue(html.startsWith("HTTP/1.1 200 OK"));
                    assertTrue(html.endsWith("</html>\n\n"));
                    break;
                case 6:
                    // header
                    assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("image/png", warcRecord.getHeader("Content-Type").value);
                    assertEquals("862", warcRecord.getHeader("Content-Length").value);
                    break;
            }
        }
        assertEquals(6,recordCounter);
    }


    public static void validateDeduplicated(File tmpWarcFile1,File tmpWarcFile2) throws FileNotFoundException, IOException {
        org.jwat.warc.WarcReader warcReader2 = WarcReaderFactory.getReader(new ByteCountingPushBackInputStream(new BufferedInputStream(new FileInputStream(
                tmpWarcFile2)), 16));
        warcReader2.getNextRecord();
        warcReader2.getNextRecord();
        WarcRecord deduplicated = warcReader2.getNextRecord();


        // Validate warc records using jwat
        org.jwat.warc.WarcReader warcReader = WarcReaderFactory.getReader(new ByteCountingPushBackInputStream(new BufferedInputStream(
                new FileInputStream(tmpWarcFile1)), 16));
        Iterator<org.jwat.warc.WarcRecord> warcIterator = warcReader.iterator();
        int recordCounter = 0;
        while (warcIterator.hasNext()) {
            recordCounter++;
            org.jwat.warc.WarcRecord warcRecord = warcIterator.next();
            InputStream payloadIs = warcRecord.getPayloadContent();
            switch (recordCounter) {
                    case 1:
                        // header
                        assertEquals("warcinfo", warcRecord.getHeader("WARC-Type").value);
                        assertEquals("application/warc-fields", warcRecord.getHeader("Content-Type").value);
                        assertEquals("133", warcRecord.getHeader("Content-Length").value);
                        // payload
                        String arcHeader = new String(IOUtils.toByteArray(payloadIs), CHARSET);
                        assertTrue("header start not as expected",
                                arcHeader.startsWith(
                                        "software: JWAT Version 1.0.0 https://sbforge.org/display/JWAT/JWAT-Tools\n"));
                        assertTrue("header end not as expected", arcHeader.endsWith("description: migrated from ARC format: WARC file version 1.0"));
                        break;
                    case 2:
                        // header
                        assertEquals("metadata", warcRecord.getHeader("WARC-Type").value);
                        assertEquals("1188", warcRecord.getHeader("Content-Length").value);
                        assertEquals("text/plain", warcRecord.getHeader("Content-Type").value);
                        // payload
                        String oldArcInfoRecord = IOUtils.toString(payloadIs);
                        assertTrue(oldArcInfoRecord.startsWith(
                                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
                        assertTrue(oldArcInfoRecord.endsWith("</arcmetadata>\n"));
                        break;
                    case 3:
                        // header
                        assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                        assertEquals("text/dns", warcRecord.getHeader("Content-Type").value);
                        assertEquals("57", warcRecord.getHeader("Content-Length").value);
                        // payload
                        String dns = IOUtils.toString(payloadIs);
                        assertTrue(dns.startsWith("20130522081726"));
                        assertTrue(dns.endsWith("fue-l.onb1.ac.at.\t3600\tIN\tA\t172.16.14.151\n"));
                        break;
                    case 4:
                        // header
                        assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                        assertEquals("text/html", warcRecord.getHeader("Content-Type").value);
                        assertEquals("490", warcRecord.getHeader("Content-Length").value);
                        // payload
                        String robots = IOUtils.toString(payloadIs);
                        assertTrue(robots.startsWith("HTTP/1.1 404 Not Found"));
                        assertTrue(robots.endsWith("</body></html>\n"));
                        break;
                    case 5:
                        // header
                        assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                        assertEquals("text/html", warcRecord.getHeader("Content-Type").value);
                        assertEquals("441", warcRecord.getHeader("Content-Length").value);
                        // payload
                        String html = IOUtils.toString(payloadIs);
                        assertTrue(html.startsWith("HTTP/1.1 200 OK"));
                        assertTrue(html.endsWith("</html>\n\n"));
                        break;
                    case 6:
                        // header
                        assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                        assertEquals("image/png", warcRecord.getHeader("Content-Type").value);
                        assertEquals("862", warcRecord.getHeader("Content-Length").value);
                        assertEquals(deduplicated.getHeader(WarcConstants.FN_WARC_REFERS_TO).value,warcRecord.getHeader(WarcConstants.FN_WARC_RECORD_ID).value);
                        assertEquals(deduplicated.getHeader(WarcConstants.FN_WARC_PAYLOAD_DIGEST).value, warcRecord.getHeader(WarcConstants.FN_WARC_PAYLOAD_DIGEST).value);
                        break;
                }
            }
        assertEquals(6, recordCounter);
    }

    @Test
    public void testDeDuplicatin() throws Exception {
        Arc2WarcMigrationConfig conf = new Arc2WarcMigrationConfig();

        String warcFileName = "old.warc";
        File arcFile = new File(Resources.getResource("arc-dedup/1-1-20130522081727-00000-prepc2.arc").toURI());
        File tmpWarcFile1 = new File(tempDir.getAbsolutePath() + "/" + warcFileName);

        ArcMigrator warcCreator = new ArcMigrator(conf, arcFile, tmpWarcFile1);
        warcCreator.migrateArcFile();

        warcFileName = "example.warc";
        arcFile = new File(Resources.getResource("arc-dedup/2-metadata-1.arc").toURI());
        File tmpWarcFile2 = new File(tempDir.getAbsolutePath() + "/" + warcFileName);
        warcCreator = new ArcMigrator(conf, arcFile, tmpWarcFile2);
        warcCreator.migrateArcFile();

        ArcMigratorTest.validateDeduplicated(tmpWarcFile1,tmpWarcFile2);

    }
}