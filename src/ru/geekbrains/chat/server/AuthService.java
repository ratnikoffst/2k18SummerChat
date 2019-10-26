package ru.geekbrains.chat.server;

import java.sql.*;

public class AuthService {

    public static void addUser(String login, String pass, String nick) {
        DBHelper.addUser(login, pass, nick);

    }


}
