package ru.isakov.client.model;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.Command;
import ru.isakov.CommandHandler;

import java.util.ArrayList;
import java.util.List;

public class Network {

    private static final Logger logger = LoggerFactory.getLogger(Network.class);

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private final String host;
    private final int port;

    private SocketChannel channel; // сокет-канал
    NioEventLoopGroup workerGroup; // пул потоков для обработки сетевых событий

    private final CommandHandler commandHandler = new CommandHandler(1);

    // конструктор
    public Network() { this(HOST, PORT); }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
        this.connect();
    }

    public void connect() {
        Thread t = new Thread(() -> {
            // запускаем в отдельном потоке
            workerGroup = new NioEventLoopGroup();
            try {
                // создаем клиентский Bootstrap
                Bootstrap b = new Bootstrap();
                // группа - канал - конвейер для сокетканала
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel; // запоминаем ссылку на соединение
                                ChannelPipeline p = socketChannel.pipeline();
                                p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                p.addLast(new LengthFieldPrepender(4));
                                p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                                p.addLast(new ObjectEncoder());
                                p.addLast(commandHandler);
                            }
                        });
                ChannelFuture future = b.connect(host, port).sync();
                future.channel().closeFuture().sync(); // ждем команду на остановку
            } catch (Exception e) {
                logger.error("Не удалось установить соединение с сервером!");
                logger.error(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR, "Не удалось установить соединение с сервером!", ButtonType.OK);
                alert.showAndWait();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.setDaemon(true); // для автозавершения треда при закрытии формы (точнее - основного потока)
        t.start();

    }

    public void close() {
        // сообщение о завершении работы клиента
//        sendCommand(Command.exitCommand());
        // закрыть канал при завершении работы клиента
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }

    public void sendCommand(Command command) {
//        channel.writeAndFlush(command);
        commandHandler.sendCommand(Command.exitCommand());
    }

}