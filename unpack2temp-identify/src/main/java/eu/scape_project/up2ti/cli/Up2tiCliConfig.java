/*
 *  Copyright 2012 The SCAPE Project Consortium.
 * 
 *  Licensed under the Apache License; Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing; software
 *  distributed under the License is distributed on an "AS IS" BASIS;
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND; either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package eu.scape_project.up2ti.cli;

import eu.scape_project.hawarp.cli.CliConfig;


/**
 * Process configuration
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class Up2tiCliConfig extends CliConfig {

    private String springConfig;
    
    private String outputDir;

    public String getSpringConfig() {
        return springConfig;
    }

    public void setSpringConfig(String springConfig) {
        this.springConfig = springConfig;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

}
