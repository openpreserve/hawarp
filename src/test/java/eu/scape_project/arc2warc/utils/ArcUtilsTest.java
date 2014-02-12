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
package eu.scape_project.arc2warc.utils;

import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.archive.io.arc.ARCRecord;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.Diagnosis;
import org.jwat.tools.core.ManagedPayload;
import org.jwat.tools.tasks.test.TestFileResult;
import org.jwat.tools.tasks.test.TestFileResultItemDiagnosis;

/**
 * Test class for ARC utility methods.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class ArcUtilsTest {

    private static final Log LOG = LogFactory.getLog(ArcUtilsTest.class);

    public ArcUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test reading ARC record payload into byte array.
     *
     * @throws IOException
     */
    @Test
    public void testArcRecordPayloadToByteArray() throws IOException {
        InputStream arcFileStream = Resources.getResource("arc/example.arc.gz").openStream();
        ArchiveReader reader = ArchiveReaderFactory.get("example.arc.gz", arcFileStream, true);
        Iterator<ArchiveRecord> recordIterator = reader.iterator();
        int recordCounter = 1;
        while (recordIterator.hasNext()) {
            ArchiveRecord nativeArchiveRecord = recordIterator.next();
            String recordIdentifier = nativeArchiveRecord.getHeader().getRecordIdentifier();
            ARCRecord arcRecord = (ARCRecord) nativeArchiveRecord;
            byte[] content = ArcUtils.arcRecordPayloadToByteArray(arcRecord);
            assertTrue(content.length > 0);
            switch (recordCounter) {
                case 1:
                    assertEquals(recordIdentifier, "20130522085320/filedesc://3-2-20130522085320-00000-prepc2.arc");
                    String arcHeader = new String(content, Charset.forName("UTF-8"));
                    assertTrue(arcHeader.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"));
                    assertTrue(arcHeader.endsWith("</arcmetadata>\n"));
                    break;
                case 2:
                    assertEquals(recordIdentifier, "20130522085319/dns:fue.onb.ac.at");
                    String dns = new String(content, Charset.forName("UTF-8"));
                    assertTrue(dns.startsWith("20130522085319"));
                    assertTrue(dns.endsWith("fue-l.onb1.ac.at.\t3600\tIN\tA\t172.16.14.151\n"));
                    break;
                case 3:
                    assertEquals(recordIdentifier, "20130522085320/http://fue.onb.ac.at/robots.txt");
                    String robots = new String(content, Charset.forName("UTF-8"));
                    assertTrue(robots.startsWith("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">"));
                    assertTrue(robots.endsWith("</body></html>\n"));
                    break;
                case 4:
                    assertEquals(recordIdentifier, "20130522085321/http://fue.onb.ac.at/test/");
                    String html = new String(content, Charset.forName("UTF-8"));
                    assertTrue(html.startsWith("<html>"));
                    assertTrue(html.endsWith("</html>\n\n"));
                    break;
                case 5:
                    assertEquals(recordIdentifier, "20130522085321/http://fue.onb.ac.at/test/image.png");
                    break;
            }

            recordCounter++;
        }
    }
    /**
     * Test reading ARC record payload into byte array.
     *
     * @throws IOException
     */
    @Test
    public void testJwatAnalyseArc() throws IOException {
        InputStream arcFileStream = Resources.getResource("arc/example.arc.gz").openStream();
        ArcRecordBase jwatArcRecord = null;
        ArcReader arcReader = null;
        try {
            arcReader = ArcReaderFactory.getReaderCompressed(arcFileStream);
        } catch (IOException ex) {
            LOG.error("I/O error while trying to read from ARC input stream ", ex);
        }
        Iterator<ArcRecordBase> arcIterator = arcReader.iterator();
        long count = 0;
        while (arcIterator.hasNext()) {
            jwatArcRecord = arcIterator.next();
            if (jwatArcRecord != null) {
                long offset = jwatArcRecord.getStartOffset();
                boolean compressed = true;
                checkArcRecord(jwatArcRecord, offset, compressed);
            }
            count++;
        }
        System.out.println("Processed "+count+" archive records");
    }

    private void checkArcRecord(ArcRecordBase arcRecord, long startOffset, boolean compressed) throws IOException {
        ManagedPayload managedPayload = ManagedPayload.checkout();
        TestFileResult result = new TestFileResult();
        TestFileResultItemDiagnosis itemDiagnosis = new TestFileResultItemDiagnosis();
        itemDiagnosis.offset = startOffset;
        switch (arcRecord.recordType) {
            case ArcRecordBase.RT_VERSION_BLOCK:
                managedPayload.manageVersionBlock(arcRecord, false);
                break;
            case ArcRecordBase.RT_ARC_RECORD:
                managedPayload.manageVersionBlock(arcRecord, false);
                break;
            default:
                throw new IllegalStateException();
        }
        arcRecord.close();
        if (arcRecord.diagnostics.hasErrors() || arcRecord.diagnostics.hasWarnings()) {
            List<Diagnosis> errList = arcRecord.diagnostics.getErrors();
            for(Diagnosis d : errList) {
                System.out.println("Error: "+d.entity + " ("+d.type.toString()+")");
                for(String info : d.information) {
                    System.out.println("- Err-Info: "+info);
                }
            }
            List<Diagnosis> warnList = arcRecord.diagnostics.getWarnings();
            for(Diagnosis d : warnList) {
                System.out.println("Warning: "+d.entity + " ("+d.type.toString()+")");
                for(String info : d.information) {
                    System.out.println("- Warn-Info: "+info);
                }
            }
        }
        if (arcRecord.hasPayload() && !arcRecord.hasPseudoEmptyPayload()) {
//	    	validate_payload(arcRecord, arcRecord.header.contentType, itemDiagnosis);
        }
        
    }
}
