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

package de.sg_o.test.photoNet.netData;

import de.sg_o.lib.photoNet.netData.cbd.CbdDataTransferBlock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class DataTransferBlockTest {
    @SuppressWarnings("SpellCheckingInspection")
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @SuppressWarnings("unused")
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Test
    void decodeTest() {
        String hex = "da54eb5a3c3392535d4deb5abc30ed5202302e53023030430430504351435143733b0930b33b0930733b093071430330304304300e530e53ed520230eb5a9f315754da54eb5a3c3392535d4deb5aaa30cc52cd522e5302300f430f435143533b533b933b933b943b9423d52bd52b372c0530782c0630792c043079340c309a3479340930792c0430782c0730182c372c0330d62bd62bf52b0230943b933b533b533b51435143104310430e53ed520230eb5a8e31da545754eb5a3c3392535d4d9253eb5a9d30cd52cd520e530e530f435143533b933b933b943bd52b372c0330782c0230792c023059345934ba340d30fb340930fc3c0b30fc340930fb3408309b34ba340b30b93402305934782c0530372c0230d62bd62bd52bd52b933b533b533b5143104310430e53cd52eb5a8331da545754eb5a3d335d4d9253eb5a9330cd520e530e530f435143533b933b943bd52b372c0230582c582c592c592c59345934ba340830fc3403303c3d0230dc3c3c3d05301d3d1d3d7e4502307d450530be4504307e450b307d450b303d3d0a301c3ddc3cdc3cfc340430fb3407309b34ba34073059345934782c0430172c172cd62bd62bd52b933b533b514351431043cd52cd52eb5a79315d4d9253eb5a3d335d4d9253eb5a8a30cd520e530e530f43523b933b943b952b372c0230582c582c592c59345934ba3402309b349a349a34fc3403301c3d1c3d3d3d04307e450430be4d0230ff5505303f560630ff550b30df4dbf4d0730df4ddf4d9f4d9f45be4509307e450b307d4504303d3d06303c3d0530fc340430fb340530ba34063059345934592c782c0230172c172cd62bd52bd52b533b514351431043cd52eb5a71315d4d9253eb5a3d335d4d9253eb5a8230cd520e530f435143523b933b952b372c0230582c592c59345934ba3402309b349b34db34dc34dc341c3d1c3d3d3d02305d3dbe4d0330de55de553f5e03305f5e3f5e05307f5e05303f5e08301f561f56df55ff4d0430be4d05307e4516305d3d3d3d02307d4507307d3d09305d453d3d0b303c3d0430fc340330fb3405309b34ba34053059345934592c582c582c172c172cd62bd52bd52b933b514310431043cd52eb5a683192535d4deb5a3e335d4d9253eb5a7b30cd520e530f43523b933b952b372c0230582c592c5934ba3402309b349b34fc3c02301c3d3d3d02305d45be4d0230de55de553f5e02307f660430bf6605309f6e9f6e5f665f663f5e0330ff550330df4dbe4d03307e4506305d3d3d3d07303c3d0430fc3c0a30fc340a30db34db34fc3c07301c3d1c3dfc3404303c3d03301d3d1d3d3c3d03303d3d07303c3d04303d3d04301c3d1c3dfc3c0230dc34dc34fb3406309b34ba3404305934592c592c582c582c172cd62bd62bd52b933b514310430f43cd52eb5a613192535d4deb5a3e335d4d9253eb5a7430cd520e530f43523b933b952b172c172c582c592c5934ba3402309b349b34dc3cdc3c1c3d1c3d1d3d1d3d5e45be4d02301e565f665f665f5e5f665f66bf6602309f6eff6e0230df76df76df6e7f6603301f5e1f5eff5503309f4dbe4502307e4503303d3d03303c3d0430fb341130ba341f30bb340330ba340330fb340b30fc340430dc3cfc3404303c3d1030dc3cdc3cdc34fb3406309b349b34ba3403305934782c0330172cd62bd52b952b933b51431043cd52eb5a5b315754da54eb5a3e33da545754eb5a6d30cd520e530e53523b933b943b172c172c582c592c59349a349a349b349b34db34dc3cdc3c1c3d1d3d1d3d00050000b283";
        hex = hex.toUpperCase();

        CbdDataTransferBlock block = new CbdDataTransferBlock(hexStringToByteArray(hex));
        assert (block.verifyChecksum());
        CbdDataTransferBlock block2 = new CbdDataTransferBlock(block.getData(), block.getOffset());
        assertArrayEquals(hexStringToByteArray(hex), block2.generate());
    }
}