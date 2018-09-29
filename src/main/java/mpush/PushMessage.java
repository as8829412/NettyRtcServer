package mpush;

import com.google.common.collect.Sets;
import com.mpush.api.push.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class PushMessage {

    public static void sendMessageToPush(String userId, List toUserIds, String content){
        PushSender sender = PushSender.create();
        sender.start().join();
        PushMsg msg = PushMsg.build(MsgType.MESSAGE, content);
        PushContext context=PushContext.build(msg)
                .setAckModel(AckModel.AUTO_ACK)
                .setUserId(userId)
                .setBroadcast(false)//广播
                //.setTags(Sets.newHashSet("test"))
                //.setCondition("tags&&tags.indexOf('test')!=-1")
                //.setUserIds(Arrays.asList("user-0", "user-1"))
                .setUserIds(toUserIds)
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

    public static void main(String[] args) {
        String content="测试推送";
        String userId="cs";
        List toUserIds=new ArrayList();
        toUserIds.add("cs1");
        new PushMessage().sendMessageToPush(userId, toUserIds,content);
    }
}
