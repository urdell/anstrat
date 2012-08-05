package com.anstrat.server.util;

import java.io.IOException;
import java.net.Socket;

import com.anstrat.server.ClientWorker;
import com.anstrat.server.util.DependencyInjector.Inject;

public class ClientWorkerFactory {

	@Inject
	private DependencyInjector injector;
	
	public ClientWorker create(Socket socket) throws IOException {
		ClientWorker worker = new ClientWorker(socket);
		injector.injectDependencies(worker);
		return worker;
	}
}
