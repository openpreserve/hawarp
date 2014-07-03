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

import static eu.scape_project.hawarp.interfaces.Identifier.MIME_UNKNOWN;
import static eu.scape_project.hawarp.utils.UUIDGenerator.getRecordID;
import eu.scape_project.tika_identify.tika.TikaIdentificationTask;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.HeaderLine;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;
import static eu.scape_project.hawarp.utils.DateUtils.GMTUTCUnixTsFormat;

/**
 *
 * @author scape
 */
class RecordMigrator {

    private static final Log LOG = LogFactory.getLog(RecordMigrator.class);

    public static final long LIMIT_LARGE_PAYLOAD = Integer.MAX_VALUE; // 4194304; // 4MB
    

    static private String warcInfoId;

    private final WarcWriter writer;
    private final String warcFileName;

    private boolean arcMetadataRecord;
    
//    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//    {
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//    }
    
    public RecordMigrator(WarcWriter writer, String warcFileName) {
        this.writer = writer;
        this.warcFileName = warcFileName;
    }

    public void createWarcInfoRecord() throws IOException, URISyntaxException {
        WarcRecord record = WarcRecord.createRecord(writer);
        record.header.addHeader("WARC-Type", "warcinfo");
        record.header.addHeader("WARC-Date", GMTUTCUnixTsFormat.format(Calendar.getInstance().getTime()));

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

        String type = (arcMetadataRecord) ? "metadata" : (mimeType.equals("text/dns")) ? "response" : "response";
        record.header.addHeader("WARC-Type", type);
        record.header.addHeader("WARC-Target-URI", jwatArcRecord.getUrlStr());
        record.header.addHeader("WARC-Date", GMTUTCUnixTsFormat.format(jwatArcRecord.getArchiveDate()));
        record.header.addHeader("WARC-Record-ID", recordId);
        if (arcMetadataRecord) {
            // ARC metadata record relates to the WARC info record
            record.header.addHeader("WARC-Concurrent-To", warcInfoId);
        }
        if (jwatArcRecord.getIpAddress() != null) {
            record.header.addHeader("WARC-IP-Address", jwatArcRecord.getIpAddress());
        }
        // Content type as available in the ARC record, not the identified mime type
        record.header.addHeader("Content-Type", mimeType);

        // Payload metadata = HTTP Response lines
        String payloadHeader = "";
        if (jwatArcRecord.getHttpHeader() != null) {
            String statusCodeStr = jwatArcRecord.getHttpHeader().statusCodeStr;

            String httpVersionStr = jwatArcRecord.getHttpHeader().httpVersion;

            if (httpVersionStr != null && statusCodeStr != null) {
                Integer statusCode = jwatArcRecord.getHttpHeader().statusCode;
                payloadHeader += httpVersionStr + " " + statusCodeStr + " " + HttpStatus.getStatusText(statusCode)+"\r\n";
            }
            List<HeaderLine> headerList = jwatArcRecord.getHttpHeader().getHeaderList();
            if (headerList != null) {
                for (HeaderLine hl : headerList) {
                    payloadHeader += hl.name + ": " + hl.value +"\r\n";
                }
            }
            payloadHeader += "\r\n"; // end of HTTP response header
        }

        byte[] payloadBytes = null;
        InputStream payloadInputStream = null;
        boolean isLarge = false;
        if (jwatArcRecord.hasPayload()) {
            long remaining = jwatArcRecord.getPayload().getRemaining();
            // WARC content length is payload length + payload header length
            record.header.addHeader("Content-Length", Long.toString(remaining + payloadHeader.length()));
            InputStream inputStream = jwatArcRecord.getPayloadContent();
            PayloadContent payloadContent = new PayloadContent(inputStream);
            if (doPayloadContIdent) {
                TikaIdentificationTask ti = TikaIdentificationTask.getInstance();
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
        
        // finished creating header, write it to the WARC record
        writer.writeHeader(record);

        // Record payload
        if (jwatArcRecord.hasPayload()) {
            // "large" payload is added as input stream (see RecordMigrator.LIMIT_LARGE_PAYLOAD)
            if (isLarge) {
                // Prepend payload metadata = HTTP Response lines
                ByteArrayInputStream bais = new ByteArrayInputStream(payloadHeader.getBytes());
                SequenceInputStream sis = new SequenceInputStream(bais, payloadInputStream);
                writer.streamPayload(sis);
            // "small" payload is added as byte array (see RecordMigrator.LIMIT_LARGE_PAYLOAD)
            } else {
                // Prepend payload metadata = HTTP Response lines
                byte[] prepend = payloadHeader.getBytes();
                byte[] combined = new byte[prepend.length + payloadBytes.length];
                System.arraycopy(prepend, 0, combined, 0, prepend.length);
                System.arraycopy(payloadBytes, 0, combined, prepend.length, payloadBytes.length);
                writer.writePayload(combined);
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
