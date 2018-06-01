package com.sd.projeto3.main;

import com.sd.projeto3.util.PropertyManagement;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private Queue<DatagramPacket> comandos = new LinkedList<>();
    
    public static void main(String[] args) throws SocketException, UnknownHostException {

        DatagramSocket socketCliente = new DatagramSocket();
        PropertyManagement pm = new PropertyManagement();
        InetAddress enderecoIP = InetAddress.getByName(pm.getAddress());;
    
        ExecutorService executor = Executors.newCachedThreadPool();

        ClientThreadReceive receive = new ClientThreadReceive(socketCliente);
        ClientThreadSend send = new ClientThreadSend(socketCliente, enderecoIP);
        
        executor.execute(receive);
        executor.execute(send);

        executor.shutdown();
    }

}
