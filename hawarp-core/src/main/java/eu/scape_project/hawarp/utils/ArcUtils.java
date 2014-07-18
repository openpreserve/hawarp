/*
 * Copyright 2012 The SCAPE Project Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. under the License.
 */
package eu.scape_project.hawarp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcRecordBase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import org.archive.io.ArchiveRecord;
//import org.archive.io.arc.ARCRecord;
//import org.archive.io.arc.ARCRecordMetaData;
//import org.archive.uid.RecordIDGenerator;
//import org.archive.uid.UUIDGenerator;

/**
 * ARC utility methods.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class ArcUtils {
    

    private static final Log LOG = LogFactory.getLog(ArcUtils.class);

//    static protected RecordIDGenerator generator = new UUIDGenerator();
    
    /**
     * Read the ARC record content into a byte array. Note that the record
     * content can be only read once, it is "consumed" afterwards.
     *
     * @param arcRecord ARC record.
     * @return Content byte array.
     * @throws IOException If content is too large to be stored in a byte array.
     */
//    public static byte[] arcRecordPayloadToByteArray(ARCRecord arcRecord) throws IOException {
//        // Byte point where the content of the ARC record begins
//        int contentBegin = (int) arcRecord.getMetaData().getContentBegin();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        BufferedInputStream buffis = new BufferedInputStream(arcRecord);
//        BufferedOutputStream buffos = new BufferedOutputStream(baos);
//        byte[] tempBuffer = new byte[BUFFER_SIZE];
//        int bytesRead;
//        // skip header content
//        buffis.skip(contentBegin);
//        while ((bytesRead = buffis.read(tempBuffer)) != -1) {
//            buffos.write(tempBuffer, 0, bytesRead);
//        }
//        buffis.close();
//        buffos.flush();
//        buffos.close();
//        return baos.toByteArray();
//    }
    

    /**
     * Write ARC record content to output stream (JWAT)
     *
     * @param arcRecord ARC record
     * @param outputStream Output stream
     * @throws IOException
     */
    public static void recordToOutputStream(ArcRecordBase arcRecord, OutputStream outputStream) throws IOException {
        InputStream bis = null;
        try {
            bis = arcRecord.getPayloadContent();
            org.apache.commons.io.IOUtils.copyLarge(bis,outputStream);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(bis);
            org.apache.commons.io.IOUtils.closeQuietly(outputStream);
        }
    }
    
}
