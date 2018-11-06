package handler;

import base.BaseHandler;
import entity.Client;
import entity.Room;
import net.sf.json.JSONObject;
import util.SimpleDateUtil;

import java.sql.SQLException;
public class GroupHandler extends BaseHandler {

    public String createRoom(String room_id,String client_id,String host){
        String error="";
        Room room=null;
        Client client=null;
        int clientSize=0;
        boolean isInitiator=true;
        JSONObject json=new JSONObject();
        if (room_id!=""&&client_id!=""){
            if (groupRoomMap.containsKey(room_id)&&groupRoomMap.get(room_id).getClientIds().containsKey(client_id)){
                groupRoomMap.get(room_id).getClientIds().remove(client_id);
                error="already exist client";
            }
            if (error==""){
                if (!groupRoomMap.containsKey(room_id)){
                    room=new Room();
                }else{
                    room=groupRoomMap.get(room_id);
                    clientSize=groupRoomMap.get(room_id).getClientIds().size();
                }
                if (clientSize>2){
                    isInitiator=false;
                    client=new Client();
                    client.setClient_id(client_id);
                    client.setInitiator(isInitiator);
                    room.getClientIds().put(client_id,client);
                }else if (clientSize==0){
                    isInitiator=true;
                    client=new Client();
                    client.setClient_id(client_id);
                    client.setInitiator(isInitiator);
                    room.setRoom_id(room_id);
                    room.addOneClient(client_id,client);
                    groupRoomMap.put(room_id,room);
                }else{
                    isInitiator=false;
                    String other=getOtherClient(room_id,client_id);
                    Client other_client=room.getClientIds().get(other);
                    if (other_client!=null&&other_client.getMessage()!=null&&other_client.getMessage().size()>0) {
                        json.put("client",client_id);
                        json.put("msg",other_client.getMessage());
                        client=new Client();
                        client.setClient_id(client_id);
                        client.setInitiator(isInitiator);
                        room.getClientIds().put(client_id,client);
                        other_client.getMessage().clear();
                    }else {
                        error = "cleint message error";
                    }
                }
            }
        }else{
            error="roomId or clientId error";
        }
        if (error!=""){
            return "{\"result\":\""+error+"\",\"params\":\"{}\"}";
        }else {
            JSONObject params = new RoomHandler().get_room_parameters(room_id, client_id, isInitiator,host);
            params.put("messages",json);
            params.put("clientSize",clientSize);
            String begin_dt= SimpleDateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
            //String begin_dt= DateUtils.getDateTime();
            try {
                RoomHandler.upRecordInfo(isInitiator, room_id, client_id, begin_dt,null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "{\"result\":\"SUCCESS\",\"params\":"+params+"}";
        }
    }
    private String getOtherClient(String roomId,String clientId){
        String otherClient="";
        for (String key:groupRoomMap.get(roomId).getClientIds().keySet()){
            if (!groupRoomMap.get(roomId).getClientIds().containsKey(clientId)){
                otherClient=key;
            }
        }
        return otherClient;
    }

    public String sendMessage(String roomId,String clientId,String msg){
        String error="";
        Client client=null;
        boolean saved=true;
        if (roomId!=""&&roomId!=null){
            if (groupRoomMap.get(roomId).getClientIds().containsKey(clientId)) {
                if (groupRoomMap.get(roomId).getClientIds().size()>2){
                    client=groupRoomMap.get(roomId).getClientIds().get(clientId);
                    client.addOneMessage(msg);
                }else if (groupRoomMap.get(roomId).getClientIds().size()==2) {
                    saved=false;
                }else{
                    client=groupRoomMap.get(roomId).getClientIds().get(clientId);
                    client.addOneMessage(msg);
                }
            }else {
                error = "UNKNOWN_CLIENT";
            }
        }else {
            error = "UNKNOWN_ROOM";
        }
        if (error!="") {
            return "{\"result\":\"" + error + "\"}";
        }
        else {
            if (!saved){
                //sendAnswerMsg(roomId,clientId,msg);
                return "{\"result\":\"SUCCESS\"}";
            }else {
                return "{\"result\":\"SUCCESS\"}";
            }
        }
    }
    public String RemoveClient(String roomId,String clientId){
        String error ="";
        if (roomId!=""&&roomId!=null){
            if (groupRoomMap.containsKey(roomId)){
                if (groupRoomMap.get(roomId).getClientIds().size()>1){
                    if (groupRoomMap.get(roomId).getClientIds().containsKey(clientId)) {
                        groupRoomMap.get(roomId).getClientIds().remove(clientId);
                    }else {
                        error = "UNKNOWN_CLIENT";
                    }
                }
                else if (groupRoomMap.get(roomId).getClientIds().size()==1){
                    groupRoomMap.get(roomId).getClientIds().get(getOtherClient(roomId,clientId)).setInitiator(true);
                }else {
                    groupRoomMap.remove(roomId);
                }
            }else {
                error = "UNKNOWN_ROOM";
            }
        }
        if (error!=""){
            return "{\"result\":\""+error+"\"}";
        }else {
            String end_dt=SimpleDateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
            try {
                RoomHandler.upRecordInfo(false, roomId, null, null,end_dt);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "{\"result\":\"SUCCESS\"}";
        }
    }
}
