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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * I/O utility methods.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class IOUtils {

    public static final int BUFFER_SIZE = 8192;

    private static final Log LOG = LogFactory.getLog(IOUtils.class);

    public static File copyStreamToTempFileInDir(InputStream inStream, String dir, String ext) throws IOException {
        String filename = System.currentTimeMillis() + RandomStringUtils.randomAlphabetic(5) + ext;
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        File tmpFile = new File(dir, filename);
        return writeFile(inStream, tmpFile);
    }

    private static File writeFile(InputStream inStream, File tmpFile) throws IOException {
        try {
            FileUtils.copyInputStreamToFile(inStream, tmpFile);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(inStream);
        }
        return tmpFile;
    }

    public static File copyInputStreamToTempFile(InputStream is, String prefix, String ext) {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile(prefix, ext);
            return writeFile(is, tmpFile);
        } catch (FileNotFoundException ex) {
            LOG.error("Temporary file not available.", ex);
        } catch (IOException ex) {
            LOG.error("I/O Error occured.", ex);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(is);
        }
        return null;
    }

    public static String copyInputStreamToString(InputStream is) {
        String strContent = null;
        try {
            strContent = org.apache.commons.io.IOUtils.toString(is);
        } catch (IOException ex) {
            LOG.error("I/O Error", ex);
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
