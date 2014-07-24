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
        FileUtils.copyInputStreamToFile(inStream, tmpFile);
        return tmpFile;
    }
}
