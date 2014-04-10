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
package eu.scape_project.arc2warc;

import static eu.scape_project.hawarp.utils.UUIDGenerator.getRecordID;
import static eu.scape_project.tika_identify.identification.IdentificationConstants.MIME_UNKNOWN;
import eu.scape_project.tika_identify.tika.TikaIdentification;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;

/**
 *
 * @author scape
 */
class RecordMigrator {
    
    private static final Log LOG = LogFactory.getLog(RecordMigrator.class);

    public static final long LIMIT_LARGE_PAYLOAD = Integer.MAX_VALUE; // 4194304; // 4MB

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static private String warcInfoId;
    
    private final WarcWriter writer; 
    private final String warcFileName;
    
    private boolean arcMetadataRecord;
    
    public RecordMigrator(WarcWriter writer, String warcFileName) {
        this.writer = writer;
        this.warcFileName = warcFileName;
    }

    public void createWarcInfoRecord() throws IOException, URISyntaxException {
        WarcRecord record = WarcRecord.createRecord(writer);
        record.header.addHeader("WARC-Type", "warcinfo");
        record.header.addHeader("WARC-Date", sdf.format(Calendar.getInstance().getTime()));
        
        warcInfoId = getRecordID().toString();
        
        record.header.addHeader("WARC-Record-ID", warcInfoId);
        record.header.addHeader("WARC-Filename", warcFileName);
        record.header.addHeader("Content-Type", "application/warc-fields");
        String description = "software: JWAT Version 1.0.0 https://sbforge.org/display/JWAT/JWAT-Tools\n"
                + "description: migrated from ARC "
                + "format: WARC file version 1.0";
        byte[] descriptionBytes = description.getBytes();
        record.header.addHeader("Content-Length", Long.toString(descriptionBytes.length));
        writer.writeHeader(record);
        ByteArrayInputStream inBytes = new ByteArrayInputStream(descriptionBytes);
        writer.streamPayload(inBytes);
        writer.closeRecord();
    }

    public void migrateRecord(ArcRecordBase jwatArcRecord, boolean doPayloadContIdent) throws IOException, URISyntaxException {
        WarcRecord record = WarcRecord.createRecord(writer);
        String recordId = getRecordID().toString();
        String mimeType = (jwatArcRecord.getContentType() != null) ? jwatArcRecord.getContentType().toString() : MIME_UNKNOWN;

        String type = (arcMetadataRecord) ? "metadata" : (mimeType.equals("text/dns")) ? "resource" : "response";
        record.header.addHeader("WARC-Type", type);
        record.header.addHeader("WARC-Target-URI", jwatArcRecord.getUrlStr());
        record.header.addHeader("WARC-Date", sdf.format(jwatArcRecord.getArchiveDate()));
        record.header.addHeader("WARC-Record-ID", recordId);
        if (arcMetadataRecord) {
            record.header.addHeader("WARC-Concurrent-To", warcInfoId);
        }
        if (jwatArcRecord.getIpAddress() != null) {
            record.header.addHeader("WARC-IP-Address", jwatArcRecord.getIpAddress());
        }
        record.header.addHeader("Content-Type", mimeType);
        byte[] payloadBytes = null;
        InputStream payloadInputStream = null;
        boolean isLarge = false;
        if (jwatArcRecord.hasPayload()) {
            long remaining = jwatArcRecord.getPayload().getRemaining();

            record.header.addHeader("Content-Length", Long.toString(remaining));

            InputStream inputStream = jwatArcRecord.getPayloadContent();
            PayloadContent payloadContent = new PayloadContent(inputStream);
            if (doPayloadContIdent) {
                TikaIdentification ti = TikaIdentification.getInstance();
                ti.setCurrentItemId(recordId);
                payloadContent.setIdentifier(ti);
                payloadContent.doPayloadIdentification(true);
            }
            if (remaining < LIMIT_LARGE_PAYLOAD) {
                payloadBytes = payloadContent.getPayloadContentAsByteArray();
            } else {
                LOG.info("Large file: " + remaining);

                payloadInputStream = payloadContent.getDumpedPayloadContentAsInputStream();
                isLarge = true;
            }

            if (payloadContent.getDigestStr() != null) {
                record.header.addHeader("WARC-Payload-Digest",
                        payloadContent.getDigestStr());
            }
            if (doPayloadContIdent) {
                record.header.addHeader("WARC-Identified-Payload-Type",
                        payloadContent.getIdentifiedPayLoadType());
            }
        }

        writer.writeHeader(record);

        if (jwatArcRecord.hasPayload()) {
            if (isLarge) {
                writer.streamPayload(payloadInputStream);
            } else {
                writer.writePayload(payloadBytes);
            }
        }

        writer.closeRecord();
        arcMetadataRecord = false;
    }

    public boolean isArcMetadataRecord() {
        return arcMetadataRecord;
    }

    public void setArcMetadataRecord(boolean arcMetadataRecord) {
        this.arcMetadataRecord = arcMetadataRecord;
    }
    
    

}
