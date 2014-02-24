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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;


/**
 * Tika File Format Identification. File format identification using Apache Tika.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TikaIdentification extends Identification {
        
    private static Tika tika;
    private static DefaultDetector detector;

    /**
     * Constructor which initialises tika
     *
     * @throws IOException
     */
    public TikaIdentification() throws IOException {
        tika = new Tika();
        detector = new DefaultDetector();
    }
    
    /**
     * Run tika identification on file
     *
     * @param filePath Absolute file path
     * @return Result list
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public HashMap<String,String> identify(File file) throws FileNotFoundException, IOException {
        HashMap<String,String> idRes = new HashMap<String,String>();
        idRes.put("mime", tika.detect(file));
        return idRes;
    }
    
}
