/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sd.projeto3.main;

import com.sd.projeto3.dao.MapaDao;
import com.sd.projeto3.model.Mapa;
import com.sd.projeto3.model.MapaDTO;
import com.sd.projeto3.util.PropertyManagement;
import com.sd.projeto3.util.Utilidades;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.SerializationUtils;

/**
 *
 * @author willi
 */
public class ServerThreadDisco implements Runnable {
    private Operacoes crud = new Operacoes();
    private DatagramSocket socketServidor;
    private static PropertyManagement pm;
    private static byte[] in;
    private MapaDao mapaDAO = new MapaDao();
    private ExecutorService executor;
    private static List<Integer> chavesMonitoradas = new ArrayList<Integer>();

    /// Recebendo o pacote da Thread Anterior;
    ServerThreadDisco(DatagramSocket socketServidor) {
        this.socketServidor = socketServidor;
    }

    @Override
    public void run() {
        try {
            executor = Executors.newCachedThreadPool();
            pm = new PropertyManagement();
            //socketServidor = new DatagramSocket(pm.getPort());

            while (true) {
                in = new byte[1400];
                DatagramPacket receivedPacket = MultiQueue.getDiscoFila();
                if(receivedPacket != null){
                    
                    Mapa maparetorno = new Mapa();
                    maparetorno = (Mapa) SerializationUtils.deserialize(receivedPacket.getData());

                    MapaDTO mapaDisco = new MapaDTO();
                    mapaDisco = tipoOperacao(maparetorno);
                    
                    if(findKey(maparetorno.getChave()))
                        mapaDisco.setMensagemMonitoramento("=====================================\n" +
                                                    "   --MONITORAMENTO DE CHAVE--\nA chave: '" + maparetorno.getChave() + "' foi acionada pela instrucao de " + Utilidades.retornaTipoOperacao(maparetorno.getTipoOperacaoId()) +
                                                    "\n=====================================\n");
                    
                    if(maparetorno.getTipoOperacaoId() == 5){
                        chavesMonitoradas.add(maparetorno.getChave());
                        mapaDisco.setMensagem("Monitoramento realizado com sucesso.");
                    }
                    
                    ServerThreadSend serverSend = new ServerThreadSend(mapaDisco, socketServidor);

                    if (serverSend != null) {
                        executor.execute(serverSend);
                    }
                }
                
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void imprimeCRUD(Mapa mapa1) {
        System.out.println("\n===============================");
        System.out.println("Chave: " + mapa1.getChave());
        System.out.println("Texto: " + mapa1.getTexto());
        System.out.println("Tipo de Operaçao: " + Utilidades.retornaTipoOperacao(mapa1.getTipoOperacaoId()));
        //System.out.println("Data: " + mapa1.getData());
        System.out.println("Tamanho da fila: " + crud.getMapa().size());
        System.out.println("===============================");
    }

    public MapaDTO tipoOperacao(Mapa mapaEntity) throws Exception {

        MapaDTO mapaDTO = new MapaDTO();

        switch (mapaEntity.getTipoOperacaoId()) {
            case 1:
                
                if(crud.buscarObjeto(mapaEntity) != null){
                    mapaDTO.setMensagem("Já existe mensagem com essa chave!");
                    break;
                }
                
                Mapa mi = mapaDAO.salvar(mapaEntity);
                
                if (mi != null) {
                    mapaDTO.setMapa(mi);
                    crud.salvar(mi);
                    imprimeCRUD(mi);
                    mapaDTO.setMensagem("Inserido com Sucesso!");

                } else {
                    mapaDTO.setMensagem("Erro ao inserir!");
                }
                break;
            case 2:
                
                if(crud.buscarObjeto(mapaEntity) == null){
                    mapaDTO.setMensagem("Chave não encontrada para atualizar!");
                }else{
                    Mapa ma = mapaDAO.salvar(mapaEntity);
                    
                    if (ma != null) {                   
                        mapaDTO.setMapa(ma);
                        crud.editar(ma);
                        imprimeCRUD(ma);
                        mapaDTO.setMensagem("Atualizado com Sucesso!");
                    }else{
                        mapaDTO.setMensagem("Erro ao atualizar!");
                    }
                }
                break;
            case 3:
                
                Mapa me = crud.buscarObjeto(mapaEntity);
                
                if(me == null){
                    mapaDTO.setMensagem("Chave não encontrada para excluir!");
                }else{ 

                    Mapa md = mapaDAO.salvar(mapaEntity);
                    
                    if (md != null) {    
                     crud.excluir(me);
                     me.setTipoOperacaoId(3);
                     mapaDTO.setMapa(me);                   
                     imprimeCRUD(me);
                     mapaDTO.setMensagem("Excluido com Sucesso!");
                    }else{
                        mapaDTO.setMensagem("Erro ao excluir!");
                    }
                     
                }
               
                break;
            case 4:
                
                Mapa mb = crud.buscarObjeto(mapaEntity);
                
                if(mb == null){
                    mapaDTO.setMensagem("Chave não encontrada!");
                }else{                
                    mb.setTipoOperacaoId(4);
                    mapaDTO.setMapa(mb);
                    imprimeCRUD(mb);
                    mapaDTO.setMensagem("Recuperado com Sucesso!");                   
                }
                break;
                case 5:
//                ComandResponse rspGrpc = ComandResponse.newBuilder().setCmd(mapaEntity.getChave() + " " + Utilidades.retornaTipoOperacao(mapaEntity.getTipoOperacaoId())).build();
//                this.responseObserverGrpc.onNext(rspGrpc);
//                this.responseObserverGrpc.onCompleted();
                break;
            default:
                mapaDTO.setMapa(null);
                mapaDTO.setMensagem("Opção inválida");

        }

        return mapaDTO;
    }

    public boolean findKey(int id){ 
        for(Integer chave : chavesMonitoradas){
            if(chave == id)
                return true;
        }
        
        
    return false; 
}
    
//    public io.grpc.stub.StreamObserver<ComandResponse> getResponseObserverGrpc() {
//        return responseObserverGrpc;
//    }
//    
//    public void setResponseObserverGrpc(io.grpc.stub.StreamObserver<ComandResponse> responseObserverGrpc) {
//        this.responseObserverGrpc = responseObserverGrpc;
//    }
}
