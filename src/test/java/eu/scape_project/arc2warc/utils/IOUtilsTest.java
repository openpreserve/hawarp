package eu.scape_project.arc2warc.utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import eu.scape_project.arc2warc.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for I/O utility methods.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class IOUtilsTest {

    public IOUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of inputStreamToByteArray method, of class IOUtils.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testInputStreamToByteArray() throws IOException {
        String testStr = "test";
        byte[] testBa = testStr.getBytes(Charset.forName("UTF-8"));
        InputStream is = new ByteArrayInputStream(testBa);

        byte[] result = IOUtils.inputStreamToByteArray(is);
        String resultStr = new String(result, Charset.forName("UTF-8"));
        assertEquals(testStr, resultStr);
    }

}
