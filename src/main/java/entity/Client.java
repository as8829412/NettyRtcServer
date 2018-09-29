package entity;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Client {
    String client_id;
    List<String> message;
    boolean isInitiator;
    Channel channel;

    public Client() {

    }

    public Client(String client_id, List<String> message, boolean isInitiator, Channel channel) {
        this.client_id = client_id;
        this.message = message;
        this.isInitiator = isInitiator;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isInitiator() {
        return isInitiator;
    }

    public void setInitiator(boolean initiator) {
        isInitiator = initiator;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public Client addOneMessage(String msg){
        if (Objects.isNull(message)){
            this.message=new ArrayList<>();
            this.message.add(msg);
        }else {
            this.message.add(msg);
        }
        return this;
    }
    @Override
    public String toString() {
        return "Client{" +
                "client_id='" + client_id + '\'' +
                ", message=" + message +
                '}';
    }


}
