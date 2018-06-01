package com.sd.projeto3.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class ClientGRPC {

	private static ExecutorService executor;

	public static void main( String[] args ) {
		
            executor = Executors.newFixedThreadPool(50);
            ClientThreadSendGRPC clientGrpc = new ClientThreadSendGRPC();
            executor.execute( clientGrpc );
		
	}
	
}
