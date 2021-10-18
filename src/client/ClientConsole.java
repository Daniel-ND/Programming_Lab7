package client;

import Exceptions.IncorrectArgsException;
import server.ServerConsole;

public class ClientConsole {
    private static final int serverPort = 50000;

    public static void main(String[] args) throws IncorrectArgsException {
        if (args.length != 2)
            throw new IncorrectArgsException("Необходимо передать два аргумента командной строки - порт клиента и адрес сервера");
        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new IncorrectArgsException("Порт должен быть задан числом");
        }
        if (port == ServerConsole.serverPort) {
            throw new IncorrectArgsException("Данный порт занят сервером");
        }
        String serverAddress = args[1];
        UDPClient client = new UDPClient(port, serverPort, serverAddress);
        Thread thread = new Thread(client);
        thread.start();
    }
}