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
package eu.scape_project.arc2warc.warc;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import eu.scape_project.hawarp.mapreduce.FlatListArcRecord;
import eu.scape_project.hawarp.utils.IOUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.common.HttpHeader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;

/**
 * WARC Creator test class
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class WarcCreatorTest {

    private static final Log LOG = LogFactory.getLog(WarcCreatorTest.class);
    private File tempDir;

    public WarcCreatorTest() {
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
        System.out.println("createWarcInfoRecord");
        String warcFileName = "example.warc";
        InputStream arcInputStream = Resources.getResource("arc/example.arc.gz").openStream();
        assertNotNull(arcInputStream);
        File tmpWarcFile = new File(tempDir.getAbsolutePath() + "/" + warcFileName);
        FileOutputStream fos = new FileOutputStream(tmpWarcFile);
        assertNotNull(fos);
        WarcWriter writer = WarcWriterFactory.getWriter(fos, false);
        assertNotNull(writer);
        WarcCreator warcCreator = new WarcCreator(writer, warcFileName);
        warcCreator.createWarcInfoRecord();
        List<FlatListArcRecord> hadoopArcRecords = getHadoopArcRecords();
        assertEquals(5,hadoopArcRecords.size());
        for(FlatListArcRecord ar : hadoopArcRecords) {
            warcCreator.createContentRecord(ar);
        }
        warcCreator.close();
        
        validateWarcFile(tmpWarcFile);
    }

    private List<FlatListArcRecord> getHadoopArcRecords() throws URISyntaxException, IOException {
        InputStream arcFileStream = Resources.getResource("arc/example.arc.gz").openStream();
        ArcRecordBase jwatArcRecord = null;
        ArcReader arcReader = null;
        try {
            arcReader = ArcReaderFactory.getReaderCompressed(arcFileStream);
        } catch (IOException ex) {
            LOG.error("I/O error while trying to read from ARC input stream ", ex);
        }
        Iterator<ArcRecordBase> arcIterator = arcReader.iterator();
        List<FlatListArcRecord> hadoopArcRecords = new ArrayList<FlatListArcRecord>();
        while (arcIterator.hasNext()) {
            FlatListArcRecord hadoopArcRecord = new FlatListArcRecord();
            jwatArcRecord = arcIterator.next();
            if (jwatArcRecord != null) {

                HttpHeader httpHeader = jwatArcRecord.getHttpHeader();
                
                hadoopArcRecord.setReaderIdentifier("example.warc.gz");
                hadoopArcRecord.setRecordIdentifier("empty");

                String currArcRecUrl = jwatArcRecord.getUrlStr();
                hadoopArcRecord.setUrl(currArcRecUrl);

                hadoopArcRecord.setDate(jwatArcRecord.getArchiveDate());

                hadoopArcRecord.setIpAddress(jwatArcRecord.getIpAddress());

                if (httpHeader != null) {
                    int statusCode = httpHeader.statusCode;
                    hadoopArcRecord.setHttpReturnCode(statusCode);
                }

                String mimeType = jwatArcRecord.getContentTypeStr();
                hadoopArcRecord.setMimeType(mimeType);

                hadoopArcRecord.setContentLength((int) jwatArcRecord.getPayload().getRemaining());
                byte[] payLoadBytes = IOUtils.inputStreamToByteArray(jwatArcRecord.getPayloadContent());
                hadoopArcRecord.setContents(payLoadBytes);

                hadoopArcRecords.add(hadoopArcRecord);
            }

        }
        return hadoopArcRecords;
    }

    private void validateWarcFile(File tmpWarcFile) throws FileNotFoundException, IOException {
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
                    String arcHeader = new String(IOUtils.inputStreamToByteArray(payloadIs), Charset.forName("UTF-8"));
                    assertTrue("header start not as expected",arcHeader.startsWith("software: JWAT Version 1.0.0 https://sbforge.org/display/JWAT/JWAT-Tools\n"));
                    assertTrue("header end not as expected",arcHeader.endsWith("description: migrated from ARC format: WARC file version 1.0"));
                    break;
                case 2:
                    // header
                    assertEquals("metadata", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("1190", warcRecord.getHeader("Content-Length").value);
                    assertEquals("text/xml", warcRecord.getHeader("Content-Type").value);
                    // payload
                    String oldArcInfoRecord = new String(IOUtils.inputStreamToByteArray(payloadIs), Charset.forName("UTF-8"));
                    assertTrue(oldArcInfoRecord.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
                    assertTrue(oldArcInfoRecord.endsWith("</arcmetadata>\n"));
                    break;
                case 3:
                    // header
                    assertEquals("resource", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("text/dns", warcRecord.getHeader("Content-Type").value);
                    assertEquals("57", warcRecord.getHeader("Content-Length").value);
                    // payload
                    String dns = new String(IOUtils.inputStreamToByteArray(payloadIs), Charset.forName("UTF-8"));
                    assertTrue(dns.startsWith("20130522085319"));
                    assertTrue(dns.endsWith("fue-l.onb1.ac.at.\t3600\tIN\tA\t172.16.14.151\n"));
                    break;
                case 4:
                    // header
                    assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("text/html", warcRecord.getHeader("Content-Type").value);
                    assertEquals("287", warcRecord.getHeader("Content-Length").value);
                    // payload
                    String robots = new String(IOUtils.inputStreamToByteArray(payloadIs), Charset.forName("UTF-8"));
                    assertTrue(robots.startsWith("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">"));
                    assertTrue(robots.endsWith("</body></html>\n"));
                    break;
                case 5:
                    // header
                    assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("text/html", warcRecord.getHeader("Content-Type").value);
                    assertEquals("164", warcRecord.getHeader("Content-Length").value);
                    // payload
                    String html = new String(IOUtils.inputStreamToByteArray(payloadIs), Charset.forName("UTF-8"));
                    assertTrue(html.startsWith("<html>"));
                    assertTrue(html.endsWith("</html>\n\n"));
                    break;
                case 6:
                    // header
                    assertEquals("response", warcRecord.getHeader("WARC-Type").value);
                    assertEquals("image/png", warcRecord.getHeader("Content-Type").value);
                    assertEquals("607", warcRecord.getHeader("Content-Length").value);
                    break;
            }
        }
        assertEquals(6,recordCounter);
    }
}
