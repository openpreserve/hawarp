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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcRecordBase;
import org.jwat.common.ByteCountingPushBackInputStream;
import org.jwat.common.HeaderLine;
import org.jwat.common.HttpHeader;
import org.jwat.common.Payload;
import org.jwat.common.PayloadWithHeaderAbstract;
import org.jwat.warc.WarcRecord;

/**
 *
 * @author onbscs
 */
public class ArchiveRecord extends ArchiveRecordBase {

    private static final Log LOG = LogFactory.getLog(ArchiveRecord.class);

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    {
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    public ArchiveRecord() {
        
    }

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
        if(arcRecord.getResultCode() != null) {
            this.httpReturnCode = arcRecord.getResultCode();
        } else {
            if(arcRecord.getHttpHeader() != null && arcRecord.getHttpHeader().statusCode != null) {
                this.httpReturnCode = arcRecord.getHttpHeader().statusCode;
            }
            
        }
    }

    ArchiveRecord(WarcRecord warcRecord) {
        boolean isResponseType = false;
        
        if (warcRecord.getHeaderList() != null) {
            HeaderLine warcTypeHl = warcRecord.getHeader("WARC-Type");
            if (warcTypeHl != null && warcTypeHl.value.equals("warcinfo")) {
                this.readerIdentifier = warcRecord.getHeader("WARC-Filename").value;
            } else if (warcTypeHl != null && warcTypeHl.value.equals("response")) {
                isResponseType = true;
            }
            HeaderLine ipAddressHl = warcRecord.getHeader("WARC-IP-Address");
            if (ipAddressHl != null) {
                this.ipAddress = ipAddressHl.value;
            }
            HeaderLine contentTypeHl = warcRecord.getHeader("Content-Type");
            if (contentTypeHl != null) {
                this.mimeType = contentTypeHl.value;
            }
            HeaderLine contentLengthHl = warcRecord.getHeader("Content-Length");
            if (contentLengthHl != null) {
                try {
                    this.contentLengthLong = Long.parseLong(contentLengthHl.value);
                } catch (NumberFormatException ex) {
                    this.contentLengthLong = -1;
                }
            }
            HeaderLine paloadDigestHl = warcRecord.getHeader("WARC-Payload-Digest");
            if (paloadDigestHl != null) {
                this.payloadDigestStr = paloadDigestHl.value;
            }

            HeaderLine targetUriHl = warcRecord.getHeader("WARC-Target-URI");
            if (targetUriHl != null) {
                this.url = targetUriHl.value;
            }
            HeaderLine dateHl = warcRecord.getHeader("WARC-Date");
            if (dateHl != null) {
                try {
                    this.date = sdf.parse(dateHl.value);
                } catch (ParseException ex) {
                    this.date = new Date(0);
                }
            }
        }

    }

}
