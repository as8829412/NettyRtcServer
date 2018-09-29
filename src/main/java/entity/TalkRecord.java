package entity;

import java.util.Date;

public class TalkRecord {
    private Integer idk;

    private String room_id;

    private  String client_ids;

    private Date begin_dt;

    private Date end_dt;

    private String chatTime;

    public Integer getIdk() {
        return idk;
    }

    public void setIdk(Integer idk) {
        this.idk = idk;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id == null ? null : room_id.trim();
    }

    public String getClient_ids() {
        return client_ids;
    }

    public void setClient_ids(String client_ids) {
        this.client_ids = client_ids;
    }

    public Date getBegin_dt() {
        return begin_dt;
    }

    public void setBegin_dt(Date begin_dt) {
        this.begin_dt = begin_dt;
    }

    public Date getEnd_dt() {
        return end_dt;
    }

    public void setEnd_dt(Date end_dt) {
        this.end_dt = end_dt;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime == null ? null : chatTime.trim();
    }

    @Override
    public String toString() {
        return "TalkRecord{" +
                "idk=" + idk +
                ", room_id='" + room_id + '\'' +
                ", client_ids='" + client_ids + '\'' +
                ", begin_dt=" + begin_dt +
                ", end_dt=" + end_dt +
                ", chatTime='" + chatTime + '\'' +
                '}';
    }
}
