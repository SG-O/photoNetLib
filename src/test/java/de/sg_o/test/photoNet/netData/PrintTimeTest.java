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

import de.sg_o.lib.photoNet.netData.PrintTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PrintTimeTest {
    private PrintTime secondTest;
    private PrintTime minuteTest;
    private PrintTime hourTest;
    private PrintTime veryLongTime;
    private PrintTime negativeTime;

    @BeforeEach
    void setUp() {
        this.secondTest = new PrintTime(21);
        this.minuteTest = new PrintTime(1041); // 17 min 21 sec
        this.hourTest = new PrintTime(29841); // 8h 17 min 21 sec
        this.veryLongTime = new PrintTime(Long.MAX_VALUE);
        this.negativeTime = new PrintTime(Long.MIN_VALUE);
    }

    @Test
    void hourTest() {
        assertEquals(0, secondTest.getHours());
        assertEquals(0, minuteTest.getHours());
        assertEquals(8, hourTest.getHours());
        assertEquals(2147483647, veryLongTime.getHours());
        assertEquals(0, negativeTime.getHours());
    }

    @Test
    void minuteTest() {
        assertEquals(0, secondTest.getMinutes());
        assertEquals(17, minuteTest.getMinutes());
        assertEquals(17, hourTest.getMinutes());
        assertEquals(0, veryLongTime.getMinutes());
        assertEquals(0, negativeTime.getMinutes());
    }

    @Test
    void secondsTest() {
        assertEquals(21, secondTest.getSeconds());
        assertEquals(21, minuteTest.getSeconds());
        assertEquals(21, hourTest.getSeconds());
        assertEquals(0, veryLongTime.getSeconds());
        assertEquals(0, negativeTime.getSeconds());
    }

    @Test
    void timeTest() {
        assertEquals(21L, secondTest.getTime());
        assertEquals(1041L, minuteTest.getTime());
        assertEquals(29841L, hourTest.getTime());
        assertEquals(7730941129200L, veryLongTime.getTime());
        assertEquals(0L, negativeTime.getTime());
    }

    @Test
    void equalsTest() {
        PrintTime secondTest = new PrintTime(21);
        PrintTime minuteTest = new PrintTime(1041); // 17 min 21 sec
        PrintTime hourTest = new PrintTime(29841); // 8h 17 min 21 sec

        assertEquals(this.secondTest, secondTest);
        assertEquals(this.minuteTest, minuteTest);
        assertEquals(this.hourTest, hourTest);

        assertNotEquals(this.secondTest, this.minuteTest);
        assertNotEquals(this.minuteTest, this.hourTest);
        assertNotEquals(this.hourTest, this.secondTest);
    }

    @Test
    void hashTest() {
        PrintTime secondTest = new PrintTime(21);
        PrintTime minuteTest = new PrintTime(1041); // 17 min 21 sec
        PrintTime hourTest = new PrintTime(29841); // 8h 17 min 21 sec

        assertEquals(this.secondTest.hashCode(), secondTest.hashCode());
        assertEquals(this.minuteTest.hashCode(), minuteTest.hashCode());
        assertEquals(this.hourTest.hashCode(), hourTest.hashCode());

        assertNotEquals(this.secondTest.hashCode(), this.minuteTest.hashCode());
        assertNotEquals(this.minuteTest.hashCode(), this.hourTest.hashCode());
        assertNotEquals(this.hourTest.hashCode(), this.secondTest.hashCode());
    }

    @Test
    void stringTest() {
        assertEquals("PrintTime{time=21(0h0m21s)}", secondTest.toString());
        assertEquals("PrintTime{time=1041(0h17m21s)}", minuteTest.toString());
        assertEquals("PrintTime{time=29841(8h17m21s)}", hourTest.toString());
        assertEquals("PrintTime{time=7730941129200(2147483647h0m0s)}", veryLongTime.toString());
        assertEquals("PrintTime{time=0(0h0m0s)}", negativeTime.toString());
    }
}