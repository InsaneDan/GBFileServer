package ru.isakov.client.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.server.Command;
import ru.isakov.server.CommandType;

public class ClientCommandHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ClientCommandHandler.class);

    private final Network network;
    private ChannelHandlerContext context;

    public ClientCommandHandler(Network network) {
        this.network = network;
    }

    // отправить команду
    public void sendCommand(Command command) {
        context.writeAndFlush(command);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.context = ctx;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // преобразуем полученный объект в Command
        Command command;
        try {
            command = (Command) msg;
            if (command.getType().equals(CommandType.AUTH_OK)) {
                logger.info("Auth OK");
                network.getClientApp().setAuthState(true);
                network.getClientApp().closeAuth();
            } else {
                logger.warn("Auth denied");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Auth denied", ButtonType.OK);
                alert.showAndWait();
                AlertPopup.show(Alert.AlertType.ERROR, "Ошибка",
                        "Ошибка авторизации", command.getData().toString());
            }
        } catch (ClassCastException classCastException) {
            logger.error(classCastException.toString());
        }


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
