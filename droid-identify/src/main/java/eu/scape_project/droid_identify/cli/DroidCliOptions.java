/*
 * Copyright 2014 onbscs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.scape_project.droid_identify.cli;

import eu.scape_project.hawarp.cli.CliOptions;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author onbscs
 */
public class DroidCliOptions extends CliOptions {

    @Override
    public String getUsage() {
        return "(java -jar|hadoop jar) hawarp/droid-identify/target/droid-identify-1.0-jar-with-dependencies.jar";
    }
    
    public DroidCliOptions() {
        super();
        options.addOption(OUTPUT_FLG, OUTPUT_OPT, true, OUTPUT_OPT_DESC);
        options.addOption(LOCAL_FLG, LOCAL_OPT, false, LOCAL_OPT_DESC);
        options.addOption(PSEUDO_FLG, PSEUDO_OPT, false, PSEUDO_OPT_DESC);
    }

    public void initOptions(CommandLine cmd, DroidCliConfig pc) {

        super.initOptions(cmd, pc);

    }

}
