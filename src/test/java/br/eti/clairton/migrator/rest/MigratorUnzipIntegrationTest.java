package br.eti.clairton.migrator.rest;

import static com.google.common.io.Files.copy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.eti.clairton.migrator.Inserter;
import br.eti.clairton.migrator.Migrator;
import br.eti.clairton.migrator.MigratorDefault;

public class MigratorUnzipIntegrationTest {
	private Migrator migrator;
	private Connection connection;

	@Before
	public void setUp() throws Exception {
		final String time = "" + new Date().getTime();
		final String folder = "target/" + time;
		if (!new File(folder).mkdirs()) {
			fail("Não criou diretorio temporario");
		}
		final File file = new File(folder + "/changelogs.zip");
		copy(new File("src/test/resources/changelogs.zip"), file);
		final InputStream changelog = new FileInputStream(file);
		final ConfigRest config = new ConfigRest(folder + "/municipio", "", "db/changelogs/changelog-main.xml") {
			@Override
			public Boolean isMigrate() {
				return true;
			}

			@Override
			public Boolean isPopulate() {
				return false;
			}

			@Override
			public Boolean isDrop() {
				return false;
			}
		};
		connection = new Resource("jdbc:hsqldb:mem:" + time).getConnection();
		final Migrator migratorDefault = new MigratorDefault(connection, config, new Inserter());
		migrator = new MigratorUnzip(changelog, migratorDefault, config);
	}

	@Test
	public void testRun() throws Exception {
		migrator.run();
		final Statement statement = connection.createStatement();
		if (statement.execute("SELECT COUNT(*) FROM aplicacoes WHERE nome='Pass';")) {
			final ResultSet result = statement.getResultSet();
			if (result.next()) {
				assertEquals(1, result.getInt(1));
				return;
			}
		}
		fail("Não executou o a migração");
	}
}
