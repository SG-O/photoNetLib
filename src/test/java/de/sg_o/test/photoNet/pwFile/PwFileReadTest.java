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

package de.sg_o.test.photoNet.pwFile;

import de.sg_o.lib.photoNet.printFile.PrintFile;
import de.sg_o.lib.photoNet.printFile.PrintFileMeta;
import de.sg_o.lib.photoNet.printFile.PrintFilePreview;
import de.sg_o.lib.photoNet.printFile.PrintLayer;
import de.sg_o.lib.photoNet.printFile.photon.PhotonPrintFileMeta;
import de.sg_o.lib.photoNet.printFile.pw.PwPrintFile;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class PwFileReadTest {

    @Test
    void fileReadTest1() throws URISyntaxException, IOException {
        URL testFileURL = this.getClass().getResource("/testFile.pws");
        assertNotNull(testFileURL);
        File testFile = new File(testFileURL.toURI());
        assertNotNull(testFile);
        PrintFile pwFile = new PwPrintFile(Files.newInputStream(testFile.toPath()), testFile.length());
        pwFile.parse();
        PrintFileMeta meta = pwFile.getMeta();
        PrintFilePreview preview = pwFile.getPreview();
        assertNotNull(meta);
        assertNotNull(preview);
        assertEquals(516, meta.getFileVersion());
        assertEquals(2560, meta.getScreenHeight());
        assertEquals(1440, meta.getScreenWidth());
        assertEquals(0, meta.getLightCuringType());
        assertEquals(60, meta.getNrLayers());
        assertEquals(3, meta.getBottomLayers());
        assertEquals(8, meta.getAntiAliasing());
        assertEquals(152, meta.getPreviewHeaderOffset());
        assertEquals(240208, meta.getLayerHeadersOffset());
        assert (Math.abs(0.0f - meta.getPlateX()) < 0.01);
        assert (Math.abs(0.0f - meta.getPlateY()) < 0.01);
        assert (Math.abs(0.0f - meta.getPlateZ()) < 0.01);
        assert (Math.abs(0.05f - meta.getLayerThickness()) < 0.01);
        assert (Math.abs(11.0f - meta.getNormalExposureTime()) < 0.01);
        assert (Math.abs(60.0f - meta.getBottomExposureTime()) < 0.01);
        assert (Math.abs(0.0f - meta.getOffTime()) < 0.01);

        URL refFile = this.getClass().getResource("/pwPreview.png");
        assertNotNull(refFile);
        BufferedImage tmp = ImageIO.read(refFile);
        BufferedImage previewFile = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        previewFile.getGraphics().drawImage(tmp, 0, 0, null);
        int[] previewRef = ((DataBufferInt) previewFile.getRaster().getDataBuffer()).getData();
        assertArrayEquals(previewRef, preview.getImage());

        System.out.println(meta);

        PrintLayer layer00 = pwFile.getLayers().get(0);
        assert (Math.abs(60.0f - layer00.getExposureTime()) < 0.01);

        refFile = this.getClass().getResource("/pwLayer00.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        int[] layerAct00 = pwFile.getLayerImage(0);
        for (int j = 0; j < tmp.getHeight(); j++) {
            for (int i = 0; i < tmp.getWidth(); i++) {
                assertEquals(tmp.getRGB(i, j), layerAct00[(j * meta.getScreenWidth()) + i]);
            }
        }

        PrintLayer layer40 = pwFile.getLayers().get(40);
        assert (Math.abs(11.0f - layer40.getExposureTime()) < 0.01);

        refFile = this.getClass().getResource("/pwLayer40.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        int[] layerAct40 = pwFile.getLayerImage(40);
        for (int j = 0; j < tmp.getHeight(); j++) {
            for (int i = 0; i < tmp.getWidth(); i++) {
                assertEquals(tmp.getRGB(i, j), layerAct40[(j * meta.getScreenWidth()) + i]);
            }
        }

        byte[] tooShort = new byte[32];
        try {
            new PhotonPrintFileMeta(tooShort);
            assert (false);
        } catch (IOException ignore) {
            assert (true);
        }

        byte[] invalidHeader = new byte[96];
        try {
            new PhotonPrintFileMeta(invalidHeader);
            assert (false);
        } catch (IOException ignore) {
            assert (true);
        }

        byte[] validHeader = new byte[96];
        validHeader[0] = 0x12;
        validHeader[1] = (byte) 0xFD;
        validHeader[2] = 0x00;
        validHeader[3] = 0x19;

        try {
            new PhotonPrintFileMeta(validHeader);
            assert (true);
        } catch (IOException ignore) {
            assert (false);
        }
    }

    @Test
    void fileReadTest2() throws URISyntaxException, IOException {
        URL testFileURL = this.getClass().getResource("/testFile2.pws");
        assertNotNull(testFileURL);
        File testFile = new File(testFileURL.toURI());
        assertNotNull(testFile);
        PrintFile pwFile = new PwPrintFile(Files.newInputStream(testFile.toPath()), testFile.length());
        pwFile.parse();
        PrintFileMeta meta = pwFile.getMeta();
        PrintFilePreview preview = pwFile.getPreview();
        assertNotNull(meta);
        assertNotNull(preview);
        assertEquals(1, meta.getFileVersion());
        assertEquals(2560, meta.getScreenHeight());
        assertEquals(1440, meta.getScreenWidth());
        assertEquals(0, meta.getLightCuringType());
        assertEquals(60, meta.getNrLayers());
        assertEquals(3, meta.getBottomLayers());
        assertEquals(1, meta.getAntiAliasing());
        assertEquals(144, meta.getPreviewHeaderOffset());
        assertEquals(75436, meta.getLayerHeadersOffset());
        assert (Math.abs(0.0f - meta.getPlateX()) < 0.01);
        assert (Math.abs(0.0f - meta.getPlateY()) < 0.01);
        assert (Math.abs(0.0f - meta.getPlateZ()) < 0.01);
        assert (Math.abs(0.05f - meta.getLayerThickness()) < 0.01);
        assert (Math.abs(8.0f - meta.getNormalExposureTime()) < 0.01);
        assert (Math.abs(60.0f - meta.getBottomExposureTime()) < 0.01);
        assert (Math.abs(1.0f - meta.getOffTime()) < 0.01);

        URL refFile = this.getClass().getResource("/pwPreview2.png");
        assertNotNull(refFile);
        BufferedImage tmp = ImageIO.read(refFile);
        BufferedImage previewFile = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        previewFile.getGraphics().drawImage(tmp, 0, 0, null);
        int[] previewRef = ((DataBufferInt) previewFile.getRaster().getDataBuffer()).getData();
        assertArrayEquals(previewRef, preview.getImage());

        System.out.println(meta);

        PrintLayer layer00 = pwFile.getLayers().get(0);
        assert (Math.abs(60.0f - layer00.getExposureTime()) < 0.01);

        refFile = this.getClass().getResource("/pwLayer200.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        int[] layerAct00 = pwFile.getLayerImage(0);
        for (int j = 0; j < tmp.getHeight(); j++) {
            for (int i = 0; i < tmp.getWidth(); i++) {
                assertEquals(tmp.getRGB(i, j), layerAct00[(j * meta.getScreenWidth()) + i]);
            }
        }

        PrintLayer layer40 = pwFile.getLayers().get(40);
        assert (Math.abs(8.0f - layer40.getExposureTime()) < 0.01);

        refFile = this.getClass().getResource("/pwLayer240.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        int[] layerAct40 = pwFile.getLayerImage(40);
        for (int j = 0; j < tmp.getHeight(); j++) {
            for (int i = 0; i < tmp.getWidth(); i++) {
                assertEquals(tmp.getRGB(i, j), layerAct40[(j * meta.getScreenWidth()) + i]);
            }
        }
    }

    @Test
    void fileReadTest3() throws URISyntaxException, IOException {
        URL testFileURL = this.getClass().getResource("/testFile3.pwmo");
        assertNotNull(testFileURL);
        File testFile = new File(testFileURL.toURI());
        assertNotNull(testFile);
        PrintFile pwFile = new PwPrintFile(Files.newInputStream(testFile.toPath()), testFile.length());
        pwFile.parse();
        PrintFileMeta meta = pwFile.getMeta();
        PrintFilePreview preview = pwFile.getPreview();
        assertNotNull(meta);
        assertNotNull(preview);
        assertEquals(1, meta.getFileVersion());
        assertEquals(2560, meta.getScreenHeight());
        assertEquals(1620, meta.getScreenWidth());
        assertEquals(0, meta.getLightCuringType());
        assertEquals(61, meta.getNrLayers());
        assertEquals(6, meta.getBottomLayers());
        assertEquals(1, meta.getAntiAliasing());
        assertEquals(144, meta.getPreviewHeaderOffset());
        assertEquals(75420, meta.getLayerHeadersOffset());
        assert (Math.abs(0.0f - meta.getPlateX()) < 0.01);
        assert (Math.abs(0.0f - meta.getPlateY()) < 0.01);
        assert (Math.abs(0.0f - meta.getPlateZ()) < 0.01);
        assert (Math.abs(0.05f - meta.getLayerThickness()) < 0.01);
        assert (Math.abs(2.0f - meta.getNormalExposureTime()) < 0.01);
        assert (Math.abs(40.0f - meta.getBottomExposureTime()) < 0.01);
        assert (Math.abs(0.5f - meta.getOffTime()) < 0.01);

        URL refFile = this.getClass().getResource("/pwPreview3.png");
        assertNotNull(refFile);
        BufferedImage tmp = ImageIO.read(refFile);
        BufferedImage previewFile = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        previewFile.getGraphics().drawImage(tmp, 0, 0, null);
        int[] previewRef = ((DataBufferInt) previewFile.getRaster().getDataBuffer()).getData();
        assertArrayEquals(previewRef, preview.getImage());

        System.out.println(meta);

        PrintLayer layer00 = pwFile.getLayers().get(0);
        assert (Math.abs(40.0f - layer00.getExposureTime()) < 0.01);

        refFile = this.getClass().getResource("/pwLayer300.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        int[] layerAct00 = pwFile.getLayerImage(0);
        for (int j = 0; j < tmp.getHeight(); j++) {
            for (int i = 0; i < tmp.getWidth(); i++) {
                assertEquals(tmp.getRGB(i, j), layerAct00[(j * meta.getScreenWidth()) + i]);
            }
        }

        PrintLayer layer40 = pwFile.getLayers().get(40);
        assert (Math.abs(2.0f - layer40.getExposureTime()) < 0.01);

        refFile = this.getClass().getResource("/pwLayer340.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        int[] layerAct40 = pwFile.getLayerImage(40);
        for (int j = 0; j < tmp.getHeight(); j++) {
            for (int i = 0; i < tmp.getWidth(); i++) {
                assertEquals(tmp.getRGB(i, j), layerAct40[(j * meta.getScreenWidth()) + i]);
            }
        }
    }
}
