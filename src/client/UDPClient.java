package client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class UDPClient implements Runnable {
    private final int clientPort;
    private final int serverPort;
    DatagramChannel channel;

    UDPClient(int port, int serverPort) {
        this.clientPort = port;
        this.serverPort = serverPort;
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
        try {
            SocketAddress address = new InetSocketAddress(50000);
            this.channel = DatagramChannel.open();
            channel.socket().bind(new InetSocketAddress(clientPort));
            while (!channel.isConnected())
                channel.connect(address);
        } catch (IOException e) {
            System.out.println("Не удаётся содать канал");
            return;
        }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean work = true;
                    while (true) {
                        //отправляем
                        work = send();
                        if (work == false)
                            break;
                        receive();
                    }

                }
            });
            //чтобы получатель работал, надо запустить его
            thread.start();
    }
}