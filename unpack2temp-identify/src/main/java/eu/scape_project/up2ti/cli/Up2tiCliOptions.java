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
package eu.scape_project.up2ti.cli;

import eu.scape_project.hawarp.cli.CliOptions;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Command line interface options.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class Up2tiCliOptions extends CliOptions {
    
    public String SPRING_FLG = "s";
    public String SPRING_OPT = "springconfig";
    public String SPRING_OPT_DESC = "Spring configuration XML file [optional].";
    
    public Up2tiCliOptions() {
        super();
        options.addOption(SPRING_FLG, SPRING_OPT, true, SPRING_OPT_DESC);
        options.addOption(OUTPUT_FLG, OUTPUT_OPT, true, OUTPUT_OPT_DESC);
        options.addOption(LOCAL_FLG, LOCAL_OPT, false, LOCAL_OPT_DESC);
    }
    
    public void initOptions(CommandLine cmd, Up2tiCliConfig pc) {
        
        super.initOptions(cmd, pc);
        
        // Spring config
        String springConfig;
        if (!(cmd.hasOption(SPRING_OPT) && cmd.getOptionValue(SPRING_OPT) != null)) {
            // optional parameter
        } else {
            springConfig = cmd.getOptionValue(SPRING_OPT);
            pc.setSpringConfig(springConfig);
            LOG.debug("Spring configuration : " + springConfig);
        }

        
    }

    @Override
    public String getUsage() {
        return "(java -jar|hadoop jar) hawarp/unpack2temp-identify/target/unpack2temp-identify-1.0-jar-with-dependencies.jar";
    }
    
}
