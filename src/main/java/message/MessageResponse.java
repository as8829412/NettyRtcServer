package message;

public class MessageResponse {
    private String msg;//{"msg":"{\"type\":\"bye\"}","error":""}
    private String error;
    private String client;
    public MessageResponse() {
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public String getError() {
        return error;
    }

    public MessageResponse(String msg, String error) {
        this.msg = msg;
        this.error = error;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "msg='" + msg + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
