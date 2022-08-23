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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class NetIO implements Serializable {
    protected final InetAddress address;

    private static final long serialVersionUID = -3459483319748283239L;
    protected final int port;
    protected final int timeout;
    private final DeviceType deviceType;
    private transient NetWorker worker;
    private transient Thread workerThread;
    private transient long counter = 0;
    private transient long executed = -1;

    public NetIO(InetAddress address, int port, int timeout, DeviceType deviceType) throws SocketException {
        if (address == null) throw new SocketException("Address Null");
        this.address = address;
        this.port = port;
        this.timeout = timeout;
        this.deviceType = deviceType;
        start();
    }

    protected void start(NetWorker worker) {
        if (isAlive()) return;
        this.counter = 0;
        this.executed = -1;
        this.worker = worker;
        this.workerThread = new Thread(worker);
        this.workerThread.start();
    }

    protected abstract void start() throws SocketException;

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public NetRequestResponse send(byte[] data, int expectedLength) {
        if (!isAlive()) return null;
        NetRequestResponse req = new NetRequestResponse(counter, data, expectedLength);
        counter++;
        worker.addJob(req);
        return req;
    }

    public NetRequestResponse sendImmediately(byte[] data, int expectedLength) {
        if (!isAlive()) return null;
        NetRequestResponse req = new NetRequestResponse(counter, data, expectedLength);
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

    public enum DeviceType {
        CBD(0),
        ACT(1),
        UNKNOWN(2);

        private static final Map<Integer, DeviceType> IdMap = Collections.unmodifiableMap(initializeMapping());
        private final int ID;

        DeviceType(int ID) {
            this.ID = ID;
        }

        public static DeviceType getFromID(int ID) {
            if (IdMap.containsKey(ID)) {
                return IdMap.get(ID);
            }
            return UNKNOWN;
        }

        private static HashMap<Integer, DeviceType> initializeMapping() {
            HashMap<Integer, DeviceType> IdMap = new HashMap<>();
            for (DeviceType s : DeviceType.values()) {
                IdMap.put(s.ID, s);
            }
            return IdMap;
        }

        public int getID() {
            return ID;
        }
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
    }

    private void writeObject(java.io.ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }
}
