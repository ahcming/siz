package cm.study.rpc.codec;

import cm.study.rpc.RpcRequest;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.util.Arrays;

import static org.testng.Assert.*;

public class ProtobufCodecTest {

    @Test
    public void testSerializer() {
        RpcRequest request = new RpcRequest();
        request.setApiName("aaa");
        request.setParams(Arrays.asList("A", "B"));

        byte[] post = ProtobufCodec.serializer(request);
        System.out.println("--> " + post.length);

        RpcRequest from = ProtobufCodec.deserializer(post, RpcRequest.class);
        System.out.println("--> " + from);
    }

    @Test
    public void testDeserializer() {
    }
}