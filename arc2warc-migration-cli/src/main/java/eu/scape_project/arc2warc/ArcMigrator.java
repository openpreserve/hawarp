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
import eu.scape_project.hawarp.mapreduce.JwatArcReaderFactory;
import eu.scape_project.hawarp.utils.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcReader;
import org.jwat.arc.ArcRecordBase;
import org.jwat.warc.WarcWriter;
import org.jwat.warc.WarcWriterFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

/**
 *
 * @author scape
 */
public class ArcMigrator {

    private static final Log LOG = LogFactory.getLog(ArcMigrator.class);

    public static final int LIMIT_LARGE_PAYLOAD =  4194304; // 4MB

    private final Arc2WarcMigrationConfig config;

    private final File arcFile;

    private final String warcFileName;

    private final String warcFilePath;
    private final byte[] buffer;

    public ArcMigrator(Arc2WarcMigrationConfig config, File arcFile) {
        this.config = config;
        this.arcFile = arcFile;
        //File input = new File(config.getInputStr());
        if (config.isDirectoryInput()) {
            String inputFileName = arcFile.getName();
            String warcExt = config.createCompressedWarc() ? ".warc.gz" : ".warc";
            warcFileName = inputFileName + warcExt;
            warcFilePath = StringUtils.ensureTrailSep(config.getOutputStr()) + warcFileName;
        } else {
            warcFilePath = config.getOutputStr();
            if (warcFilePath.contains(File.separator)) {
                warcFileName = warcFilePath.substring(warcFilePath.lastIndexOf(File.separator) + 1);
            } else {
                warcFileName = warcFilePath;
            }
        }
        buffer = new byte[LIMIT_LARGE_PAYLOAD];
    }

    public void migrate() {
        FileInputStream fileInputStream = null;
        ArcReader reader = null;
        FileOutputStream outputStream = null;
        WarcWriter writer = null;
        try {
            fileInputStream = new FileInputStream(arcFile);
            reader = JwatArcReaderFactory.getReader(fileInputStream);

            outputStream = new FileOutputStream(new File(warcFilePath));
            writer = WarcWriterFactory.getWriter(outputStream, config.createCompressedWarc());
            RecordMigrator recordMigrator = new RecordMigrator(writer, warcFileName, buffer);
            recordMigrator.createWarcInfoRecord();
            recordMigrator.setArcMetadataRecord(true);

            Iterator<ArcRecordBase> arcIterator = reader.iterator();
            while (arcIterator.hasNext()) {
                ArcRecordBase jwatArcRecord = arcIterator.next();
                if (jwatArcRecord != null) {
                    recordMigrator.migrateRecord(jwatArcRecord,
                            config.isContentTypeIdentification());
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
            IOUtils.closeQuietly(fileInputStream);
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
        }
    }

}
