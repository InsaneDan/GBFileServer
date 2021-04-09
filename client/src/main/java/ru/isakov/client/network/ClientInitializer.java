package ru.isakov.client.network;

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

public class ClientInitializer extends ChannelInitializer<SocketChannel> {

    private static final ChannelHandler DECODER = new ObjectDecoder(ClassResolvers.cacheDisabled(null));
    private static final ChannelHandler ENCODER = new ObjectEncoder();
    private static final int lengthFieldLength = 4; // длина заголовка
    private static final ChannelHandler LFB_FRAME_DECODER =
            new LengthFieldBasedFrameDecoder(1024*1024*100, 0, lengthFieldLength, 0, lengthFieldLength);
    private static final ChannelHandler LENGTH_FIELD_PREPENDER =
            new LengthFieldPrepender(lengthFieldLength);
    private static final ClientHandler CLIENT_HANDLER = new ClientHandler();

    private final SslContext sslCtx;

    public ClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), ClientNetwork.HOST, ClientNetwork.PORT));
        }

        // наполнение конвейера
        pipeline.addLast(
                LFB_FRAME_DECODER,
                LENGTH_FIELD_PREPENDER,
                DECODER,
                ENCODER,
                CLIENT_HANDLER
        );
    }
}
