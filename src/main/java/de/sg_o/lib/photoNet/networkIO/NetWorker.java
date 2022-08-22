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

import java.net.InetAddress;
import java.util.LinkedList;

public abstract class NetWorker implements Runnable {
    protected final InetAddress address;
    protected final int port;

    protected final LinkedList<NetRequestResponse> toDo = new LinkedList<>();
    protected final LinkedList<NetRequestResponse> done = new LinkedList<>();

    public NetWorker(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void addJob(NetRequestResponse job) {
        synchronized (this) {
            toDo.add(job);
        }
    }

    public void addImportantJob(NetRequestResponse job) {
        synchronized (this) {
            toDo.addFirst(job);
        }
    }

    public int doneSize() {
        int size;
        synchronized (this) {
            size = done.size();
        }
        return size;
    }

    public NetRequestResponse pollDone() {
        NetRequestResponse job;
        synchronized (this) {
            job = done.poll();
        }
        return job;
    }

    public abstract void stop();
}
