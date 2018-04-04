/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package jdk.incubator.http.internal.websocket;

import java.nio.ByteBuffer;

public class MockTransport extends TransportSupplier {

    public MockTransport() {
        super(new NullRawChannel());
    }

    public static class NullRawChannel implements RawChannel {

        @Override
        public void registerEvent(RawEvent event) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ByteBuffer initialByteBuffer() {
            return ByteBuffer.allocate(0);
        }

        @Override
        public ByteBuffer read() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long write(ByteBuffer[] srcs, int offset, int length) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void shutdownInput() {
        }

        @Override
        public void shutdownOutput() {
        }

        @Override
        public void close() {
        }
    }
}