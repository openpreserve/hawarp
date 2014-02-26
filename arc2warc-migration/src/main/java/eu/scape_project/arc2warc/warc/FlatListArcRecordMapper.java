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
package eu.scape_project.arc2warc.warc;

import eu.scape_project.hawarp.mapreduce.FlatListArcRecord;
import eu.scape_project.hawarp.utils.DigestUtils;
import static eu.scape_project.tika_identify.identification.IdentificationConstants.MIME_UNKNOWN;
import eu.scape_project.tika_identify.tika.TikaIdentification;
import eu.scape_project.tika_identify.webarchive.PayloadContent;
import java.io.IOException;
import java.io.InputStream;
import org.jwat.arc.ArcRecordBase;

/**
 * JWAT ARC-record to Hadoop Flat List ARC record conversion. The JWAT ARC
 * record properties are mapped into a flat list object which is used as the
 * intermediate format to do the mapping from ARC to WARC.
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class FlatListArcRecordMapper {

    public static FlatListArcRecord map(String filePathString, ArcRecordBase jwatArcRecord, boolean identify) throws IOException {
        FlatListArcRecord flArcRecord = new FlatListArcRecord();
        flArcRecord.setReaderIdentifier(filePathString);
        flArcRecord.setUrl(jwatArcRecord.getUrlStr());
        flArcRecord.setDate(jwatArcRecord.getArchiveDate());
        String mime = (jwatArcRecord.getContentType() != null) ? jwatArcRecord.getContentType().toString() : MIME_UNKNOWN;
        flArcRecord.setMimeType(mime);
        flArcRecord.setType("response");
        long remaining = jwatArcRecord.getPayload().getRemaining();
        flArcRecord.setContentLength((int) remaining);
        if (remaining < Integer.MAX_VALUE) {
            InputStream is = jwatArcRecord.getPayloadContent();
            PayloadContent payloadContent = new PayloadContent(is);
            if (identify) {
                TikaIdentification ti = TikaIdentification.getInstance();
                payloadContent.setIdentifier(ti);
                payloadContent.doPayloadIdentification(true);
            }
            payloadContent.readPayloadContent();
            byte[] payLoadBytes = payloadContent.getPayloadBytes();
            flArcRecord.setPayloadDigestStr(DigestUtils.SHAsum(payLoadBytes));
            flArcRecord.setContents(payLoadBytes);
            if (identify) {
                flArcRecord.setIdentifiedPayloadType(payloadContent.getIdentifiedPayLoadType());
            }
        }
        if (jwatArcRecord.getIpAddress() != null) {
            flArcRecord.setIpAddress(jwatArcRecord.getIpAddress());
        }
        flArcRecord.setHttpReturnCode(200);
        return flArcRecord;
    }

}
