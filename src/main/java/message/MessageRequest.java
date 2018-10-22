package message;

public class MessageRequest {
    private String cmd;
    private String roomId;
    private String clientId;
    private String msg;
    private String toUser;
    private String type;//1:单聊，2:群聊

    public String getType() {
        return type;
    }
    public String getCmd() {
        return cmd;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getMsg() {
        return msg;
    }

    public String getToUser() {
        return toUser;
    }


    @Override
    public String toString() {
        return "MessageRequest{" +
                "cmd='" + cmd + '\'' +
                ", roomId='" + roomId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", msg='" + msg + '\'' +
                ", toUser='" + toUser + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
