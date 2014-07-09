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
package eu.scape_project.cdx_creator.cli;

import eu.scape_project.hawarp.cli.CliOptions;
import java.io.File;
import org.apache.commons.cli.CommandLine;

/**
 * Command line interface options.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class CDXCreatorOptions extends CliOptions {

    public String INPUTPATHREGEX_FLG = "r";
    public String INPUTPATHREGEX_OPT = "regex";
    public String INPUTPATHREGEX_OPT_DESC = "Only input paths matching the regular expression will be processed. [optional].";
    
    public String COMPUTEPAYLOADDIGEST_FLG = "d";
    public String COMPUTEPAYLOADDIGEST_OPT = "digest";
    public String COMPUTEPAYLOADDIGEST_OPT_DESC = "Calculate payload digest. [optional].";

    public CDXCreatorOptions() {
        options.addOption(OUTPUT_FLG, OUTPUT_OPT, true, OUTPUT_OPT_DESC);
        options.addOption(INPUTPATHREGEX_FLG, INPUTPATHREGEX_OPT, true, INPUTPATHREGEX_OPT_DESC);
        options.addOption(PROPERTIESFILE_FLG, PROPERTIESFILE_OPT, true, PROPERTIESFILE_OPT_DESC);
        options.addOption(COMPUTEPAYLOADDIGEST_FLG, COMPUTEPAYLOADDIGEST_OPT, false, COMPUTEPAYLOADDIGEST_OPT_DESC);

    }

    public void initOptions(CommandLine cmd, CDXCreatorConfig pc) {

        super.initOptions(cmd, pc);

        if (cmd.hasOption(OUTPUT_OPT) && cmd.getOptionValue(OUTPUT_OPT) != null) {
            File input = new File(cmd.getOptionValue(INPUT_OPT));
            String outputStr = cmd.getOptionValue(OUTPUT_OPT);
            File output = new File(outputStr);
            if (output.exists()) {
                throw new IllegalArgumentException("The output file/directory must not exist!");
            }
            if (input.isDirectory()) {
                output.mkdirs();
            }
        }
        
        if(cmd.hasOption(COMPUTEPAYLOADDIGEST_FLG)) {
            pc.setCreatePayloadDigest(true);
        }

    }

    @Override
    public String getUsage() {
        return "hadoop jar "
                + "target/arc2warc-migration-1.0-SNAPSHOT-jar-with-dependencies.jar";
    }
}
