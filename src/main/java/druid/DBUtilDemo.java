package druid;


import entity.TalkRecord;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtilDemo {

    public static List<TalkRecord> queryRecord(String roomId) throws SQLException{
        QueryRunner qr=new QueryRunner(DBUtilConnection.getInstance().getDataSource());
        String sql="select idk, room_id, client_ids, begin_dt, end_dt, chat_time from talk_record where room_id='"+roomId+"' order by begin_dt desc";
        List<TalkRecord> talkRecords=qr.query(sql,new BeanListHandler<>(TalkRecord.class));
        List list=new ArrayList();
        for (int i=0;i<talkRecords.size();i++){
            list.add(talkRecords.get(i));
            //System.out.println("i:"+talkRecords.get(i).toString());
        }
        return list;
    }
    public static int insertRecord(TalkRecord talkRecord) throws  SQLException{
        QueryRunner qr=new QueryRunner(DBUtilConnection.getInstance().getDataSource());
        String sql = "insert into talk_record (idk, room_id, client_ids, begin_dt, end_dt, chat_time) values(?,?,?,?,?,?)";
        Object [] params = new Object[]{talkRecord.getIdk(), talkRecord.getRoom_id(),talkRecord.getClient_ids(),talkRecord.getBegin_dt(),talkRecord.getEnd_dt(),talkRecord.getChatTime()};
        return (qr.execute(sql,params));
    }
    public static int updateRecord(TalkRecord talkRecord) throws  SQLException{
        QueryRunner qr=new QueryRunner(DBUtilConnection.getInstance().getDataSource());
        String sql="update talk_record set end_dt = ? , chat_time = ? where idk = ?";
        Object [] params = new Object[]{talkRecord.getEnd_dt(),talkRecord.getChatTime(),talkRecord.getIdk()};
        return (qr.update(sql,params));
    }
    public static int updateClientRecord(TalkRecord talkRecord) throws  SQLException{
        QueryRunner qr=new QueryRunner(DBUtilConnection.getInstance().getDataSource());
        String sql="update talk_record set client_ids = ? where idk = ?";
        Object [] params = new Object[]{talkRecord.getClient_ids(),talkRecord.getIdk()};
        return (qr.update(sql,params));
    }
    public static void close() throws SQLException{
        DBUtilConnection.getInstance().getConnection().close();
    }
}
