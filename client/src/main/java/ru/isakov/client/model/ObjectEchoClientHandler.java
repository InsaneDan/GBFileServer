package ru.isakov.client.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.isakov.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler implementation for the object examples.echo client.  It initiates the
 * ping-pong traffic between the object examples.echo client and server by sending the
 * first message to the server.
 */
public class ObjectEchoClientHandler extends ChannelInboundHandlerAdapter {

//    private final List<Integer> firstMessage;
    private final Command firstMessage;

    /**
     * Creates a client-side handler.
     */
    public ObjectEchoClientHandler() {
        firstMessage = Command.exitCommand();
//        firstMessage = new ArrayList<Integer>(100);
//        for (int i = 0; i < 100; i ++) {
//            firstMessage.add(Integer.valueOf(i));
//        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // Send the first message if this handler is a client-side handler.
        ctx.writeAndFlush(firstMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // Echo back the received object to the server.
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
