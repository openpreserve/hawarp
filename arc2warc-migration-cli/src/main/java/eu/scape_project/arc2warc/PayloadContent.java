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

import eu.scape_project.hawarp.utils.StreamUtils;
import eu.scape_project.hawarp.interfaces.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static eu.scape_project.hawarp.interfaces.Identifier.MIME_UNKNOWN;
import static eu.scape_project.hawarp.utils.IOUtils.BUFFER_SIZE;

/**
 * @author scape
 */
public class PayloadContent {

    private static final Log LOG = LogFactory.getLog(PayloadContent.class);
    private final long length;
    private final byte[] buffer;

    InputStream inputStream;

    private boolean consumed;

    String identifiedPayLoadType;

    private boolean identified;

    private Identifier identifier;

    private boolean doPayloadIdentification;

    private String digestStr;


    public PayloadContent(InputStream inputStream, long length, byte[] buffer) {
        this.length = length;
        this.buffer = buffer;
        identifiedPayLoadType = MIME_UNKNOWN;
        identified = false;
        consumed = false;
        identifier = null;
        doPayloadIdentification = false;
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
            throw new IllegalStateException(
                    "An identifier must be set before reading the payload content in order to get the identified payload type.");
        }
        return identifiedPayLoadType;
    }


    public InputStream getPayloadContentAsInputStream() throws IOException {
        if (length >= buffer.length) {
            File tempDir = org.apache.commons.io.FileUtils.getTempDirectory();
            final File tmp = File.createTempFile(RandomStringUtils.randomAlphabetic(10), "tmp", tempDir);
            tmp.deleteOnExit();
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(tmp);
                copyAndCheck(outputStream);
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
            return new FileInputStream(tmp);
        } else {
            final ByteBuffer wrap = ByteBuffer.wrap(buffer);
            wrap.clear();
            OutputStream outStream = StreamUtils.newOutputStream(wrap);
            copyAndCheck(outStream);
            wrap.flip();
            return StreamUtils.newInputStream(wrap);
        }
    }


    private void copyAndCheck(OutputStream outputStream) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("SHA-1 not known");
        }
        BufferedInputStream buffis = new BufferedInputStream(inputStream);
        BufferedOutputStream buffos = new BufferedOutputStream(outputStream);
        try {
            byte[] tempBuffer = new byte[BUFFER_SIZE];
            int bytesRead;
            boolean firstByteArray = true;
            while ((bytesRead = buffis.read(tempBuffer)) != -1) {
                md.update(tempBuffer, 0, bytesRead);
                buffos.write(tempBuffer, 0, bytesRead);
                if (doPayloadIdentification && firstByteArray && bytesRead > 0) {
                    identified = identifyPayloadType(tempBuffer);
                }
                firstByteArray = false;
            }
            digestStr = "sha1:" + calcDigest(md);
            consumed = true;
        } finally {
            IOUtils.closeQuietly(buffis);
            IOUtils.closeQuietly(buffos);
        }
    }

    private String calcDigest(MessageDigest md) {
        byte[] mdbytes = md.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte mdbyte : mdbytes) {
            hexString.append(Integer.toHexString(0xFF & mdbyte));
        }
        return hexString.toString();
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
