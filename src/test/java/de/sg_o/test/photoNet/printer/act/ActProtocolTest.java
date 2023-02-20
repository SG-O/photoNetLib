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

package de.sg_o.test.photoNet.printer.act;

import de.sg_o.lib.photoNet.manager.Environment;
import de.sg_o.lib.photoNet.netData.FileListItem;
import de.sg_o.lib.photoNet.netData.FolderList;
import de.sg_o.lib.photoNet.netData.PrintTime;
import de.sg_o.lib.photoNet.netData.Status;
import de.sg_o.lib.photoNet.networkIO.NetIO;
import de.sg_o.lib.photoNet.printFile.PrintFilePreview;
import de.sg_o.lib.photoNet.printer.Folder;
import de.sg_o.lib.photoNet.printer.Printer;
import de.sg_o.test.photoNet.emulator.ApEmulator;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ActProtocolTest {

    @SuppressWarnings("BusyWait")
    @Test
    void emulatorTest() throws IOException, InterruptedException {
        ApEmulator emulator = new ApEmulator(6000, 10000);

        Thread runner = new Thread(emulator);
        runner.start();

        Environment environment = new Environment("", 2000);
        assertEquals(0, environment.getConnected().size());

        environment.connect("127.0.0.1", NetIO.DeviceType.ACT);
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
        assertEquals("v0.1", p.getVer());
        assertEquals("", p.getMac());
        assertEquals("0000000000000001", p.getId());

        assertEquals(Status.State.IDLE, p.getStatus().getState());
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
        assertEquals("v0.1", p.getVer());
        assertEquals("", p.getMac());
        assertEquals("0000000000000001", p.getId());


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
        assertEquals(3, folderList.getItems().size());

        FileListItem model1 = folderList.getItems().get("Model1.pwmb");
        assertNotNull(model1);
        assertEquals("Model1.pwmb", model1.getName());
        assertEquals("Model1.pwmb", model1.getFullPath());
        assertNull(model1.getDownload(null, 3));

        FileListItem model2 = folderList.getItems().get("Model2.pwmb");
        assertNotNull(model2);
        assertEquals("Model2.pwmb", model2.getName());
        assertEquals("Model2.pwmb", model2.getFullPath());

        PrintFilePreview preview1 = model1.getPreview(0);
        assertEquals(168, preview1.getImgHeight());
        assertEquals(224, preview1.getImgWidth());

        model2.print();

        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }

        System.out.println(p.getStatus());
        assertEquals(Status.State.PRINTING, p.getStatus().getState());
        assertEquals("Model2.pwmb", p.getStatus().getOpenedFile());
        assert ((103.1 - Math.abs(p.getStatus().getZ())) < 0.01);
        assert ((0.88 - Math.abs(p.getStatus().getProgress())) < 0.01);
        assertEquals(new PrintTime(6844), p.getStatus().getTime());
        p.pause();
        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        assertEquals(Status.State.PAUSE, p.getStatus().getState());

        emulator.finishPrint();
        p.update();
        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        assertEquals(Status.State.FINISHED, p.getStatus().getState());

        model2.print();

        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        assertEquals(Status.State.PRINTING, p.getStatus().getState());
        assertEquals("Model2.pwmb", p.getStatus().getOpenedFile());

        p.stop();
        while (!p.getStatus().isUpdated()) {
            Thread.sleep(100);
        }
        assertEquals(Status.State.FINISHED, p.getStatus().getState());
        model2.delete();

        emulator.close();
    }
}
