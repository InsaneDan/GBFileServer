package ru.isakov.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.logging.Logger;

// to start debug mode: mvn clean javafx:run@debug

public class ServerApp {
    private static final int PORT = 8189;

    public static void main(String[] args) {

        Logger logger = Logger.getLogger(ServerApp.class.getName());

        int port = PORT; // порт по умолчанию, если не указан в параметрах при запуске
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // создаем два пула потоков (менеджеры потоков): для обработки подключений (bossGroup) и обработки данных
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // Bootstrap выполняет преднастройку (инициализацию и конфигурацию) сервера
            ServerBootstrap b = new ServerBootstrap();
            // Bootstrap будет использовать: 2 группы потоков (bossGroup, workerGroup), канал (NioServerSocketChannel),
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // после подключения клиента информация о соединении хранится в SocketChannel
                        //
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
//                            socketChannel.pipeline().addLast(new MainHandler()); // работа с байтами
                            socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new MainHandler()); // конвертируем String в ByteBuffer при отправке и получении
                        }
                    });
            logger.info("Сервер запущен");
            // сервер должен стартовать .sync() на указанном порту .bind(port)
            // ChannelFuture - исполняемая задача
            ChannelFuture future = b.bind(port).sync();
            // ожидаем, пока сервер не будет остановлен
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // закрываем пулы потоков после остановки сервера (даем возможность GC очистить память)
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("Сервер остановлен");
        }
    }
}
