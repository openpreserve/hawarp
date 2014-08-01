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
package eu.scape_project.arc2warc.cli;

import eu.scape_project.hawarp.cli.CliOptions;
import org.apache.commons.cli.CommandLine;

import java.io.File;

/**
 * Command line interface options.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class Arc2WarcMigrationOptions extends CliOptions {

    public String CONTENTTYPEID_FLG = "m";
    public String CONTENTTYPEID_OPT = "mimeident";
    public String CONTENTTYPEID_OPT_DESC = "Do payload mime type identification. [optional].";

    public String INPUTPATHREGEX_FLG = "r";
    public String INPUTPATHREGEX_OPT = "regex";
    public String INPUTPATHREGEX_OPT_DESC = "Only input paths matching the regular expression will be processed. [optional].";

    public String WARCCOMPRESSED_FLG = "x";
    public String WARCCOMPRESSED_OPT = "comprwarc";
    public String WARCCOMPRESSED_OPT_DESC = "Create compressed WARC file. [optional].";

    public Arc2WarcMigrationOptions() {
        options.addOption(OUTPUT_FLG, OUTPUT_OPT, true, OUTPUT_OPT_DESC);
        options.addOption(CONTENTTYPEID_FLG, CONTENTTYPEID_OPT, false, CONTENTTYPEID_OPT_DESC);
        options.addOption(INPUTPATHREGEX_FLG, INPUTPATHREGEX_OPT, true, INPUTPATHREGEX_OPT_DESC);
        options.addOption(WARCCOMPRESSED_FLG, WARCCOMPRESSED_OPT, false, WARCCOMPRESSED_OPT_DESC);
    }

    public void initOptions(CommandLine cmd, Arc2WarcMigrationConfig pc) {

        super.initOptions(cmd, pc);

        // content type identification
        if (cmd.hasOption(CONTENTTYPEID_OPT)) {
            pc.setContentTypeIdentification(true);
            System.out.println("Payload mime type identification is active");
        }

        // input path regex filter
        String inputPathRegexFilter;
        if (!(cmd.hasOption(INPUTPATHREGEX_OPT) && cmd.getOptionValue(INPUTPATHREGEX_OPT) != null)) {
            pc.setInputPathRegexFilter(".*");
        } else {
            inputPathRegexFilter = cmd.getOptionValue(INPUTPATHREGEX_OPT);
            pc.setInputPathRegexFilter(inputPathRegexFilter);
            System.out.println("Input path regex filter: " + inputPathRegexFilter);
        }

        // create compressed warc
        if (cmd.hasOption(WARCCOMPRESSED_OPT)) {
            pc.setCreateCompressedWarc(true);
            System.out.println("Create compressed WARC file output");
        }

        if (cmd.hasOption(OUTPUT_OPT) && cmd.getOptionValue(OUTPUT_OPT) != null) {
            File input = new File(cmd.getOptionValue(INPUT_OPT));
            String outputStr = cmd.getOptionValue(OUTPUT_OPT);
            File output = new File(outputStr);
            if (input.isDirectory()) {
                output.mkdirs();
            }

        }

    }

    @Override
    public String getUsage() {
        return "hadoop jar "
                + "target/arc2warc-migration-1.0-SNAPSHOT-jar-with-dependencies.jar";
    }
}
