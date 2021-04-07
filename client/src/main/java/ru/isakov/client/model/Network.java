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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isakov.Command;
import ru.isakov.CommandHandler;
import ru.isakov.client.ClientApp;
import ru.isakov.client.controller.AuthController;

public class Network {

    private static final Logger logger = LoggerFactory.getLogger(Network.class);

    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private final String host;
    private final int port;

    private Boolean connected = null; // состояние подключения к серверу
    private ClientApp clientApp;

    private SocketChannel channel; // сокет-канал
    NioEventLoopGroup workerGroup; // пул потоков для обработки сетевых событий

    private final ClientCommandHandler clientCommandHandler = new ClientCommandHandler(this);

    // конструктор
    public Network() { this(HOST, PORT); }


    public Network(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Network(ClientApp clientApp) {
        this();
        this.clientApp = clientApp;
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
                                p.addLast(clientCommandHandler);
                            }
                        });
                ChannelFuture future = b.connect(host, port).sync();
                connected = true; // флаг - успешно подключились
                future.channel().closeFuture().sync(); // ждем команду на остановку
            } catch (Exception e) {
                connected = false; // флаг - соединение с сервером не установлено
                logger.error("Не удалось установить соединение с сервером!");
                logger.error(e.getMessage());
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.setDaemon(true); // для автозавершения треда при закрытии формы (основного потока)
        t.start();
    }

    // получить состояние подключения к серверу
    public Boolean isConnected() {
//        if (channel.isOpen() != connected) {
//            System.out.println("ОШИБКА ПРИ ОПРЕДЕЛЕНИИ СОСТОЯНИЯ ПОДКЛЮЧЕНИЯ !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        }
        return connected;
    }

    public ClientApp getClientApp() {
        return clientApp;
    }

    public void close() {
        // сообщение о завершении работы клиента
        sendCommand(Command.exitCommand());
        // закрыть канал при завершении работы клиента
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
    }

    public void sendCommand(Command command) {
        clientCommandHandler.sendCommand(command);
    }

}