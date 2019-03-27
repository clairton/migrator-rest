package br.eti.clairton.migrator.rest;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.util.EnumSet.of;
import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.REQUEST;
import static org.eclipse.jetty.servlet.ServletContextHandler.SESSIONS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ListenerHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.weld.environment.servlet.Listener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptor;

public class UploaderTest {
	private Server server;
	private String host;

	@Before
	public void setUp() throws Exception {
		server = new Server();
		final ServerConnector connector = new ServerConnector(server);
		connector.setPort(0);
		server.addConnector(connector);
		final ServletContextHandler context = new ServletContextHandler(SESSIONS);
		context.setContextPath("/");
		context.setResourceBase(getProperty("java.io.tmpdir"));
		server.setHandler(context);
		context.getServletHandler().addListener(new ListenerHolder(Listener.class));
		context.addFilter(VRaptor.class, "/*", of(REQUEST, FORWARD));
		server.start();
		final String defaultHost;
		if (connector.getHost() == null) {
			defaultHost = "localhost";
		} else {
			defaultHost = connector.getHost();
		}
		int port = connector.getLocalPort();
		host = new URI(format("http://%s:%d", defaultHost, port)).toString();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void testRun() throws Exception {
		final Uploader uploader = new Uploader();
		final File file = new File("src/test/resources/changelogs.zip");
		final String url = host + "/migrator";
		final String token = "123";
		assertTrue(uploader.run(file, url, token));
		final Connection connection = new Resource().getConnection();
		final Statement statement = connection.createStatement();
		final ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM aplicacoes WHERE nome = 'Pass'");
		result.next();
		assertEquals(1, result.getInt(1));
	}

}
