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
package eu.scape_project.hawarp.cli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CLI configuration
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public abstract class CliConfig implements Cloneable {

    protected String inputStr;
    protected boolean local;
    protected boolean pseudoDistributed;

    protected static final Log LOG = LogFactory.getLog(CliConfig.class);

    /**
     * Empty constructor
     */
    public CliConfig() {

    }

    public String getInputStr() {
        return inputStr;
    }

    public void setInputStr(String inputStr) {
        this.inputStr = inputStr;
    }

    /**
     * Execute in local/cluster mode
     *
     * @return local/cluster mode
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * Execute in local/cluster mode
     *
     * @param local local/cluster mode
     */
    public void setLocal(boolean local) {
        this.local = local;
    }

    /**
     * Execute hadoop job in pseudo-distributed mode. Does not apply if the
     * application is executed in local application mode (parameter -l).
     *
     * @return distributed/pseudo-distributed
     */
    public boolean isPseudoDistributed() {
        return pseudoDistributed;
    }

    /**
     * Execute hadoop job in pseudo-distributed mode. Does not apply if the
     * application is executed in local application mode (parameter -l).
     *
     * @param pseudoDistributed distributed/pseudo-distributed
     */
    public void setPseudoDistributed(boolean pseudoDistributed) {
        this.pseudoDistributed = pseudoDistributed;
    }

}
