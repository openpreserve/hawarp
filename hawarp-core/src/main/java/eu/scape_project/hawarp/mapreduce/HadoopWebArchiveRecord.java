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
package eu.scape_project.hawarp.mapreduce;

import eu.scape_project.hawarp.webarchive.ArchiveRecordBase;
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
public class HadoopWebArchiveRecord extends ArchiveRecordBase implements Writable {

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
        payloadDigestStr = "";
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public String getPayloadDigestStr() {
        if(payloadDigestStr == null) {
            return "";
        }
        return payloadDigestStr;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(readerIdentifier);
        out.writeUTF(recordIdentifier);
        out.writeUTF(url);
        out.writeUTF(mimeType);
        out.writeUTF(identifiedPayloadType);
        out.writeLong(date.getTime());
        out.writeInt(httpReturnCode);
        out.writeUTF(ipAddress);
        out.writeUTF(type);
        out.write(contentLength);
        out.write(contents, 0, contentLength);
        out.writeUTF(payloadDigestStr);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        readerIdentifier = in.readUTF();
        recordIdentifier = in.readUTF();
        url = in.readUTF();
        mimeType = in.readUTF();
        identifiedPayloadType = in.readUTF();
        date = new Date(in.readLong());
        httpReturnCode = in.readInt();
        ipAddress = in.readUTF();
        type = in.readUTF();
        contentLength = in.readInt();
        in.readFully(contents, 0, contentLength);
        payloadDigestStr = in.readUTF();
    }

}
