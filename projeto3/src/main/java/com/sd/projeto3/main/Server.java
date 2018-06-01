package com.sd.projeto3.main;

import com.sd.projeto3.dao.MapaDao;
import com.sd.projeto3.model.Mapa;
import com.sd.projeto3.proto.SubscribeResponse;
import com.sd.projeto3.util.PropertyManagement;
import io.grpc.stub.StreamObserver;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server{
    
    private static MapaDao mapaDAO = new MapaDao();
    
        private static PropertyManagement mySettings = new PropertyManagement();
        private static DatagramSocket serverSocket;
	private static Queue< String > logQueue = new LinkedList< String >();
	private static Queue< String > executeQueue = new LinkedList< String >();
	private static Map< String, List< StreamObserver< SubscribeResponse > > > observers = new HashMap< String, List< StreamObserver< SubscribeResponse > > >();
        private static ExecutorService executor;
		
    
    public static void main(String[] args) throws Exception {
        List<Mapa> logs = new ArrayList<Mapa>();
        Operacoes crud = new Operacoes();
        serverSocket = new DatagramSocket( mySettings.getPortGRPC());
       
		
        executor = Executors.newFixedThreadPool(50);
        
        logs = mapaDAO.buscarTodos();
        
        for(Mapa m: logs){
            crud.salvar(m);
        }
        
        System.out.println("Log do Disco Recuperado");
        System.out.println("Tamanho do log: " + crud.getMapa().size() + "\n");
        
        
        System.out.println("Servidor Iniciado...");
        new Thread(new ServerThreadReceive()).start();
        
        ThreadAlertSubscribes executorThread = new ThreadAlertSubscribes( serverSocket, logQueue, executeQueue, crud, observers );
		
        ServerThreadGRPC grpcServerThread = new ServerThreadGRPC( logQueue, executeQueue, crud, mySettings, observers );
	
        executor.execute( grpcServerThread );
        executor.execute( executorThread );
    }
   
}
