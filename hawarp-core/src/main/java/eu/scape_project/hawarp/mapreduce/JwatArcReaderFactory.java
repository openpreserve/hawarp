/*
 * Copyright 2014 scape.
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

package eu.scape_project.hawarp.mapreduce;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;

/**
 *
 * @author scape
 */
public class JwatArcReaderFactory {
    
    private static final Log LOG = LogFactory.getLog(JwatArcReaderFactory.class);

    public static ArcReader getReader(InputStream inputStream) {
        ArcReader reader = null;
        try {
            // Read first two bytes to check if we have a gzipped input stream
            PushbackInputStream pb = new PushbackInputStream(inputStream, 2);
            byte[] signature = new byte[2];
            pb.read(signature);
            pb.unread(signature); 
            // use compressed reader if gzip magic number is matched
            if (signature[ 0] == (byte) 0x1f && signature[ 1] == (byte) 0x8b)
            {
                reader = ArcReaderFactory.getReaderCompressed(pb);
            } else {
                reader = ArcReaderFactory.getReaderUncompressed(pb);
            }
        } catch (IOException ex) {
            LOG.error("Unable to create reader", ex);
        }
        return reader;
    }
    
}
