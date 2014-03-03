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

/**
 * Process configuration
 *
 * @author Sven Schlarb https://github.com/shsdev
 */
public class CliConfig implements Cloneable {

    private String inputDirStr;
    private String outputDirStr;
    
    private boolean local;
    private boolean contentTypeIdentification;
    private String inputPathRegexFilter;
    
    private boolean createCompressedWarc;
    
    private boolean localTestJob;
    
    private String arc2hwarMappingFilePath;

    /**
     * Empty constructor
     */
    public CliConfig() {

    }

    /**
     * Getter for the directories parameter
     *
     * @return Input directory
     */
    public String getInputDirStr() {
        return inputDirStr;
    }

    /**
     * Setter for the input directory parameter
     *
     * @param inputDirStr Input directory
     */
    public void setInputDirStr(String inputDirStr) {
        this.inputDirStr = inputDirStr;
    }

    /**
     * Getter for the directories parameter
     *
     * @return Output directory
     */
    public String getOutputDirStr() {
        return outputDirStr;
    }

    /**
     * Setter for the input directory parameter
     *
     * @param outputDirStr Output directory
     */
    public void setOutputDirStr(String outputDirStr) {
        this.outputDirStr = outputDirStr;
    }

    public boolean isContentTypeIdentification() {
        return contentTypeIdentification;
    }

    public void setContentTypeIdentification(boolean contentTypeIdentification) {
        this.contentTypeIdentification = contentTypeIdentification;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
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

    public boolean isLocalTestJob() {
        return localTestJob;
    }

    public void setLocalTestJob(boolean localTestJob) {
        this.localTestJob = localTestJob;
    }

    public String getArc2hwarMappingFilePath() {
        return arc2hwarMappingFilePath;
    }

    public void setArc2hwarMappingFilePath(String arc2hwarMappingFilePath) {
        this.arc2hwarMappingFilePath = arc2hwarMappingFilePath;
    }

    /**
     * Clone object
     *
     * @return cloned object
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public CliConfig clone() throws CloneNotSupportedException {
        try {
            return (CliConfig) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }

}
