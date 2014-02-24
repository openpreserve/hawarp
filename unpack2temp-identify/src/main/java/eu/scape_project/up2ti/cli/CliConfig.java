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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Process configuration
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class CliConfig implements Cloneable {
    
    private static final Log LOG = LogFactory.getLog(CliConfig.class);

    private String dirStr;

    private String springConfig;

    private boolean mapReduceJob;

    /**
     * Empty constructor
     */
    public CliConfig() {

    }

    public boolean isMapReduceJob() {
        return mapReduceJob;
    }

    public void setMapReduceJob(boolean mapReduceJob) {
        this.mapReduceJob = mapReduceJob;
    }


    /**
     * Getter for the directories parameter
     *
     * @return Directories parameter
     */
    public String getDirStr() {
        return dirStr;
    }

    /**
     * Setter for the directories parameter
     *
     * @param dirStr Directories parameter
     */
    public void setDirStr(String dirsStr) {
        this.dirStr = dirsStr;
    }

    public String getSpringConfig() {
        return springConfig;
    }

    public void setSpringConfig(String springConfig) {
        this.springConfig = springConfig;
    }

    /**
     * Clone object
     *
     * @return cloned object
     */
    @Override
    public CliConfig clone() {
        try {
            return (CliConfig) super.clone();
        } catch (CloneNotSupportedException ex) {
            LOG.error("CloneNotSupportedException:", ex);
            throw new AssertionError();
        }
    }

}
