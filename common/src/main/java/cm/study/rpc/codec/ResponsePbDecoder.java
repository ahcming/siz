package cm.study.rpc.codec;

import cm.study.rpc.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ResponsePbDecoder extends ByteToMessageDecoder {

    private static Logger ILOG = LoggerFactory.getLogger(ResponsePbDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int size = in.readableBytes();
        byte[] buf = new byte[size];
        in.readBytes(buf);

        ILOG.debug("receive response data, size: {}", size);

        out.add(ProtobufCodec.deserializer(buf, RpcResponse.class));
    }
}
