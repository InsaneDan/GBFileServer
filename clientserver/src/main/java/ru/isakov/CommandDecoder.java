package ru.isakov;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class CommandDecoder extends ReplayingDecoder<Command> {

    private final Charset charset = Charset.forName("UTF-8");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//
//        Command command = new Command();
//        // тип команды
//        command.setType(CommandType.values()[in.readInt()]);
//        // значение команды
//        command.setData(in.readInt());
//        int strLen = in.readInt();
//        data.setStringValue(
//                in.readCharSequence(strLen, charset).toString());
//        out.add(data);
    }
}
