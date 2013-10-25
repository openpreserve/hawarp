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
package eu.scape_project.archiventory.characterisers;

import eu.scape_project.archiventory.container.TestContainer;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Fits directory characterisation test
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class FitsDirectoryCharacterisationTest {

    public static final boolean IS_TEST_ENABLED = true;
    private FitsDirectoryCharacterisation fdc;
    private TestContainer tc;

    public FitsDirectoryCharacterisationTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        tc = new TestContainer();
        tc.init("testdir", null);
        fdc = new FitsDirectoryCharacterisation();
        fdc.setContainer(tc);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of characterise method, of class FitsDirectoryCharacterisation.
     */
    @Test
    public void testCharacterise() throws Exception {
        if (IS_TEST_ENABLED) {
            DualHashBidiMap result = fdc.characterise();
            assertNotNull("No characterisation result", result);
            System.out.println(fdc.getCommand());
            Set<String> keys = result.keySet();
            List<String> testkeys = tc.getTestfiles();
            assertTrue("Result list does not contain all keys", keys.containsAll(testkeys));
            for (String key : keys) {
                String fileName = (String) result.get(key);
                System.out.println(fileName);
            }
        }
    }
}
