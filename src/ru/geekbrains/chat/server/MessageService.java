package ru.geekbrains.chat.server;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageService {

    public static void newMessage(int idUser, String nick, String msg) {
        if (DBHelper.newMessage(idUser, nick, msg)) {
            System.out.println("Сообщение не прошло !");
        }
    }

    public static void sendAllMessages(ClientHandler client) throws SQLException {
        ResultSet rs = DBHelper.getAllMessages();
        if (rs != null) {
            while (rs.next()) {
                String text=rs.getString(5)+" "+rs.getString(3)+":" + rs.getString(4);
                client.sendMsg(text);
            }
        }
    }
}
