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
package eu.scape_project.pc.tika;

import eu.scape_project.pc.hadoop.TikaIdentifyHadoopJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Droid File Format Identification. File format identification using the Droid
 * version 6.1 API. A TikaIdentification object can be initialised with a
 * default signature file version 67 using the default constructor or with
 * another signature file using the constructor that allows to give a path to
 * another signature file. Identification can be performed using a file or an
 * input stream.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TikaIdentification {

    private static Logger logger = LoggerFactory.getLogger(TikaIdentification.class.getName());
    
    private static Tika tika;
    private static DefaultDetector detector;
    
    // Singleton Instance
    private static TikaIdentification instance = null;

    /**
     * Get instance with default signature file
     *
     * @return TikaIdentification instance
     * @throws IOException
     * @throws SignatureParseException
     */
    public static TikaIdentification getInstance() throws IOException {
        if (instance == null) {
            instance = new TikaIdentification();
        }
        return instance;
    }

    /**
     * Constructor which initialises tika
     *
     * @throws IOException
     * @throws SignatureParseException
     */
    private TikaIdentification() throws IOException {
        tika = new Tika();
        detector = new DefaultDetector();
    }


    /**
     * Run droid identification on file
     *
     * @param filePath Absolute file path
     * @return Result list
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String identify(String filePath) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        String mimetype = tika.detect(file);
        return mimetype;
    }

   
}
