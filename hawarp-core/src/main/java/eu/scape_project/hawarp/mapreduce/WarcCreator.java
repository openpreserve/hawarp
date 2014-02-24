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
package eu.scape_project.hawarp.mapreduce;

import eu.scape_project.hawarp.mapreduce.FlatListArcRecord;
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

//import static eu.scape_project.tika_identify.identification.IdentificationConstants.*;

/**
 * Creating WARC records using JWAT. This class creates WARC records using JWAT
 * ARC record reader, see https://sbforge.org/display/JWAT/JWAT-Tools.
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public abstract class WarcCreator {

    private static final Log LOG = LogFactory.getLog(WarcCreator.class);


    protected WarcWriter writer;
    
    protected String fileName;

    public WarcCreator() {
    }

//    public WarcCreator(WarcWriter writer, String fileName) {
//        this.writer = writer;
//        this.fileName = fileName;
//    }

    public void close() throws IOException {
        writer.close();
    }
    

    static protected RecordIDGenerator generator = new UUIDGenerator();

    static protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd'T'HH:mm:ss'Z'");
    

    protected boolean isFirstRecord;

    protected boolean payloadIdentification;
   
    public abstract void createWarcInfoRecord() throws IOException;

  
    public abstract void createContentRecord(FlatListArcRecord arcRecord) throws IOException;

    public boolean isPayloadIdentification() {
        return payloadIdentification;
    }

    public void setPayloadIdentification(boolean payloadIdentification) {
        this.payloadIdentification = payloadIdentification;
    }

    public void setFilename(String filename) {
        this.fileName = filename;
    }

    public void setWriter(WarcWriter writer) {
        this.writer = writer;
    }

}
