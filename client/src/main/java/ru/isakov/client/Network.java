package ru.isakov.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Network {
    private SocketChannel channel;

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    public Network(Callback onMessageReceivedCallback) {
        Thread t = new Thread(() -> { // запускаем в отдельном потоке, т.к. future.channel().closeFuture().sync() блокирующая операция, будет заблокирован запуск интерфейса
            // пул потоков для обработки сетевых событий
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                // создаем Bootstrap (обычный, не ServerBootstrap)
                Bootstrap b = new Bootstrap();
                // группа - канал - конвейер для сокетканала
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel; // запоминаем ссылку на соединение
                                // преобразуем String в ByteBuffer, иначе при пересылке (в методе sendMessage) и получении будем получать ошибку
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
        t.setDaemon(true); // будет автозавершаться при закрытии формы
        t.start();
    }

    public void close() {
        channel.close(); // закрыть канал при завершении работы клиента
    }

    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }
}