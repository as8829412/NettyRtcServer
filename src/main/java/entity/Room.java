package entity;


import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Room {

    public String room_id;

    Map<String,Client> clientIds;

    public Room() {
    }

    public Room(String room_id, Map<String, Client> clientIds) {
        this.room_id = room_id;
        this.clientIds = clientIds;
    }

    public Map<String, Client> getClientIds() {
        return clientIds;
    }

    public Room addOneClient(String clientId,Client client){
        if (Objects.isNull(clientIds)){
            this.clientIds=new ConcurrentHashMap<String, Client>();
        }
        this.clientIds.put(clientId,client);
        return this;
    }
    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    @Override
    public String toString() {
        return "Room{" +
                "room_id='" + room_id + '\'' +
                ", clientIds=" + clientIds +
                '}';
    }

}
