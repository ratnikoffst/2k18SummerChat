package ru.geekbrains.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;

    public Server() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {
            if (DBHelper.openBase()) {
                System.out.println("Базу открыть не удалось !");
            }
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен. Ожидаем клиентов...");
            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DBHelper.disconnect();
        }
    }

    public void sendPersonalMsg(ClientHandler from, String nickTo, String msg) {

        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickTo)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден в чате");
    }

    public void broadcastMsg(ClientHandler from, int id, String nick, String msg) {

        MessageService.newMessage(id, nick, msg);

        for (ClientHandler o : clients) {
            if (!o.checkBlackList(from.getNick())) {
                o.sendMsg(nick + ": " + msg);
            }
        }
    }

    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void broadcastClientsList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientslist ");
        for (ClientHandler o : clients) {
            sb.append(o.getNick() + " ");
        }
        String out = sb.toString();
        for (ClientHandler o : clients) {
            o.sendMsg(out);
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        try {
            MessageService.sendAllMessages(client);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        broadcastClientsList();
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        broadcastClientsList();
    }
}
