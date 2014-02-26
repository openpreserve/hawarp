/*
 * Copyright 2012 The SCAPE Project Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. under the License.
 */
package eu.scape_project.hawarp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * I/O utility methods.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class IOUtils {

    public static final int BUFFER_SIZE = 8192;

    private static final Log LOG = LogFactory.getLog(IOUtils.class);

    /**
     * Read the ARC record content into a byte array. Note that the record
     * content can be only read once, it is "consumed" afterwards.
     *
     * @param is Input stream
     * @return Content byte array.
     * @throws IOException If content is too large to be stored in a byte array.
     */
    public static byte[] inputStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream buffis = new BufferedInputStream(is);
        BufferedOutputStream buffos = new BufferedOutputStream(baos);
        byte[] tempBuffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = buffis.read(tempBuffer)) != -1) {
            buffos.write(tempBuffer, 0, bytesRead);
        }
        buffis.close();
        buffos.flush();
        buffos.close();
        return baos.toByteArray();
    }

    public static File copyStreamToTempFileInDir(InputStream inStream, String dir, String ext) throws IOException {
        String filename = System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(5) + ext;
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        FileOutputStream fos = null;
        File tmpFile = null;
        try {
            tmpFile = new File(dir + filename);
            fos = new FileOutputStream(tmpFile);
            org.apache.commons.io.IOUtils.copy(inStream, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException ex) {
            LOG.error("Temporary file not available.", ex);
        } catch (IOException ex) {
            LOG.error("I/O Error", ex);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return tmpFile;
    }

}
