package ru.isakov.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.server.model.*;

public class ServerApp {

    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);
    // порт по умолчанию, если не указан в параметрах при запуске
    private static final int PORT = 8189;

    public static void main(String[] args) {

        int port = args.length > 0 ? Integer.parseInt(args[0]) : PORT;
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
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    // inbound
                                    new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ChunkedWriteHandler(),
                                    // outbound
                                    new LengthFieldPrepender(4),
                                    new ObjectEncoder(),
                                    // app
                                    new ObjectEchoServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);;
            logger.info("Сервер запущен");
            // сервер должен стартовать .sync() на указанном порту .bind(port)
            // ChannelFuture - исполняемая задача
            ChannelFuture future = b.bind(port).sync();

            // TODO: 24.03.2021 подключение базы данных для проверки логина/пароля и получения рабочей директории

            // ожидаем, пока сервер не будет остановлен
            future.channel().closeFuture().sync();


        } catch (Exception e) {
           logger.error(e.getMessage());
        } finally {

            // TODO: 24.03.2021 отсоединиться от БД

            // закрываем пулы потоков после остановки сервера (даем возможность GC очистить память)
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("Сервер остановлен");
        }
    }
}
