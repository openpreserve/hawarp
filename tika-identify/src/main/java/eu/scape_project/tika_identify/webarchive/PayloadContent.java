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
package eu.scape_project.tika_identify.webarchive;

import static eu.scape_project.tika_identify.identification.IdentificationConstants.*;
import static eu.scape_project.hawarp.utils.IOUtils.BUFFER_SIZE;
import eu.scape_project.tika_identify.identification.Identifier;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    public void readPayloadContent() throws IOException {
        payloadBytes = inputStreamToByteArray();
        if (inputStream.read() == -1) {
            consumed = true;
        }
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

    private byte[] inputStreamToByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream buffis = new BufferedInputStream(inputStream);
        BufferedOutputStream buffos = new BufferedOutputStream(baos);
        byte[] tempBuffer = new byte[BUFFER_SIZE];
        int bytesRead;
        boolean firstByteArray = true;
        while ((bytesRead = buffis.read(tempBuffer)) != -1) {
            buffos.write(tempBuffer, 0, bytesRead);
            if (doPayloadIdentification && firstByteArray && tempBuffer != null && bytesRead > 0) {
                identified = identifyPayloadType(tempBuffer);
            }
            firstByteArray = false;
        }
        buffis.close();
        buffos.flush();
        buffos.close();

        return baos.toByteArray();
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

    public byte[] getPayloadBytes() {
        if (!isConsumed()) {
            throw new IllegalStateException("Input stream must be read before accessing content");
        }
        return payloadBytes;
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

}
