/*
 * Copyright 2014 onbscs.
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
package eu.scape_project.hawarp.utils;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public final class UUIDGenerator {

    private static final String PREFIX = "urn:uuid:";

    public static URI getRecordID() throws URISyntaxException {
        return new URI(PREFIX + UUID.randomUUID().toString());
    }


    public static URI getRecordID(File file, long offset) throws URISyntaxException {
        return new URI(PREFIX + UUID.nameUUIDFromBytes((file.getName() + "," + offset).getBytes()).toString());
    }

    public static URI getRecordID(String name) throws URISyntaxException {
        return new URI(PREFIX + UUID.nameUUIDFromBytes(name.getBytes()).toString());
    }
}