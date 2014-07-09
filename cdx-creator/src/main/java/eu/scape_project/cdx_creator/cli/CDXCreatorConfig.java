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
package eu.scape_project.cdx_creator.cli;

import eu.scape_project.hawarp.cli.CliConfig;

/**
 * Process configuration
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class CDXCreatorConfig extends CliConfig {

    private String inputPathRegexFilter;
    private boolean directoryInput;
    private String cdxfileCsColumns;
    private String cdxfileCsHeader;
    private boolean createPayloadDigest;

    /**
     * Empty constructor
     */
    public CDXCreatorConfig() {

    }

    public String getInputPathRegexFilter() {
        return inputPathRegexFilter;
    }

    public void setInputPathRegexFilter(String inputPathRegexFilter) {
        this.inputPathRegexFilter = inputPathRegexFilter;
    }

    public boolean isDirectoryInput() {
        return directoryInput;
    }

    public void setDirectoryInput(boolean directoryInput) {
        this.directoryInput = directoryInput;
    }

    public String getCdxfileCsColumns() {
        return cdxfileCsColumns;
    }

    public void setCdxfileCsColumns(String cdxfileCsColumns) {
        this.cdxfileCsColumns = cdxfileCsColumns;
    }

    public String getCdxfileCsHeader() {
        return cdxfileCsHeader;
    }

    public void setCdxfileCsHeader(String cdxfileCsHeader) {
        this.cdxfileCsHeader = cdxfileCsHeader;
    }

    public boolean isCreatePayloadDigest() {
        return createPayloadDigest;
    }

    public void setCreatePayloadDigest(boolean createPayloadDigest) {
        this.createPayloadDigest = createPayloadDigest;
    }
    
}
