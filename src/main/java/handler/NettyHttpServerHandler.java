package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static io.netty.buffer.Unpooled.copiedBuffer;
/*
 * 自定义处理的handler
 */
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final static Logger log= LoggerFactory.getLogger(NettyHttpServerHandler.class);

    /*
     * 处理请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        FullHttpResponse response = null;
        log.info("httpRequest:"+fullHttpRequest);
        if (fullHttpRequest.method() == HttpMethod.GET) {
            String data = "GET method over";
            ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
            response = HttpsParamsHandler.responseOK(HttpResponseStatus.OK, buf);
        } else if (fullHttpRequest.method() == HttpMethod.POST) {
            String[] uri=fullHttpRequest.uri().split("/");
            String data = "POST method over";
            String host=fullHttpRequest.headers().get("Host");
            RoomHandler roomHandler=new RoomHandler();
            String roomId=uri[2];
            if ("join".equals(uri[1])){
                String clientId = String.valueOf(new Random().nextInt(1000000000));
                data = roomHandler.joinRoom(roomId, clientId,host);
            }
            if ("message".equals(uri[1])){
                String msg = HttpsParamsHandler.getPostParamsFromChannel(fullHttpRequest).get("msg").toString();
                data=roomHandler.sendMessage(roomId,uri[3],msg);
            }
            if ("leave".equals(uri[1])){
                data = roomHandler.leaveRoom(roomId, uri[3]);
            }
            ByteBuf content = copiedBuffer(data, CharsetUtil.UTF_8);
            response = HttpsParamsHandler.responseOK(HttpResponseStatus.OK, content);

        } else {
            response = HttpsParamsHandler.responseOK(HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
        }
        // 发送响应(发起人)
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


}
