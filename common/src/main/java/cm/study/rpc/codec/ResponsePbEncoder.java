package cm.study.rpc.codec;

import cm.study.rpc.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponsePbEncoder extends MessageToByteEncoder<RpcResponse> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) throws Exception {
        out.writeBytes(ProtobufCodec.serializer(msg));
    }
}
