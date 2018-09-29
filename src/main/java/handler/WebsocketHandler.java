package handler;

import base.BaseHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebsocketHandler extends BaseHandler {

    public boolean joinRoom(String room_id, String client_id, ChannelHandlerContext ctx){
        if (roomMap.size()>0&&roomMap.get(room_id).getClientIds()!=null&&roomMap.get(room_id).getClientIds().get(client_id).getChannel()!=null){
            return false;
        }
        roomMap.get(room_id).getClientIds().get(client_id).setChannel(ctx.channel());//(client_id,ctx.channel());
        return true;
    }
    //发送给指定人消息
    public  void sendToMsg(Channel ctx, TextWebSocketFrame msg){
        for (Channel key:group) {
            if (key.id().equals(ctx.id())){
                key.writeAndFlush(msg);
            }
        }
    }
}
