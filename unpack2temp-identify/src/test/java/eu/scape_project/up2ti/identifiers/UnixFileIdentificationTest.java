/*
 *  Copyright 2012 The SCAPE Project Consortium.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package eu.scape_project.up2ti.identifiers;

import eu.scape_project.up2ti.identifiers.UnixFileIdentification;
import eu.scape_project.up2ti.container.ArcContainer;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * UnixFileIdentification test class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class UnixFileIdentificationTest {
    
    private static ArcContainer arcFilesMap;
    
    public UnixFileIdentificationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        arcFilesMap = ArcFilesTestMap.getInstance().getMap();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of identifyFileList method, of class UnixFileIdentification.
     */
    @Test
    public void testIdentifyFileList() throws Exception {
        UnixFileIdentification id = new UnixFileIdentification();
        id.setTool("unixfile");
        id.setOutputKeyFormat("%1$s/%2$s");
        id.setOutputValueFormat("%1$s %2$s %3$s");
        id.setCommand("file --mime-type");
        HashMap<String, List<String>> result = id.identifyFileList(arcFilesMap);
        assertEquals(5, result.size());
        for(String res : result.keySet()) {
            List<String> valueList = result.get(res);
            for(String val : valueList) {
                System.out.println(val);
            }
        }
        String tmpTestFilePath = ArcFilesTestMap.getInstance().getTmpTestFile().getAbsolutePath();
        List<String> vals = result.get(tmpTestFilePath+"/20130522085321/http://fue.onb.ac.at/test/image.png");
        assertEquals(vals.get(0),"unixfile mime image/png");
    }
}
