package tk.mybatis.springboot.util;

import IceInternal.Ex;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/7/13.
 */
@Slf4j
@Data
public class DbHelper {
    /**
     * 数据库操作类，主要是读取字段类型等
     *
     * @author Danny
     */
    public DbHelper(String url, String username, String password,String regionName) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.regionName = regionName;
    }

    private String regionName;
    private String url;
    private String username;
    private String password;
    private String driver = "com.mysql.jdbc.Driver";

    {
        try {
            Class.forName(driver);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    public void closeConnection(Connection con) {
        try {
            if(!con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeUpdate(String sql){
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            closeConnection(connection);
        }
    }

    public <T> Set<T> executeQuery(String sql, Class<T> targetClass){
        Connection connection = getConnection();
        Set<T> result = new HashSet<>();
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                T obj = targetClass.newInstance();
                Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(targetClass);
                for (Method method : allDeclaredMethods) {
                    if (method.getName().startsWith("set")) {
                        String temp = method.getName().substring(3);
                        if (StringUtils.isEmpty(temp) || "Class".equals(temp)) continue;
                        String fieldName = temp.substring(0, 1).toLowerCase() + temp.substring(1);

                        //                  System.out.println(fieldName);
                        //                  System.out.println(resultSet.getObject(fieldName));
                        //                   System.out.println(resultSet.getObject(fieldName).getClass().getName());
                        try {
                            Object value = resultSet.getObject(fieldName);
                            if (null != value) {
                                //System.out.println(fieldName+value.getClass().getName());
                                method.invoke(obj, value);
                            }
                        }
                        catch (Exception e){
                            //Ignore
                        }
                    }
                }
                result.add(obj);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            closeConnection(connection);
        }
        return result;
    }

    /**
     * 获取数据库中表的信息
     *
     * @throws SQLException
     */
    public List<DataBaseTable> getTables() throws SQLException {
        List<DataBaseTable> list = new ArrayList<>();

        Connection con = getConnection();
        DatabaseMetaData databaseMetaData = con.getMetaData();

        //获取所有表
        ResultSet rs = databaseMetaData.getTables(con.getCatalog(), "%", "%", new String[]{"TABLE"});

        while (rs.next()) {
            DataBaseTable table = new DataBaseTable();
            table.setTableComment(rs.getString("REMARKS"));
            table.setTableName(rs.getString("TABLE_NAME"));
            list.add(table);
        }

        closeConnection(con);
        return list;
    }

    /**
     * 获取列信息
     *
     * @param tableName
     * @return
     * @throws Exception
     */
    public List<DataBaseColumn> getColumns(String tableName) throws Exception {
        List<DataBaseColumn> list = new ArrayList<>();
        Connection con = getConnection();
        DatabaseMetaData databaseMetaData = con.getMetaData();

        ResultSet rs = databaseMetaData.getColumns(null, "%", tableName, "%");

        while (rs.next()) {
            DataBaseColumn column = new DataBaseColumn();
            column.setColumnComment(rs.getString("REMARKS"));
            column.setColumnName(rs.getString("COLUMN_NAME"));
            column.setSqlType(rs.getInt("DATA_TYPE"));
            column.setSqlTypeName(rs.getString("TYPE_NAME"));
            column.setJavaType(getJavaType(column.getSqlType()));
            list.add(column);
        }

        closeConnection(con);
        return list;
    }

    /**
     * 获取java类型
     *
     * @param dataType
     * @return
     */
    public String getJavaType(int dataType) {
        String result = null;
        switch (dataType) {
            case 4:
            case 5:
            case -6:
                result = "Integer";
                break;
            case 1:
            case -1:
            case 12:
                result = "String";
                break;
            case 8:
                result = "Double";
                break;
            case 3:
            case -5:
                result = "java.math.BigInteger";
                break;
            case 6:
                result = "Float";
                break;
            case 91:
            case 92:
            case 93:
                result = "java.util.Date";
                break;
            default:
                result = "String";
        }
        return result;
    }
}

@Data
class DataBaseColumn {
    /**
     * 列名
     */
    private String columnName;
    /**
     * 列注释
     */
    private String columnComment;
    /**
     * SQL类型 int类型
     */
    private Integer sqlType;

    private String sqlTypeName;

    private String javaType;

}

@Data
class DataBaseTable {
    private String tableName;
    private String tableComment;
    private List<DataBaseColumn> columnList = new ArrayList<DataBaseColumn>();
}
