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
package eu.scape_project.archiventory.characterisers;

import eu.scape_project.archiventory.container.Container;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

/**
 * DirectoryCharacterisation interface
 * @author onbscs
 */
public interface DirectoryCharacterisation {
    
    public void setContainer(Container container);
    
    public Container getContainer();
    
    /**
     * Run characterisation on a file or directory
     * @return HashMap, Key: Identifier, Value: Path to characterisation result file
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public abstract DualHashBidiMap characterise() throws FileNotFoundException, IOException;
    
}
