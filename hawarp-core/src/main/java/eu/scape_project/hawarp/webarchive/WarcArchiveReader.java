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
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcReaderFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.warc.WarcReader;
import org.jwat.warc.WarcReaderFactory;
import org.jwat.warc.WarcRecord;

/**
 *
 * @author onbscs
 */
public class WarcArchiveReader implements ArchiveReader {

    WarcReader reader;

    Iterator<WarcRecord> iterator;

    public WarcArchiveReader(InputStream is, boolean compressed) throws IOException {
        if (compressed) {
            reader = WarcReaderFactory.getReaderCompressed(is);
        } else {
            reader = WarcReaderFactory.getReaderUncompressed(is);
        }
        iterator = reader.iterator();
    }

    @Override
    public boolean hasNext() {
        if (iterator == null) {
            throw new IllegalStateException("Iterator not initialised!");
        }
        return iterator.hasNext();
    }

    @Override
    public ArchiveRecord next() {
        if (iterator == null) {
            throw new IllegalStateException("Iterator not initialised!");
        }
        WarcRecord arcRecord = iterator.next();
        ArchiveRecord archiveRecord = new ArchiveRecord(arcRecord);
        return archiveRecord;
    }

    @Override
    public void remove() {
        if (iterator == null) {
            throw new IllegalStateException("Iterator not initialised!");
        }
        iterator.remove();
    }

}
