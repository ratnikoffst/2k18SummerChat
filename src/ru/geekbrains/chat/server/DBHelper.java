package ru.geekbrains.chat.server;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DBHelper {
    private static Connection mConnection;
    private static Statement mStatement;
    static DateFormat sdfTime = new SimpleDateFormat("d.MM.yyyy HH:mm:ss");

    public static boolean openBase() {
        try {
            Class.forName("org.sqlite.JDBC");
            mConnection = DriverManager.getConnection("jdbc:sqlite:users.db");
            mStatement = mConnection.createStatement();
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean newMessage(int idUser, String nick, String msg) {
        Calendar cal = Calendar.getInstance();
        String time = sdfTime.format(cal.getTime());
        try {

            String st = "INSERT INTO messages (idusers,time,message,nick)\n" + "VALUES (" + idUser + ",'" + time + "','" + msg + "','" + nick + "');";
            //  INSERT INTO messages (idusers,time,message,nick)
            mStatement.executeUpdate(st);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ResultSet getNickByLoginAndPass(String login, String pass) {
        try {
            ResultSet rs = mStatement.executeQuery("SELECT id,nickname, password FROM main WHERE login = '" + login + "'");
            int myHash = Integer.parseInt(pass);
            if (rs.next()) {
                String nick = rs.getString(2);
                int dbHash = rs.getInt(3);
                if (myHash == dbHash) {
                    return rs;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void disconnect() {
        try {
            mConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUser(String login, String pass, String nick) {
        try {
            String query = "INSERT INTO main (login, password, nickname) VALUES (?, ?, ?);";
            PreparedStatement ps = mConnection.prepareStatement(query);
            ps.setString(1, login);
            ps.setInt(2, pass.hashCode());
            ps.setString(3, nick);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static ResultSet getAllMessages() {
        try {
            ResultSet rs = mStatement.executeQuery("SELECT * FROM messages;");
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
