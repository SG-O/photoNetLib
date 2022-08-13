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

package de.sg_o.lib.photoNet.netData;

import de.sg_o.lib.photoNet.networkIO.NetIO;

import java.util.LinkedList;

public class FolderList {
    private final LinkedList<FileListItem> items = new LinkedList<>();
    private String path;

    public FolderList(String path, String response, NetIO io) {
        if (response == null) return;
        this.path = path;
        String[] lines = response.split("\\r?\\n");
        for (int i = 1; i < (lines.length - 2); i++) {
            try {
                items.add(new FileListItem(path, lines[i], io));
            } catch (Exception ignored) {
            }
        }
    }

    @SuppressWarnings("unused")
    public String getPath() {
        return path;
    }

    @SuppressWarnings("unused")
    public LinkedList<FileListItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "FolderList{" +
                "path='" + path + '\'' +
                ", items=" + items +
                '}';
    }
}
