/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ru.isakov.server.server;

import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import ru.isakov.server.Command;

import java.net.InetAddress;
import java.util.Date;

/**
 * Handles a server-side channel.
 */
@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("SERVER channelActive");

        // Send greeting for a new connection.
        ctx.write("Welcome to " + InetAddress.getLocalHost().getHostName() + "!");
        ctx.write("It is " + new Date() + " now.");
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SERVER channelRead object: " + msg.toString());

        if (msg instanceof Command) {
            System.out.println("SERVER got command: " + ((Command) msg).getType() + " > " + ((Command) msg).getData());
        } else {
            System.out.println("WRONG COMMAND");
        }

        // Generate and write a response.
        String response;
        boolean close = false;
        if (msg == null) {
            response = "Please type something.";
        } else if ("bye".equals(msg.toString().toLowerCase())) {
            response = "Have a good day!";
            close = true;
        } else {
            response = "Did you say '" + msg + "'?";
        }

        // We do not need to write a ChannelBuffer here.
        // We know the encoder inserted at TelnetPipelineFactory will do the conversion.
        System.out.println("SERVER *** WRITE *** object: " + response);
        ChannelFuture future = ctx.write(response);

        // Close the connection after sending 'Have a good day!'
        // if the client has sent 'bye'.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("SERVER channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
