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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import eu.scape_project.hawarp.webarchive.ArchiveRecord;
import eu.scape_project.hawarp.webarchive.ArchiveRecordBase;

/**
 *
 * @author onbscs
 */
@JsonPropertyOrder(
        {"readerIdentifier", "recordIdentifier", "recordIdentifier","url",
        "mimeType","identifiedPayloadType","date","httpReturnCode", "ipAddress",
        "type", "contentLength", "contentLengthLong", "payloadDigestStr"}
)
public class CdxArchiveRecord extends ArchiveRecord {
    
}
