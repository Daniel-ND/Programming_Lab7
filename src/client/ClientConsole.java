
package client;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.*;


public class ClientConsole {
    public static void main(String[] args) {
        if (args.length > 1 || args.length < 1){
            System.out.println("Необходимо передать один аргумент командной строки - порт клиента");
            return;
        }
        int port = Integer.parseInt(args[0]);
        System.out.println("Клиент с портом " + port);
        UDPClient client = new UDPClient( port, 50000);
        Thread thread = new Thread(client);
        thread.start();
    }
}