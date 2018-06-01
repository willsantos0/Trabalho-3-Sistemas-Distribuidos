package com.sd.projeto3.main;

import com.sd.projeto3.proto.SubscribeResponse;
import com.sd.projeto3.util.PropertyManagement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class ServerThreadGRPC implements Runnable {

	private Queue<String> logQueue;

	private Queue<String> executeQueue;

	private Operacoes context;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	
	private PropertyManagement settings;
	
	private List< String > subscribeAlert = new ArrayList< String >();
	
	private Map< String, List< StreamObserver< SubscribeResponse > > > observers;


	public ServerThreadGRPC( Queue< String > logQueue, Queue< String > executeQueue, Operacoes context, PropertyManagement settings, Map< String, List< StreamObserver< SubscribeResponse > > > observers ) {
		super();
		this.logQueue = logQueue;
		this.executeQueue = executeQueue;
		this.context = context;
		this.settings = settings;
		this.observers = observers;
	}

	@Override
	public void run() {

		try {

			Server server = ServerBuilder.forPort( settings.getPortGRPC()).addService( new com.sd.projeto3.service.ContextService( logQueue, executeQueue, context, observers ) ).build();

			// Start the server
			server.start();

			// Server threads are running in the background.
			System.out.println( "Server started" );
			// Don't exit the main thread. Wait until server is terminated.
			
			Future< String > future = wait( server );
			try {
				while( !future.isDone() ) {
					Thread.sleep( 300 );
				}
				future.get();
			} catch ( InterruptedException e ) {
				e.printStackTrace();
			} catch ( ExecutionException e ) {
				e.printStackTrace();
			} finally {
				future.cancel( true );
			}
			
			server.awaitTermination();

		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
	
	public Future< String > wait( Server server ) {
		return executor.submit( () -> {
			server.awaitTermination();
			return null;
		} );
	}

}
