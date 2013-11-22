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
package eu.scape_project.spacip.cli;

import static eu.scape_project.spacip.Spacip.DEFAULT_ITEMS_PER_INVOCATION;
import static eu.scape_project.spacip.Spacip.DEFAULT_OUTPUT_FILE_SUFFIX;
import static eu.scape_project.spacip.Spacip.DEFAULT_SCAPE_PLATFORM_INVOKE;
import static eu.scape_project.spacip.Spacip.DEFAULT_UNPACK_HDFS_PATH;
import static eu.scape_project.spacip.Spacip.DEFAULT_JOBOUTPUT_HDFS_PATH;
import static eu.scape_project.spacip.Spacip.DEFAULT_TOOLOUTPUT_HDFS_PATH;
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
    public static final String NPT_OPT_DESC = "Number of items to be processed per task (e.g. 50). [optional, default: " + DEFAULT_ITEMS_PER_INVOCATION + "].";

    public static final String SPI_FLG = "i";
    public static final String SPI_OPT = "sci";
    public static final String SPI_OPT_DESC = "Scape platform invocation command (e.g. 'fits dirxml'). [optional, default: '" + DEFAULT_SCAPE_PLATFORM_INVOKE + "'].";

    public static final String OFS_FLG = "e";
    public static final String OFS_OPT = "ofs";
    public static final String OFS_OPT_DESC = "output file suffix (e.g. '.fits.xml'). [optional, default: '" + DEFAULT_OUTPUT_FILE_SUFFIX + "'].";

    public static final String UHP_FLG = "u";
    public static final String UHP_OPT = "ofs";
    public static final String UHP_OPT_DESC = "Unpack hdfs path (e.g. '/user/name/unpacked/'). [optional, default '" + DEFAULT_UNPACK_HDFS_PATH + "'].";
    
    public static final String THP_FLG = "t";
    public static final String THP_OPT = "thp";
    public static final String THP_OPT_DESC = "Tool output hdfs path (e.g. '/user/name/tooloutput/'). [optional, default '" + DEFAULT_TOOLOUTPUT_HDFS_PATH + "'].";

    public static final String JOP_FLG = "o";
    public static final String JOP_OPT = "jop";
    public static final String JOP_OPT_DESC = "Job output hdfs path (e.g. '/user/name/joboutput/'). [optional, default: '" + DEFAULT_JOBOUTPUT_HDFS_PATH + "'].";

    public static org.apache.commons.cli.Options OPTIONS = new org.apache.commons.cli.Options();
    public static final String USAGE = "java jar "
            + "target/spacip-1.0-SNAPSHOT-jar-with-dependencies.jar "
            + "";

    static {
        OPTIONS.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
        OPTIONS.addOption(DIR_FLG, DIR_OPT, true, DIR_OPT_DESC);
        OPTIONS.addOption(NPT_FLG, NPT_OPT, true, NPT_OPT_DESC);
        OPTIONS.addOption(SPI_FLG, SPI_OPT, true, SPI_OPT_DESC);
        OPTIONS.addOption(OFS_FLG, OFS_OPT, true, OFS_OPT_DESC);
        OPTIONS.addOption(UHP_FLG, UHP_OPT, true, UHP_OPT_DESC);
        OPTIONS.addOption(JOP_FLG, JOP_OPT, true, JOP_OPT_DESC);
        OPTIONS.addOption(THP_FLG, THP_OPT, true, THP_OPT_DESC);
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
            pc.setNumItemsPerInvokation(DEFAULT_ITEMS_PER_INVOCATION);
        } else {
            try {
                numItemsPerInvokation = Integer.parseInt(cmd.getOptionValue(NPT_OPT));
                pc.setNumItemsPerInvokation(numItemsPerInvokation);
            } catch (NumberFormatException ex) {
                exit("Parameter 'Number of items to be processed per invocation' is not a valid number.", 1);
            }
            
        }
        logger.debug("Number of items to be processed per invocation: " + pc.getNumItemsPerInvokation());

        // scape platform invoke
        String scapePlatformInvoke;
        if (!(cmd.hasOption(SPI_OPT) && cmd.getOptionValue(SPI_OPT) != null)) {
            pc.setScapePlatformInvoke(DEFAULT_SCAPE_PLATFORM_INVOKE);
        } else {
            scapePlatformInvoke = cmd.getOptionValue(SPI_OPT);
            pc.setScapePlatformInvoke(scapePlatformInvoke);
        }
        logger.debug("Scape platform invoke: " + pc.getScapePlatformInvoke());

        // output file suffix
        String outputFileSuffix;
        if (!(cmd.hasOption(OFS_OPT) && cmd.getOptionValue(OFS_OPT) != null)) {
            pc.setOutputFileSuffix(DEFAULT_OUTPUT_FILE_SUFFIX);
        } else {
            outputFileSuffix = cmd.getOptionValue(OFS_OPT);
            pc.setOutputFileSuffix(outputFileSuffix);
        }
        logger.debug("Ouput file suffix: " + pc.getOutputFileSuffix());

        // unpack hdfs path
        String unpackHdfsPath;
        if (!(cmd.hasOption(UHP_OPT) && cmd.getOptionValue(UHP_OPT) != null)) {
            pc.setUnpackHdfsPath(DEFAULT_UNPACK_HDFS_PATH);
        } else {
            unpackHdfsPath = cmd.getOptionValue(UHP_OPT);
            pc.setUnpackHdfsPath(unpackHdfsPath);
        }
        logger.debug("Unpack hdfs path: " + pc.getUnpackHdfsPath());
        
        
        // joboutput hdfs path
        String joboutputHdfsPath;
        if (!(cmd.hasOption(JOP_OPT) && cmd.getOptionValue(JOP_OPT) != null)) {
            pc.setJoboutputHdfsPath(DEFAULT_JOBOUTPUT_HDFS_PATH);
        } else {
            joboutputHdfsPath = cmd.getOptionValue(JOP_OPT);
            pc.setJoboutputHdfsPath(joboutputHdfsPath);
        }
        logger.debug("Joboutput hdfs path: " + pc.getJoboutputHdfsPath());

        // tooloutput hdfs path
        String tooloutputHdfsPath;
        if (!(cmd.hasOption(THP_OPT) && cmd.getOptionValue(THP_OPT) != null)) {
            pc.setTooloputHdfsPath(DEFAULT_TOOLOUTPUT_HDFS_PATH);
        } else {
            tooloutputHdfsPath = cmd.getOptionValue(THP_OPT);
            pc.setTooloputHdfsPath(tooloutputHdfsPath);
        }
        logger.debug("Joboutput hdfs path: " + pc.getTooloputHdfsPath());

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
