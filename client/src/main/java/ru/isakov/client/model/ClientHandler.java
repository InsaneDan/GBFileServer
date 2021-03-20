package ru.isakov.client.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.isakov.client.Callback;

import java.nio.charset.StandardCharsets;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private Callback onMessageReceivedCallback;

    public ClientHandler(Callback onMessageReceivedCallback) {
        this.onMessageReceivedCallback = onMessageReceivedCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        if (onMessageReceivedCallback != null) {
            System.out.println(s);
            onMessageReceivedCallback.callback(s);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
