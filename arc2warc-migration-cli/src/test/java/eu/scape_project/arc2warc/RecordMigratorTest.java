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
package eu.scape_project.arc2warc;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;

/**
 *
 * @author scape
 */
public class RecordMigratorTest {

    private static FileOutputStream outputStream = null;
    private static WarcWriter writer = null;
    private static File tempDir;
    
    private static String warcFileName;

    public RecordMigratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws FileNotFoundException {
        warcFileName = "example.warc";
        tempDir = Files.createTempDir();
        File tmpWarcFile = new File(tempDir.getAbsolutePath() + "/" + warcFileName);
        outputStream = new FileOutputStream(tmpWarcFile);
        writer = WarcWriterFactory.getWriter(outputStream, false);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        FileUtils.deleteDirectory(tempDir);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createWarcInfoRecord method, of class RecordMigrator.
     */
    @Test
    public void testCreateWarcInfoRecord() throws Exception {
        
    }

    /**
     * Test of migrateRecord method, of class RecordMigrator.
     */
    @Test
    public void testMigrateRecord() throws Exception {
       
    }

}
