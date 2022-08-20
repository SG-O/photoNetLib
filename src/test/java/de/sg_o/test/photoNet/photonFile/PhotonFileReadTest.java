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

package de.sg_o.test.photoNet.photonFile;

import de.sg_o.lib.photoNet.photonFile.PhotonFile;
import de.sg_o.lib.photoNet.photonFile.PhotonFileMeta;
import de.sg_o.lib.photoNet.photonFile.PhotonFilePreview;
import de.sg_o.lib.photoNet.photonFile.PhotonLayer;
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

class PhotonFileReadTest {
    @Test
    void fileReadTest() throws URISyntaxException, IOException {
        URL testFileURL = this.getClass().getResource("/testFile.photon");
        assertNotNull(testFileURL);
        File testFile = new File(testFileURL.toURI());
        assertNotNull(testFile);
        PhotonFile photonFile = new PhotonFile(Files.newInputStream(testFile.toPath()), testFile.length());
        photonFile.parse();
        PhotonFileMeta meta = photonFile.getMeta();
        PhotonFilePreview preview = photonFile.getPreview();
        PhotonFilePreview thumbnail = photonFile.getThumbnail();
        assertNotNull(meta);
        assertNotNull(preview);
        assertNotNull(thumbnail);
        assertEquals(4, meta.getFileVersion());
        assertEquals(2560, meta.getScreenHeight());
        assertEquals(1440, meta.getScreenWidth());
        assertEquals(1, meta.getLightCuringType());
        assertEquals(60, meta.getNrLayers());
        assertEquals(3, meta.getBottomLayers());
        assertEquals(8, meta.getAntiAliasing());
        assertEquals(112, meta.getPreviewHeaderOffset());
        assertEquals(7914, meta.getPreviewThumbnailHeaderOffset());
        assertEquals(12369, meta.getLayerHeadersOffset());
        assert (Math.abs(68.04f - meta.getPlateX()) < 0.01);
        assert (Math.abs(120.96f - meta.getPlateY()) < 0.01);
        assert (Math.abs(150.0f - meta.getPlateZ()) < 0.01);
        assert (Math.abs(0.05f - meta.getLayerThickness()) < 0.01);
        assert (Math.abs(11.0f - meta.getNormalExposureTime()) < 0.01);
        assert (Math.abs(60.0f - meta.getBottomExposureTime()) < 0.01);
        assert (Math.abs(4.0f - meta.getOffTime()) < 0.01);

        String toStringExpected = getToString();

        assertEquals(toStringExpected, photonFile.toString());

        URL refFile = this.getClass().getResource("/preview.png");
        assertNotNull(refFile);
        BufferedImage tmp = ImageIO.read(refFile);
        BufferedImage previewFile = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        previewFile.getGraphics().drawImage(tmp, 0, 0, null);
        int[] previewRef = ((DataBufferInt) previewFile.getRaster().getDataBuffer()).getData();
        assertArrayEquals(previewRef, preview.getImage());

        refFile = this.getClass().getResource("/thumbnail.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        BufferedImage thumbnailFile = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        thumbnailFile.getGraphics().drawImage(tmp, 0, 0, null);
        int[] thumbnailRef = ((DataBufferInt) thumbnailFile.getRaster().getDataBuffer()).getData();
        assertArrayEquals(thumbnailRef, thumbnail.getImage());

        PhotonLayer layer00 = photonFile.getLayers().get(0);
        assert (Math.abs(0.05f - layer00.getAbsolutePosition()) < 0.01);
        assert (Math.abs(60.0f - layer00.getExposureTime()) < 0.01);
        assert (Math.abs(4.0f - layer00.getOffTime()) < 0.01);

        refFile = this.getClass().getResource("/layer00.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        BufferedImage layerFile00 = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        layerFile00.getGraphics().drawImage(tmp, 0, 0, null);
        int[] layerRef00 = ((DataBufferInt) layerFile00.getRaster().getDataBuffer()).getData();
        int[] layerAct00 = photonFile.getLayerImage(0);
        assertArrayEquals(layerRef00, layerAct00);

        PhotonLayer layer40 = photonFile.getLayers().get(40);
        assert (Math.abs(2.05f - layer40.getAbsolutePosition()) < 0.01);
        assert (Math.abs(11.0f - layer40.getExposureTime()) < 0.01);
        assert (Math.abs(4.0f - layer40.getOffTime()) < 0.01);

        refFile = this.getClass().getResource("/layer40.png");
        assertNotNull(refFile);
        tmp = ImageIO.read(refFile);
        BufferedImage layerFile40 = new BufferedImage(tmp.getWidth(), tmp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        layerFile40.getGraphics().drawImage(tmp, 0, 0, null);
        int[] layerRef40 = ((DataBufferInt) layerFile40.getRaster().getDataBuffer()).getData();
        assertArrayEquals(layerRef40, photonFile.getLayerImage(40));

        layerAct00 = photonFile.getLayerImage(0);
        assertArrayEquals(layerRef00, layerAct00);

        try {
            new PhotonFileMeta(null);
            assert (false);
        } catch (IOException ignore) {
            assert (true);
        }

        byte[] tooShort = new byte[32];
        try {
            new PhotonFileMeta(tooShort);
            assert (false);
        } catch (IOException ignore) {
            assert (true);
        }

        byte[] invalidHeader = new byte[96];
        try {
            new PhotonFileMeta(invalidHeader);
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
            new PhotonFileMeta(validHeader);
            assert (true);
        } catch (IOException ignore) {
            assert (false);
        }
    }

    private String getToString() {
        return "PhotonFile{meta=PhotonFileMeta{fileVersion=4, plateX=68.04, plateY=120.96, " +
                "plateZ=150.0, layerThickness=0.05, normalExposureTime=11.0, bottomExposureTime=60.0, offTime=4.0, " +
                "bottomLayers=3, screenHeight=2560, screenWidth=1440, lightCuringType=1, nrLayers=60, layerHeadersOffset=12369, " +
                "previewHeaderOffset=112, previewThumbnailHeaderOffset=7914, antiAliasing=8}, " +
                "preview=PhotonFilePreview{imgWidth=400, imgHeight=300, imgDataOffset=144, imgDataLength=7770}, " +
                "thumbnail=PhotonFilePreview{imgWidth=200, imgHeight=125, imgDataOffset=7946, imgDataLength=3488}, " +
                "layers=[PhotonLayer{absolutePosition=0.05, exposureTime=60.0, offTime=4.0, imgDataOffset=29733, imgDataLength=30559}, " +
                "PhotonLayer{absolutePosition=0.1, exposureTime=60.0, offTime=4.0, imgDataOffset=274843, imgDataLength=30567}, " +
                "PhotonLayer{absolutePosition=0.15, exposureTime=60.0, offTime=4.0, imgDataOffset=520005, imgDataLength=30569}, " +
                "PhotonLayer{absolutePosition=0.2, exposureTime=11.0, offTime=4.0, imgDataOffset=765209, imgDataLength=30573}, " +
                "PhotonLayer{absolutePosition=0.25, exposureTime=11.0, offTime=4.0, imgDataOffset=1010447, imgDataLength=30579}, " +
                "PhotonLayer{absolutePosition=0.3, exposureTime=11.0, offTime=4.0, imgDataOffset=1255731, imgDataLength=30583}, " +
                "PhotonLayer{absolutePosition=0.35, exposureTime=11.0, offTime=4.0, imgDataOffset=1501047, imgDataLength=30587}, " +
                "PhotonLayer{absolutePosition=0.4, exposureTime=11.0, offTime=4.0, imgDataOffset=1746407, imgDataLength=30593}, " +
                "PhotonLayer{absolutePosition=0.45, exposureTime=11.0, offTime=4.0, imgDataOffset=1991807, imgDataLength=30599}, " +
                "PhotonLayer{absolutePosition=0.5, exposureTime=11.0, offTime=4.0, imgDataOffset=2237253, imgDataLength=30603}, " +
                "PhotonLayer{absolutePosition=0.55, exposureTime=11.0, offTime=4.0, imgDataOffset=2482731, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.6, exposureTime=11.0, offTime=4.0, imgDataOffset=2728235, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.65, exposureTime=11.0, offTime=4.0, imgDataOffset=2973739, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.7, exposureTime=11.0, offTime=4.0, imgDataOffset=3219243, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.75, exposureTime=11.0, offTime=4.0, imgDataOffset=3464747, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.8, exposureTime=11.0, offTime=4.0, imgDataOffset=3710251, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.85, exposureTime=11.0, offTime=4.0, imgDataOffset=3955755, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.9, exposureTime=11.0, offTime=4.0, imgDataOffset=4201259, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=0.95, exposureTime=11.0, offTime=4.0, imgDataOffset=4446763, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.0, exposureTime=11.0, offTime=4.0, imgDataOffset=4692267, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.05, exposureTime=11.0, offTime=4.0, imgDataOffset=4937771, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.1, exposureTime=11.0, offTime=4.0, imgDataOffset=5183275, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.15, exposureTime=11.0, offTime=4.0, imgDataOffset=5428779, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.2, exposureTime=11.0, offTime=4.0, imgDataOffset=5674283, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.25, exposureTime=11.0, offTime=4.0, imgDataOffset=5919787, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.3, exposureTime=11.0, offTime=4.0, imgDataOffset=6165291, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.35, exposureTime=11.0, offTime=4.0, imgDataOffset=6410795, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.4, exposureTime=11.0, offTime=4.0, imgDataOffset=6656299, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.45, exposureTime=11.0, offTime=4.0, imgDataOffset=6901803, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.5, exposureTime=11.0, offTime=4.0, imgDataOffset=7147307, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.55, exposureTime=11.0, offTime=4.0, imgDataOffset=7392811, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.6, exposureTime=11.0, offTime=4.0, imgDataOffset=7638315, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.65, exposureTime=11.0, offTime=4.0, imgDataOffset=7883819, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.7, exposureTime=11.0, offTime=4.0, imgDataOffset=8129323, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.75, exposureTime=11.0, offTime=4.0, imgDataOffset=8374827, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.8, exposureTime=11.0, offTime=4.0, imgDataOffset=8620331, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.85, exposureTime=11.0, offTime=4.0, imgDataOffset=8865835, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.9, exposureTime=11.0, offTime=4.0, imgDataOffset=9111339, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=1.95, exposureTime=11.0, offTime=4.0, imgDataOffset=9356843, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=2.0, exposureTime=11.0, offTime=4.0, imgDataOffset=9602347, imgDataLength=30605}, " +
                "PhotonLayer{absolutePosition=2.05, exposureTime=11.0, offTime=4.0, imgDataOffset=9847851, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.1, exposureTime=11.0, offTime=4.0, imgDataOffset=10109285, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.15, exposureTime=11.0, offTime=4.0, imgDataOffset=10370718, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.2, exposureTime=11.0, offTime=4.0, imgDataOffset=10632151, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.25, exposureTime=11.0, offTime=4.0, imgDataOffset=10893584, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.3, exposureTime=11.0, offTime=4.0, imgDataOffset=11155017, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.35, exposureTime=11.0, offTime=4.0, imgDataOffset=11416449, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.4, exposureTime=11.0, offTime=4.0, imgDataOffset=11677881, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.45, exposureTime=11.0, offTime=4.0, imgDataOffset=11939313, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.5, exposureTime=11.0, offTime=4.0, imgDataOffset=12200745, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.55, exposureTime=11.0, offTime=4.0, imgDataOffset=12462177, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.6, exposureTime=11.0, offTime=4.0, imgDataOffset=12723610, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.65, exposureTime=11.0, offTime=4.0, imgDataOffset=12985042, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.7, exposureTime=11.0, offTime=4.0, imgDataOffset=13246474, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.75, exposureTime=11.0, offTime=4.0, imgDataOffset=13507906, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.8, exposureTime=11.0, offTime=4.0, imgDataOffset=13769338, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.85, exposureTime=11.0, offTime=4.0, imgDataOffset=14030770, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.9, exposureTime=11.0, offTime=4.0, imgDataOffset=14292202, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=2.95, exposureTime=11.0, offTime=4.0, imgDataOffset=14553634, imgDataLength=32588}, " +
                "PhotonLayer{absolutePosition=3.0, exposureTime=11.0, offTime=4.0, imgDataOffset=14815066, imgDataLength=32589}]}";
    }
}