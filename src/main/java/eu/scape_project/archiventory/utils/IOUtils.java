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
package eu.scape_project.archiventory.utils;

import java.io.*;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * I/O Utils
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class IOUtils {

    private static Logger logger = LoggerFactory.getLogger(IOUtils.class.getName());

    /**
     * Copy input stream to temporary file
     *
     * @param is Input sream
     * @param prefix Prefix of temporary file
     * @param ext Extension of temporary file
     * @return Temporary file
     */
    public static File copyInputStreamToTempFile(InputStream is, String prefix, String ext) {
        FileOutputStream fos = null;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(prefix, ext);
            fos = new FileOutputStream(tmpFile);
            org.apache.commons.io.IOUtils.copy(is, fos);
            fos.flush();
        } catch (FileNotFoundException ex) {
            logger.error("Temporary file not available.", ex);
        } catch (IOException ex) {
            logger.error("I/O Error occured.", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException _) {
                    // ignore
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException _) {
                    // ignore
                }
            }
            return tmpFile;
        }
    }

    /**
     * Copy byte array to file in temporary directory
     *
     * @param barray byte array
     * @param dir Directory where the temporary file is created
     * @param ext Extension of temporary file
     * @return Temporary file
     */
    public static File copyByteArrayToTempFileInDir(byte[] barray, String dir, String ext) {
        String filename = System.currentTimeMillis()+RandomStringUtils.randomAlphabetic(5)+ext;
        if(!dir.endsWith("/")) {
            dir += "/";
        }
        FileOutputStream fos = null;
        File tmpFile = null;
        try {
            tmpFile = new File(dir+filename);
            fos = new FileOutputStream(tmpFile);
            org.apache.commons.io.IOUtils.write(barray, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
            logger.error("Temporary file not available.", ex);
        } catch (IOException ex) {
            logger.error("I/O Error", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException _) {
                    // ignore
                }
            }
        }
        return tmpFile;
    }
    
    /**
     * Copy byte array to temporary file
     *
     * @param barray byte array
     * @param prefix Prefix of temporary file
     * @param ext Extension of temporary file
     * @return Temporary file
     */
    public static File copyByteArrayToTempFile(byte[] barray, String prefix, String ext) {
        FileOutputStream fos = null;
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(prefix, ext);
            fos = new FileOutputStream(tmpFile);
            org.apache.commons.io.IOUtils.write(barray, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
            logger.error("Temporary file not available.", ex);
        } catch (IOException ex) {
            logger.error("I/O Error", ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException _) {
                    // ignore
                }
            }
        }
        return tmpFile;
    }

    public static String copyInputStreamToString(InputStream is) {
        String strContent = null;
        try {
            strContent = org.apache.commons.io.IOUtils.toString(is);
        } catch (IOException ex) {
            logger.error("I/O Error", ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException _) {
                    // ignore
                }
            }
        }
        return strContent;
    }
}
