/*
 *  Copyright 2012 The SCAPE Project Consortium.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package eu.scape_project.arc2warc.mapreduce;

import java.io.*;
import java.util.Date;
import org.apache.hadoop.io.Writable;

/**
 * Hadoop ARC record which can be used as key-value type. This object is used as
 * an intermediate representation of a record. It maps the metadata properties
 * of the ARC record into a flat list which is then used to map the properties
 * to WARC properties. The internal representation of a record is the ArcRecord
 * class which also contains the payload of a record. The payload is read into a
 * byte array, therefore it can hold a maximum of Integer.MAX_VALUE bytes. It is
 * important to note that, depending on the memory available in the cluster, the
 * payload size limit may be much lower.
 *
 * @author shsdev https://github.com/shsdev
 */
public class ArcRecord implements Writable {

    private String readerIdentifier;
    private String recordIdentifier;
    private String url = null;
    private String mimeType = null;
    private Date date = null;
    private int httpReturnCode = -1;
    private String ipAddress;
    private String type;
    private int contentLength;
    private byte[] contents;

    /**
     * Reset the properties
     */
    public void clear() {
        readerIdentifier = "";
        recordIdentifier = "";
        url = "";
        mimeType = null;
        date = null;
        httpReturnCode = -1;
        type = "";
        contentLength = 0;
    }

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

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(readerIdentifier);
        out.writeUTF(recordIdentifier);
        out.writeUTF(url);
        out.writeUTF(mimeType);
        out.writeLong(date.getTime());
        out.writeInt(httpReturnCode);
        out.writeUTF(ipAddress);
        out.writeUTF(type);
        out.write(contentLength);
        out.write(contents, 0, contentLength);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        readerIdentifier = in.readUTF();
        recordIdentifier = in.readUTF();
        url = in.readUTF();
        mimeType = in.readUTF();
        date = new Date(in.readLong());
        httpReturnCode = in.readInt();
        ipAddress = in.readUTF();
        type = in.readUTF();
        contentLength = in.readInt();
        in.readFully(contents, 0, contentLength);
    }

}
