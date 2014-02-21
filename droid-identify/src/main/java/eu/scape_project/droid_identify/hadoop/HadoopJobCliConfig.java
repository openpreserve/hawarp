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
package eu.scape_project.droid_identify.hadoop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Process configuration 
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class HadoopJobCliConfig implements Cloneable {
    
    private static final Log LOG = LogFactory.getLog(HadoopJobCliConfig.class);

    private String dirStr;
    private String hadoopJobName;

    public String getHadoopJobName() {
        return hadoopJobName;
    }

    public void setHadoopJobName(String hadoopJobName) {
        this.hadoopJobName = hadoopJobName;
    }

    /**
     * Empty constructor
     */
    public HadoopJobCliConfig() {
        
    }

    

    /**
     * Getter for the directories parameter
     * @return Directories parameter
     */
    public String getDirStr() {
        return dirStr;
    }

    /**
     * Setter for the directories parameter
     * @param dirStr Directories parameter
     */
    public void setDirStr(String dirsStr) {
        this.dirStr = dirsStr;
    }

    /**
     * Clone object
     * @return cloned object
     */
    @Override public HadoopJobCliConfig clone() {
        try {
            return (HadoopJobCliConfig) super.clone();
        } catch (CloneNotSupportedException ex) {
            LOG.error("CloneNotSupportedException:",ex);
            throw new AssertionError();
        }
    }
    
}
