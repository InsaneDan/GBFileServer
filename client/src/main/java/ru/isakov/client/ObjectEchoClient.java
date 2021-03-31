package ru.isakov.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import ru.isakov.client.model.ObjectEchoClientHandler;

public final class ObjectEchoClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8189"));

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();

                    p.addLast(
                            // inbound
                            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                            new ByteArrayDecoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            // outbound
                            new LengthFieldPrepender(4),
                            new ByteArrayEncoder(),
                            new ObjectEncoder(),
                            // app
                            new ObjectEchoClientHandler());
                }
             });

            // Start the connection attempt.
            b.connect(HOST, PORT).sync().channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
