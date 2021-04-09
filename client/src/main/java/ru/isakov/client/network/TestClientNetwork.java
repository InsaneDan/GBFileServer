package ru.isakov.client.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import ru.isakov.client.network.ClientNetwork;
import ru.isakov.server.Command;
import ru.isakov.server.CommandType;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestClientNetwork {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static Channel channel;
    static ChannelFuture lastWriteFuture;

    public static void main(String[] args) throws Exception {

        ClientNetwork clientNetwork = new ClientNetwork();
        clientNetwork.connect();
    }

}
