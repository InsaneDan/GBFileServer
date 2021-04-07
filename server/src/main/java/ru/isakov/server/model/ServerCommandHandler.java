package ru.isakov.server.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.Command;
import ru.isakov.CommandType;
import ru.isakov.commands.AuthCommandData;
import ru.isakov.server.auth.BaseAuthService;

// обработка команд, предназначенных только для сервера
public class ServerCommandHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerCommandHandler.class);

    ChannelHandlerContext context;

    public void sendCommand(Command command) {
        context.writeAndFlush(command);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Command command;
        // получена команда
        if (msg instanceof Command) {

            command = (Command) msg;
            Command response = new Command();

            if (command.getType().equals(CommandType.AUTH)) {
                logger.debug("Получен запрос авторизации (AUTH command)");
                BaseAuthService baseAuthService = new BaseAuthService();
                baseAuthService.start();
//                baseAuthService.isAuthOK((AuthCommandData) command.getData());
                response = baseAuthService.isAuthOK((AuthCommandData) command.getData());
                baseAuthService.stop();

                ctx.writeAndFlush(response);

            }

            if (command.getType().equals(CommandType.REG)) {
                System.out.println("Server get REG command");
            }
        } else {
            logger.error("Полученный объект не является командой");
            throw new ClassCastException("Полученный объект не является командой");
        }

//        ctx.write(msg);
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
