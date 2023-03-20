/*
 *
 *   Copyright (C) 2022 Joerg Bayer (SG-O)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package de.sg_o.lib.photoNet.printer.cbd;

import de.sg_o.lib.photoNet.networkIO.NetIO;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("SerializableHasSerializationMethods")
public class CbdRootFolder extends CbdFolder {
    private static final long serialVersionUID = -8663219925914257172L;

    public CbdRootFolder(NetIO io) throws UnsupportedEncodingException {
        super("", io);
    }
}