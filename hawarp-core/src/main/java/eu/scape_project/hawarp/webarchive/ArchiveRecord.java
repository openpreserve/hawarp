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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.HeaderLine;
import org.jwat.common.Payload;
import org.jwat.common.PayloadWithHeaderAbstract;
import org.jwat.warc.WarcRecord;

/**
 *
 * @author onbscs
 */
public class ArchiveRecord extends ArchiveRecordBase {

    ArchiveRecord(ArcRecordBase arcRecord) {
        this.readerIdentifier = arcRecord.getFileName();
        this.url = arcRecord.getUrlStr();
        if (!arcRecord.header.ipAddressStr.isEmpty()) {
            this.ipAddress = arcRecord.header.ipAddressStr;
        }
        if (!arcRecord.header.contentTypeStr.isEmpty()) {
            this.mimeType = arcRecord.header.contentTypeStr;
        } else {
            this.mimeType = "unknown/unknown";
        }
        this.type = "response";
        this.date = arcRecord.getArchiveDate();
    }

    ArchiveRecord(WarcRecord warcRecord) {
//        WARC-Type: warcinfo
//        WARC-Record-ID: <urn:uuid:1fa08584-e401-4584-ba2c-8b52e18bd030>
//        WARC-Date: 2014-04-25T12:42:41Z
//        Content-Length: 133
//        Content-Type: application/warc-fields
//        WARC-Filename: example.warc.gz
        System.out.println("##########################################");
        if (warcRecord.getHeaderList() != null) {
            HeaderLine hl = warcRecord.getHeader("WARC-Type");
            if (hl != null && hl.value.equals("warcinfo")) {
                this.readerIdentifier = warcRecord.getHeader("WARC-Filename").value;
            }
        }
        List<HeaderLine> hlList = warcRecord.getHeaderList();
        for (HeaderLine hl : hlList) {
            System.out.println(hl.name + ": " + hl.value);;
        }
        System.out.println("-------------------------------------");
        if (warcRecord.hasPayload()) {
            Payload payload = warcRecord.getPayload();
            
            PayloadWithHeaderAbstract payloadHeaderWrapped = payload.getPayloadHeaderWrapped();
            
            if (payloadHeaderWrapped != null) {
                
                List<HeaderLine> headerList = warcRecord.getPayload().getPayloadHeaderWrapped().getHeaderList();
                if (headerList != null) {
                    for (HeaderLine hl : headerList) {
                        System.out.println(hl.name + ": " + hl.value);;
                    }
                }
            }
        }

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
