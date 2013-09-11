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
package eu.scape_project.tb.cipex.cli;

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
    public static final String DIR_OPT_DESC = "Directory with container file(s) [required].";
    
    
    public static final String SPRING_FLG = "s";
    public static final String SPRING_OPT = "springconfig";
    public static final String SPRING_OPT_DESC = "Spring configuration XML file [optional].";
    
    
    public static final String MAPREDUCE_FLG = "m";
    public static final String MAPREDUCE_OPT = "mapreduce";
    public static final String MAPREDUCE_OPT_DESC = "Start Hadoop Map Reduce job [optional].";
    
    
    public static org.apache.commons.cli.Options OPTIONS = new org.apache.commons.cli.Options();
    public static final String USAGE = "java jar "
            + "target/cipex-1.0-SNAPSHOT-jar-with-dependencies.jar "
            + "";
    static {
        OPTIONS.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
        OPTIONS.addOption(DIR_FLG, DIR_OPT, true, DIR_OPT_DESC);
        OPTIONS.addOption(SPRING_FLG, SPRING_OPT, true, SPRING_OPT_DESC);
        OPTIONS.addOption(MAPREDUCE_FLG, MAPREDUCE_OPT, false, MAPREDUCE_OPT_DESC);
    }
    
    public static void initOptions(CommandLine cmd, CliConfig pc) {
        // dir
        String dirStr;
        if (!(cmd.hasOption(DIR_OPT) && cmd.getOptionValue(DIR_OPT) != null)) {
            exit("No directory given.", 1);
        } else {
            dirStr = cmd.getOptionValue(DIR_OPT);
            pc.setDirStr(dirStr);
            logger.debug("Directory: " + dirStr);
        }
        // dir
        String springConfig;
        if (!(cmd.hasOption(SPRING_OPT) && cmd.getOptionValue(SPRING_OPT) != null)) {
            // optional parameter
        } else {
            springConfig = cmd.getOptionValue(SPRING_OPT);
            pc.setSpringConfig(springConfig);
            logger.debug("Spring configuration : " + springConfig);
        }
        // mapreduce
        if (cmd.hasOption(MAPREDUCE_OPT)) {
            pc.setMapReduceJob(true);
            logger.debug("Application will be started as MapReduce job");
        }
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
