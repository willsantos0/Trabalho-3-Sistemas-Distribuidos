
package com.sd.projeto3.main;


import com.sd.projeto3.model.Mapa;
import com.sd.projeto3.proto.ContextRequest;
import com.sd.projeto3.proto.ContextResponse;
import com.sd.projeto3.proto.ContextServiceGrpc;
import com.sd.projeto3.proto.SubscribeRequest;
import com.sd.projeto3.proto.SubscribeResponse;

import com.sd.projeto3.util.PropertyManagement;
import com.sd.projeto3.util.Utilidades;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author Willian
 */
public class ClientThreadSendGRPC implements Runnable {
    
    private static final PropertyManagement pm = new PropertyManagement();;

    private ExecutorService executor = Executors.newFixedThreadPool(1);
  
    @Override
    public void run() {
        try {
           	
            final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:" + pm.getPortGRPC()).usePlaintext(true).build();
            final ManagedChannel aSyncChannel = ManagedChannelBuilder.forTarget("localhost:" + pm.getPortGRPC()).usePlaintext( true).build();

            while (true) {

                menu(channel, aSyncChannel);
                Thread.sleep(2000);
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

        
    public void menu(ManagedChannel channel, ManagedChannel aSyncChannel) throws Exception {

        int opcao = 0, chave = 0;
        String msg;
        BufferedReader mensagem;
        Mapa mapa;
        mensagem = new BufferedReader(new InputStreamReader(System.in));

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n===============================");
        System.out.println("Digite a operação: ");
        System.out.println("1 - Inserir");
        System.out.println("2 - Atualizar");
        System.out.println("3 - Excluir");
        System.out.println("4 - Buscar");
        System.out.println("5 - Monitorar Chave");
        System.out.println("6 - Sair");
        System.out.println("Opção:");

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
                    
                    ContextServiceGrpc.ContextServiceBlockingStub stub = ContextServiceGrpc.newBlockingStub(channel);
                    ContextRequest contextRequest = ContextRequest.newBuilder().setInstruction(Utilidades.retornaTipoOperacao(opcao) + ";" + mapa.getChave() + ";" + mapa.getTexto()).build();

                    ContextResponse contextResponse = null;
                    
                    contextResponse = stub.insert(contextRequest);
	            System.out.println(contextResponse.getMessage());
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
                    
                    ContextServiceGrpc.ContextServiceBlockingStub stub = ContextServiceGrpc.newBlockingStub(channel);
                    ContextRequest contextRequest = ContextRequest.newBuilder().setInstruction( Utilidades.retornaTipoOperacao(opcao) + ";" + mapa.getChave() + ";" + mapa.getTexto()).build();

                    ContextResponse contextResponse = null;
                    
                    contextResponse = stub.update(contextRequest);
                    System.out.println( contextResponse.getMessage() );

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
                    
                    ContextServiceGrpc.ContextServiceBlockingStub stub = ContextServiceGrpc.newBlockingStub(channel);
                    ContextRequest contextRequest = ContextRequest.newBuilder().setInstruction(Utilidades.retornaTipoOperacao(opcao) + ";" + mapa.getChave()).build();

                    ContextResponse contextResponse = null;
                    contextResponse = stub.delete(contextRequest);
                    System.out.println(contextResponse.getMessage());
                  
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
                    
                    ContextServiceGrpc.ContextServiceBlockingStub stub = ContextServiceGrpc.newBlockingStub(channel);
                    ContextRequest contextRequest = ContextRequest.newBuilder().setInstruction(Utilidades.retornaTipoOperacao(opcao) + ";" + mapa.getChave()).build();

                    ContextResponse contextResponse = null;
                    
                    contextResponse = stub.find(contextRequest);
		    System.out.println(contextResponse.getMessage());
//                    send(objectSearch);
                }

                break;
            case 5:
                System.out.println("Digite a chave da mensagem que deseja monitorar:");
                chave = scanner.nextInt();
                
                ContextServiceGrpc.ContextServiceStub aSyncStub = ContextServiceGrpc.newStub( aSyncChannel );
                SubscribeRequest subscribeRequest = SubscribeRequest.newBuilder().setKey(String.valueOf(chave)).build();
                aSyncStub.subscribe( subscribeRequest, new StreamObserver< SubscribeResponse >() {
                        public void onNext( SubscribeResponse response ) {
                                System.out.println( response.getMessage() );
                }
                public void onError(Throwable t) {
                }
                public void onCompleted() {				         
                        channel.shutdownNow();
                }
                });
                break;
            case 6:
                System.exit(1);
                break;
            default:
                System.out.println("Opção Inválida");
                break;
        }
    }
    
}
