/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.scape_project.spacip.utils;

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
public class StrUtTest {
    
    private static PropertyUtil pu;
    
    public StrUtTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
        pu = new PropertyUtil("/eu/scape_project/spacip/config.properties");
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
        String result = StrUt.normdir(dir);
        assertEquals(expResult, result);
        dir = "test/";
        result = StrUt.normdir(dir);
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
        String pattern = pu.getProp("commandpattern");
        String result = StrUt.formatCommandOutput(pattern, inlist, outlist);
        assertEquals(expResult, result);
    }
    
}