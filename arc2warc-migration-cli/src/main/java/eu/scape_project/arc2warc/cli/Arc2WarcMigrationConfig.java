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
package eu.scape_project.arc2warc.cli;

import eu.scape_project.hawarp.cli.CliConfig;

/**
 * Process configuration
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class Arc2WarcMigrationConfig extends CliConfig {

    private boolean contentTypeIdentification;
    private String inputPathRegexFilter;
    private boolean createCompressedWarc;
    private boolean directoryInput;

    /**
     * Empty constructor
     */
    public Arc2WarcMigrationConfig() {

    }

    public boolean isContentTypeIdentification() {
        return contentTypeIdentification;
    }

    public void setContentTypeIdentification(boolean contentTypeIdentification) {
        this.contentTypeIdentification = contentTypeIdentification;
    }

    public String getInputPathRegexFilter() {
        return inputPathRegexFilter;
    }

    public void setInputPathRegexFilter(String inputPathRegexFilter) {
        this.inputPathRegexFilter = inputPathRegexFilter;
    }

    public boolean createCompressedWarc() {
        return createCompressedWarc;
    }

    public void setCreateCompressedWarc(boolean createCompressedWarc) {
        this.createCompressedWarc = createCompressedWarc;
    }

    public boolean isDirectoryInput() {
        return directoryInput;
    }

    public void setDirectoryInput(boolean directoryInput) {
        this.directoryInput = directoryInput;
    }

}
