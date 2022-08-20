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

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DataTransfer {
    protected final AtomicBoolean paused = new AtomicBoolean();
    protected final AtomicBoolean aborted = new AtomicBoolean();
    protected final AtomicBoolean running = new AtomicBoolean();
    protected final AtomicBoolean complete = new AtomicBoolean();
    protected final AtomicBoolean failed = new AtomicBoolean();

    protected float transferSpeed = 0.0f; //in B/s

    public abstract void run();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public abstract boolean isComplete();

    @SuppressWarnings("unused")
    public abstract boolean hasFailed();

    @SuppressWarnings("unused")
    public abstract float getProgress();

    @SuppressWarnings("unused")
    public abstract String getName();

    @SuppressWarnings("unused")
    public abstract boolean isPaused();

    @SuppressWarnings("unused")
    public abstract void setPaused(boolean paused);

    @SuppressWarnings("unused")
    public abstract boolean isRunning();

    @SuppressWarnings("unused")
    public abstract void abort();

    @SuppressWarnings("unused")
    public abstract boolean isAborted();

    @SuppressWarnings("unused")
    public abstract void end();

    @SuppressWarnings("unused")
    public float getTransferSpeed() {
        return transferSpeed;
    }
}
