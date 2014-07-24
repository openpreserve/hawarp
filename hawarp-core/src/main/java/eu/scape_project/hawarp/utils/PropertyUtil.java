/*
 *  Copyright 2011 The IMPACT Project Consortium.
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

package eu.scape_project.hawarp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * PropertyUtils
 * @author shsdev https://github.com/shsdev
 * @version 0.5
 */
public class PropertyUtil {

    private static final Log LOG = LogFactory.getLog(PropertyUtil.class);
    
    private Properties properties;

    /**
     * Construct the property utils object from the properties file
     * @param propertiesFile a string path to a properties file
     * @param loadPropertiesFromFile Load properties file from local file system
     * @throws RuntimeException
     */
    public PropertyUtil(String propertiesFile, boolean loadPropertiesFromFile) throws RuntimeException {
        try {
            properties = new Properties();
            if(loadPropertiesFromFile) {
                FileInputStream fis = new FileInputStream(new File(propertiesFile));
                properties.load(fis);
                LOG.info("Property file loaded from local file system: \"" + propertiesFile + "\".");
            } else {
                properties.load(PropertyUtil.class.getResourceAsStream(propertiesFile));
                LOG.info("Property file loaded from resource: \"" + propertiesFile + "\".");
            }
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load properties file!");
        }
    }

    /**
     * get a property value by key
     * @param key the property key
     * @return the property value
     */
    public String getProp(String key) {
        return properties.getProperty(key);
    }
}