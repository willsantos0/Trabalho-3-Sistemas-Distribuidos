
package com.sd.projeto3.main;

import com.sd.projeto3.model.Mapa;
import com.sd.projeto3.util.PropertyManagement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Willian
 */
public class ClientThreadSend implements Runnable {
    
    private DatagramSocket socketCliente;
    private InetAddress enderecoIP;
    private static PropertyManagement pm;

    
    public ClientThreadSend(DatagramSocket socketCliente, InetAddress enderecoIP) throws SocketException{
           this.socketCliente = socketCliente;
           this.enderecoIP = enderecoIP;
           this.pm = new PropertyManagement();
        }
      
    @Override
    public void run() {
        try {
            while (true) {

                menu();
                Thread.sleep(2000);
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

        
    public void menu() throws Exception {

        int opcao = 0, chave = 0;
        String msg;
        BufferedReader mensagem;
        Mapa mapa;
        mensagem = new BufferedReader(new InputStreamReader(System.in));

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n===============================");
        System.out.println("Digite a operacao: ");
        System.out.println("1 - Inserir");
        System.out.println("2 - Atualizar");
        System.out.println("3 - Excluir");
        System.out.println("4 - Buscar");
        System.out.println("5 - Monitorar chave");
        System.out.println("6 - Sair");
        System.out.println("Opcao:");

        opcao = scanner.nextInt();

        switch (opcao) {
            case 1:
                System.out.println("Digite a chave:");
                chave = scanner.nextInt();

                System.out.println("Digite a Mensagem:");
                msg = mensagem.readLine();

                mapa = new Mapa();

                mapa.setChave(chave);
                mapa.setTipoOperacaoId(1);
                mapa.setTexto(msg);

                byte[] object = SerializationUtils.serialize(mapa);

                if (object.length > 1400) {
                    System.out.println("Pacote maior que o suportado!");
                } else {
                    send(object);
                }

                break;
            case 2:
                System.out.println("Digite a chave da mensagem que deseja atualizar:");
                chave = scanner.nextInt();

                System.out.println("Digite a Mensagem:");
                msg = mensagem.readLine();

                mapa = new Mapa();
                mapa.setChave(chave);
                mapa.setTipoOperacaoId(2);
                mapa.setTexto(msg);

                byte[] objectUpdate = SerializationUtils.serialize(mapa);

                if (objectUpdate.length > 1400) {
                    System.out.println("Pacote maior que o suportado!");
                } else {
                    send(objectUpdate);
                }

                break;
            case 3:
                System.out.println("Digite a chave da mensagem que deseja excluir:");
                chave = scanner.nextInt();

                mapa = new Mapa();
                mapa.setChave(chave);
                mapa.setTipoOperacaoId(3);

                byte[] objectDelete = SerializationUtils.serialize(mapa);

                if (objectDelete.length > 1400) {
                    System.out.println("Pacote maior que o suportado!");
                } else {
                    send(objectDelete);
                }

                break;
            case 4:
                System.out.println("Digite a chave da mensagem que deseja buscar:");
                chave = scanner.nextInt();

                mapa = new Mapa();
                mapa.setChave(chave);
                mapa.setTipoOperacaoId(4);

                byte[] objectSearch = SerializationUtils.serialize(mapa);

                if (objectSearch.length > 1400) {
                    System.out.println("Pacote maior que o suportado!");
                } else {
                    send(objectSearch);
                }

                break;
            case 5:
                System.out.println("Digite a chave da mensagem que deseja monitorar:");
                chave = scanner.nextInt();

                mapa = new Mapa();
                mapa.setChave(chave);
                mapa.setTipoOperacaoId(5);

                byte[] objectmonitoring = SerializationUtils.serialize(mapa);

                send(objectmonitoring);
               
                break;
            case 6:
                System.exit(1);
                break;
            default:
                System.out.println("Opcao invalida");
                break;
        }
    }
    
    public DatagramPacket send(byte[] outData) throws IOException {

        DatagramPacket sendPacket = new DatagramPacket(outData, outData.length, enderecoIP, pm.getPort());
        socketCliente.send(sendPacket);

        return sendPacket;
    }
}
