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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import eu.scape_project.hawarp.webarchive.ArchiveRecord;
import java.util.Date;

/**
 *
 * @author onbscs
 */
@JsonPropertyOrder(
        {
            "url", // a original url
            "mimeType", // m mime type of original document
            "date", // b date
            "httpReturnCode", // s response code
            "ipAddress" // e IP
        }
)
@JsonFilter("cdxfields")
public class CdxArchiveRecord extends ArchiveRecord {

    private CdxArchiveRecord(String url, String mimeType, Date date,
            int httpReturnCode, String ipAddress) {
        this.url = url;
        this.mimeType = mimeType;
        this.date = date;
        this.httpReturnCode = httpReturnCode;
        this.ipAddress = ipAddress;
    }

    public static CdxArchiveRecord fromArchiveRecord(ArchiveRecord ar) {
        return new CdxArchiveRecord(ar.getUrl(), ar.getMimeType(), ar.getDate(),
                ar.getHttpReturnCode(), ar.getIpAddress());
    }

    @JsonIgnore
    @Override
    public String getRecordIdentifier() {
        return this.recordIdentifier;
    }

    @JsonIgnore
    @Override
    public String getReaderIdentifier() {
        return this.readerIdentifier;
    }

    @JsonIgnore
    @Override
    public String getIdentifiedPayloadType() {
        return this.identifiedPayloadType;
    }

    @JsonIgnore
    @Override
    public String getType() {
        return this.type;
    }

    @JsonIgnore
    @Override
    public int getContentLength() {
        return this.contentLength;
    }

    @JsonIgnore
    @Override
    public long getContentLengthLong() {
        return this.contentLengthLong;
    }

}
