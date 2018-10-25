package message;

public class MessageRequest {
    private String cmd;
    private String roomid;
    private String clientid;
    private String msg;
    private String toUser;
    private String type;//1:单聊，2:群聊


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "cmd='" + cmd + '\'' +
                ", roomid='" + roomid + '\'' +
                ", clientid='" + clientid + '\'' +
                ", msg='" + msg + '\'' +
                ", toUser='" + toUser + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
