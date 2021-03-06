package org.reldb.rel.client.connection.stream;

import java.io.*;

import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.utilities.ClassPathHack;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;
import org.reldb.rel.Rel;

public class ClientLocalConnection extends ClientConnection {
	
	private Rel rel;
	protected CrashHandler errorHandler;

	/** Establish a connection with a server. */
	public ClientLocalConnection(String databaseDir, boolean createDbAllowed, CrashHandler errorHandler, String[] additionalJars) throws IOException, DatabaseFormatVersionException {
		ClassPathHack.addFile("lib/RelDBMS.jar");
		rel = new Rel(databaseDir, createDbAllowed, additionalJars);
		this.errorHandler = errorHandler;
		obtainInitialServerResponse();
		errorHandler.setInitialServerResponse(initialServerResponse.toString());
	}
	
	public InputStream getServerResponseInputStream() throws IOException {
		return rel.getServerResponseInputStream();
	}
	
	public void sendEvaluate(String source) {
		try {
			rel.sendEvaluate(source);
		} catch (Throwable t) {
			rel.reset();
			errorHandler.process(t, source);
		}
	}
	
	public void sendExecute(String source) {
		try {
			rel.sendExecute(source);
		} catch (Throwable t) {
			rel.reset();
			errorHandler.process(t, source);
		}
	}
	
	public void close() throws IOException {
		rel.close();
	}

	public void reset() throws IOException {
		rel.reset();
	}

	public static void convertToLatestFormat(String dbURL, PrintStream conversionOutput, String[] additionalJars) throws DatabaseFormatVersionException, IOException {
		Rel.convertToLatestFormat(dbURL, conversionOutput, additionalJars);
	}
}
