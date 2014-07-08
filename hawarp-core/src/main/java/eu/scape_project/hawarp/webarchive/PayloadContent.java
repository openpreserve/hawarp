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
package eu.scape_project.hawarp.webarchive;

import eu.scape_project.hawarp.interfaces.Identifier;
import static eu.scape_project.hawarp.interfaces.Identifier.MIME_UNKNOWN;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    private boolean error;

    private PayloadContent() {
        identifiedPayLoadType = MIME_UNKNOWN;
        identified = false;
        consumed = false;
        identifier = null;
        doPayloadIdentification = false;
        error = false;
    }

    public PayloadContent(InputStream inputStream) {
        this();
        this.inputStream = inputStream;
    }

    public void readPayloadContent() {
        try {
            payloadBytes = inputStreamToByteArray();
            if (inputStream.read() == -1) {
                consumed = true;
            }
        } catch (IOException ex) {
            LOG.error("I/O Exception while checking stream", ex);
            this.error = true;
        } 
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public String getIdentifiedPayLoadType() {
        if(isError()) {
            throw new IllegalStateException("Unable to get payload bytes, an error occurred while reading the content");
        }
        if (!isConsumed()) {
            throw new IllegalStateException("Input stream must be read before accessing the payload type.");
        }
        if (identifier == null) {
            throw new IllegalStateException("An identifier must be set before reading the payload content in order to get the identified payload type.");
        }
        return identifiedPayLoadType;
    }

    private byte[] inputStreamToByteArray() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedInputStream buffis = new BufferedInputStream(inputStream);
            BufferedOutputStream buffos = new BufferedOutputStream(baos);
            byte[] tempBuffer = new byte[8192];
            int bytesRead;
            boolean firstByteArray = true;
            while ((bytesRead = buffis.read(tempBuffer)) != -1) {
                buffos.write(tempBuffer, 0, bytesRead);
                if (doPayloadIdentification && firstByteArray && tempBuffer != null && bytesRead > 0) {
                    identified = identifyPayloadType(tempBuffer);
                }
                firstByteArray = false;
            }
            //buffis.close();
            buffos.flush();
            buffos.close();
            
            return baos.toByteArray();
        } catch (IOException ex) {
            LOG.error("Error while trying to read payload content", ex);
            this.error = true;
            return null;
        } 
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

    public byte[] getPayloadBytes() throws IllegalStateException {
        if(isError()) {
            throw new IllegalStateException("Unable to get payload bytes, an error occurred while reading the content");
        }
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

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}
