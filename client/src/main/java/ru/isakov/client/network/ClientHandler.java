package ru.isakov.client.network;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.isakov.server.Command;

@Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("CLIENT channelRead: " + msg.toString());
        ctx.flush();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        super.channelReadComplete(ctx);
        System.out.println("CLIENT channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public void sendCommand(Command command) {
        context.writeAndFlush(command);
    }

}
