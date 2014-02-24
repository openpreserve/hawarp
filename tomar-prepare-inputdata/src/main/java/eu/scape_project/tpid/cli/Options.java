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
package eu.scape_project.tpid.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command line interface options.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class Options {

    private static Logger logger = LoggerFactory.getLogger(Options.class.getName());
    // Statics to set up command line arguments
    public static final String HELP_FLG = "h";
    public static final String HELP_OPT = "help";
    public static final String HELP_OPT_DESC = "print this message [optional].";

    public static final String DIR_FLG = "d";
    public static final String DIR_OPT = "dir";
    public static final String DIR_OPT_DESC = "HDFS directory containing (the) text file(s) with HDFS paths to container files (e.g. '/user/name/inputdir/'). [required].";

    public static final String NPT_FLG = "n";
    public static final String NPT_OPT = "npt";
    public static final String NPT_OPT_DESC = "Number of items to be processed per task (e.g. 50). [optional, default: " + 50 + "].";

    public static org.apache.commons.cli.Options OPTIONS = new org.apache.commons.cli.Options();
    public static final String USAGE = "java jar "
            + "target/tpid-1.0-SNAPSHOT-jar-with-dependencies.jar "
            + "";

    static {
        OPTIONS.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
        OPTIONS.addOption(DIR_FLG, DIR_OPT, true, DIR_OPT_DESC);
        OPTIONS.addOption(NPT_FLG, NPT_OPT, true, NPT_OPT_DESC);
    }

    public static void initOptions(CommandLine cmd, CliConfig pc) {

        // dir
        String dirStr;
        if (!(cmd.hasOption(DIR_OPT) && cmd.getOptionValue(DIR_OPT) != null)) {
            exit("HDFS input directory parameter missing.", 1);
        } else {
            dirStr = cmd.getOptionValue(DIR_OPT);
            pc.setDirStr(dirStr);
            logger.debug("Input directory: " + dirStr);
        }

        // num items per invokation
        int numItemsPerInvokation;
        if (!(cmd.hasOption(NPT_OPT) && cmd.getOptionValue(NPT_OPT) != null)) {
            pc.setNumItemsPerInvokation(0);
        } else {
            try {
                numItemsPerInvokation = Integer.parseInt(cmd.getOptionValue(NPT_OPT));
                pc.setNumItemsPerInvokation(numItemsPerInvokation);
            } catch (NumberFormatException ex) {
                exit("Parameter 'Number of items to be processed per invocation' is not a valid number.", 1);
            }
            
        }
        logger.debug("Number of items to be processed per invocation: " + pc.getNumItemsPerInvokation());

    }

    public static void exit(String msg, int status) {
        if (status > 0) {
            logger.error(msg);
        } else {
            logger.info(msg);
        }
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, OPTIONS, true);
        System.exit(status);
    }
}
