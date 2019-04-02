package br.eti.clairton.migrator.rest;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import br.eti.clairton.migrator.Config;

@Singleton
public class Resource {
	private final Config config = new Config("datasets") {
		private int calls = 0;

		@Override
		public Boolean isDrop() {
			return calls++ > 1;
		}
	};

	private final Connection connection;

	public Resource() throws Exception {
		this("jdbc:hsqldb:file:target/database/migrator;hsqldb.lock_file=false;shutdown=true;create=true");
	}

	public Resource(final String url) throws Exception {
		connection = DriverManager.getConnection(url, "sa", "");
		connection.setAutoCommit(true);
	}

	@Rest
	@Default
	@Produces
	public Config getConfig() {
		return config;
	}

	@Produces
	public Connection getConnection() {
		return connection;
	}

	public void closeConnection(@Disposes final Connection connection) throws Exception {
		connection.close();
	}
}
