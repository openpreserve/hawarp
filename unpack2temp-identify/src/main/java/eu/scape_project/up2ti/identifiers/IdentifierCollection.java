/*
 * Copyright 2012 The SCAPE Project Consortium.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * under the License.
 */

package eu.scape_project.up2ti.identifiers;

import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sven Schlarb https://github.com/shsdev
 * @version 0.1
 */
@Component
public class IdentifierCollection {
    
    private static final Log LOG = LogFactory.getLog(IdentifierCollection.class);

    private Collection<Identification> identifiers;

    public Collection<Identification> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Collection<Identification> identifiers) {
        this.identifiers = identifiers;
    }
    
}

