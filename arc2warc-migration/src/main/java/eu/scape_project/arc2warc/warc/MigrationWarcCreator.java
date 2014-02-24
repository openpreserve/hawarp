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
package eu.scape_project.arc2warc.warc;

import eu.scape_project.hawarp.mapreduce.FlatListArcRecord;
import eu.scape_project.hawarp.mapreduce.WarcCreator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.archive.uid.RecordIDGenerator;
import org.archive.uid.UUIDGenerator;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;

import static eu.scape_project.tika_identify.identification.IdentificationConstants.*;
import org.jwat.common.Payload;

/**
 * Creating WARC records using JWAT. This class creates WARC records using JWAT
 * ARC record reader, see https://sbforge.org/display/JWAT/JWAT-Tools.
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class MigrationWarcCreator extends WarcCreator {
   
    @Override
    public void createWarcInfoRecord() throws IOException {
        WarcRecord record = WarcRecord.createRecord(writer);
        record.header.addHeader("WARC-Type", "warcinfo");
        record.header.addHeader("WARC-Date", sdf.format(Calendar.getInstance().getTime()));
        record.header.addHeader("WARC-Record-ID", generator.getRecordID().toString());
        record.header.addHeader("WARC-Filename", fileName);
        record.header.addHeader("Content-Type", "application/warc-fields");
        String description = "software: JWAT Version 1.0.0 https://sbforge.org/display/JWAT/JWAT-Tools\n"
                + "description: migrated from ARC"
                + "format: WARC file version 1.0";
        byte[] descriptionBytes = description.getBytes();
        record.header.addHeader("Content-Length", Long.toString(descriptionBytes.length));
        writer.writeHeader(record);
        ByteArrayInputStream inBytes = new ByteArrayInputStream(descriptionBytes);
        
        writer.streamPayload(inBytes);
        writer.closeRecord();
    }
  
    @Override
    public void createContentRecord(FlatListArcRecord arcRecord) throws IOException {
        WarcRecord record = WarcRecord.createRecord(writer);
     
        String recordId = generator.getRecordID().toString();
        String type = (isFirstRecord) ? "metadata" : "response";
        record.header.addHeader("WARC-Type", type);
        record.header.addHeader("WARC-Target-URI", arcRecord.getUrl());
        record.header.addHeader("WARC-Date", sdf.format(arcRecord.getDate()));
        record.header.addHeader("WARC-Record-ID", recordId);
        record.header.addHeader("WARC-IP-Address", arcRecord.getIpAddress());
        String arcRecordMime = arcRecord.getMimeType();
        String mimeType = (arcRecordMime != null) ? arcRecordMime : MIME_UNKNOWN;
        record.header.addHeader("Content-Type", mimeType);
        byte[] contents = arcRecord.getContents();
        record.header.addHeader("WARC-Payload-Digest", arcRecord.getPayloadDigestStr());
        record.header.addHeader("WARC-Identified-Payload-Type", arcRecord.getIdentifiedPayloadType());
        record.header.addHeader("Content-Length", Long.toString(contents.length));
        writer.writeHeader(record);
        ByteArrayInputStream inBytes = new ByteArrayInputStream(contents);
        
        
        
        writer.streamPayload(inBytes);
        writer.closeRecord();
        isFirstRecord = false;
    }

}
