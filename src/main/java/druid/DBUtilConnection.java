package druid;

import com.alibaba.druid.pool.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtilConnection {
    static final Logger log= LoggerFactory.getLogger(DBUtilConnection.class);
    private static DBUtilConnection conn=null;
    private static DruidDataSource druid=null;
    static {

        try {
            Properties properties = loadPropertiesFile("classes/druid.properties");
//            System.out.println(properties);
//            properties.load(DBUtilConnection.class.getResourceAsStream("classes/druid.properties"));
            //读取属性文件创建连接池
            druid = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties); //DruidDataSrouce工厂模式
        } catch (Exception e) {
            log.error("获取配置失败");
            e.printStackTrace();
        }
    }

    /**
     * 数据库连接池单例
     * @return
     */
    public static synchronized DBUtilConnection getInstance(){
        if (null == conn){
            conn = new DBUtilConnection();
        }
        return conn;
    }
    public DruidDataSource getDataSource() throws SQLException {
        return druid;
    }
    /**
     * 返回druid数据库连接
     * @return
     * @throws SQLException
     */
    public DruidPooledConnection getConnection() throws SQLException{
        return druid.getConnection();
    }
    /**
     * @param fullFile 配置文件名
     * @return Properties对象
     */
    private static Properties loadPropertiesFile(String fullFile) {
        String webRootPath = null;
        if (null == fullFile || fullFile.equals("")){
            throw new IllegalArgumentException("Properties file path can not be null" + fullFile);
        }
        webRootPath = DBUtilConnection.class.getClassLoader().getResource("").getPath();
        webRootPath = new File(webRootPath).getParent();
        InputStream inputStream = null;
        Properties p =null;
        try {
            inputStream = new FileInputStream(new File(webRootPath + File.separator + fullFile));
            p = new Properties();
            p.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream){
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return p;
    }

    /**
     * 释放资源
     * @param conn
     * @param stmt
     * @param rs
     */
    public static void close(DruidPooledConnection conn, DruidPooledStatement stmt, DruidPooledResultSet rs){
        if (rs!=null){
            try {
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if (stmt!=null){
            try {
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if (conn!=null){
            try {
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
