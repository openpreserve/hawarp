/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.up2ti.container;

import java.io.*;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author onbscs
 */
public class ArcContainerTest {

    private ArcContainer instance;
    DualHashBidiMap bidiIdentifierFilenameMap;

    public ArcContainerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        InputStream testFileStream = ZipContainer.class.getResourceAsStream("test.arc.gz");
        if (testFileStream == null) {
            fail();
        }

        File tmpTestFile = File.createTempFile("test", ".arc.gz");
        FileOutputStream fos = new FileOutputStream(tmpTestFile);
        IOUtils.copy(testFileStream, fos);
        fos.close();
        String containerFileName = "test.arc.gz";
        InputStream containerFileStream = new FileInputStream(tmpTestFile);
        instance = new ArcContainer();
        instance.init(containerFileName, containerFileStream);
        bidiIdentifierFilenameMap = instance.getBidiIdentifierFilenameMap();

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of init method, of class ArcContainer.
     */
    @Test
    public void testInit() throws Exception {
        String key = "test.arc.gz/20130522085321/http://fue.onb.ac.at/test/";
        assertTrue(bidiIdentifierFilenameMap.containsKey(key));
        String value = (String) bidiIdentifierFilenameMap.get(key);
        assertNotNull(value);
        File tmpFile = new File(value);
        assertTrue("File does not exist: " + tmpFile.getAbsolutePath(), tmpFile.exists());
    }

}
