package message;

public class MessageRequest {
    private String cmd;
    private String roomid;
    private String clientid;
    private String msg;
    private String toUser;

    public String getCmd() {
        return cmd;
    }

    public String getRoomid() {
        return roomid;
    }

    public String getClientid() {
        return clientid;
    }

    public String getMsg() {
        return msg;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "cmd='" + cmd + '\'' +
                ", roomid='" + roomid + '\'' +
                ", clientid='" + clientid + '\'' +
                ", msg='" + msg + '\'' +
                ", toUser='" + toUser + '\'' +
                '}';
    }
}
