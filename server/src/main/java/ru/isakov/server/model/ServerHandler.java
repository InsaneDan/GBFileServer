package ru.isakov.server.model;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;



public class ServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);


    private static final List<Channel> channels = new ArrayList<>(); // список подключенных клиентов
    private static AtomicInteger newClientIndex = new AtomicInteger(1);
    private String clientName;


    // TODO: 26.03.2021 после авторизации для каждого клиента своя папка, временно - общая
    private String serverPath = "D:/testDirServer";

    private ChannelHandlerContext ctx;

    public void setServerPath(String serverPath){
        this.serverPath = serverPath;
    }

    public String getServerPath(){
        return serverPath;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }






    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Запрос на подключение: " + ctx);
        channels.add(ctx.channel());
        clientName = "Клиент #" + newClientIndex;
        newClientIndex.getAndIncrement();
        logger.info("Подключился новый клиент: " + clientName);
    }

    // channelRead0 для работы со строками
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
//        System.out.println("Получено сообщение: " + s);
//        if (s.startsWith("/")) {
//            if (s.startsWith("/changename ")) { // /changename myname1
//                String newNickname = s.split("\\s", 2)[1];
//                broadcastMessage("SERVER", "Клиент " + clientName + " сменил ник на " + newNickname);
//                clientName = newNickname;
//            }
//            return;
//        }
//        broadcastMessage(clientName, s);
    }

    // для работы с объектами (в виде байтов)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        Command command = (Command) msg;


        buf.release();
    }

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