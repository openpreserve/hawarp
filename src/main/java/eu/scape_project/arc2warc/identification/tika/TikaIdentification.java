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
package eu.scape_project.arc2warc.identification.tika;

import static eu.scape_project.arc2warc.identification.IdentificationConstants.*;
import eu.scape_project.arc2warc.identification.Identifier;
import eu.scape_project.arc2warc.identification.PayloadContent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
 * version 6.1 API. A TikaIdentification object can be initialised with a
 * default signature file version 67 using the default constructor or with
 * another signature file using the constructor that allows to give a path to
 * another signature file. Identification can be performed using a file or an
 * input stream.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TikaIdentification implements Identifier {

    private static final Log LOG = LogFactory.getLog(PayloadContent.class);

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
     * Run tika identification on file
     *
     * @param filePath Absolute file path
     * @return Result list
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public String identify(String filePath) {
        File file = new File(filePath);
        try {
            return tika.detect(file);
        } catch (IOException ex) {
            LOG.warn("Identification failed.", ex);
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
            Metadata metadata = new Metadata();
            ByteArrayInputStream bis = new ByteArrayInputStream(prefix);
            TikaInputStream tis = TikaInputStream.get(bis);

            MimeTypes mimeTypes
                    = TikaConfig.getDefaultConfig().getMimeRepository();
            MediaType mediaType = mimeTypes.detect(tis, metadata);
            return mediaType.toString();

        } catch (Exception e) {
            LOG.warn("Tika identification failed.", e);
            return MIME_UNKNOWN;
        }

//        try {
//            String mimetype = tika.detect(prefix);
//            LOG.info("Tika says: " + mimetype);
//            return mimetype;
//        } catch (java.lang.Exception e) {
//            LOG.warn("Tika identification failed.");
//            return MIME_UNKNOWN;
//        }
    }

}
