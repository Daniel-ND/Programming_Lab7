package client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class UDPClient implements Runnable {
    private final int clientPort;
    private final int serverPort;
    private String serverAdress;
    DatagramChannel channel;

    UDPClient(int port, int serverPort, String serverAdress) {
        this.clientPort = port;
        this.serverPort = serverPort;
        this.serverAdress = serverAdress;
    }

    boolean send(){
        String requestMessage;

        Scanner in = new Scanner(System.in);
        requestMessage = in.nextLine();
        if (requestMessage.equals("exit"))
            return false;
        byte[] data = requestMessage.getBytes();
        try{
        channel.write(ByteBuffer.wrap(data));}
        catch (IOException e) {
            System.err.println("can't send package");
        }
        return true;
        //DatagramPacket datagramRequestPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), serverPort);
    }

    void receive(){
        try{
            //System.out.println("пытаюсь получить ответ");
            ByteBuffer inBuffer = ByteBuffer.allocate(1024);
            int ans = channel.read(inBuffer);
            //System.out.println(ans + "))");
            //System.out.println("получил ответ");
            byte[] arr = inBuffer.array();
            String recievedMessage = new String(arr);
            //System.out.println("пытаюсь вывести что-то разумное");
            System.out.println("response from server:" + "\n" + recievedMessage.trim());

            if (ans == -1)
                System.out.println("Не удаётся получить ответ от сервера :(");

        }
        catch (Exception e) {
        System.out.println("Не удаётся получить ответ от сервера :(");
        }
    }

    @Override
    public void run() {
      ///здесь получаем сообщения с сервера
        InetAddress serverA;
        try{serverA = InetAddress.getByName(serverAdress);}
        catch (UnknownHostException e){
            System.out.println("Не удаётся распознать хост");
            return;
        }
        try {
           SocketAddress address = new InetSocketAddress(serverA, serverPort);
           this.channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(clientPort));
           while (!channel.isConnected())
                channel.connect(address);
           System.out.println("Клиент с портом " + clientPort + " запущен");
        } catch (IOException e) {
           System.out.println("Не удаётся содать канал. Порт клиента занят");
           return;
        }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean work = true;
                    while (true) {
                        //отправляем
                        System.out.println("Отправляю");
                        work = send();
                        System.out.println("Отправил");
                        if (work == false)
                            break;
                        System.out.println("Сейчас получу ответ");
                        receive();
                        System.out.println("Получил");
                    }

                }
            });
            //чтобы получатель работал, надо запустить его
            thread.start();
    }
}