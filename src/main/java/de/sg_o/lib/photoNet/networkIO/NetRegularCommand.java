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

public abstract class NetRegularCommand {
    protected NetRequestResponse response;

    public boolean isExecuted() {
        if (response == null) return true;
        if (response.getResponse() != null) return true;
        return response.getError() != null;
    }

    public boolean isError() {
        if (response == null) return false;
        return response.getError() != null;
    }

    @SuppressWarnings("unused")
    public String getError() {
        if (!isExecuted()) return null;
        if (!isError()) return null;
        if (response == null) return null;
        return response.getError().trim();
    }

    public abstract String getResponse();
}
