package mpush;

import com.google.gson.Gson;
import com.mpush.api.push.*;
import message.MessageResponse;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class PushMessage {
    private static PushSender sender = PushSender.create();
    static {
        sender.start();
    }
    public static String managerMsg(String toUserId, JSONObject msg){
        MessageResponse response=new MessageResponse();
        response.setMsg(msg.toString());
        response.setError("");
        String str= new Gson().toJson(response);
        List toUserList=new ArrayList();
        toUserList.add(toUserId);
        //PushMessage.sendMessageToPush(toUserId,toUserList,str);
        sendP2PMsg(toUserId,str);
        return "Success";
    }
    public static void sendMessageToPush(String userId, List toUserIds, String content){

        //sender.start().join();
        PushMsg msg = PushMsg.build(MsgType.MESSAGE, content);
        PushContext context=PushContext.build(msg)
                .setAckModel(AckModel.AUTO_ACK)
                .setUserId(userId)
                .setBroadcast(false)//广播
                //.setTags(Sets.newHashSet("test"))
                //.setCondition("tags&&tags.indexOf('test')!=-1")
                //.setUserIds(toUserIds)
                .setTimeout(2000)
                .setCallback(new PushCallback() {
                    @Override
                    public void onResult(PushResult result) {
                        System.err.println("\n\n" + result);
                    }
                });
        FutureTask<PushResult> future = sender.send(context);
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }
    public static void sendP2PMsg(String userId, String content){
        sender.start().join();
        PushMsg msg = PushMsg.build(MsgType.MESSAGE, content);
        PushContext context=PushContext.build(msg)
                .setAckModel(AckModel.AUTO_ACK)
                .setUserId(userId)
                .setBroadcast(false)//广播
                //.setTags(Sets.newHashSet("test"))
                //.setCondition("tags&&tags.indexOf('test')!=-1")
                //.setUserIds(Arrays.asList("user-0", "user-1"))
                .setTimeout(2000)
                .setCallback(new PushCallback() {
                    @Override
                    public void onResult(PushResult result) {
                        System.err.println("\n\n" + result);
                    }
                });
        FutureTask<PushResult> future = sender.send(context);
        System.out.println("PushResult============="+future.toString());
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }
    public static void main(String[] args) {
        String content="测试推送";
        String userId="cs";
        List toUserIds=new ArrayList();
        toUserIds.add("cs1");
        new PushMessage().sendMessageToPush(userId, toUserIds,content);
    }
}
