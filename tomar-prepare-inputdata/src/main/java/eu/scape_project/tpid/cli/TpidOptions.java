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

import eu.scape_project.hawarp.cli.CliOptions;
import org.apache.commons.cli.CommandLine;

/**
 * Command line interface options.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class TpidOptions extends CliOptions {

    public String NPT_FLG = "n";
    public String NPT_OPT = "npt";
    public String NPT_OPT_DESC = "Number of items to be processed per task (e.g. 50). [optional, default: " + 50 + "].";
    
    public TpidOptions() {
        options.addOption(NPT_FLG, NPT_OPT, true, NPT_OPT_DESC);
        options.addOption(PSEUDO_FLG, PSEUDO_OPT, false, PSEUDO_OPT_DESC);
        options.addOption(PROPERTIESFILE_FLG, PROPERTIESFILE_OPT, true, PROPERTIESFILE_OPT_DESC);
    }

    public void initOptions(CommandLine cmd, TpidCliConfig pc) {

       super.initOptions(cmd, pc);

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
        LOG.debug("Number of items to be processed per invocation: " + pc.getNumItemsPerInvokation());

    }

    @Override
    public String getUsage() {
        return "(java -jar|hadoop jar) hawarp/tomar-prepare-inputdata/target/tomar-prepare-inputdata-1.0-jar-with-dependencies.jar";
    }
}
