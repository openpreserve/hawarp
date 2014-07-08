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
package eu.scape_project.hawarp.webarchive;

import eu.scape_project.hawarp.interfaces.ArchiveReader;
import eu.scape_project.hawarp.gzip.GzipPushbackStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jwat.arc.ArcConstants;
import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.gzip.GzipReader;
import org.jwat.warc.WarcConstants;

/**
 *
 * @author onbscs
 */
public class ArchiveReaderFactory {

    public static final int LEADING_BYTES_BUFFER_LENGTH = 16;

    public static ArchiveReader getReader(InputStream inputStream, String archiveFileName) {
        ArchiveReader reader = null;
        try {
            if (archiveFileName.endsWith(".gz")) {
                reader = new ArcArchiveReader(inputStream, true);
                

            } else if (archiveFileName.endsWith(".arc")) {
                reader = new ArcArchiveReader(inputStream, false);
               
            } else if (archiveFileName.endsWith(".warc")) {
                reader = new WarcArchiveReader(inputStream, false);
                
            } else if (archiveFileName.endsWith(".warc.gz")) {
                reader = new WarcArchiveReader(inputStream, true);
                
            }
        } catch (IOException ex) {
            Logger.getLogger(ArchiveReaderFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

//        try {
//            ByteCountingPushBackInputStream mainBcPbin = new ByteCountingPushBackInputStream(inputStream, LEADING_BYTES_BUFFER_LENGTH);
//            if (GzipReader.isGzipped(mainBcPbin)) {
//                GzipPushbackStream gzPbis = new GzipPushbackStream(mainBcPbin);
//                ByteCountingPushBackInputStream subBcPbin = new ByteCountingPushBackInputStream(gzPbis, LEADING_BYTES_BUFFER_LENGTH);
//                Logger.getLogger(ArchiveReaderFactory.class.getName()).log(Level.INFO, "Using GZIP Reader");
//                reader = getReaderByMagicHeader(subBcPbin);
//            } else {
//                Logger.getLogger(ArchiveReaderFactory.class.getName()).log(Level.INFO, "Using uncompressed Reader");
//                reader = getReaderByMagicHeader(mainBcPbin);
//            }
//            return reader;
//        } catch (IOException ex) {
//            Logger.getLogger(ArchiveReaderFactory.class.getName()).log(Level.SEVERE, null, ex);
//        }
        return reader;
    }

//    private static ArchiveReader getReaderByMagicHeader(ByteCountingPushBackInputStream bcPbis) throws IOException {
//        byte[] leadingBytes = new byte[LEADING_BYTES_BUFFER_LENGTH];
//        bcPbis.read(leadingBytes);
//        String leadingStr = new String(leadingBytes);
//        bcPbis.unread(leadingBytes);
//        if (leadingStr.startsWith(ArcConstants.ARC_MAGIC_HEADER)) {
//            Logger.getLogger(ArchiveReaderFactory.class.getName()).log(Level.INFO, "Using ARC Reader");
//            return new ArcArchiveReader(bcPbis);
//        } else if (leadingStr.startsWith(WarcConstants.WARC_MAGIC_HEADER)) {
//            Logger.getLogger(ArchiveReaderFactory.class.getName()).log(Level.INFO, "Using WARC Reader");
//            return new WarcArchiveReader(bcPbis);
//        } else {
//            Logger.getLogger(ArchiveReaderFactory.class.getName()).log(Level.SEVERE, "Reader cannot be initialised");
//            return null;
//        }
//
//    }
}
