package ru.isakov.server.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    private static CopyOnWriteArrayList<Channel> clients = new CopyOnWriteArrayList<>(); // список подключенных клиентов
    private static AtomicInteger newClientIndex = new AtomicInteger(0);
    private String clientName;

    public static CopyOnWriteArrayList<Channel> getClients() { return clients; }
    public static void setClients(CopyOnWriteArrayList<Channel> clients) { ConnectionHandler.clients = clients; }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Запрос на подключение: " + ctx);
        Channel ch = ctx.channel();
        if(!clients.contains(ch)) {
            clients.add(ch);
            clientName = "Клиент #" + newClientIndex.getAndIncrement();
            logger.info("Подключен новый пользователь: " + clientName);
            return;
        }
        logger.info("Пользователь уже был подключен ранее");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Клиент " + clientName + " вышел из сети");
        Channel ch = ctx.channel();
        if(clients.contains(ch)) {
            clients.remove(ch);
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) throws Exception {
        logger.error(throwable.getMessage());
    }
}