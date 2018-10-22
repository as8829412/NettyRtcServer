package handler;

import base.BaseHandler;
import entity.Client;
import entity.Room;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import message.MessageResponse;
import net.sf.json.JSONObject;


public class WebsocketHandler extends BaseHandler {
    //private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    public boolean joinRoom(String room_id, String client_id, ChannelHandlerContext ctx){
        if (roomMap.size()>0&&roomMap.get(room_id).getClientIds()!=null&&roomMap.get(room_id).getClientIds().get(client_id).getChannel()!=null){
            return false;
        }
        roomMap.get(room_id).getClientIds().get(client_id).setChannel(ctx.channel());//(client_id,ctx.channel());
        return true;
    }

    public static boolean registerGroup(String room_id, String client_id, ChannelHandlerContext ctx){
        if (groupRoomMap.size()>0&&groupRoomMap.get(room_id).getClientIds()!=null&&groupRoomMap.get(room_id).getClientIds().get(client_id).getChannel()!=null){
            return false;
        }
        groupRoomMap.get(room_id).getClientIds().get(client_id).setChannel(ctx.channel());//(client_id,ctx.channel());
        return true;
    }
    //发送消息给群聊房间里除自己外的用户
    public static void sendGroupMsg(String roomId, Channel channel, TextWebSocketFrame msg){
        for (Client client:groupRoomMap.get(roomId).getClientIds().values()){
            if (client.getChannel().id()!=channel.id()){
                client.getChannel().writeAndFlush(msg);
            }
        }
    }
    //发送消息给单聊房间里除自己外的用户
    public static void sendRoomMsg(String roomId,Channel channel,TextWebSocketFrame msg){
        for (Client client:roomMap.get(roomId).getClientIds().values()){
            if (client.getChannel().id()!=channel.id()){
                client.getChannel().writeAndFlush(msg);
            }
        }
    }
    //广播(除自己外)
    public static void sendMessage(ChannelHandlerContext ctx, TextWebSocketFrame msg){
        System.out.println("roomMap"+roomMap.entrySet());
        System.out.println("ctx.id========="+ctx.channel().id());
        for (Channel key:group) {
            if (!key.id().equals(ctx.channel().id())){
                key.writeAndFlush(msg);
            }
        }
    }
    //发送给指定人消息
    public static void sendToMsg(Channel ctx, TextWebSocketFrame msg){
        for (Channel key:group) {
            if (key.id().equals(ctx.id())){
                key.writeAndFlush(msg);
            }
        }
    }
    public static void isMsgBye(String msg,Channel channel){
        JSONObject json=JSONObject.fromObject(msg);
        if ("bye".equals(json.get("type"))){
            removeChannel(channel);
        }
    }
    public static boolean isBlank(String message) {
        if (message.length() == 0) {
            return true;
        } else if (message == null) {
            return true;
        } else {
            return false;
        }
    }

    public static void removeChannel(Channel channel){
        for (Room room:roomMap.values()){
            for (Client client:room.getClientIds().values()){
                if (client.getChannel().id()==channel.id()){
                    room.getClientIds().remove(client.getClient_id());
                }
            }
        }
    }
    public static String reponseMsg(String msg,String error,String clientId){
        MessageResponse response=new MessageResponse();
        response.setError(error);
        response.setMsg(msg);
        response.setClient(clientId);
        JSONObject json=JSONObject.fromObject(response);
        return json.toString();
    }
}
