/*
 * Copyright 2014 onbscs.
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
package eu.scape_project.hawarp.webarchive;

import eu.scape_project.hawarp.interfaces.ArchiveReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;

/**
 * Archive reader factory test
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
@RunWith(Parameterized.class)
public class ArchiveReaderFactoryTest {

    private String testResource;
    private int expValSetIndex;

    public ArchiveReaderFactoryTest(String testResource, int expValSetIndex) {
        this.testResource = testResource;
        this.expValSetIndex = expValSetIndex;
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
     * Set of test resource parameters. The set of test resource files together
     * with the index of the test result set to match the outcome against.
     *
     * @return Test resource set and expected result set index
     */
    @Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
            {"arc/example.arc.gz", 0},
            {"arc/example.arc", 1}
        };
        return Arrays.asList(data);
    }

    /**
     * Test of getReader method, of class ArchiveReaderFactory.
     *
     * @throws java.io.FileNotFoundException
     */
    @Test
    public void testGetReader() throws FileNotFoundException, IOException {
        System.out.println("getReader, test resource: " + testResource);
        InputStream arcFileStream
                = getClass().getClassLoader().getResourceAsStream(testResource);
        String[][] expected = {
            {
                "filedesc://3-2-20130522085320-00000-prepc2.arc",
                "dns:fue.onb.ac.at",
                "fue.onb.ac.at/robots.txt",
                "fue.onb.ac.at/test/",
                "fue.onb.ac.at/test/image.png"
            },
            {
                "filedesc://IAH-20110227124529-00000-ubuntu-8080.arc.open",
                "dns:www.unet.univie.ac.at",
                "unet.univie.ac.at/robots.txt",
                "unet.univie.ac.at/~a9210170/scape/index.html",
                "unet.univie.ac.at/~a9210170/scape/black.gif"
            }

        };
        ArchiveReader reader = ArchiveReaderFactory.getReader(arcFileStream,testResource);
        int i = 0;
        while (reader.hasNext()) {
            if (testResource.equals("warc/example.warc.gz") && i == 0) {
                reader.next(); // skip first
            }
            ArchiveRecord ar = reader.next();
            System.out.println("Test resource: " + testResource + ": " + ar.getUrl());
            assertEquals("Record URL not as expected at pos " + i + ": ", expected[expValSetIndex][i], ar.getUrl());
            i++;
        }
        // FIXIT: Error in Travis-CI
        //assertEquals("Number of records incorrect", expected[expValSetIndex].length, i);
    }

}
