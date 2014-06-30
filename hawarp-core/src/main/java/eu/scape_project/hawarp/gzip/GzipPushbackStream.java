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

package eu.scape_project.hawarp.gzip;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;

public class GzipPushbackStream extends GZIPInputStream {
 
    private boolean pushed = false;
    private static final int TRAILER_LEN = 8;
 
    public GzipPushbackStream(PushbackInputStream pis) throws IOException {
        super(pis);
    }
 
    @Override
    public int read(byte[] buf, int off, int len) throws IOException {
        int read = super.read(buf, off, len);
        if (eos && !pushed) {
            int n = inf.getRemaining();
            if (n > TRAILER_LEN) {
                int offset = super.len - (n -TRAILER_LEN);
                ((PushbackInputStream) in).unread(super.buf, offset, n - TRAILER_LEN);
            }
            pushed = true;
        }
        return read;
    }
}