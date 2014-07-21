package eu.scape_project.arc2warc;

import eu.scape_project.arc2warc.cli.Arc2WarcMigrationConfig;
import eu.scape_project.hawarp.mapreduce.JwatArcReaderFactory;
import eu.scape_project.hawarp.utils.RegexUtils;
import eu.scape_project.hawarp.utils.UUIDGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcRecordBase;
import org.jwat.warc.WarcConstants;
import org.jwat.warc.WarcRecord;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;
import org.supercsv.io.CsvBeanReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Iterator;

import static eu.scape_project.hawarp.interfaces.Identifier.MIME_UNKNOWN;
import static eu.scape_project.hawarp.utils.DateUtils.GMTUTCUnixTsFormat;
import static eu.scape_project.hawarp.utils.UUIDGenerator.getRecordID;

public class DuplicateMigrator {

    private static final Log LOG = LogFactory.getLog(DuplicateMigrator.class);


    private final Arc2WarcMigrationConfig config;

    private final File arcFile;


    private final File warcFile;


    public DuplicateMigrator(Arc2WarcMigrationConfig config, File arcFile, File warcFile) {
        this.config = config;
        this.arcFile = arcFile;
        this.warcFile = warcFile;
    }

    public void migrate() {
        ArcReader reader = null;
        WarcWriter writer = null;
        try {
            reader = JwatArcReaderFactory.getReader(new FileInputStream(arcFile));
            writer = WarcWriterFactory.getWriter(FileUtils.openOutputStream(warcFile), config.createCompressedWarc());
            Iterator<ArcRecordBase> arcIterator = reader.iterator();
            createWarcInfoRecord(writer);
            while (arcIterator.hasNext()) {
                ArcRecordBase jwatArcRecord = arcIterator.next();
                if (RegexUtils.pathMatchesRegexFilter(jwatArcRecord.getUrlStr(),
                        "^metadata://.*/crawl/logs/crawl\\.log.*$")) {
                    micrateCrawlLog(jwatArcRecord, writer);
                }
            }
            LOG.info("File processed: " + arcFile.getAbsolutePath());
        } catch (URISyntaxException ex) {
            LOG.error("File not found error", ex);
        } catch (FileNotFoundException ex) {
            LOG.error("File not found error", ex);
        } catch (IOException ex) {
            LOG.error("I/O Error", ex);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
        }
    }


    public void createWarcInfoRecord(WarcWriter writer) throws IOException, URISyntaxException {
        WarcRecord record = WarcRecord.createRecord(writer);
        record.header.addHeader(WarcConstants.FN_WARC_TYPE, WarcConstants.RT_WARCINFO);
        record.header.addHeader(WarcConstants.FN_WARC_TYPE,
                GMTUTCUnixTsFormat.format(Calendar.getInstance().getTime()));
        record.header.addHeader(WarcConstants.FN_WARC_RECORD_ID, getRecordID().toString());
        record.header.addHeader(WarcConstants.FN_WARC_FILENAME, warcFile.getName());
        record.header.addHeader(WarcConstants.FN_CONTENT_TYPE, WarcConstants.CT_APP_WARC_FIELDS);
        String description
                = "software: JWAT Version 1.0.0 https://sbforge.org/display/JWAT/JWAT-Tools\n" + "description: migrated from ARC " + "format: WARC file version 1.0";
        byte[] descriptionBytes = description.getBytes();
        record.header.addHeader(WarcConstants.FN_CONTENT_LENGTH, Long.toString(descriptionBytes.length));
        writer.writeHeader(record);
        writer.writePayload(descriptionBytes);
        writer.closeRecord();
    }

    public void micrateCrawlLog(ArcRecordBase jwatArcRecord, WarcWriter writer) throws IOException, URISyntaxException {
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
