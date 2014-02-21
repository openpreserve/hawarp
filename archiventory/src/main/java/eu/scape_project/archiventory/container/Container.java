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
package eu.scape_project.archiventory.container;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

/**
 * Interface which must be implemented by a container.
 *
 * @author onbscs
 */
public interface Container {

    /**
     * Initalise container data structure. A container must initialise a
     * bidirectional map (DualHashBidiMap) by reading items from the container
     * input stream. The bidirectional map must use some item identifier as key
     * and a temporary file name pointing to an existing file as value. The data
     * structure must be initialised before the bidirectional HashMap is
     * accessed, see {@link #getBidiIdentifierFilenameMap()}.
     *
     * @param containerFileName File name of the container file
     * @param containerFileStream File stream of the container file
     * @throws IOException
     */
    public void init(String containerFileName, InputStream containerFileStream) throws IOException;

    /**
     * Get bidirectional map (DualHashBidiMap). The bidirectional map has one
     * value per key and one key per value which is a strict 1:1 relationship.
     * The data structure must be initialised first, see {@link #init(String, InputStream)}.
     *
     * @return bidirectional map (DualHashBidiMap)
     */
    public DualHashBidiMap getBidiIdentifierFilenameMap();
    
    /**
     * Directory where container files are extracted to. The files of a 
     * container file must be extracted to a temporary directory in order to
     * enable usage of tools which take a complete directory as input. 
     * @return 
     */
    public String getExtractDirectoryName();
}
