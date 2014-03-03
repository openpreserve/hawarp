/*
 * Copyright 2012 The SCAPE Project Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */
package eu.scape_project.arc2warc;

import eu.scape_project.arc2warc.cli.Config;
import eu.scape_project.arc2warc.cli.Options;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.scape_project.hawarp.utils.RegexUtils;

import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.cli.ParseException;

/**
 * ARC to WARC conversion.
 *
 * @author Sven Schlarb <https://github.com/shsdev>
 */
public class Arc2WarcMigration {

    private static final Log LOG = LogFactory.getLog(Arc2WarcMigration.class);

    private static Config config;

    public Arc2WarcMigration() {
    }

    public static Config getConfig() {
        return config;
    }

    /**
     * Main entry point.
     *
     * @param args
     * @throws java.io.IOException
     * @throws org.apache.commons.cli.ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        Configuration conf = new Configuration();
        // Command line interface
        config = new Config();
        CommandLineParser cmdParser = new PosixParser();
        GenericOptionsParser gop = new GenericOptionsParser(conf, args);
        CommandLine cmd = cmdParser.parse(Options.OPTIONS, gop.getRemainingArgs());
        if ((args.length == 0) || (cmd.hasOption(Options.HELP_OPT))) {
            Options.exit("Usage", 0);
        } else {
            Options.initOptions(cmd, config);
        }
        Arc2WarcMigration a2wm = new Arc2WarcMigration();
        long startMillis = System.currentTimeMillis();
        File input = new File(config.getInputStr());
        if (config.isDirectoryInput()) {
            a2wm.traverseDir(input);
        } else {
            ArcMigrator arcMigrator = new ArcMigrator(config, input);
            arcMigrator.migrate();
        }
        long elapsedTimeMillis = System.currentTimeMillis() - startMillis;
        LOG.info("Processing time (sec): " + elapsedTimeMillis / 1000F);
        System.exit(0);
    }

    /**
     * Traverse the root directory recursively
     *
     * @param dir Root directory
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void traverseDir(File dirStructItem) {
        if (dirStructItem.isDirectory()) {
            String[] children = dirStructItem.list();
            for (String child : children) {
                traverseDir(new File(dirStructItem, child));
            }
        } else if (!dirStructItem.isDirectory()) {
            String filePath = dirStructItem.getAbsolutePath();
            if (RegexUtils.pathMatchesRegexFilter(filePath, config.getInputPathRegexFilter())) {
                ArcMigrator arcMigrator = new ArcMigrator(config, dirStructItem);
                arcMigrator.migrate();
            }
        }
    }

}
