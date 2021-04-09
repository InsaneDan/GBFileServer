/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ru.isakov.server.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final ChannelHandler DECODER = new ObjectDecoder(ClassResolvers.cacheDisabled(null));
    private static final ChannelHandler ENCODER = new ObjectEncoder();
    private static final int lengthFieldLength = 4; // длина заголовка
    private static final ChannelHandler LFB_FRAME_DECODER =
            new LengthFieldBasedFrameDecoder(1024*1024*100, 0, lengthFieldLength, 0, lengthFieldLength);
    private static final ChannelHandler LENGTH_FIELD_PREPENDER =
            new LengthFieldPrepender(lengthFieldLength);
    private static final ServerHandler SERVER_HANDLER = new ServerHandler();

    private final SslContext sslCtx;

    public ServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        // наполнение конвейера
        pipeline.addLast(
                LFB_FRAME_DECODER,
                LENGTH_FIELD_PREPENDER,
                DECODER,
                ENCODER,
                SERVER_HANDLER
        );
    }
}
