package message;


import java.io.Serializable;
import java.util.Map;

public class NotifyDO implements Serializable {
    private static final long serialVersionUID = 1L;
    public String msgId;
    public String title;
    public String content;
    public Integer nid; //主要用于聚合通知，非必填
    public Byte flags; //特性字段。 0x01:声音   0x02:震动 0x03:闪灯
    public String largeIcon; // 大图标
    public String ticker; //和title一样
    public Integer number;
    public Map<String, String> extras;

    public NotifyDO() {
    }

    public NotifyDO(String content) {
        this.content = content;
    }

}
