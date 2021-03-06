package br.eti.clairton.migrator.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletRequest;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;
import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.MigratorDefault;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;

public class AbstractControllerTest {
	private MockResult result;
	private ServletRequest request;
	private AbstractMigratorController controller;
	private MigratorDefault migrator;
	private Config config;

	@Before
	public void setUp() {
		request = mock(ServletRequest.class);
		result = new MockResult();
		final Liquibase liquibase = mock(Liquibase.class);
		final Database database = mock(Database.class);
		DatabaseConnection connection = mock(DatabaseConnection.class);
		when(database.getConnection()).thenReturn(connection);
		when(liquibase.getDatabase()).thenReturn(database);
		migrator = mock(MigratorDefault.class);
		when(migrator.getLiquibase()).thenReturn(liquibase);
		final String path = "target/changelog-" + new Date().getTime() + "/db/changelogs/changelog-main.xml";
		config = new Config("", path);
		controller = new MigratorController(request, result, migrator, config);
	}

	@Test
	public void test() throws IOException {
		final File changelog = new File("src/test/resources/changelogs.zip");
		final UploadedFile file = mock(UploadedFile.class);
		when(file.getFile()).thenReturn(new FileInputStream(changelog));
		when(file.getSize()).thenReturn(1l);
		when(file.getFileName()).thenReturn("changelogs.zip");
		controller.run(file, "auth-permissions");
	}
}
