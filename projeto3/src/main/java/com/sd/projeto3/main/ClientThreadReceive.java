/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd.projeto3.main;

import com.sd.projeto3.model.MapaDTO;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Willian
 */
public class ClientThreadReceive implements Runnable{
        private byte[] receiveData = new byte[1400];
        private DatagramSocket socketCliente;
        
        public ClientThreadReceive(DatagramSocket socketCliente) throws SocketException{
           this.socketCliente = socketCliente;
        }
        
        // metodo que recebe resposta do servidor
        @Override
        public void run() {
            try {
                while (true) {
                    DatagramPacket pacoteRecebido = new DatagramPacket(receiveData, receiveData.length);
                    socketCliente.receive(pacoteRecebido);
                    //String msg = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());
                    MapaDTO maparetorno = (MapaDTO) SerializationUtils.deserialize(pacoteRecebido.getData());

                    if(maparetorno.getMensagemMonitoramento() != null)
                        System.out.println(maparetorno.getMensagemMonitoramento());
                    
                    if (maparetorno == null) {
                        System.out.println(maparetorno.getMensagem());
                    } else {
                        if (maparetorno.getMapa().getTipoOperacaoId() == 4) {
                            objetoRetornado(maparetorno);
                        } else {
                            System.out.println(maparetorno.getMensagem());
                        }
                    }

                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void objetoRetornado(MapaDTO mapa) {
            System.out.println("\n================================");
            System.out.println("Chave: " + mapa.getMapa().getChave());
            System.out.println("Texto: " + mapa.getMapa().getTexto());
        }
}
