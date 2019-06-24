package server;

import client.UDPClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConsole {
    public static void main(String[] args) {
        UDPServer server = new UDPServer(50000);
        System.out.println("Сервер запустился и принимает сообщения");
        Thread thread = new Thread(server, "Поток сервера");
        thread.start();

    }
}