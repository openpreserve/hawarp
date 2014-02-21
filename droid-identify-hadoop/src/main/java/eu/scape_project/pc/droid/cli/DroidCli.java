/*
 *  Copyright 2011 The SCAPE Project Consortium.
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
package eu.scape_project.pc.droid.cli;

import eu.scape_project.pc.droid.DroidIdentification;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.droid.core.SignatureParseException;

/**
 * Tika command line application
 *
 * @author shsdev https://github.com/shsdev
 * @version 0.2
 */
public class DroidCli {

    // Logger instance
    private static Logger logger = LoggerFactory.getLogger(DroidCli.class.getName());
    // Statics to set up command line arguments
    private static final String HELP_FLG = "h";
    private static final String HELP_OPT = "help";
    private static final String HELP_OPT_DESC = "print this message.";
    private static final String DIR_FLG = "d";
    private static final String DIR_OPT = "dir";
    private static final String DIR_OPT_DESC = "directory containing files.";
    // Static for command line option parsing
    private static Options OPTIONS = new Options();
    private DroidIdentification dihj;
    
    private HashMap<String, Integer> result;
    
    private static int counter = 0;

    static {
        OPTIONS.addOption(HELP_FLG, HELP_OPT, false, HELP_OPT_DESC);
        OPTIONS.addOption(DIR_FLG, DIR_OPT, true, DIR_OPT_DESC);

    }

    public DroidCli() {

        try {
            dihj = new DroidIdentification();
            result = new HashMap<String, Integer>();

        } catch (SignatureParseException ex) {
            logger.error("Error parsing signature file", ex);
        } catch (IOException ex) {
            logger.error("File not found error", ex);
        }
    }

    public static void main(String[] args) {
        // Static for command line option parsing
        DroidCli tc = new DroidCli();

        CommandLineParser cmdParser = new PosixParser();
        try {
            CommandLine cmd = cmdParser.parse(OPTIONS, args);
            if ((args.length == 0) || (cmd.hasOption(HELP_OPT))) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(Constants.USAGE, OPTIONS, true);
                System.exit(0);
            } else {
                if (cmd.hasOption(DIR_OPT) && cmd.getOptionValue(DIR_OPT) != null) {
                    String dirStr = cmd.getOptionValue(DIR_OPT);
                    logger.info("Directory: " + dirStr);

                    // *** start timer
                    long startClock = System.currentTimeMillis();
                    try {
                        tc.processFiles(new File(dirStr));

                    } catch (FileNotFoundException ex) {
                        logger.error("File not found", ex);
                    } catch (IOException ex) {
                        logger.error("I/O Exception", ex);
                    }

                    // *** stop timer
                    long elapsedTimeMillis = System.currentTimeMillis() - startClock;
                    logger.info("Identification finished after "+elapsedTimeMillis+" milliseconds");

                } else {
                    logger.error("No directory given.");
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(Constants.USAGE, OPTIONS, true);
                    System.exit(1);
                }
            }
        } catch (ParseException ex) {
            logger.error("Problem parsing command line arguments.", ex);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Constants.USAGE, OPTIONS, true);
            System.exit(1);
        }
    }

    private void processFiles(File path) throws FileNotFoundException, IOException {

        if (path.isDirectory()) {
            String[] children = path.list();
            for (int i = 0; i < children.length; i++) {
                processFiles(new File(path, children[i]));
            }
        } else {
            processFile(path);
        }
    }

    private synchronized void processFile(File path) throws FileNotFoundException, IOException {
        if (dihj != null) {
            String puid = dihj.identify(path.toString());
            counter++;
            System.out.println(counter + "\t" + path.toString() + "\t" +puid);
        } else {
            logger.error("Droid identifier not available");
        }

    }
}
