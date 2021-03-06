/*
 * Copyright (c) 2015, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
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
package jdk.incubator.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import jdk.incubator.http.internal.common.SSLTube;
import jdk.incubator.http.internal.common.Utils;
import jdk.incubator.http.internal.common.SysLogger.Level;
import java9.util.concurrent.CompletableFuture;

/**
 * An SSL tunnel built on a Plain (CONNECT) TCP tunnel.
 */
class AsyncSSLTunnelConnection extends AbstractAsyncSSLConnection {

    final PlainTunnelingConnection plainConnection;
    final PlainHttpPublisher writePublisher;
    volatile SSLTube flow;

    AsyncSSLTunnelConnection(InetSocketAddress addr,
                             HttpClientImpl client,
                             String[] alpn,
                             InetSocketAddress proxy)
    {
        super(addr, client, Utils.getServerName(addr), alpn);
        this.plainConnection = new PlainTunnelingConnection(addr, proxy, client);
        this.writePublisher = new PlainHttpPublisher();
    }

    @Override
    public CompletableFuture<Void> connectAsync() {
        debug.log(Level.DEBUG, "Connecting plain tunnel connection");
        // This will connect the PlainHttpConnection flow, so that
        // its HttpSubscriber and HttpPublisher are subscribed to the
        // SocketTube
        return plainConnection
                .connectAsync()
                .thenApply( unused -> {
                    debug.log(Level.DEBUG, "creating SSLTube");
                    // create the SSLTube wrapping the SocketTube, with the given engine
                    flow = new SSLTube(engine,
                                       client().theExecutor(),
                                       plainConnection.getConnectionFlow());
                    return null;} );
    }

    @Override
    boolean connected() {
        return plainConnection.connected(); // && sslDelegate.connected();
    }

    @Override
    HttpPublisher publisher() { return writePublisher; }

    @Override
    public String toString() {
        return "AsyncSSLTunnelConnection: " + super.toString();
    }

    @Override
    PlainTunnelingConnection plainConnection() {
        return plainConnection;
    }

    @Override
    ConnectionPool.CacheKey cacheKey() {
        return ConnectionPool.cacheKey(address, plainConnection.proxyAddr);
    }

    @Override
    public void close() {
        plainConnection.close();
    }

    @Override
    void shutdownInput() throws IOException {
        plainConnection.channel().shutdownInput();
    }

    @Override
    void shutdownOutput() throws IOException {
        plainConnection.channel().shutdownOutput();
    }

    @Override
    SocketChannel channel() {
        return plainConnection.channel();
    }

    @Override
    boolean isProxied() {
        return true;
    }

    @Override
    SSLTube getConnectionFlow() {
       return flow;
   }
}
