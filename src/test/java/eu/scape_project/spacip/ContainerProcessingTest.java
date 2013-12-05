/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.spacip;

import eu.scape_project.spacip.ContainerProcessing;
import java.io.*;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.arc.ARCRecord;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author onbscs
 */
public class ContainerProcessingTest {

    private File tmpTestFile;

    public ContainerProcessingTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        InputStream in = ContainerProcessingTest.class.getResourceAsStream("test.arc.gz");
        if (in == null) {
            fail();
        }
        tmpTestFile = File.createTempFile("test", ".arc.gz");
        if (tmpTestFile == null) {
            fail();
        }
        FileOutputStream out = new FileOutputStream(tmpTestFile);
        IOUtils.copy(in, out);
        in.close();
        out.close();
    }

    @After
    public void tearDown() {
        tmpTestFile.delete();
    }

    /**
     * Test write content to output stream
     */
    @Test
    public void testArcToOutputStream() throws Exception {
        FileInputStream fis = new FileInputStream(tmpTestFile);
        ArchiveReader reader = ArchiveReaderFactory.get("test.arc.gz", fis, true);
        Iterator<ArchiveRecord> recordIterator = reader.iterator();
        while (recordIterator.hasNext()) {
            ArchiveRecord nativeArchiveRecord = recordIterator.next();
            ArchiveRecordHeader header = nativeArchiveRecord.getHeader();
            String mimeSuffix = header.getMimetype().replaceAll("/", "-");
            ARCRecord arcRecord = (ARCRecord) nativeArchiveRecord;
            File tmpDir = new File("/tmp/arcrecords");
            tmpDir.mkdir();
            File tmpOutFile = File.createTempFile("arcrecord", mimeSuffix, tmpDir);
            assertTrue("Record file not created", tmpOutFile.exists());
            FileOutputStream fos = new FileOutputStream(tmpOutFile);
            ContainerProcessing.recordToOutputStream(arcRecord, fos);
            tmpDir.deleteOnExit();
            tmpOutFile.deleteOnExit();
        }

    }

    /**
     * Local ARC container file iteration test
     */
//    @Test
//    public void testIterateOverContainerRecords() throws Exception {
//        
//        String path = "";
//        int expected = 0;
//        int result = 0;
//        
//        path = "/home/onbscs/5arcs/2791-10-20090916153253-00000-webcrawler04.onb.ac.at.arc.gz";
//        expected = 8589;
//        result = ContainerProcessing.iterateOverContainerRecords(path);
//        assertEquals(expected, result);
//        
//        path = "/home/onbscs/5arcs/2791-10-20090916153253-00002-webcrawler04.onb.ac.at.arc.gz";
//        expected = 10256;
//        result = ContainerProcessing.iterateOverContainerRecords(path);
//        assertEquals(expected,result);
//        
//        path = "/home/onbscs/5arcs/2791-10-20090916155516-00006-webcrawler04.onb.ac.at.arc.gz";
//        expected = 7559;
//        result = ContainerProcessing.iterateOverContainerRecords(path);
//        assertEquals(expected,result);
//        
//        path = "/home/onbscs/5arcs/2791-10-20090916155809-00008-webcrawler04.onb.ac.at.arc.gz";
//        expected = 6239;
//        result = ContainerProcessing.iterateOverContainerRecords(path);
//        assertEquals(expected,result);
//        
//        path = "/home/onbscs/5arcs/2791-10-20090916161807-00013-webcrawler04.onb.ac.at.arc.gz";
//        expected = 9580;
//        result = ContainerProcessing.iterateOverContainerRecords(path);
//        assertEquals(expected,result);
//
//    }

}
