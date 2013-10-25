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
package eu.scape_project.archiventory.characterisers;

import eu.scape_project.archiventory.container.Container;
import eu.scape_project.archiventory.utils.IOUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fits directory characterisation
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class FitsDirectoryCharacterisation extends DualHashBidiMap implements DirectoryCharacterisation {

    
    public static final String FITS_XML_EXT = ".fits.xml";
    private Container container;
    String command;
    private String outDirStr;
//    private String stdOut;
//    private String stdErr;

//    public String getStdErr() {
//        return stdErr;
//    }
//
//    public void setStdErr(String stdErr) {
//        this.stdErr = stdErr;
//    }
//
//    public String getStdOut() {
//        return stdOut;
//    }
//
//    public void setStdOut(String stdOut) {
//        this.stdOut = stdOut;
//    }
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public DualHashBidiMap characterise() throws FileNotFoundException, IOException {
        
        outDirStr = "/tmp/archiventory_fitsout_"+RandomStringUtils.randomAlphabetic(10);
        File outDir = new File(outDirStr);
        outDir.mkdir();

        String cmd = "/usr/local/java/fits-0.6.2/fits.sh -r ";
        cmd += "-i " + container.getExtractDirectoryName() + " ";
        cmd += "-o " + outDir.getAbsolutePath();
        this.command = cmd;

//        InputStream stdIs = p.getInputStream();
//        stdOut = IOUtils.copyInputStreamToString(stdIs);
//        InputStream errIs = p.getErrorStream();
//        stdErr = IOUtils.copyInputStreamToString(errIs);

        try {
            Process p = this.executeProcess(cmd);
            p.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Error executing fits command: "+command);
        }
        collectCharacterisationResult();

        return this;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    /**
     * Run unix file identification on file
     *
     * @param filePath Absolute file path
     * @return Result list
     * @throws FileNotFoundException
     * @throws IOException
     */
    private synchronized Process executeProcess(String command) throws FileNotFoundException, IOException {
        Process p;
        if (!command.isEmpty()) {
            p = Runtime.getRuntime().exec(command);
        } else {
            throw new IllegalArgumentException("Command missing");
        }
        return p;
    }

    private void collectCharacterisationResult() {
        Set<String> keys = container.getBidiIdentifierFilenameMap().keySet();
        //Collection<String> fileCollection = container.getBidiIdentifierFilenameMap().values();
        DualHashBidiMap bidiIdentifierFilenameMap = container.getBidiIdentifierFilenameMap();
        for (String key : keys) {
            String fileName = (String) bidiIdentifierFilenameMap.get(key);
            File origFile = new File(fileName);
            String fitsFileName = outDirStr + File.separator
                    + origFile.getName() + FITS_XML_EXT;
            File fitsFile = new File(fitsFileName);
            if (fitsFile.exists()) {
                this.put(key, fitsFile.getAbsolutePath());
            }
        }

    }
}
