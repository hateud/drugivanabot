package io.github.hateud;

import java.lang.ref.PhantomReference;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataBase {

    String baseDir = System.getProperty("user.dir");
    String dbPath = baseDir + "\\app\\resources\\db\\users.db";
    Connection connection;
    public DataBase() throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
            System.out.println("Bd");

            String users = """
                    CREATE TABLE IF NOT EXISTS users (
                        userId INTEGER PRIMARY KEY,
                        apiId INTEGER,
                        apiHash TEXT,
                        channels TEXT,
                        triggers TEXT
                    );""";
            String messages = """
                    CREATE TABLE IF NOT EXISTS messages(
                        userId INTEGER PRIMARY KEY,
                        msgId INTEGER
                    );
                    """;
            Statement stm = connection.createStatement();
            stm.execute(users);
            stm.execute(messages);
            stm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addUser(long userId){
        String sql = "INSERT INTO users(userId) VALUES(?)";
        try{
            PreparedStatement psrm = connection.prepareStatement(sql);

            psrm.setLong(1,userId);
//            psrm.setLong(2,apiId);
//            psrm.setString(3,apiHash);
//            psrm.setString(4,channels);
//            psrm.setString(5,triggers);
            psrm.executeUpdate();
            psrm.close();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setConfig(Long userId, String channels, String triggers){
        String sql = "UPDATE users SET channels = ?, triggers = ? WHERE userId = ?";
        try (PreparedStatement psrm = connection.prepareStatement(sql)){
            psrm.setString(1, channels);
            psrm.setString(2, triggers);
            psrm.setLong(3, userId);
            psrm.executeUpdate();
            psrm.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Long> getUsersList(){
        ArrayList<Long> map = new ArrayList<>();
        String sql = "SELECT userId FROM users";
        try (PreparedStatement psrm = connection.prepareStatement(sql);
             ResultSet rs = psrm.executeQuery()){
            while (rs.next()){
                map.add(rs.getLong("userId"));

            }
            return map;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public ArrayList<Object[]> getData(Long userId){
        ArrayList<Object[]> map = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE userId = ?";
        try (PreparedStatement psrm = connection.prepareStatement(sql)){
            psrm.setLong(1, userId);
            ResultSet rs = psrm.executeQuery();
            while (rs.next()){
                map.add(new Object[]{
                        rs.getLong("userId"),
                        rs.getLong("apiId"),
                        rs.getString("apiHash"),
                        rs.getString("channels"),
                        rs.getString("triggers")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public void setApi(long userId, Long apiId, String apiHash){
        String sql = "UPDATE users SET apiId = ?, apiHash = ? WHERE userId = ?";

        try {
            PreparedStatement psrm = connection.prepareStatement(sql);
            psrm.setLong(1, apiId);
            psrm.setString(2, apiHash);
            psrm.setLong(3, userId);
            psrm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setChannels(long userId, String channels){
        String sql = "UPDATE users SET channels = ?, WHERE userId = ?";
        try{
            PreparedStatement psrm = connection.prepareStatement(sql);
            psrm.setString(1,channels);
            psrm.setLong(2,userId);
            psrm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMsg(Long userId, int msgId){
        String sql = "INSERT INTO messages(userId, msgId) VALUES (?,?) ON CONFLICT(userId) DO UPDATE SET msgId = ?";
        try (PreparedStatement psrm = connection.prepareStatement(sql)){

            psrm.setLong(1, userId);
            psrm.setInt(2, msgId);
            psrm.setInt(3, msgId);
            psrm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Long, Integer> getMsgs() {
        String sql = "SELECT * FROM messages";
        HashMap<Long, Integer> map = new HashMap<>();
        try {
            PreparedStatement psrm = connection.prepareStatement(sql);
            ResultSet rs = psrm.executeQuery();
            while (rs.next()){
                int msgId = rs.getInt("msgId");
                Long userId = rs.getLong("userId");
                map.put(userId, msgId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    public void clearData(Long userId) {
        String sql = "DELETE FROM users WHERE userId = ?";
        try (PreparedStatement psrm = connection.prepareStatement(sql)){
            psrm.setLong(1, userId);
            psrm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
