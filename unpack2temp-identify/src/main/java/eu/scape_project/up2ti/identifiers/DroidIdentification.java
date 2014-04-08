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
package eu.scape_project.up2ti.identifiers;

import eu.scape_project.hawarp.utils.IOUtils;
import eu.scape_project.up2ti.container.ZipContainer;
import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.gov.nationalarchives.droid.core.BinarySignatureIdentifier;
import uk.gov.nationalarchives.droid.core.SignatureParseException;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResult;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

import org.springframework.core.io.Resource;

/**
 * Droid File Format Identification. File format identification using the Droid
 * version 6.1 API.
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
public class DroidIdentification extends Identification {

    private static final Log LOG = LogFactory.getLog(DroidIdentification.class);

    private BinarySignatureIdentifier bsi;

    /**
     * Disabled empty constructor
     */
    private DroidIdentification() {
    }

    /**
     * Constructor which initialises a given specified signature file
     *
     * @param sigFilePath
     */
    public DroidIdentification(Resource resource) throws IOException {
        try {
            InputStream is = resource.getInputStream();
            File tmpFile = IOUtils.copyInputStreamToTempFile(is, "DroidSignatureFile", ".xml");
            tmpFile.deleteOnExit();
            bsi = new BinarySignatureIdentifier();
            bsi.setSignatureFile(tmpFile.getAbsolutePath());
            bsi.init();
        } catch (SignatureParseException ex) {
            LOG.error("Signature parse error", ex);
        }
    }

    /**
     * Run droid identification on file
     *
     * @param filePath Absolute file path
     * @return Result list
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public HashMap<String, String> identify(File file) throws FileNotFoundException {
        HashMap<String, String> droidIdRes = new HashMap<String, String>();
        InputStream in = null;
        IdentificationRequest request = null;

        try {
            URI resourceUri = file.toURI();
            in = new FileInputStream(file);
            LOG.debug("Identification of resource: " + resourceUri.toString());
            RequestMetaData metaData = new RequestMetaData(file.length(), file.lastModified(), file.getName());
            LOG.debug("File length: " + file.length());
            LOG.debug("File modified: " + file.lastModified());
            LOG.debug("File name: " + file.getName());
            RequestIdentifier identifier = new RequestIdentifier(resourceUri);
            request = new FileSystemIdentificationRequest(metaData, identifier);
            request.open(in);
            IdentificationResultCollection results = bsi.matchBinarySignatures(request);
            bsi.removeLowerPriorityHits(results);
            if (results == null || results.getResults() == null || results.getResults().isEmpty()) {
                LOG.debug("No identification result");
            } else {
                List<IdentificationResult> result = results.getResults();
                if (result != null && !result.isEmpty()) {
                    for (IdentificationResult ir : result) {
                        String mime = ir.getMimeType();
                        if (mime != null && !mime.isEmpty()) {
                            // take first mime, ignore others
                            if (!droidIdRes.containsKey("mime")) {
                                droidIdRes.put("mime", mime);
                            }
                        }
                        String puid = ir.getPuid();
                        if (puid != null && !puid.isEmpty()) {
                            // take first puid, ignore others
                            if (!droidIdRes.containsKey("puid")) {
                                droidIdRes.put("puid", puid);
                            }
                        }
                    }
                }
            }
            in.close();
            request.close();
        } catch (IOException ex) {
            LOG.error("I/O Exception", ex);
        } finally {
            try {
                in.close();
                request.close();
            } catch (IOException _) {
            }
        }
        if (!droidIdRes.containsKey("mime")) {
            droidIdRes.put("mime", "application/octet-stream");
        }
        if (!droidIdRes.containsKey("puid")) {
            droidIdRes.put("puid", "fmt/0");
        }
        return droidIdRes;
    }
}
