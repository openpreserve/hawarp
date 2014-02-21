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
package eu.scape_project.archiventory.identifiers;

import eu.scape_project.archiventory.utils.IOUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

/**
 * Unix file identification
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class UnixFileIdentification extends Identification {

    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * un unix file identification on file (used!)
     *
     * @param recordFileList
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public String identify(Collection<String> recordFileList) throws FileNotFoundException, IOException {
        if (command.isEmpty()) {
            command = "file --mime-type";
        }
        command += " "; // add trailing white space
        // files names are added to the command, the unix tool "file" returns
        // one file identification result per line.
        for (String fileName : recordFileList) {
            File file = new File(fileName);
            command += file.getAbsolutePath() + " ";
        }
        return this.executeProcess(command);
    }

    /**
     * Run unix file identification on file (unused)
     *
     * @param filePath Absolute file path
     * @return Result list
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public HashMap<String, String> identify(File file) throws FileNotFoundException, IOException {
        if (command.isEmpty()) {
            command = "file --mime-type";
        }
        command += " "; // add trailing white space
        command += file.getAbsolutePath();
        HashMap<String, String> idRes = new HashMap<String, String>();
        idRes.put("mime", this.executeProcess(command));
        return idRes;
    }

    /**
     * Run unix file identification on file
     *
     * @param filePath Absolute file path
     * @return Result list
     * @throws FileNotFoundException
     * @throws IOException
     */
    private synchronized String executeProcess(String command) throws FileNotFoundException, IOException {
        Process p = Runtime.getRuntime().exec(command);
        InputStream is = p.getInputStream();
        return IOUtils.copyInputStreamToString(is);
    }

    @Override
    public HashMap<String, List<String>> identifyFileList(DualHashBidiMap fileRecidBidiMap) throws IOException {
        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
        String ufidRes = this.identify(fileRecidBidiMap.values());
        Scanner s = new Scanner(ufidRes);
        // one file identification result per line
        s.useDelimiter("\n");
        while (s.hasNext()) {
            // output syntax of the unix-tool 'file' is ${fileName} : ${mimeType}
            StringTokenizer st = new StringTokenizer(s.next(), ":");
            String fileName = st.nextToken().trim();
            // output key
            String key = (String) fileRecidBidiMap.getKey(fileName);
            if (key != null) {
                String containerFileName = key.substring(0, key.indexOf("/"));
                String containerIdentifier = key.substring(key.indexOf("/") + 1);
                String outputKey = String.format(outputKeyFormat, containerFileName, containerIdentifier);
                // output value
                String property = "mime";
                String value = st.nextToken().trim();
                String outputValue = String.format(outputValueFormat, tool, property, value);
                List<String> valueLineList = new ArrayList<String>();
                valueLineList.add(outputValue);
                resultMap.put(outputKey, valueLineList);
            } else {
            }
        }
        return resultMap;
    }
}
