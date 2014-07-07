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
package eu.scape_project.cdx_creator;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.FilterExceptFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import eu.scape_project.cdx_creator.cli.CDXCreatorConfig;
import eu.scape_project.hawarp.interfaces.ArchiveReader;
import static eu.scape_project.hawarp.utils.DateUtils.GMTGTechDateFormat;
import eu.scape_project.hawarp.utils.StringUtils;
import eu.scape_project.hawarp.webarchive.ArchiveReaderFactory;
import eu.scape_project.hawarp.webarchive.ArchiveRecord;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CDX creation task
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class CDXCreationTask {

    private static final Log LOG = LogFactory.getLog(CDXCreationTask.class);

    private final CDXCreatorConfig config;

    private final File archiveFile;
    
    private final String archiveFileName;

    private final String cdxFileName;

    private final String cdxFilePath;
    
   

    public CDXCreationTask(CDXCreatorConfig config, File archiveFile, String archiveFileName) {
        
        this.config = config;
        this.archiveFile = archiveFile;
        this.archiveFileName = archiveFileName;
        if (config.isDirectoryInput()) {
            String inputFileName = archiveFile.getName();
            String warcExt = ".cdx.csv";
            cdxFileName = inputFileName + warcExt;
            cdxFilePath = StringUtils.ensureTrailSep(config.getOutputStr()) + cdxFileName;
        } else {
            if (config.getOutputStr() == null) {
                String inputFileName = archiveFile.getName();
                String warcExt = ".cdx.csv";
                cdxFileName = inputFileName + warcExt;
                cdxFilePath = StringUtils.ensureTrailSep(archiveFile.getAbsolutePath()) + cdxFileName;
            } else {
                cdxFilePath = config.getOutputStr();
                if (cdxFilePath.contains(File.separator)) {
                    cdxFileName = cdxFilePath.substring(cdxFilePath.lastIndexOf(File.separator) + 1);
                } else {
                    cdxFileName = cdxFilePath;
                }
            }
        }
    }

    public void createIndex() {
        FileInputStream fileInputStream = null;
        ArchiveReader reader = null;
        FileOutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(archiveFile);
            reader = ArchiveReaderFactory.getReader(fileInputStream);

            List<CdxArchiveRecord> cdxArchRecords = new ArrayList<CdxArchiveRecord>();
            while (reader.hasNext()) {
                ArchiveRecord archRec = (ArchiveRecord) reader.next();
                CdxArchiveRecord cdxArchRec = CdxArchiveRecord.fromArchiveRecord(archRec);
                cdxArchRec.setContainerFileName(archiveFileName);
                cdxArchRec.setContainerLengthStr(Long.toString(archiveFile.length()));
                cdxArchRecords.add(cdxArchRec);
            }

            CsvMapper mapper = new CsvMapper();
            mapper.setDateFormat(GMTGTechDateFormat);
            
            String cdxfileCsColumns = config.getCdxfileCsColumns();
            List<String> cdxfileCsColumnsList = Arrays.asList(cdxfileCsColumns.split("\\s*,\\s*"));
            String[] cdxfileCsColumnsArray = cdxfileCsColumnsList.toArray(new String[cdxfileCsColumnsList.size()]);

            CsvSchema.Builder builder = CsvSchema.builder();
            for (String cdxField : cdxfileCsColumnsList) {
                builder.addColumn(cdxField);
            }
            builder.setColumnSeparator('\t');
            CsvSchema schema = builder.build();
            schema = schema.withoutQuoteChar();

            SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                    .addFilter("cdxfields", FilterExceptFilter.filterOutAllExcept(cdxfileCsColumnsArray));

            ObjectWriter cdxArchRecordsWriter = mapper.writer(filterProvider).withSchema(schema);

            PrintStream pout = null;
            String outputPathStr = config.getOutputStr();
            if (outputPathStr != null) {
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(outputPathStr, true);
                    pout = new PrintStream(fos);
                    System.setOut(pout);
                } catch (FileNotFoundException ex) {
                    LOG.error("File not found error", ex);
                }
            }
            System.out.println(config.getCdxfileCsHeader());

            cdxArchRecordsWriter.writeValue(System.out, cdxArchRecords);

            if (pout != null) {
                pout.close();
            }

        } catch (FileNotFoundException ex) {
            LOG.error("File not found error", ex);
        } catch (IOException ex) {
            LOG.error("I/O Error", ex);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (IOException ex) {
                LOG.error("I/O Error", ex);
            }
        }
    }

}
