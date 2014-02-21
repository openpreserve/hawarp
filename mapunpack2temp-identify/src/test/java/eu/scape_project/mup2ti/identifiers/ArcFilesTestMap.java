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
package eu.scape_project.mup2ti.identifiers;

import eu.scape_project.mup2ti.container.ArcContainer;
import java.io.*;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Arc files test map used in the tests.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class ArcFilesTestMap {

    private static final Log LOG = LogFactory.getLog(ArcFilesTestMap.class);
    // Singleton Instance
    private static ArcFilesTestMap instance = null;
    private ArcContainer map;
    
    private File tmpTestFile;

    public static ArcFilesTestMap getInstance() throws IOException {
        if (instance == null) {
            instance = new ArcFilesTestMap();
        }
        return instance;
    }

    private ArcFilesTestMap() throws IOException {
        InputStream testFileStream = ArcFilesTestMap.class.getResourceAsStream("test.arc.gz");
        tmpTestFile = File.createTempFile("test", ".arc.gz");
        FileOutputStream fos = new FileOutputStream(tmpTestFile);
        IOUtils.copy(testFileStream, fos);
        fos.close();
        FileInputStream fileInputStream = new FileInputStream(tmpTestFile);
        map = new ArcContainer();
        map.init(tmpTestFile.getAbsolutePath(), fileInputStream);
    }

    public ArcContainer getMap() {
        return map;
    }

    public File getTmpTestFile() {
        return tmpTestFile;
    }

    public void setTmpTestFile(File tmpTestFile) {
        this.tmpTestFile = tmpTestFile;
    }
    
}
