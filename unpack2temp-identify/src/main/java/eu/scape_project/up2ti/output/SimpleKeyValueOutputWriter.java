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
package eu.scape_project.up2ti.output;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

/**
 * Simple key-value reporter.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class SimpleKeyValueOutputWriter implements OutWritable {

    private String separator;
    
    private static final Log LOG = LogFactory.getLog(SimpleKeyValueOutputWriter.class);

    /**
     * Constructor
     *
     * @param separator Separator
     */
    public SimpleKeyValueOutputWriter(String separator) {
        this.separator = separator;
    }

    /**
     * Record method for command line application.
     *
     * @param resultMap Result map where K: recordkey-identificationtype, V:
     * tool identificationtype identificationresult)
     */
    @Override
    public void write(HashMap<String, List<String>> resultMap) {
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
     * @param mos Multiple output writer
     */
    @Override
    public void write(HashMap<String, List<String>> resultMap, MultipleOutputs mos) {
        Iterator iter = resultMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            List<String> valueList = resultMap.get(key);
            try {
                for (String value : valueList) {
                    mos.write("idtab",new Text(key), new Text(value));
                }
            } catch (IOException ex) {
                LOG.error("I/O Error", ex);
            } catch (InterruptedException ex) {
                LOG.error("Interrupted Error", ex);
            }
        }
    }
}