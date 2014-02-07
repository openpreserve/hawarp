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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;

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
    public static final String INPUT_OPT_DESC = "HDFS Input directory with ARC files. [required].";
    
    public static final String OUTPUT_FLG = "o";
    public static final String OUTPUT_OPT = "output";
    public static final String OUTPUT_OPT_DESC = "HDFS Output directory where the WARC files will be stored. [required].";
    
    public static final String LOCAL_FLG = "l";
    public static final String LOCAL_OPT = "local";
    public static final String LOCAL_OPT_DESC = "Use local file system instead of HDFS (debugging). [optional].";
    
    public static org.apache.commons.cli.Options OPTIONS = new org.apache.commons.cli.Options();
    public static final String USAGE = "hadoop jar "
            + "target/arc2warc-hadoop-1.0-SNAPSHOT-jar-with-dependencies.jar "
            + "-i <directory> -o <directory>";

    static {
        OPTIONS.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
        OPTIONS.addOption(INPUT_FLG, INPUT_OPT, true, INPUT_OPT_DESC);
        OPTIONS.addOption(OUTPUT_FLG, OUTPUT_OPT, true, OUTPUT_OPT_DESC);
        OPTIONS.addOption(LOCAL_FLG, LOCAL_OPT, false, LOCAL_OPT_DESC);
    }

    public static void initOptions(CommandLine cmd, CliConfig pc) {
        
        // input directory
        String inputDirStr;
        if (!(cmd.hasOption(INPUT_OPT) && cmd.getOptionValue(INPUT_OPT) != null)) {
            exit("No input directory given.", 1);
        } else {
            inputDirStr = cmd.getOptionValue(INPUT_OPT);
            pc.setInputDirStr(inputDirStr);
            System.out.println("Input directory: " + inputDirStr);
        }
        
        // output directory
        String outputDirStr;
        if (!(cmd.hasOption(INPUT_OPT) && cmd.getOptionValue(INPUT_OPT) != null)) {
            exit("No output directory given.", 1);
        } else {
            outputDirStr = cmd.getOptionValue(INPUT_OPT);
            pc.setOutputDirStr(outputDirStr);
            System.out.println("Output directory: " + outputDirStr);
        }
        
        // local mode
        if (cmd.hasOption(LOCAL_OPT)) {
            pc.setLocal(true);
            System.out.println("Local mode, reading/writing from/to local file system (debugging)");
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
