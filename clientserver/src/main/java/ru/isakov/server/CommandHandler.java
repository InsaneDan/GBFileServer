package ru.isakov.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Handles both client-side and server-side handler depending on which constructor was called

public class CommandHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
    private ChannelHandlerContext context;
    private int owner;

    // конструктор: owner = 0 для сервера, = 1 для клиента
    public CommandHandler(int owner) {
        this.owner = owner;
    }

    // отправить команду
    public void sendCommand(Command command) {
        context.writeAndFlush(command);
    }

    // клиент отключился (разрыв соединения, закрыл приложение, выключил WiFi)
//    @Override
//    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        super.channelUnregistered(ctx);
//    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
        if (owner == 1) {
            logger.info("КЛИЕНТ: Подключение к серверу выполнено");
        } else {
            logger.info("СЕРВЕР: Клиент подключился");
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // преобразуем полученный объект в Command
//        Command command;
//        try {
//            command = (Command) msg;
//            if (command.getType().equals(CommandType.AUTH_OK)) {
//                logger.warn("АВТОРИЗИРОВАН!");
//            }
//        } catch (ClassCastException classCastException) {
//            logger.error(classCastException.toString());
//            return;
//        }


//        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
//        System.out.println("channelReadComplete");
//        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
