package handler;

import base.BaseHandler;
import druid.DBUtilDemo;
import entity.Client;
import entity.Room;
import entity.TalkRecord;
import mpush.PushMessage;
import net.sf.json.JSONObject;
import util.SimpleDateUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RoomHandler extends BaseHandler {

    //呼叫
    public void call(String roomId,String clientId,String toUser){
        //{"msg":"{\"type\":\"bye\"}","error":""}
        JSONObject json=new JSONObject();
        json.put("type","call");
        json.put("msg",clientId+"用户呼叫你!");
        String result= PushMessage.managerMsg(toUser,json);
        //return "{\"result\":\"SUCCESS\"}";
    }
    //呼叫回应
    public void callBack(String roomId,String clientId,String msg){
        String other=getOtherClient(roomId,clientId);
        JSONObject json=new JSONObject();
        json.put("type","call");
        json.put("msg",msg);
        String result= PushMessage.managerMsg(other,json);
    }
    //加入房间
    //type:1单聊，2群聊
    public String joinRoom(String roomId,String clientId,String host){
        String error="";
        boolean isInitiator=false;
        Room room=null;
        Client client=null;
        List<String> message = new ArrayList<>();
        int clientSize=0;
        if (!"".equals(roomId)&&!"".equals(clientId)){
            if(roomMap.containsKey(roomId)&&roomMap.get(roomId).getClientIds().size()>1){//&&roomMap.get(roomId).getClientIds().get(clientId)!=null
                roomMap.remove(roomId);
            }
            if (!roomMap.containsKey(roomId)){
                room=new Room();
            }else{
                room=roomMap.get(roomId);
                clientSize=roomMap.get(roomId).getClientIds().size();
            }
            if (clientSize>1){
                error="room full";
            }
            else if (null!=room.getClientIds()&&room.getClientIds().containsKey(clientId)) {
                error = "UPLICATE_CLIENT";
            }
            if (error==""){
                if (clientSize==0){
                    isInitiator=true;
                    room.setRoom_id(roomId);
                    client=new Client();
                    client.setClient_id(clientId);
                    client.setInitiator(isInitiator);
                    room.addOneClient(clientId,client);
                    roomMap.put(roomId,room);
                }else{
                    isInitiator=false;
                    String other=getOtherClient(roomId,clientId);
                    Client other_client=room.getClientIds().get(other);
                    if (other_client!=null&&other_client.getMessage()!=null&&other_client.getMessage().size()>0) {
                        message.addAll(other_client.getMessage());
                        client=new Client();
                        client.setClient_id(clientId);
                        client.setInitiator(isInitiator);
                        room.getClientIds().put(clientId,client);
                        other_client.getMessage().clear();
                    }else {
                        error = "cleint message error";
                    }
                }
            }
        }else{
            error="room or client is null";
        }
        if (error!=""){
            return "{\"result\":\""+error+"\",\"params\":\"{}\"}";
        }else {
            JSONObject params = this.get_room_parameters(roomId, clientId, isInitiator,host);
            params.put("messages",message);
            String begin_dt= SimpleDateUtil.getCurrentDate("yyyy-MM-dd HH:mm:ss");
            //String begin_dt= DateUtils.getDateTime();
            try {
                upRecordInfo(isInitiator, roomId, clientId, begin_dt,null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "{\"result\":\"SUCCESS\",\"params\":"+params+"}";
        }
    }

    public String sendMessage(String roomId,String clientId,String msg){
        String error="";
        Client client=null;
        boolean saved=true;
        if (roomId!=""&&roomId!=null){
            if (roomMap.get(roomId).getClientIds().containsKey(clientId)) {
                if (roomMap.get(roomId).getClientIds().size()>1) {
                    saved=false;
                }else{
                    client=roomMap.get(roomId).getClientIds().get(clientId);
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
                sendAnswerMsg(roomId,clientId,msg);
                return "{\"result\":\"SUCCESS\"}";
            }else {
                return "{\"result\":\"SUCCESS\"}";
            }
        }
    }
    private void sendAnswerMsg(String room_id,String client_id,String msg){
        String toUser=getOtherClient(room_id,client_id);
        JSONObject json=new JSONObject();
        json.put("cmd","send");
        json.put("msg",msg);
        String result= PushMessage.managerMsg(toUser,json);
    }
    public String leaveRoom(String roomId,String clientId){
        String error ="";
        if (roomId!=""&&roomId!=null){
            if (roomMap.containsKey(roomId)){
                System.out.println("clientId:"+clientId+"======="+roomMap.get(roomId).toString());
                if (roomMap.get(roomId).getClientIds().containsKey(clientId)) {
                    roomMap.get(roomId).getClientIds().remove(clientId);
                }else {
                    error = "UNKNOWN_CLIENT";
                }
                if (roomMap.get(roomId).getClientIds().size()>0){
                    roomMap.get(roomId).getClientIds().get(getOtherClient(roomId,clientId)).setInitiator(true);
                }else {
                    roomMap.remove(roomId);
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
                upRecordInfo(false, roomId, null, null,end_dt);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "{\"result\":\"SUCCESS\"}";
        }
    }

    protected static void upRecordInfo(boolean isInitiator,String room_id, String client_id,String begin_dt,String end_dt) throws SQLException {
        List<TalkRecord> talkRecord= DBUtilDemo.queryRecord(room_id);//record.selectRecord(room_id);
        TalkRecord talk=null;
        List<String> list = new ArrayList<>();
        if ("".equals(end_dt)||null==end_dt) {
            if (isInitiator) {
                talk = new TalkRecord();
                talk.setRoom_id(room_id);
                talk.setClient_ids(client_id);
                Date beginDt= SimpleDateUtil.parse("yyyy-MM-dd HH:mm:ss",begin_dt);
                talk.setBegin_dt(beginDt);
                DBUtilDemo.insertRecord(talk);
            } else {
                talk = talkRecord.get(0);
                client_id=talk.getClient_ids()+","+client_id;
                String str[] = talk.getClient_ids().split(",");
                list = Arrays.asList(str);
                if (list.size()<2){
                    talk.setClient_ids(client_id);
                    DBUtilDemo.updateClientRecord(talk);
                }

            }
        }else{
            talk = talkRecord.get(0);
            Date beginDt=talk.getBegin_dt();
            Date endDt=SimpleDateUtil.parse("yyyy-MM-dd HH:mm:ss",end_dt);
            String chatTime=SimpleDateUtil.getTimeDifferent(beginDt,endDt);
            talk.setEnd_dt(endDt);
            talk.setChatTime(chatTime);
            DBUtilDemo.updateRecord(talk);
        }
    }
    private String getOtherClient(String roomid,String clientId){
        String otherClient="";
        for (String key:roomMap.get(roomid).getClientIds().keySet()){
            if (!roomMap.get(roomid).getClientIds().containsKey(clientId)){
                otherClient=key;
            }
        }
        return otherClient;
    }
    //获取房间配置
    protected static JSONObject get_room_parameters(String room_id,String client_id,boolean isInitiator,String host){
        JSONObject params=new JSONObject();
        String ice_transports = "";//request.get('it')
//        String ice_server_transports = "";// request.get('tt')
        String ice_server_override = "None";
        String pc_config=make_pc_config(ice_transports, ice_server_override);
//        //控制各种网络特性的选项
//        dtls = request.get('dtls')
//        dscp = request.get('dscp')
//        ipv6 = request.get('ipv6')
//        pc_constraints = make_pc_constraints
//        params.put("pc_config",pc_config);
//        params.put("pc_constraints",pc_constraints);
        String wss_url="ws://"+host+"/ws";
        String wss_post_url="http://"+host+"/ws";
        params.put("ice_server_url","http://119.28.51.83:3033/iceconfig?key=none");
        params.put("wss_url",wss_url);
        params.put("wss_post_url",wss_post_url);
        params.put("pc_config",pc_config);

        if (room_id!=""&&room_id!=null) {
            //String room_link = "http://rtc.tlifang.com" + "/r/" + room_id;
            params.put("room_id",room_id);
            //params.put("room_link",room_link);
        }
        if (client_id!=""&&client_id!=null) {
            params.put("client_id",client_id);
        }
        params.put("is_initiator",isInitiator);

        return params;
    }
    private static String make_pc_config(String ice_transports,String ice_server_override){
        JSONObject pc_config=new JSONObject();
        pc_config.put("iceServers",new ArrayList<>());
        pc_config.put("bundlePolicy","max-bundle");
        pc_config.put("rtcpMuxPolicy","require");
        return pc_config.toString();
    }
}
