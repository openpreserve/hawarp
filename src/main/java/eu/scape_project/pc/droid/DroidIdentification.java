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
package eu.scape_project.pc.droid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;

import uk.gov.nationalarchives.droid.core.interfaces.DroidCore;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationMethod;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultImpl;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;
import uk.gov.nationalarchives.droid.core.signature.ByteReader;
import uk.gov.nationalarchives.droid.core.signature.FileFormat;
import uk.gov.nationalarchives.droid.core.signature.FileFormatCollection;
import uk.gov.nationalarchives.droid.core.signature.FileFormatHit;
import uk.gov.nationalarchives.droid.core.signature.droid6.FFSignatureFile;
import uk.gov.nationalarchives.droid.submitter.FileIdentificationRequestFactory;

/**
 * Droid File Format Identification Hadoop Job.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class DroidIdentification {
    
    public static final String SIGNATURE_FILE_V67_URL = "http://www.nationalarchives.gov.uk/documents/DROID_SignatureFile_V67.xml";
    
    private String sigFilePath;
    
    private BinarySignatureIdentifier bsi;
    
    public DroidIdentification() throws IOException, SignatureParseException {
        
        URL sigFileV67Url = new URL(SIGNATURE_FILE_V67_URL);
        InputStream sigFileStream = sigFileV67Url.openStream();
        File tmpSigFile = File.createTempFile("tmpsigfile", ".xml");
        FileOutputStream fos = new FileOutputStream(tmpSigFile);
        IOUtils.copy(sigFileStream, fos);
        fos.close();
        sigFilePath = tmpSigFile.getAbsolutePath();
        this.init();
    }
    
    public DroidIdentification(String sigFilePath) throws SignatureParseException {
        this.sigFilePath = sigFilePath;
        this.init();
    }
    
    private void init() throws SignatureParseException {
        bsi = new BinarySignatureIdentifier();
        bsi.setSignatureFile(sigFilePath);
        bsi.init();
    }
    
    public IdentificationResult identify(String filePath) throws FileNotFoundException, IOException {
        File file = new File(filePath);
        URI resourceUri = file.toURI();
        InputStream in = new FileInputStream(file);
        
        RequestMetaData metaData = new RequestMetaData(file.length(), file.lastModified(),file.getName());
        RequestIdentifier identifier = new RequestIdentifier(resourceUri);
        identifier.setParentId(1L);
        IdentificationRequest request = new FileSystemIdentificationRequest(metaData, identifier);
        request.open(in);
        IdentificationResultCollection results = bsi.matchBinarySignatures(request);
        
        IdentificationResult result = results.getResults().iterator().next();
        return result;
    }
}
