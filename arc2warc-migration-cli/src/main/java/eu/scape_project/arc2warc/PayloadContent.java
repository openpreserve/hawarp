/*
 * Copyright 2014 scape.
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
package eu.scape_project.arc2warc;

import static eu.scape_project.hawarp.utils.IOUtils.BUFFER_SIZE;
import eu.scape_project.hawarp.interfaces.Identifier;
import static eu.scape_project.hawarp.interfaces.Identifier.MIME_UNKNOWN;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author scape
 */
public class PayloadContent {

    private static final Log LOG = LogFactory.getLog(PayloadContent.class);

    InputStream inputStream;

    private boolean consumed;

    String identifiedPayLoadType;

    private boolean identified;

    private byte[] payloadBytes;

    private Identifier identifier;

    private boolean doPayloadIdentification;

    private String digestStr;

    private PayloadContent() {
        identifiedPayLoadType = MIME_UNKNOWN;
        identified = false;
        consumed = false;
        identifier = null;
        doPayloadIdentification = false;
    }

    public PayloadContent(InputStream inputStream) {
        this();
        this.inputStream = inputStream;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public String getIdentifiedPayLoadType() {
        if (!isConsumed()) {
            throw new IllegalStateException("Input stream must be read before accessing the payload type.");
        }
        if (identifier == null) {
            throw new IllegalStateException("An identifier must be set before reading the payload content in order to get the identified payload type.");
        }
        return identifiedPayLoadType;
    }

    public byte[] getPayloadContentAsByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        checkedInputStreamToOutputStream(baos);
        return baos.toByteArray();
    }

    public InputStream getDumpedPayloadContentAsInputStream() throws IOException {
        File tempDir = org.apache.commons.io.FileUtils.getTempDirectory();
        File tempFile = File.createTempFile(RandomStringUtils.randomAlphabetic(10), "tmp", tempDir);
        FileOutputStream outputStream = new FileOutputStream(tempFile);
        checkedInputStreamToOutputStream(outputStream);
        outputStream.close();
        FileInputStream fis = new FileInputStream(tempFile);
        return fis;
    }

    private void checkedInputStreamToOutputStream(OutputStream outputStream) throws IOException {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            LOG.warn("Message digest algorithm not available", ex);
        }
        BufferedInputStream buffis = new BufferedInputStream(inputStream);
        BufferedOutputStream buffos = new BufferedOutputStream(outputStream);
        byte[] tempBuffer = new byte[BUFFER_SIZE];
        int bytesRead;
        boolean firstByteArray = true;
        while ((bytesRead = buffis.read(tempBuffer)) != -1) {
            if (md != null) {
                md.update(tempBuffer, 0, bytesRead);
            }
            buffos.write(tempBuffer, 0, bytesRead);
            if (doPayloadIdentification && firstByteArray && tempBuffer != null && bytesRead > 0) {
                identified = identifyPayloadType(tempBuffer);
            }
            firstByteArray = false;
        }
        if (md != null) {
            byte[] mdbytes = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < mdbytes.length; i++) {
                hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }
            digestStr = "sha1:" + hexString.toString();
        }
        buffis.close();
        buffos.flush();
        buffos.close();
        consumed = true;
    }

    private boolean identifyPayloadType(byte[] prefix) {
        if (identifier != null) {
            identifiedPayLoadType = identifier.identify(prefix);
            return true;
        } else {
            return false;
        }
    }

    public boolean isIdentified() {
        return identified;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public boolean doPayloadIdentification() {
        return doPayloadIdentification;
    }

    public void doPayloadIdentification(boolean doPayloadIdentification) {
        this.doPayloadIdentification = doPayloadIdentification;
    }

    public String getDigestStr() {
        if (!isConsumed()) {
            throw new IllegalStateException("Input stream must be read before digest string is available");
        }
        return digestStr;
    }

}
