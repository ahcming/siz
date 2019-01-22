package cm.study.rpc.codec;

import cm.study.rpc.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RequestPbDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = in.readableBytes();
        byte[] buf = new byte[size];
        in.readBytes(buf);

        out.add(ProtobufCodec.deserializer(buf, RpcRequest.class));
    }
}
