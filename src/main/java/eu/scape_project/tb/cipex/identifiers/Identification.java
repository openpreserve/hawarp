/*
 *  Copyright 2012 The SCAPE Project Consortium.
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
package eu.scape_project.tb.cipex.identifiers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

/**
 * Arc RecordList Identification interface
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public abstract class Identification {

    public String tool;
    public String outputKeyFormat;
    public String outputValueFormat;

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getOutputKeyFormat() {
        return outputKeyFormat;
    }

    public void setOutputKeyFormat(String outputKeyFormat) {
        this.outputKeyFormat = outputKeyFormat;
    }

    public String getOutputValueFormat() {
        return outputValueFormat;
    }

    public void setOutputValueFormat(String outputValueFormat) {
        this.outputValueFormat = outputValueFormat;
    }

    /**
     * Interface for a container record identifier. Input (HashMap) { recordkey,
     * temporaryfilename }. Output { recordkey, tool/property/value } (default
     * order)
     *
     * @param tmpFileRecHashMap Input { "recordkey": "tempfilename" }
     * @return Output { "recordkey": "tool/property/value" }
     * @throws IOException
     */
    public abstract HashMap<String, String> identify(File file) throws FileNotFoundException, IOException;

    /**
     * File list identification. Processing a bi-directional map of records
     * where each record has one temporary file and each temporary file has one
     * record key (strict 1:1 relationship).
     *
     * @param fileRecidBidiMap Input { "recordkey" <-> "tempfilename" }
     * @return Output { "recordkey": [ "tool/property/value" ] }
     * @throws IOException
     */
    public HashMap<String, List<String>> identifyFileList(DualHashBidiMap fileRecidBidiMap) throws IOException {
        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
        Iterator iter = fileRecidBidiMap.keySet().iterator();
        while (iter.hasNext()) {
            String recordKey = (String) iter.next();
            String tmpFileName = (String) fileRecidBidiMap.get(recordKey);
            File file = new File(tmpFileName);
            HashMap idRes = this.identify(file);
            String containerFileName = recordKey.substring(0, recordKey.indexOf("/"));
            String containerIdentifier = recordKey.substring(recordKey.indexOf("/") + 1);
            String outputKey = String.format(outputKeyFormat, containerFileName, containerIdentifier);
            List<String> valueLineList = new ArrayList<String>();
            for (Object k : idRes.keySet()) {
                String property = (String) k;
                String value = (String) idRes.get(k);
                String outputValue = String.format(outputValueFormat, tool, property, value);
                valueLineList.add(outputValue);

            }
            resultMap.put(outputKey, valueLineList);
        }
        return resultMap;
    }
}
