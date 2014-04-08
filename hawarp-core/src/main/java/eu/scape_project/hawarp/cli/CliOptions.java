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
package eu.scape_project.hawarp.cli;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Command line interface options. Some of the options defined in this class are
 * added to all command line applications by default (see constructor where the
 * HELP_OPT, INPUT_OPT, and PSEUDO_OPT options are added), other options are
 * added in the implementing class only in case they are used.
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public abstract class CliOptions {

    // Statics to set up command line arguments
    public String HELP_FLG = "h";
    public String HELP_OPT = "help";
    public String HELP_OPT_DESC = "print this message [optional].";

    public String INPUT_FLG = "i";
    public String INPUT_OPT = "input";
    public String INPUT_OPT_DESC = "Path to input. [required].";

    public String OUTPUT_FLG = "o";
    public String OUTPUT_OPT = "output";
    public String OUTPUT_OPT_DESC = "Path to output. [optional].";

    public String LOCAL_FLG = "l";
    public String LOCAL_OPT = "local";
    public String LOCAL_OPT_DESC = "Execute as java application (java -jar ...), "
            + "default Hadoop job (hadoop jar ...). [optional].";

    public String PSEUDO_FLG = "p";
    public final String PSEUDO_OPT = "pseudo";
    public final String PSEUDO_OPT_DESC = "Execute Hadoop job in "
            + "pseudo-distributed mode and use local file system "
            + "(\"file://\") instead of HDFS (\"hdfs://\"). [optional].";
    
    public String PROPERTIESFILE_FLG = "c";
    public String PROPERTIESFILE_OPT = "propconffilepath";
    public String PROPERTIESFILE_OPT_DESC = "File system path to properties file. [optional].";

    public org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    public CliOptions() {
        options.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
        options.addOption(INPUT_FLG, INPUT_OPT, true, INPUT_OPT_DESC);
    }

    protected static final Log LOG = LogFactory.getLog(CliOptions.class);

    public void initOptions(CommandLine cmd, CliConfig pc) {

        // input string
        String inputStr;
        if (!(cmd.hasOption(INPUT_OPT) && cmd.getOptionValue(INPUT_OPT) != null)) {
            exit("No input given.", 1);
        } else {
            inputStr = cmd.getOptionValue(INPUT_OPT);
            pc.setInputStr(inputStr);
            LOG.info("Input: " + inputStr);
            if (cmd.hasOption(LOCAL_OPT)) {
                File input = new File(pc.getInputStr());
                if (!input.exists()) {
                    exit("Input not found", 1);
                }
            }
        }

        // output string
        if (options.hasOption(OUTPUT_OPT)) {
            String outputStr;
            if (!(cmd.hasOption(OUTPUT_OPT) && cmd.getOptionValue(OUTPUT_OPT) != null)) {
                // output is an optional parameter
            } else {
                outputStr = cmd.getOptionValue(OUTPUT_OPT);
                pc.setOutputStr(outputStr);
                LOG.info("Output: " + outputStr);
            }
        }

        // local java application execution mode
        if (options.hasOption(LOCAL_OPT)) {
            // local/cluster
            if (cmd.hasOption(LOCAL_OPT)) {
                pc.setLocal(true);
                LOG.info("Execute application in local mode (default: hadoop job)");
            }
        }

        // distributed/pseudo-distributed
        if (options.hasOption(PSEUDO_OPT)) {
            if (cmd.hasOption(PSEUDO_OPT)) {
                pc.setPseudoDistributed(true);
                LOG.info("Execute Hadoop job in pseudo-distributed mode (default: distributed)");
            }
        }
        
        // configuration file path
        if (options.hasOption(PROPERTIESFILE_OPT)) {
            String configFilePath;
            if (!(cmd.hasOption(PROPERTIESFILE_OPT) && cmd.getOptionValue(PROPERTIESFILE_OPT) != null)) {
                // optional parameter
            } else {
                configFilePath = cmd.getOptionValue(PROPERTIESFILE_OPT);
                pc.setPropertiesFilePath(configFilePath);
            }
        }

    }

    public abstract String getUsage();

    public void exit(String msg, int status) {
        if (status > 0) {
            LOG.info(msg);
        } else {
            LOG.info(msg);
        }
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(this.getUsage(), options, true);
        System.exit(status);
    }

}
