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
package eu.scape_project.tb.cipex.reporters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple key-value reporter.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class SimpleKeyValueReporter implements Reportable {

    private String separator;
    private static Logger logger = LoggerFactory.getLogger(SimpleKeyValueReporter.class.getName());

    /**
     * Constructor
     *
     * @param separator Separator
     */
    public SimpleKeyValueReporter(String separator) {
        this.separator = separator;
    }

    /**
     * Record method for command line application.
     *
     * @param resultMap Result map where K: recordkey-identificationtype, V:
     * tool identificationtype identificationresult)
     */
    @Override
    public void report(HashMap<String, List<String>> resultMap) {
        Iterator iter = resultMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            List<String> valueList = resultMap.get(key);
            for (String value : valueList) {
                System.out.println(key + separator + value);
            }
        }
    }

    /**
     * Record method for hadoop job.
     *
     * @param resultMap Result map where K: recordkey-identificationtype, V:
     * tool identificationtype identificationresult)
     * @param context Mapper context for creating the key-value output.
     */
    @Override
    public void report(HashMap<String, List<String>> resultMap, Mapper.Context context) {
        Iterator iter = resultMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            List<String> valueList = resultMap.get(key);
            try {
                for (String value : valueList) {
                    context.write(new Text(key), new Text(value));
                }
            } catch (IOException ex) {
                logger.error("I/O Error", ex);
            } catch (InterruptedException ex) {
                logger.error("Interrupted Error", ex);
            }
        }
    }
}
