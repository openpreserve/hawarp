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

import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reportable interface to be implemented by reporting classes.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public interface Reportable {

    /**
     * Reporting method for command line application. Input is a single value or
     * a list of tool/property/value strings for each record key.
     *
     * @param resultMap { "recordkey": [ "tool/property/value" ] }
     */
    public void report(HashMap<String, List<String>> resultMap);

    /**
     * Reporting method for hadoop job. Input is a single value or
     * a list of tool/property/value strings for each record key and the
     * mapper context where output values can be written.
     *
     * @param resultMap Result map where K: recordkey-identificationtype, V:
     * tool identificationtype identificationresult)
     * @param context Mapper context for creating the key-value output.
     */ // HashMap<Integer, List<String>> myMap = new HashMap<Integer, List<String>>();
    public void report(HashMap<String, List<String>> resultMap, MultipleOutputs mos);
}
