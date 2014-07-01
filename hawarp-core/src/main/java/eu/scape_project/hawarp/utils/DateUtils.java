/*
 * Copyright 2014 onbscs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.scape_project.hawarp.utils;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Date utils
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class DateUtils {

    public static final SimpleDateFormat GMTUTCUnixTsFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    {
        GMTUTCUnixTsFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    public static final SimpleDateFormat GMTGTechDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    {
        GMTGTechDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

}
