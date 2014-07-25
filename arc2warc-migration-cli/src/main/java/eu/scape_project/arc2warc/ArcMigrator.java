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

import eu.scape_project.arc2warc.cli.Arc2WarcMigrationConfig;
import eu.scape_project.hawarp.utils.JwatArcReaderFactory;
import eu.scape_project.hawarp.utils.RegexUtils;
import eu.scape_project.hawarp.utils.UUIDGenerator;
import eu.scape_project.tika_identify.tika.TikaIdentificationTask;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.HeaderLine;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;
import org.supercsv.io.CsvBeanReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import static eu.scape_project.hawarp.interfaces.Identifier.MIME_UNKNOWN;
import static eu.scape_project.hawarp.utils.DateUtils.GMTUTCUnixTsFormat;
import static eu.scape_project.hawarp.utils.UUIDGenerator.getRecordID;

/**
 * @author scape
 */
public class ArcMigrator {

    private static final Log LOG = LogFactory.getLog(ArcMigrator.class);

    public static final int LIMIT_LARGE_PAYLOAD = 4194304; // 4MB

    private final Arc2WarcMigrationConfig config;

    private final File arcFile;

    private final File warcFile;
    private final boolean deduplicate;
    private final byte[] buffer;

    //Migration state
    private String warcInfoId;
    private WarcWriter writer;
    private ArcReader reader;

    public ArcMigrator(Arc2WarcMigrationConfig config, File arcFile, File warcFile, boolean deduplicate) {
        this.config = config;
        this.arcFile = arcFile;
        this.warcFile = warcFile;
        this.deduplicate = deduplicate;
        buffer = new byte[LIMIT_LARGE_PAYLOAD];
    }

    public void migrateArcFile() {
        try {
            reader = JwatArcReaderFactory.getReader(new FileInputStream(arcFile));
            FileUtils.forceMkdir(warcFile.getParentFile());
            writer = WarcWriterFactory.getWriter(new FileOutputStream(warcFile), config.createCompressedWarc());
            warcInfoId = getRecordID().toString();
            createWarcInfoRecord();
            Iterator<ArcRecordBase> arcIterator = reader.iterator();
            boolean first = true;
            while (arcIterator.hasNext()) {
                ArcRecordBase jwatArcRecord = arcIterator.next();
                if (first) {
                    migrateMetadataRecord(jwatArcRecord);
                    first = false;
                } else if (deduplicate) {
                    if (RegexUtils.pathMatchesRegexFilter(jwatArcRecord.getUrlStr(),
                            "^metadata://.*/crawl/logs/crawl\\.log.*$")) {
                        migrateCrawlLog(jwatArcRecord, writer);
                    }
                } else {
                    migrateResponseRecord(jwatArcRecord);
                }
            }
            LOG.info("File processed: " + arcFile.getAbsolutePath());
        } catch (URISyntaxException ex) {
            LOG.error("File not found error", ex);
            throw new RuntimeException(ex);
        } catch (FileNotFoundException ex) {
            LOG.error("File not found error", ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            LOG.error("I/O Error", ex);
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
            clear();
        }
    }

    private void clear() {
        warcInfoId = null;
        reader = null;
        writer = null;
    }

    private void migrateMetadataRecord(ArcRecordBase jwatArcRecord) throws IOException, URISyntaxException {
        migrateRecord(jwatArcRecord, true);
    }

    private void migrateResponseRecord(ArcRecordBase jwatArcRecord) throws IOException, URISyntaxException {
        migrateRecord(jwatArcRecord, false);
    }


    public void createWarcInfoRecord() throws IOException, URISyntaxException {
        WarcRecord record = WarcRecord.createRecord(writer);
        record.header.addHeader(WarcConstants.FN_WARC_TYPE, WarcConstants.RT_WARCINFO);
        record.header.addHeader(WarcConstants.FN_WARC_DATE,
                GMTUTCUnixTsFormat.format(Calendar.getInstance().getTime()));
        record.header.addHeader(WarcConstants.FN_WARC_RECORD_ID, warcInfoId);
        record.header.addHeader(WarcConstants.FN_WARC_FILENAME, warcFile.getName());
        record.header.addHeader(WarcConstants.FN_CONTENT_TYPE, WarcConstants.CT_APP_WARC_FIELDS);
        String description
                = "software: JWAT Version 1.0.0 https://sbforge.org/display/JWAT/JWAT-Tools\n" + "description: migrated from ARC " + "format: WARC file version 1.0";
        byte[] descriptionBytes = description.getBytes();
        record.header.addHeader(WarcConstants.FN_CONTENT_LENGTH, Long.toString(descriptionBytes.length));
        writer.writeHeader(record);
        ByteArrayInputStream inBytes = new ByteArrayInputStream(descriptionBytes);
        writer.streamPayload(inBytes);
        writer.closeRecord();
    }


    public void migrateRecord(ArcRecordBase arcRecord, boolean arcMetadataRecord) throws
                                                                                  IOException,
                                                                                  URISyntaxException {
        String recordId = getRecordID(arcFile, reader.getStartOffset()).toString();
        WarcRecord warcRecord = WarcRecord.createRecord(writer);
        // Standard headers, url, date, record id
        warcRecord.header.addHeader(WarcConstants.FN_WARC_TARGET_URI, arcRecord.getUrlStr());
        warcRecord.header.addHeader(WarcConstants.FN_WARC_DATE, GMTUTCUnixTsFormat.format(arcRecord.getArchiveDate()));
        warcRecord.header.addHeader(WarcConstants.FN_WARC_RECORD_ID, recordId);
        /* Mimetype*/
        if (arcRecord.getContentType() != null) {
            warcRecord.header.addHeader(WarcConstants.FN_CONTENT_TYPE, arcRecord.getContentType().toString());
        } else {
            warcRecord.header.addHeader(WarcConstants.FN_CONTENT_TYPE, MIME_UNKNOWN);
        }
        // Is this metadata about the warc file or is it a "real" record
        String type;
        if (arcMetadataRecord) {
            // ARC metadata record relates to the WARC info record
            warcRecord.header.addHeader(WarcConstants.FN_WARC_CONCURRENT_TO, warcInfoId);
            type = WarcConstants.RT_METADATA;
        } else {
            type = WarcConstants.RT_RESPONSE;
        }
        warcRecord.header.addHeader(WarcConstants.FN_WARC_TYPE, type);
        if (arcRecord.getIpAddress() != null) {
            warcRecord.header.addHeader(WarcConstants.FN_WARC_IP_ADDRESS, arcRecord.getIpAddress());
        }
        // Payload metadata = HTTP Response lines
        String payloadHeader = constructPayloadHeader(arcRecord);
        InputStream payloadContentStream = null;
        if (arcRecord.hasPayload()) {
            InputStream inputStream = arcRecord.getPayloadContent();
            long remaining;
            try {
                remaining = arcRecord.getPayload().getRemaining();
            } catch (IOException e){
                if (arcRecord.getStartOffset() == 0 && arcRecord.getArchiveLength() == 77){
                    remaining = 0;
                } else {
                    throw new IOException(e);
                }
            }
            long contentLength
                    = remaining + payloadHeader.getBytes().length;// WARC content length is payload length + payload header length
            warcRecord.header.addHeader(WarcConstants.FN_CONTENT_LENGTH, contentLength, null);
            PayloadContent payloadContent = new PayloadContent(inputStream, remaining, buffer);
            if (config.isContentTypeIdentification()) {
                TikaIdentificationTask ti = TikaIdentificationTask.getInstance();
                ti.setCurrentItemId(recordId);
                payloadContent.setIdentifier(ti);
                payloadContent.doPayloadIdentification(true);
            }
            payloadContentStream = payloadContent.getPayloadContentAsInputStream();
            if (payloadContent.getDigestStr() != null) {
                warcRecord.header.addHeader(WarcConstants.FN_WARC_PAYLOAD_DIGEST, payloadContent.getDigestStr());
            }
            if (config.isContentTypeIdentification()) {
                warcRecord.header.addHeader(WarcConstants.FN_WARC_IDENTIFIED_PAYLOAD_TYPE,
                        payloadContent.getIdentifiedPayLoadType());
            }
        } else {
            warcRecord.header.addHeader(WarcConstants.FN_CONTENT_LENGTH, 0, null);
        }
        // finished creating header, write it to the WARC record
        writer.writeHeader(warcRecord);
        // Record payload
        if (arcRecord.hasPayload()) {
            // Prepend payload metadata = HTTP Response lines
            ByteArrayInputStream payloadHeaderStream = new ByteArrayInputStream(payloadHeader.getBytes());
            SequenceInputStream sis = new SequenceInputStream(payloadHeaderStream, payloadContentStream);
            writer.streamPayload(sis);
        }
        writer.closeRecord();
    }

    private String constructPayloadHeader(ArcRecordBase jwatArcRecord) {
        String payloadHeader = "";
        if (jwatArcRecord.getHttpHeader() != null) {
            String statusCodeStr = jwatArcRecord.getHttpHeader().statusCodeStr;
            String httpVersionStr = jwatArcRecord.getHttpHeader().httpVersion;
            if (httpVersionStr != null && statusCodeStr != null) {
                Integer statusCode = jwatArcRecord.getHttpHeader().statusCode;
                payloadHeader
                        += httpVersionStr + " " + statusCodeStr + " " + HttpStatus.getStatusText(statusCode) + "\r\n";
            }
            List<HeaderLine> headerList = jwatArcRecord.getHttpHeader().getHeaderList();
            if (headerList != null) {
                for (HeaderLine hl : headerList) {
                    payloadHeader += hl.name + ": " + hl.value + "\r\n";
                }
            }
            payloadHeader += "\r\n"; // end of HTTP response header
        }
        return payloadHeader;
    }


    public void migrateCrawlLog(ArcRecordBase jwatArcRecord, WarcWriter writer) throws IOException, URISyntaxException {
        final CsvBeanReader beanReader = CrawlLogEntry.getCsvBeanReader(jwatArcRecord);
        try {
            Iterator<CrawlLogEntry> iterator = CrawlLogEntry.asIterator(beanReader);
            while (iterator.hasNext()) {
                CrawlLogEntry crawlLogEntry = iterator.next();
                writeDuplicateEntry(crawlLogEntry, writer);
            }
        } finally {
            IOUtils.closeQuietly(beanReader);
        }
    }


    private void writeDuplicateEntry(CrawlLogEntry crawlLogEntry, WarcWriter writer) throws
                                                                                     URISyntaxException,
                                                                                     IOException {
        String duplicationEntry = crawlLogEntry.findDuplicationEntry();
        if (duplicationEntry == null) {
            return;
        }
        WarcRecord record = WarcRecord.createRecord(writer);
        record.header.addHeader(WarcConstants.FN_WARC_TYPE, WarcConstants.RT_REVISIT);
        record.header.addHeader(WarcConstants.FN_WARC_TARGET_URI, crawlLogEntry.getDownloaded());
        record.header.addHeader(WarcConstants.FN_WARC_DATE,
                GMTUTCUnixTsFormat.format(crawlLogEntry.getLoggingTimestamp()));
        record.header.addHeader(WarcConstants.FN_WARC_PROFILE, WarcConstants.PROFILE_IDENTICAL_PAYLOAD_DIGEST);
        record.header.addHeader(WarcConstants.FN_WARC_RECORD_ID, UUIDGenerator.getRecordID().toString());
        record.header.addHeader(WarcConstants.FN_WARC_REFERS_TO,
                UUIDGenerator.getRecordID(duplicationEntry).toString());
        if (crawlLogEntry.getSha1Digest() != null) {
            record.header.addHeader(WarcConstants.FN_WARC_PAYLOAD_DIGEST, crawlLogEntry.getSha1Digest());
        }
        record.header.addHeader(WarcConstants.FN_CONTENT_TYPE,
                (crawlLogEntry.getMimetype() != null) ? crawlLogEntry.getMimetype() : MIME_UNKNOWN);
        record.header.addHeader(WarcConstants.FN_CONTENT_LENGTH, 0, null);
        // finished creating header, write it to the WARC record
        writer.writeHeader(record);
        writer.closeRecord();
    }
}
