package ru.isakov;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseDataEncoder extends MessageToByteEncoder<Command> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf out) throws Exception {
//        out.writeInt(command.getData().toString());
    }
}