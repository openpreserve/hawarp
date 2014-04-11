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
package eu.scape_project.tika_identify.tika;

import eu.scape_project.hawarp.interfaces.Identifier;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;

/**
 * Droid File Format Identification. File format identification using the Droid
 version 6.1 API. A TikaIdentificationTask object can be initialised with a
 default signature file version 67 using the default constructor or with
 another signature file using the constructor that allows to give a path to
 another signature file. Identification can be performed using a file or an
 input stream.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TikaIdentificationTask implements Identifier {

    private static final Log LOG = LogFactory.getLog(TikaIdentificationTask.class);

    private static Tika tika;
    private static DefaultDetector detector;

    // Singleton Instance
    private static TikaIdentificationTask instance = null;
    
    private String currentItemId;

    /**
     * Get instance with default signature file
     *
     * @return TikaIdentificationTask instance
     * @throws IOException
     * @throws SignatureParseException
     */
    public static TikaIdentificationTask getInstance() throws IOException {
        if (instance == null) {
            instance = new TikaIdentificationTask();
        }
        return instance;
    }

    /**
     * Constructor which initialises tika
     *
     * @throws IOException
     * @throws SignatureParseException
     */
    private TikaIdentificationTask() throws IOException {
        tika = new Tika();
        detector = new DefaultDetector();
    }

    /**
     * Run tika identification on file
     *
     * @param filePath Absolute file path
     * @return Result list
     */
    @Override
    public String identify(String filePath) {
        File file = new File(filePath);
        try {
            return tika.detect(file);
        } catch (IOException ex) {
            LOG.warn("Identification failed: "+currentItemId, ex);
            return MIME_UNKNOWN;
        }
    }

    /**
     * Run tika identification on file
     *
     * @param inStream Absolute file path
     * @return Result list
     */
    public String identify(InputStream inStream) {
        try {
            return tika.detect(inStream);
        } catch (IOException ex) {
            LOG.warn("Identification failed: "+currentItemId, ex);
            return MIME_UNKNOWN;
        }
    }

    /**
     * Run tika identification using the begin of a byte array
     *
     * @param prefix Begin of byte array content
     * @return Result list
     */
    @Override
    public String identify(byte[] prefix) {
        try {
            String mimetype = tika.detect(prefix);
            return mimetype;
        } catch (java.lang.Exception e) {
            LOG.warn("Tika identification failed: "+currentItemId);
            return MIME_UNKNOWN;
        }
    }

    @Override
    public String getCurrentItemId() {
        return currentItemId;
    }

    @Override
    public void setCurrentItemId(String currentItemId) {
        this.currentItemId = currentItemId;
    }
    
    

}
