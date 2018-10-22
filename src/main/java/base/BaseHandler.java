package base;

import entity.Room;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseHandler {
    //存放单聊房间id和房间数据
    public static Map<String, Room> roomMap=new ConcurrentHashMap<String, Room>();
    //存放群聊房间id和房间数据
    public static Map<String, Room> groupRoomMap=new ConcurrentHashMap<String, Room>();

    /**
     * 以下类是用来存储访问的channle，channelGroup的原型是set集合，保证channle的唯一，如需根据参数标注存储，可以使用currentHashMap来存储。
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 一个通道对应一个用户信息
    //public static ConcurrentHashMap<Channel, Client> channelMap=new ConcurrentHashMap<Channel, Client>();
}
