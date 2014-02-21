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
package eu.scape_project.spacip.utils;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * String utils
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class StringUtils {

    private static Logger logger = LoggerFactory.getLogger(StringUtils.class.getName());

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

    public static String formatCommandOutput(String pattern, String inlist, String outlist) {
        String ptmapred_cmd = String.format(pattern, inlist, outlist);
        return ptmapred_cmd;
    }

}
