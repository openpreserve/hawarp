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
package eu.scape_project.hawarp.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * String utils
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class StringUtils {

    private static final Log LOG = LogFactory.getLog(StringUtils.class);

    /**
     * Creates a normalised directory string. Parts will be normalised to a
     * directory string which only has a trailing file path separator. The parts
     * are then concatenated to the final output string.
     *
     * @param dirs Directory strings
     * @return
     */
    public static String normdir(String... dirs) {
        String outDir = "";
        for (String dir : dirs) {
            String dirPart = (dir.startsWith(File.separator) ? dir.substring(1) : dir);
            outDir += ((dirPart.endsWith(File.separator)) ? dirPart : dirPart + File.separator);
        }
        return outDir;
    }

    public static String ensureTrailSep(String path) {
        if (path.charAt(path.length() - 1) != File.separatorChar) {
            path += File.separator;
        }
        return path;
    }

}
