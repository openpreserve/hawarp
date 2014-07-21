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
import org.jwat.warc.WarcReaderFactory;

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
        conf.setInputStr(arcFile.getAbsolutePath());
        conf.setOutputStr(tmpWarcFile.getAbsolutePath());
        conf.setDirectoryInput(false);
        ArcMigrator warcCreator = new ArcMigrator(conf, arcFile);
        warcCreator.migrate();       
        validateWarcFile(tmpWarcFile);
    }

    public static void validateWarcFile(File tmpWarcFile) throws FileNotFoundException, IOException {
        // Validate warc records using jwat
        InputStream is = new FileInputStream(tmpWarcFile);
        ByteCountingPushBackInputStream pbin = new ByteCountingPushBackInputStream(new BufferedInputStream(is, 8192), 16);
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
}