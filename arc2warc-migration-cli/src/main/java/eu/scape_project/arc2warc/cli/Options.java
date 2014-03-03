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

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Command line interface options.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class Options {

    // Statics to set up command line arguments
    public static final String HELP_FLG = "h";
    public static final String HELP_OPT = "help";
    public static final String HELP_OPT_DESC = "print this message [optional].";

    public static final String INPUT_FLG = "i";
    public static final String INPUT_OPT = "input";
    public static final String INPUT_OPT_DESC = "Input directory containing ARC files or single ARC file path. [required].";

    public static final String OUTPUT_FLG = "o";
    public static final String OUTPUT_OPT = "output";
    public static final String OUTPUT_OPT_DESC = "Output directory or output WARC file path when using input ARC file path. [required].";

    public static final String CONTENTTYPEID_FLG = "p";
    public static final String CONTENTTYPEID_OPT = "payloadid";
    public static final String CONTENTTYPEID_OPT_DESC = "Do payload mime type identification. [optional].";

    public static final String INPUTPATHREGEX_FLG = "x";
    public static final String INPUTPATHREGEX_OPT = "iregex";
    public static final String INPUTPATHREGEX_OPT_DESC = "Only input paths matching the regular expression will be processed. [optional].";

    public static final String WARCCOMPRESSED_FLG = "c";
    public static final String WARCCOMPRESSED_OPT = "comprwarc";
    public static final String WARCCOMPRESSED_OPT_DESC = "Create compressed WARC file. [optional].";

    public static org.apache.commons.cli.Options OPTIONS = new org.apache.commons.cli.Options();
    public static final String USAGE = "hadoop jar "
            + "target/arc2warc-migration-1.0-SNAPSHOT-jar-with-dependencies.jar";

    static {
        OPTIONS.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
        OPTIONS.addOption(INPUT_FLG, INPUT_OPT, true, INPUT_OPT_DESC);
        OPTIONS.addOption(OUTPUT_FLG, OUTPUT_OPT, true, OUTPUT_OPT_DESC);
        OPTIONS.addOption(CONTENTTYPEID_FLG, CONTENTTYPEID_OPT, false, CONTENTTYPEID_OPT_DESC);
        OPTIONS.addOption(INPUTPATHREGEX_FLG, INPUTPATHREGEX_OPT, true, INPUTPATHREGEX_OPT_DESC);
        OPTIONS.addOption(WARCCOMPRESSED_FLG, WARCCOMPRESSED_OPT, false, WARCCOMPRESSED_OPT_DESC);
    }

    public static void initOptions(CommandLine cmd, Config pc) {

        // input directory
        String inputStr;
        
        if (!(cmd.hasOption(INPUT_OPT) && cmd.getOptionValue(INPUT_OPT) != null)) {
            exit("No input directory given.", 1);
        } else {
            inputStr = cmd.getOptionValue(INPUT_OPT);
            pc.setInputStr(inputStr);
            System.out.println("Input: " + inputStr);
            File input = new File(pc.getInputStr());
            if (!input.exists()) {
                exit("Input not found", 1);
            }
            if(input.isDirectory()) {
               pc.setDirectoryInput(true);
            }
        }

        // output directory
        String outputStr;
        if (!(cmd.hasOption(OUTPUT_OPT) && cmd.getOptionValue(OUTPUT_OPT) != null)) {
            exit("No output directory given.", 1);
        } else {
            outputStr = cmd.getOptionValue(OUTPUT_OPT);
            pc.setOutputStr(outputStr);
            System.out.println("Output: " + outputStr);
            File output = new File(pc.getOutputStr());
            if (pc.isDirectoryInput()) {
                output.mkdirs();
            }
        }

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

    }

    public static void exit(String msg, int status) {
        if (status > 0) {
            System.out.println(msg);
        } else {
            System.out.println(msg);
        }
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, OPTIONS, true);
        System.exit(status);
    }
}
