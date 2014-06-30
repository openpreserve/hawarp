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

import eu.scape_project.hawarp.mapreduce.HadoopWebArchiveRecord;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jwat.arc.ArcRecordBase;
import org.mvel2.MVEL;

/**
 * JWAT ARC-record to Hadoop Web Archive Record conversion. The JWAT ARC record
 * properties are mapped into a flat list object which is used as the
 * intermediate format to do the mapping from ARC to WARC.
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
@Deprecated
public class WebArchiveRecordMapper {

    private static final Log LOG = LogFactory.getLog(WebArchiveRecordMapper.class);

    public static HadoopWebArchiveRecord map(Serializable compiledarc2hwar, String filePathString, ArcRecordBase jwatArcRecord, boolean identify) throws Arc2WarcMigrationException {

        // MVEL-mapped properties
        HadoopWebArchiveRecord flArcRecord = new HadoopWebArchiveRecord();
        try {
            Map vars = new HashMap();
            vars.put("flArcRecord", flArcRecord);
            vars.put("jwatArcRecord", jwatArcRecord);
            vars.put("filePathString", filePathString);
            vars.put("identify", identify);
            MVEL.executeExpression(compiledarc2hwar, flArcRecord, vars);
        } catch (IllegalStateException ex) {
            throw new Arc2WarcMigrationException("Unable to create flat list record, errors occurred in file");
        }

        return flArcRecord;
    }

}
