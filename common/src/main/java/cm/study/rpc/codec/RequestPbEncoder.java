package cm.study.rpc.codec;

import cm.study.rpc.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestPbEncoder extends MessageToByteEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
        out.writeBytes(ProtobufCodec.serializer(msg));
    }
}
