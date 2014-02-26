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
package eu.scape_project.up2ti.identifiers;

import eu.scape_project.up2ti.identifiers.DroidIdentification;
import eu.scape_project.up2ti.container.ArcContainer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.*;
import static org.junit.Assert.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;

/**
 * DroidIdentification test class.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class DroidIdentificationTest {

    private static ArcContainer arcFilesMap;

    private static DroidIdentification id;

    public DroidIdentificationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        arcFilesMap = ArcFilesTestMap.getInstance().getMap();
        InputStream droidSigFile = DroidIdentification.class.getResourceAsStream("DROID_SignatureFile_V67.xml");
        Resource r = new InputStreamResource(droidSigFile);
        id = new DroidIdentification(r);
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
     * Test of identify method, of class DroidIdentification.
     */
    @Test
    public void testIdentify() throws Exception {
        id.setTool("droid");
        id.setOutputKeyFormat("%1$s/%2$s");
        id.setOutputValueFormat("%1$s %2$s %3$s");
        HashMap<String, List<String>> result = id.identifyFileList(arcFilesMap);
        assertEquals(5, result.size());
        for (String res : result.keySet()) {
            List<String> valueList = result.get(res);
            for (String val : valueList) {
                System.out.println(val);
            }
        }
        String tmpTestFilePath = ArcFilesTestMap.getInstance().getTmpTestFile().getAbsolutePath();
        List<String> vals = result.get(tmpTestFilePath + "/20130522085321/http://fue.onb.ac.at/test/image.png");
        assertEquals(vals.get(0), "droid mime image/png");
        assertEquals(vals.get(1), "droid puid fmt/11");
    }
}
