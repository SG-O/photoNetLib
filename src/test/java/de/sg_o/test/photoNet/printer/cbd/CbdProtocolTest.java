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

package de.sg_o.test.photoNet.printer.cbd;

import de.sg_o.lib.photoNet.manager.Environment;
import de.sg_o.lib.photoNet.netData.FileListItem;
import de.sg_o.lib.photoNet.netData.FolderList;
import de.sg_o.lib.photoNet.netData.PrintTime;
import de.sg_o.lib.photoNet.netData.Status;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.printFile.PrintFileMeta;
import de.sg_o.lib.photoNet.printFile.PrintFilePreview;
import de.sg_o.lib.photoNet.printer.Folder;
import de.sg_o.lib.photoNet.printer.Printer;
import de.sg_o.test.photoNet.emulator.CbdEmulator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CbdProtocolTest {

    @SuppressWarnings("BusyWait")
    @Test
    void emulatorTest() throws IOException, InterruptedException {
        CbdEmulator emulator = new CbdEmulator(3000, 10000);

        Thread runner = new Thread(emulator);
        runner.start();

        Environment environment = new Environment("", 2000);
        assertEquals(0, environment.getConnected().size());

        environment.connect("127.0.0.1", NetIO.DeviceType.CBD);
        assertEquals(1, environment.getConnected().size());
        Printer p = environment.getConnected().get(0);
        assertNotNull(p);

        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        while (!p.isInfoValid()) {
            Thread.sleep(100);
        }

        assertEquals("127.0.0.1", p.getIp());
        assertEquals("SomeName", p.getName());
        assertEquals("V1.4.1", p.getVer());
        assertEquals("00:e0:4c:00:00:00", p.getMac());
        assertEquals("00,00,00,00,00,00,00,01", p.getId());

        assertEquals(Status.State.IDLE, p.getStatus().getState());
        assertFalse(p.getStatus().cameOnline());
        assertNull(p.getStatus().getOpenedFile());
        assert (Math.abs(p.getStatus().getZ()) < 0.01);
        assert (Math.abs(p.getStatus().getProgress()) < 0.01);
        assertEquals(new PrintTime(0), p.getStatus().getTime());

        p.setName("NewName");
        p.update();
        while (!p.isInfoValid()) {
            Thread.sleep(100);
        }

        assertEquals("127.0.0.1", p.getIp());
        assertEquals("NewName", p.getName());
        assertEquals("V1.4.1", p.getVer());
        assertEquals("00:e0:4c:00:00:00", p.getMac());
        assertEquals("00,00,00,00,00,00,00,01", p.getId());


        Folder rootFolder = p.getRootFolder();
        assertEquals("", rootFolder.getPath());
        assert (!rootFolder.isUpToDate());
        assertNull(rootFolder.getFolderList());
        rootFolder.update();
        assertNull(rootFolder.getFolderList());
        while (!rootFolder.isUpToDate()) {
            Thread.sleep(100);
        }
        assert (rootFolder.isUpToDate());

        FolderList folderList = rootFolder.getFolderList();
        assertNotNull(folderList);
        assertEquals("", folderList.getPath());
        assertEquals(1, folderList.getItems().size());

        FileListItem model1 = folderList.getItems().get("testFile.photon");
        assertNotNull(model1);
        assertEquals("testFile.photon", model1.getName());
        assertEquals("testFile.photon", model1.getFullPath());
        assertNotNull(model1.getDownload(null, 3));

        PrintFileMeta meta = model1.getMeta();
        System.out.println(meta);
        assertNotNull(meta);
        PrintFilePreview preview1 = model1.getPreview(meta.getPreviewHeaderOffset());
        assertNotNull(preview1);
        assertEquals(300, preview1.getImgHeight());
        assertEquals(400, preview1.getImgWidth());

        model1.print();

        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }

        System.out.println(p.getStatus());
        assertEquals(Status.State.PRINTING, p.getStatus().getState());
        assertEquals("testFile.photon", p.getStatus().getOpenedFile());
        assert ((0 - Math.abs(p.getStatus().getZ())) < 0.01);
        assert ((0 - Math.abs(p.getStatus().getProgress())) < 0.01);
        assertEquals(new PrintTime(20), p.getStatus().getTime());
        p.pause();
        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        assertEquals(Status.State.PAUSE, p.getStatus().getState());

        model1.print();

        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        assertEquals(Status.State.PRINTING, p.getStatus().getState());
        assertEquals("testFile.photon", p.getStatus().getOpenedFile());

        p.stop();
        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        assertEquals(Status.State.FINISHED, p.getStatus().getState());
        p.getStatus().removePrint();
        assertEquals(Status.State.IDLE, p.getStatus().getState());
        model1.delete();

        emulator.close();
    }
}
