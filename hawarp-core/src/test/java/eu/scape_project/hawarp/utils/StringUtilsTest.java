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
package eu.scape_project.hawarp.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * String utilities test class
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class StringUtilsTest {

    public StringUtilsTest() {
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
        result = StringUtils.normdir("test", "test2", "test3");
        assertEquals(expResult, result);
        result = StringUtils.normdir("test/", "test2/", "test3/");
        assertEquals(expResult, result);
        result = StringUtils.normdir("test", "test2/", "/test3");
        assertEquals(expResult, result);
        result = StringUtils.normdir("test", "/test2/", "/test3/");
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
        String pattern = "--input=\"hdfs:///./\" --inputlist=\"%1$s\" --output=\"hdfs:///./\" --outputlist=\"%2$s\"";
        String result = StringUtils.formatCommandOutput(pattern, inlist, outlist);
        assertEquals(expResult, result);
    }

}
