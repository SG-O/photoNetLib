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

import java.util.Arrays;

public class NetRequestResponse {
    private final long ID;
    private final byte[] request;
    private byte[] response = null;
    private String error = null;
    private final int expectedLength;

    public NetRequestResponse(long ID, byte[] request, int expectedLength) {
        this.ID = ID;
        this.request = request;
        this.expectedLength = expectedLength;
    }

    public synchronized long getID() {
        return ID;
    }

    public synchronized byte[] getRequest() {
        return request;
    }

    public synchronized byte[] getResponse() {
        return response;
    }

    public synchronized void setResponse(byte[] response) {
        this.response = response;
    }

    public synchronized String getError() {
        return error;
    }

    public synchronized void setError(String error) {
        this.error = error;
    }

    public int getExpectedLength() {
        return Math.max(expectedLength, 0);
    }

    @Override
    public synchronized String toString() {
        return "NetRequestResponse{" +
                "ID=" + ID +
                ", request=" + Arrays.toString(request) +
                ", response=" + Arrays.toString(response) +
                '}';
    }
}
