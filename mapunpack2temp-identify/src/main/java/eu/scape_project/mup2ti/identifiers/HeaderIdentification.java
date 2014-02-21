package eu.scape_project.mup2ti.identifiers;

///*
// *  Copyright 2012 The SCAPE Project Consortium.
// * 
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// * 
// *       http://www.apache.org/licenses/LICENSE-2.0
// * 
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// *  under the License.
// */
//package eu.scape_project.archiventory.identifiers;
//
//import eu.scape_project.archiventory.container.ArcFilesMap;
//import eu.scape_project.archiventory.container.Container;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import org.apache.commons.collections.bidimap.DualHashBidiMap;
//import org.archive.io.ArchiveRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * ARC Mime Header Identification. This class reads the mime types from the
// * ARC Header, so it is not an identification in the narrower sense.
// *
// * @author Sven Schlarb https://github.com/shsdev
// * @version 0.1
// */
//public class HeaderIdentification extends Identification {
//    
//    private static Logger logger = LoggerFactory.getLogger(HeaderIdentification.class.getName());
//    private Container container;
//
//    /**
//     * Constructor.
//     *
//     */
//    public HeaderIdentification() {
//    }
//
//    public Container getArcFilesMap() {
//        return container;
//    }
//
//    public void setArcFilesMap(Container arcFilesMap) {
//        this.container = arcFilesMap;
//    }
//
//    /**
//     * Identification of a list of files (map of files/identifiers)
//     *
//     * @param tmpFileRecHashMap Map of files K: temp file V: identifier
//     * @return Map of results K: identifier V: identification result
//     * @throws IOException
//     */
//    @Override 
//    public HashMap<String, List<String>> identifyFileList(DualHashBidiMap fileRecidBidiMap) throws IOException {
//        HashMap<String, List<String>> resultMap = new HashMap<String, List<String>>();
//        
//        List<ArchiveRecord> archiveRecords = container.getArchiveRecords();
//        for (ArchiveRecord ar : archiveRecords) {
//            // output key
//            String containerFileName = ar.getHeader().getReaderIdentifier();
//            String containerIdentifier = ar.getHeader().getRecordIdentifier();
//            String outputKey = String.format(outputKeyFormat, containerFileName, containerIdentifier);
//            // output value
//            String property = "mime";
//            String value = ar.getHeader().getMimetype();
//            String outputValue = String.format(outputValueFormat, tool, property, value);
//            List<String> valueLineList = new ArrayList<String>();
//            valueLineList.add(outputValue);
//            if (ar.getHeader().getLength() < Integer.MAX_VALUE) {
//                resultMap.put(outputKey, valueLineList);
//            }
//        }
//        return resultMap;
//    }
//
//    @Override
//    public HashMap<String,String> identify(File file) throws FileNotFoundException, IOException {
//        throw new UnsupportedOperationException("Not supported.");
//    }
//}
