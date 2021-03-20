package ru.isakov.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainHandler extends SimpleChannelInboundHandler<String> {
    private static final List<Channel> channels = new ArrayList<>(); // список всех подключенных клиентов
    private static AtomicInteger newClientIndex = new AtomicInteger(1);
    private String clientName;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился: " + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент #" + newClientIndex;
        newClientIndex.getAndIncrement();
        broadcastMessage("SERVER",  "Подключился новый клиент: " + clientName);
    }

    // channelRead0 для работы со строками
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("Получено сообщение: " + s);
        if (s.startsWith("/")) {
            if (s.startsWith("/changename ")) { // /changename myname1
                String newNickname = s.split("\\s", 2)[1];
                broadcastMessage("SERVER", "Клиент " + clientName + " сменил ник на " + newNickname);
                clientName = newNickname;
            }
            return;
        }
        broadcastMessage(clientName, s);
    }

/*
    // для работы с объектами (в виде байтов)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        while (buf.readableBytes() > 0) {
            System.out.print((char) buf.readByte());
        }
        System.out.println("");
        buf.release();
    }
*/

    // сообщения от сервера
    public void broadcastMessage(String clientName, String message) {
        String out = String.format("[%s]: %s\n", clientName, message);
        for (Channel c : channels) {
            c.writeAndFlush(out);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент " + clientName + " вышел из сети");
        channels.remove(ctx.channel()); // удаляем клиента из списка
        broadcastMessage("SERVER", "Клиент " + clientName + " вышел из сети");
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Клиент " + clientName + " отключился");
        channels.remove(ctx.channel());
        broadcastMessage("SERVER", "Клиент " + clientName + " вышел из сети");
        ctx.close();
    }
}