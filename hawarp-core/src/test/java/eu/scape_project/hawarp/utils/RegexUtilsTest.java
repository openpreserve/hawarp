/*
 * Copyright 2014 scape.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.scape_project.hawarp.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author scape
 */
public class RegexUtilsTest {

    public RegexUtilsTest() {
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
     * Test of pathMatchesRegexFilter method, of class RegexUtils.
     */
    @Test
    public void testPathMatchesRegexFilter() {
        System.out.println("pathMatchesRegexFilter");

        ArrayList<String> pathList = new ArrayList<String>();
        pathList.add("2324-9-20090523013441-00000-webcrawler04.onb.ac.at.arc.gz");
        pathList.add("/user/hadoop/2471-9-20090602014830-00003-webcrawler04.onb.ac.at.arc.gz");
        pathList.add("/user/hadoop/2380-9-20090527011449-00000-webcrawler04.onb.ac.at.gz");
        for (String path : pathList) {
            assertTrue(RegexUtils.pathMatchesRegexFilter(path, ".*"));
        }
        assertTrue(RegexUtils.pathMatchesRegexFilter(pathList.get(0), "[0-9]{4,4}-[0-9]?-[0-9]{5,20}-[0-9]{5,5}-.*"));
        assertFalse(RegexUtils.pathMatchesRegexFilter(pathList.get(1), "[0-9]{4,4}-[0-9]?-[0-9]{5,20}-[0-9]{5,5}-.*"));

        assertTrue(RegexUtils.pathMatchesRegexFilter(pathList.get(0), ".*\\.arc\\.gz"));
        assertTrue(RegexUtils.pathMatchesRegexFilter(pathList.get(1), ".*\\.arc\\.gz"));
        assertFalse(RegexUtils.pathMatchesRegexFilter(pathList.get(2), ".*\\.arc\\.gz"));
    }

    @Test
    public void testGetMatchingLines() {
        System.out.println("testPathMatchesRegexFilter");
        String text = "begin\n"
                + "2013-05-22T08:29:53.196Z http://fue.onb.ac.at\n"
                + "2013-05-22T08:29:52.667Z dns:fue.onb.ac.at\n"
                + "end";
        String patternStr = "[0-9]{4,4}-[0-9]{2,2}-[0-9]{2,2}T[0-9]{2,2}:[0-9]{2,2}:[0-9]{2,2}.[0-9]{3,3}Z.*";
        List<String> result = RegexUtils.getMatchingLines(text, patternStr);
        assertEquals(2, result.size());
        for (String r : result) {
            assertTrue(r.startsWith("2013-05-22"));
        }
        InputStream is = new ByteArrayInputStream(text.getBytes());
        List<String> result2 = RegexUtils.getMatchingLines(is, patternStr);
        assertEquals(2, result2.size());
        for (String r : result2) {
            assertTrue(r.startsWith("2013-05-22"));
        }
    }

}
