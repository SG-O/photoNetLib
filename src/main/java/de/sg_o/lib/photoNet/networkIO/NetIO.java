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

package de.sg_o.lib.photoNet.networkIO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetIO implements Serializable {
    private static final long serialVersionUID = -3459483319748283239L;
    private final InetAddress address;
    private final int port;
    private final int timeout;
    private transient NetWorker worker;
    private transient Thread workerThread;
    private transient long counter = 0;
    private transient long executed = -1;

    public NetIO(InetAddress address, int port, int timeout) throws SocketException {
        if (address == null) throw new SocketException("Address Null");
        this.address = address;
        this.port = port;
        this.timeout = timeout;
        start();
    }

    public NetIO(String address, int port, int timeout) throws UnknownHostException, SocketException {
        this(InetAddress.getByName(address), port, timeout);
    }

    public void start() throws SocketException {
        if (isAlive()) return;
        counter = 0;
        executed = -1;
        worker = new NetWorker(address, port, timeout);
        workerThread = new Thread(worker);
        workerThread.start();
    }

    public NetRequestResponse send(byte[] data) {
        if (!isAlive()) return null;
        NetRequestResponse req = new NetRequestResponse(counter, data);
        counter++;
        worker.addJob(req);
        return req;
    }

    public NetRequestResponse sendImmediately(byte[] data) {
        if (!isAlive()) return null;
        NetRequestResponse req = new NetRequestResponse(counter, data);
        counter++;
        worker.addImportantJob(req);
        return req;
    }

    public boolean isAlive() {
        if (worker == null) return false;
        if (workerThread == null) return false;
        return workerThread.isAlive();
    }

    @SuppressWarnings("unused")
    public long getLastExecuted() {
        while (worker.doneSize() > 0) {
            executed = worker.pollDone().getID();
        }
        return executed;
    }

    @SuppressWarnings("unused")
    public long getCounter() {
        return counter;
    }

    public void stop() {
        worker.stop();
        while (workerThread.isAlive()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        worker = null;
        workerThread = null;
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        worker = null;
        workerThread = null;
        counter = 0;
        executed = -1;
        start();
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }
}
