package message;


import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserInfoManager {

    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

//    /**
//     * 发送点对点消息
//     *senderId:发送人
//     * receiverId:接收人
//     * @param message
//     */
//    public static void sendP2PMessage(String senderId, String receiverId, String message) {
//
//        /**
//         * A给B发消息，应该是B收到消息，并在B的对话框并输出消息
//         */
//        boolean hasReceiverId = false;
//        Set<Channel> keySet = userInfos.keySet();
//        if (!UserInfoManager.isBlank(message)) {
//            try {
//                rwLock.readLock().lock();
//                // 取出所有的channel,然后遍历，寻找sender对应的channel
//
//                for (Channel ch : keySet) {
//                    Client userInfo = userInfos.get(ch);
//                    // 当前通道不是接收者的话，重新遍历
//                    if (!userInfo.getClient_id().equals(receiverId) )
//                        continue;
//                    // 当前通道是接收者的
//                    String backMessage = senderId + "发来消息：" + message;
//                    ch.writeAndFlush(new TextWebSocketFrame(backMessage));
//                    hasReceiverId = true;
//                    break;
//                }
//                if (hasReceiverId == false) {
//                    // 对方不在线
//                    for (Channel ch : keySet) {
//                        Client userInfo = userInfos.get(ch);
//                        if (!userInfo.getClient_id().equals(senderId) )
//                            continue;
//                        String backMessage = receiverId + "不在线";
//                        ch.writeAndFlush(new TextWebSocketFrame(backMessage));
//                        break;
//                    }
//                }
//            } finally {
//                rwLock.readLock().unlock();
//            }
//        }
//    }


}