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
import eu.scape_project.hawarp.webarchive.ArchiveRecordBase;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author onbscs
 */
//@JsonPropertyOrder(
//        {
//            "url", // a original url
//            "mimeType", // m mime type of original document
//            "date", // b date
//            "httpReturnCode", // s response code
//            "ipAddress", // e IP
//            "offsetCompressed" // offset in container
//        }
//)
@JsonFilter("cdxfields")
public class CdxArchiveRecord extends ArchiveRecord {

    /**
     * Private constructor (see fromArchiveRecord)
     */
    private CdxArchiveRecord() {
    }

    /**
     * Copy superclass fields using reflection. The ArchiveRecord base
     * class is part of the core module hawarp-core. CDX creator specific
     * class annotations are therefore added in this derived class.
     *
     * @param ar ArchiveRecord object
     * @return CdxArchiveRecord object
     */
    public static CdxArchiveRecord fromArchiveRecord(ArchiveRecord ar) {
        CdxArchiveRecord cdxArchiveRecord = new CdxArchiveRecord();
        String methodName = null;
        try {

            Field[] fields = ar.getClass().getSuperclass().getDeclaredFields();
            for (Field field : fields) {

                methodName = field.getName().substring(0, 1).toUpperCase()
                        + field.getName()
                        .substring(1, field.getName().length());

                Method method = ar.getClass().getSuperclass().getMethod("set" + methodName, field.getType());
                method.invoke(cdxArchiveRecord, field.get(ar));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cdxArchiveRecord;
    }

}
