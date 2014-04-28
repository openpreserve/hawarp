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

import java.util.Date;

/**
 *
 * @author onbscs
 */
public abstract class ArchiveRecordBase {
    
    protected String readerIdentifier;
    protected String recordIdentifier;
    protected String url = null;
    protected String mimeType = null;
    protected String identifiedPayloadType;
    protected Date date = null;
    protected int httpReturnCode = -1;
    protected String ipAddress;
    protected String type;
    protected int contentLength;
    protected long contentLengthLong;
    protected String payloadDigestStr;
    
    public String getReaderIdentifier() {
        return readerIdentifier;
    }

    public void setReaderIdentifier(String readerIdentifier) {
        this.readerIdentifier = readerIdentifier;
    }

    public String getRecordIdentifier() {
        return recordIdentifier;
    }

    public void setRecordIdentifier(String recordIdentifier) {
        this.recordIdentifier = recordIdentifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getIdentifiedPayloadType() {
        return identifiedPayloadType;
    }

    public void setIdentifiedPayloadType(String identifiedPayloadType) {
        this.identifiedPayloadType = identifiedPayloadType;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getHttpReturnCode() {
        return httpReturnCode;
    }

    public void setHttpReturnCode(int httpReturnCode) {
        this.httpReturnCode = httpReturnCode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public long getContentLengthLong() {
        return contentLengthLong;
    }

    public void setContentLengthLong(long contentLengthLong) {
        this.contentLengthLong = contentLengthLong;
    }
    
    public void setPayloadDigestStr(String payloadDigestStr) {
        this.payloadDigestStr = payloadDigestStr;
    }
    
}
