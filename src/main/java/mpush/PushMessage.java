package mpush;

import com.google.gson.Gson;
import com.mpush.api.Constants;
import com.mpush.api.push.*;
import com.mpush.api.router.ClientLocation;
import com.mpush.tools.Jsons;
import message.MessageResponse;
import message.NotifyDO;
import message.OfflineMsg;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;

public class PushMessage {
    private static final Logger logger=LoggerFactory.getLogger(PushMessage.class);
    private static PushSender mpusher = PushSender.create();
    private final AtomicLong msgIdSeq = new AtomicLong(1);//TODO业务自己处理
    static {
        mpusher.start();
    }
    public static String managerMsg(String toUserId, JSONObject msg){
        MessageResponse response=new MessageResponse();
        response.setMsg(msg.toString());
        response.setError("");
        String str= new Gson().toJson(response);
        List toUserList=new ArrayList();
        toUserList.add(toUserId);
        sendP2PMsg(toUserId,str);
        return "Success";
    }

    public static void sendP2PMsg(String userId, String content){
        PushMsg msg = PushMsg.build(MsgType.NOTIFICATION, content);
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
        FutureTask<PushResult> future = mpusher.send(context);
        System.out.println("PushResult============="+future.toString());
//        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(30));
    }

    public boolean notify(String userId, NotifyDO notifyDO){
        PushMsg pushMsg = PushMsg.build(MsgType.NOTIFICATION_AND_MESSAGE, Jsons.toJson(notifyDO));
        pushMsg.setMsgId(Long.toString(msgIdSeq.incrementAndGet()));
        byte[] content = Jsons.toJson(pushMsg).getBytes(Constants.UTF_8);

        doSend(userId, content, new PushCallback() {
            int retryCount = 0;

            @Override
            public void onSuccess(String userId, ClientLocation location) {
                logger.warn("send msg success");
            }

            @Override
            public void onFailure(String userId, ClientLocation clientLocation) {
                saveOfflineMsg(new OfflineMsg(userId, content));
            }

            @Override
            public void onOffline(String userId, ClientLocation clientLocation) {
                if (clientLocation != null) {
                    String os = clientLocation.getOsName().toLowerCase();
                    if (os.contains("ios")) {
                        send2ANPs(userId, notifyDO, clientLocation.getDeviceId());
                    } else if (os.contains("android")) {
                        if (os.contains("xiaomi")) {
                            send2MiPush(userId, notifyDO);
                        } else if (os.contains("huawei")) {
                            send2HuaweiPush(userId, notifyDO);
                        } else {
                            send2JPush(userId, notifyDO);
                        }
                    } else {
                        saveOfflineMsg(new OfflineMsg(userId, content));
                    }
                } else {
                    saveOfflineMsg(new OfflineMsg(userId, content));
                }
            }

            @Override
            public void onTimeout(String userId, ClientLocation clientLocation) {
                if (retryCount++ > 1) {
                    saveOfflineMsg(new OfflineMsg(userId, content));
                } else {
                    doSend(userId, content, this);
                }
            }
        });

        return true;
    }

    public boolean send(String userId, byte[] content) {
        doSend(userId, content, new PushCallback() {
            int retryCount = 0;

            @Override
            public void onSuccess(String userId, ClientLocation location) {
                logger.warn("send msg success");
            }

            @Override
            public void onFailure(String userId, ClientLocation clientLocation) {
                saveOfflineMsg(new OfflineMsg(userId, content));
            }

            @Override
            public void onOffline(String userId, ClientLocation clientLocation) {
                saveOfflineMsg(new OfflineMsg(userId, content));
            }

            @Override
            public void onTimeout(String userId, ClientLocation clientLocation) {
                if (retryCount++ > 1) {
                    saveOfflineMsg(new OfflineMsg(userId, content));
                } else {
                    doSend(userId, content, this);
                }
            }
        });
        return true;
    }



    private void doSend(String userId, byte[] content, PushCallback callback) {
        mpusher.send(new PushContext(content)
                .setUserId(userId)
                .setCallback(callback)
        );
    }

    private void send2ANPs(String userId, NotifyDO notifyDO, String deviceToken) {
        logger.info("send to ANPs");
    }

    private void send2MiPush(String userId, NotifyDO notifyDO) {
        logger.info("send to xiaomi push");
    }

    private void send2HuaweiPush(String userId, NotifyDO notifyDO) {
        logger.info("send to huawei push");
    }

    private void send2JPush(String userId, NotifyDO notifyDO) {
        logger.info("send to jpush");
    }

    private void saveOfflineMsg(OfflineMsg offlineMsg) {
        logger.info("save offline msg to db");
    }
}
