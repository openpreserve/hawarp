package eu.scape_project.arc2warc;

import org.jwat.arc.ArcRecordBase;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CrawlLogEntry {

    private Date loggingTimestamp;
    private Integer statusCode;
    private Long size;
    private String downloaded;
    private String breadcrumbs;
    private String referrer;
    private String mimetype;
    private String workerThreadID;
    private String fetchTimestamp;
    //private long fetchDuration;
    private String sha1Digest;
    private String sourceTag;
    private String annotations;

    public CrawlLogEntry(Date loggingTimestamp, int statusCode, long size, String downloaded, String breadcrumbs,
                         String referrer, String mimetype, String workerThreadID, String fetchTimestamp,
                         String sha1Digest, String sourceTag, String annotations) {
        this.loggingTimestamp = loggingTimestamp;
        this.statusCode = statusCode;
        this.size = size;
        this.downloaded = downloaded;
        this.breadcrumbs = breadcrumbs;
        this.referrer = referrer;
        this.mimetype = mimetype;
        this.workerThreadID = workerThreadID;
        this.fetchTimestamp = fetchTimestamp;
        this.sha1Digest = sha1Digest;
        this.sourceTag = sourceTag;
        this.annotations = annotations;
    }

    public CrawlLogEntry() {
    }

    static Iterator<CrawlLogEntry> asIterator(final CsvBeanReader beanReader) {
        // the header elements are used to map the values to the bean (names must match)
        final String[] header = new String[]{"loggingTimestamp", "statusCode", "size", "downloaded", "breadcrumbs",
                                             "referrer", "mimetype", "workerThreadID", "fetchTimestamp", "sha1Digest",
                                             "sourceTag", "annotations"};
        final CellProcessor[] processors = new CellProcessor[]{new ParseDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
                                                               new ParseInt(),//returnCode
                                                               new LongOrNull(),//size
                                                               new StrNotNullOrEmpty(),//downloaded
                                                               null, //breadcrumpts
                                                               null, // referrer
                                                               new NotNull(), // mimetype
                                                               null, // workerID
                                                               null, // fetchTimestamp
                                                               new StrNotNullOrEmpty(), // sha1
                                                               null, // source
                                                               new NotNull(), // annotations
        };
        final CrawlLogEntry entry = new CrawlLogEntry();
        return new Iterator<CrawlLogEntry>() {

            CrawlLogEntry next = null;

            @Override
            public boolean hasNext() {
                if (next == null) {
                    try {
                        next = beanReader.read(entry, header, processors);
                    } catch (IOException e) {
                        return false;
                    }
                }
                return next != null;
            }

            @Override
            public CrawlLogEntry next() {
                if (next != null) {
                    CrawlLogEntry tmp = next;
                    next = null;
                    return tmp;
                } else {
                    if (hasNext()) {
                        return next();
                    } else {
                        throw new NoSuchElementException();
                    }
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    static CsvBeanReader getCsvBeanReader(ArcRecordBase jwatArcRecord) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(jwatArcRecord.getPayloadContent()));
        return new CsvBeanReader(new ITokenizer() {

            int l = 0;
            String line;

            @Override
            public int getLineNumber() {
                return 0;
            }

            @Override
            public String getUntokenizedRow() {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                l++;
                return line;
            }

            @Override
            public boolean readColumns(List<String> columns) throws IOException {
                columns.clear();
                String line = getUntokenizedRow();
                if (line != null) {
                    String[] splits = line.split("\\s+");
                    Collections.addAll(columns, splits);
                    return true;
                }
                return false;
            }

            @Override
            public void close() throws IOException {
                reader.close();
            }
        }, CsvPreference.STANDARD_PREFERENCE);
    }


    public void setLoggingTimestamp(Date loggingTimestamp) {
        this.loggingTimestamp = loggingTimestamp;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }

    public void setBreadcrumbs(String breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public void setWorkerThreadID(String workerThreadID) {
        this.workerThreadID = workerThreadID;
    }

    public void setFetchTimestamp(String fetchTimestamp) {
        this.fetchTimestamp = fetchTimestamp;
    }

    public void setSha1Digest(String sha1Digest) {
        this.sha1Digest = sha1Digest;
    }

    public void setSourceTag(String sourceTag) {
        this.sourceTag = sourceTag;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public Date getLoggingTimestamp() {
        return loggingTimestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public long getSize() {
        return size;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public String getBreadcrumbs() {
        return breadcrumbs;
    }

    public String getReferrer() {
        return referrer;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getWorkerThreadID() {
        return workerThreadID;
    }

    public String getFetchTimestamp() {
        return fetchTimestamp;
    }

    public String getSha1Digest() {
        return sha1Digest;
    }

    public String getSourceTag() {
        return sourceTag;
    }

    public String getAnnotations() {
        return annotations;
    }

    public List<String> getAnnotationsList() {
        CsvListReader listReader = new CsvListReader(new StringReader(getAnnotations()),
                CsvPreference.STANDARD_PREFERENCE);
        try {
            return listReader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String findDuplicationEntry() {
        for (String annotation : getAnnotationsList()) {
            if (annotation.startsWith("duplicate:")) {
                return annotation.split(":")[1];
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "CrawlLogEntry{" +
               "loggingTimestamp='" + loggingTimestamp + '\'' +
               ", statusCode=" + statusCode +
               ", size=" + size +
               ", downloaded='" + downloaded + '\'' +
               ", breadcrumbs='" + breadcrumbs + '\'' +
               ", referrer='" + referrer + '\'' +
               ", mimetype='" + mimetype + '\'' +
               ", workerThreadID='" + workerThreadID + '\'' +
               ", fetchTimestamp='" + fetchTimestamp + '\'' +
               ", sha1Digest='" + sha1Digest + '\'' +
               ", sourceTag='" + sourceTag + '\'' +
               ", annotations='" + annotations + '\'' +
               '}';
    }

    private static class LongOrNull extends CellProcessorAdaptor implements StringCellProcessor {
        private LongOrNull() {
        }

        private LongOrNull(CellProcessor next) {
            super(next);
        }

        @Override
        public Object execute(Object value, CsvContext context) {
            validateInputNotNull(value, context);
            Long result;
            if (value instanceof Long) {
                result = (Long) value;
            } else if (value instanceof String) {
                try {
                    result = Long.parseLong((String) value);
                } catch (final NumberFormatException e) {
                    result = null;
                }
            } else {
                final String actualClassName = value.getClass().getName();
                throw new SuperCsvCellProcessorException(String.format(
                        "the input value should be of type Long or String but is of type %s", actualClassName), context, this);
            }
            return next.execute(result, context);
        }
    }
}
