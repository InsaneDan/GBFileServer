package ru.isakov.client.model;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.Command;
//import ru.isakov.Command;


public class Network {

    private static final Logger logger = LoggerFactory.getLogger(Network.class);

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private SocketChannel channel; // сокет-канал
    NioEventLoopGroup workerGroup; // пул потоков для обработки сетевых событий

    public Network(Callback onMessageReceivedCallback) {
        Thread t = new Thread(() -> {
            // запускаем в отдельном потоке, т.к. future.channel().closeFuture().sync() блокирующая операция => будет заблокирован запуск интерфейса ???
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
                                // преобразуем String в ByteBuffer, иначе при пересылке (в методе sendMessage) и получении будет ошибка
                                socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ClientHandler(onMessageReceivedCallback));
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync();
                future.channel().closeFuture().sync(); // ждем команду на остановку
            } catch (Exception e) {
                logger.error("Не удалось установить соединение с сервером!");
                logger.error(e.toString());
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
        channel.writeAndFlush(command);
    }

}