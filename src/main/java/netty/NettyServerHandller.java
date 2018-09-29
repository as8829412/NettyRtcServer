package netty;

import base.BaseHandler;
import handler.HttpsParamsHandler;
import handler.RoomHandler;
import handler.WebsocketHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import message.MessageRequest;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.buffer.Unpooled.copiedBuffer;

//自定义业务
public class NettyServerHandller extends SimpleChannelInboundHandler<Object> {
    private static final Logger log= LoggerFactory.getLogger(NettyServerHandller.class);
    // 用于服务器端web套接字打开和关闭握手
    private WebSocketServerHandshaker handshaker;
    //客户端与服务端创建连接的时候调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("客户端与服务端连接开启，客户端remoteAddress：" + ctx.channel().remoteAddress());
        BaseHandler.group.add(ctx.channel());
    }
    //客户端与服务端断开连接的时候调用
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("客户端与服务端连接关闭..."+ ctx.channel().remoteAddress().toString());
        BaseHandler.group.remove(ctx.channel());
    }

    //每个信息入站调用
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {// 如果是HTTP请求，进行HTTP操作
            handlerHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {// 如果是Websocket请求，则进行websocket操作
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 处理客户端与服务端之前的websocket业务
     *
     * @param ctx
     * @param frame
     */
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否是关闭链路的指令
        //System.out.println("websocket get");
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            System.out.println("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
        }
//        String requestMsg = ((TextWebSocketFrame) frame).text();
//        Channel channel = ctx.channel();
//        System.out.println(channel.remoteAddress() + ": " + requestMsg);
//        //返回【谁发的发给谁】
//        ctx.channel().writeAndFlush(new TextWebSocketFrame("来自服务端: " + LocalDateTime.now()));
        // 返回应答消息
        String requestMsg = ((TextWebSocketFrame) frame).text();
        System.out.println("服务端收到：id:" +ctx.channel().id()+",消息:"+ requestMsg);
        TextWebSocketFrame tws =null;// new TextWebSocketFrame( ctx.channel().id() + "：" + requestMsg+ ",时间:"+LocalDateTime.now());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MessageRequest msg=objectMapper.readValue(requestMsg,MessageRequest.class);
            String error="";
            WebsocketHandler wsHandler=new WebsocketHandler();
            switch (msg.getCmd()){
                case "register":
                    if (msg.getRoomid() ==""||msg.getRoomid()=="null"){
                        error="RoomID is null";
                        break;
                    }
                    if (msg.getClientid() ==""||msg.getClientid()=="null") {
                        error = "ClientID is null";
                        break;
                    }
                    boolean bool=wsHandler.joinRoom(msg.getRoomid(), msg.getClientid(), ctx);
                    if (!bool){
                        error="register is false";
                    };
                    break;
                case "send":
                    if (msg.getMsg()==""){
                        error ="msg is miss";
                        break;
                    }
                    //判断房间是否存在用户session
                    if (BaseHandler.roomMap.get(msg.getRoomid()).getClientIds().get(msg.getToUser()).getChannel()!=null){
                        error ="toUser:"+msg.getToUser()+" is not register";
                        break;
                    }
                    tws=new TextWebSocketFrame(msg.getMsg());
                    wsHandler.sendToMsg(BaseHandler.roomMap.get(msg.getRoomid()).getClientIds().get(msg.getToUser()).getChannel(),tws);
                    break;
                default:
                    error="402";
            }
            if (error!=""){
                tws=new TextWebSocketFrame(error);
                ctx.channel().writeAndFlush(tws);
            }
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //sendMessage(ctx,tws);
        // 群发
        //BaseHandler.group.writeAndFlush(tws);
    }

    //广播(除自己外)
    private void sendMessage(ChannelHandlerContext ctx,TextWebSocketFrame msg){
        for (Channel key:BaseHandler.group) {
            if (!key.id().equals(ctx.channel().id())){
                key.writeAndFlush(msg);
            }
        }
    }
    /**
     * 处理客户端向服务端发起http握手请求的业务
     *
     * @param ctx
     * @param request
     */
    private void handlerHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        FullHttpResponse response = null;
        if (request.method() == HttpMethod.GET){
            String data = "GET method over";
            ByteBuf buf = copiedBuffer(data, CharsetUtil.UTF_8);
            response = HttpsParamsHandler.responseOK(HttpResponseStatus.OK, buf);
        }else if (request.method() == HttpMethod.POST){
            String uri=request.uri().replace("/","");
            String roomId=HttpsParamsHandler.getPostParamsFromChannel(request).get("roomId").toString();
            String clientId=HttpsParamsHandler.getPostParamsFromChannel(request).get("clientId").toString();
            String data = "POST method over";
            String host=request.headers().get("Host");
            RoomHandler roomHandler=new RoomHandler();
            if (!"".equals(roomId)&&null!=roomId&&!"".equals(clientId)&&null!=clientId) {
                switch (uri) {
                    case "join":
                        data = roomHandler.joinRoom(roomId, clientId,host);
                        break;
                    case "message":
                        String msg = HttpsParamsHandler.getPostParamsFromChannel(request).get("msg").toString();
                        data=roomHandler.sendMessage(roomId,clientId,msg);
                        break;
                    case "leave":
                        data = roomHandler.leaveRoom(roomId, clientId);
                        break;
                    case "call":
                        String toUser=HttpsParamsHandler.getPostParamsFromChannel(request).get("toUser").toString();
                        roomHandler.call(roomId,clientId,toUser);
                        break;
                    case "callBack":
                        String flag = HttpsParamsHandler.getPostParamsFromChannel(request).get("msg").toString();
                        roomHandler.callBack(roomId,clientId,flag);
                        break;
                    default:
                        data = "{\"result\":\"error 404!\",\"params\":\"{}\"}";
                }
            }else{
                data="{\"result\":\"roomId or clientId error!\",\"params\":\"{}\"}";
            }
            ByteBuf content = copiedBuffer(data, CharsetUtil.UTF_8);
            response = HttpsParamsHandler.responseOK(HttpResponseStatus.OK, content);

        } else {
            response = HttpsParamsHandler.responseOK(HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
        }
        // 发送响应(发起人)
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    // 读操作时捕获到异常时调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
    // 通知处理器最后的 channelRead() 是当前批处理中的最后一条消息时调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    /*
     * 功能：读空闲时移除Channel
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent evnet = (IdleStateEvent) evt;
            // 判断Channel是否读空闲, 读空闲时移除Channel
            if (evnet.state().equals(IdleState.READER_IDLE)) {
                //UserInfoManager.removeChannel(ctx.channel());
            }
        }
    }

}
