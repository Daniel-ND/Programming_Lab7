
package client;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.*;


public class ClientConsole {
    public static void main(String[] args) {
        if (args.length != 2){
            System.out.println("Необходимо передать два аргумента командной строки - порт клиента и адрес сервера");
            return;
        }
        int port = 0;
        try { port = Integer.parseInt(args[0]);}
        catch (NumberFormatException e){
            System.out.println("Порт должен быть задан числом");
            return;
        }
        if (port == 50000){
            System.out.println("Данный порт занят сервером");
            return;
        }
        String adress = args[1];
        //System.out.println("Клиент с портом " + port);
        UDPClient client = new UDPClient( port, 50000, adress);
        Thread thread = new Thread(client);
        thread.start();
    }
}