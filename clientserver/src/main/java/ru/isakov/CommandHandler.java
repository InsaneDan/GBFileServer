package ru.isakov;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles both client-side and server-side handler depending on which constructor was called.
 */
public class CommandHandler extends ChannelInboundHandlerAdapter {

    private int owner;
    ChannelHandlerContext context;

    public CommandHandler(int owner) {
        this.owner = owner;
    }

    public void sendCommand(Command command) {
        context.writeAndFlush(command);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
        if (owner == 1) {
            System.out.println("КЛИЕНТ: Подключение к серверу выполнено");
        } else {
            System.out.println("СЕРВЕР: Клиент подключился");
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (owner == 1) {
            System.out.println("Msg received by CLIENT");
        }
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
