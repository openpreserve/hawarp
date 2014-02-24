/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.scape_project.tpid.utils;

import eu.scape_project.tpid.utils.PropertyUtil;
import eu.scape_project.tpid.utils.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author onbscs
 */
public class StringUtilsTest {
    
    private static PropertyUtil pu;
    
    public StringUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        pu = new PropertyUtil("/eu/scape_project/tpid/config.properties");
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
     * Test of normdir method, of class StrUt.
     */
    @Test
    public void testNormdir() {
        System.out.println("normdir");
        String dir = "test";
        String expResult = "test/";
        String result = StringUtils.normdir(dir);
        assertEquals(expResult, result);
        dir = "test/";
        result = StringUtils.normdir(dir);
        assertEquals(expResult, result);
        expResult = "test/test2/test3/";
        result = StringUtils.normdir("test","test2","test3");
        assertEquals(expResult, result);
        result = StringUtils.normdir("test/","test2/","test3/");
        assertEquals(expResult, result);
        result = StringUtils.normdir("test","test2/","/test3");
        assertEquals(expResult, result);
        result = StringUtils.normdir("test","/test2/","/test3/");
        assertEquals(expResult, result);
        
    }
    

    /**
     * Test of formatCommandOutput method, of class StrUt.
     */
    @Test
    public void testFormatCommandOutput() {
        System.out.println("formatCommandOutput");
        String inlist = "hdfs:///user/in1.txt,hdfs:///user/in2.txt";
        String outlist = "hdfs:///user/out1.txt,hdfs:///user/out2.txt";
        String expResult = "--input=\"hdfs:///./\" --inputlist=\"hdfs:///user/in1.txt,hdfs:///user/in2.txt\" --output=\"hdfs:///./\" --outputlist=\"hdfs:///user/out1.txt,hdfs:///user/out2.txt\"";
        String pattern = pu.getProp("tomar.param.pattern");
        String result = StringUtils.formatCommandOutput(pattern, inlist, outlist);
        assertEquals(expResult, result);
    }
    
}