/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.scape_project.archiventory.characterisers;

import eu.scape_project.archiventory.container.TestContainer;
import eu.scape_project.archiventory.utils.IOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.hadoop.thirdparty.guava.common.io.Files;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author onbscs
 */
public class FitsDirectoryCharacterisationTest {
    
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
        tc.init("testdir",null);
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
        DualHashBidiMap result = fdc.characterise();
        assertNotNull("No characterisation result",result);
        System.out.println(fdc.getCommand());
//        System.out.println(fdc.getStdOut());
//        System.out.println(fdc.getStdErr());
        
        Set<String> keys = result.keySet();
        List<String> testkeys = tc.getTestfiles();
        assertTrue("Result list does not contain all keys",keys.containsAll(testkeys));
        for (String key : keys) {
            String fileName = (String)result.get(key);
            System.out.println(fileName);
        }
    }
    
}
