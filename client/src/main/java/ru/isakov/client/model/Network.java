package ru.isakov.client.model;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Network {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private SocketChannel channel; // сокет-канал
    NioEventLoopGroup workerGroup; // пул потоков для обработки сетевых событий

    private String host;
    private int port;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    public Network() {
        this(HOST, PORT);
    }

    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

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
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.setDaemon(true); // для автозавершения треда при закрытии формы (точнее - основного потока)
        t.start();
    }

    public void close() {
        // закрыть канал при завершении работы клиента
        channel.close();
    }

/*

    private void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }
*/


    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }





    public static void showNetworkError(String errorDetails, String errorTitle, Stage dialogStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
        alert.setTitle("Network Error");
        alert.setHeaderText(errorTitle);
        alert.setContentText(errorDetails);
        alert.showAndWait();
    }



}