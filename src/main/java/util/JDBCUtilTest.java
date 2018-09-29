package util;

import com.alibaba.druid.pool.DruidPooledConnection;
import druid.DBUtilConnection;
import entity.TalkRecord;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public  class JDBCUtilTest {
    public static List<TalkRecord> queryRecord(String roomId) throws SQLException{
        DruidPooledConnection conn = DBUtilConnection.getInstance().getConnection();
        String sql="select idk, room_id, client_ids, begin_dt, end_dt, chat_time from talk_record where room_id="+roomId;
        PreparedStatement ps=conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        List<TalkRecord> talkRecords=new ArrayList<>();
        ResultSetMetaData md=rs.getMetaData();
        //int col=md.getColumnCount();
        while (rs.next()){
            TalkRecord talk=new TalkRecord();
            talk.setIdk(rs.getInt("idk"));
            talk.setRoom_id(rs.getString("room_id"));
            talk.setClient_ids(rs.getString("client_ids"));
            talk.setBegin_dt(rs.getDate("begin_dt"));
            talk.setEnd_dt(rs.getDate("end_dt"));
            talk.setChatTime(rs.getString("chat_time"));
            talkRecords.add(talk);
        }
        rs.close();
        conn.close();
        return talkRecords;
    }

    public static Boolean insertRecord(TalkRecord talk){
        DruidPooledConnection conn =null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try {
            conn = DBUtilConnection.getInstance().getConnection();
            String sql="insert into talk_record (idk, room_id, client_ids,begin_dt, end_dt, chat_time)";
            int i = ps.executeUpdate(sql);
            System.out.println("影响的行数： " + i);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
    //增
		/*
		Statement statement = connection.createStatement();
		String sql = "insert into users values(3,\"abc\",\"aasd\",\"asd\")";

		int executeUpdate = statement.executeUpdate(sql);
		System.out.println("成功增加"+executeUpdate+"条数据");
		*/
    //删
		/*
		Statement statement = connection.createStatement();
		String sql = "delete from users where id = 1";

		int executeUpdate = statement.executeUpdate(sql);
		System.out.println("成功删除"+executeUpdate+"条数据");
		 */

		/*
		 * 改
		Statement statement = connection.createStatement();
		String sql = "update users set name = \"asadasd\" where id =1";

		int executeUpdate = statement.executeUpdate(sql);
		System.out.println("成功修改"+executeUpdate+"条数据");
		*/

}
