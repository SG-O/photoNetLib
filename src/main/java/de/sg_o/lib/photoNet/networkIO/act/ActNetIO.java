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

package de.sg_o.lib.photoNet.networkIO.act;

import de.sg_o.lib.photoNet.networkIO.NetIO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ActNetIO extends NetIO {

    private static final long serialVersionUID = -9071377806313137150L;

    @SuppressWarnings("unused")
    public ActNetIO(InetAddress address, int port, int timeout) throws SocketException {
        super(address, port, timeout, DeviceType.ACT);
    }

    public ActNetIO(String address, int port, int timeout) throws UnknownHostException, SocketException {
        super(InetAddress.getByName(address), port, timeout, DeviceType.ACT);
    }

    public void start() throws SocketException {
        super.start(new ActNetWorker(address, port, timeout));
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        start();
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }
}
