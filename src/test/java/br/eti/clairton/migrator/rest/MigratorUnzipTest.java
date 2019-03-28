package br.eti.clairton.migrator.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import br.eti.clairton.migrator.Inserter;
import br.eti.clairton.migrator.Migrator;
import br.eti.clairton.migrator.MigratorDefault;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ResourceAccessor;

public class MigratorUnzipTest {
	@Test
	public void testRun() throws Exception {
		final DatabaseConnection databaseConnection = mock(JdbcConnection.class);
		when(databaseConnection.getDatabaseProductName()).thenReturn("postgres");
		final MigratorDefault defaultMigrator = mock(MigratorDefault.class);
		when(defaultMigrator.getInserter()).thenReturn(mock(Inserter.class));
		final DatabaseConnection connection = mock(JdbcConnection.class);
		final Liquibase liquibase = mock(Liquibase.class);
		final Database database = mock(Database.class);
		when(database.getConnection()).thenReturn(connection);
		when(liquibase.getDatabase()).thenReturn(database);
		final ResourceAccessor accessor = mock(ResourceAccessor.class);
		when(liquibase.getResourceAccessor()).thenReturn(accessor);
		when(defaultMigrator.getLiquibase()).thenReturn(liquibase);
		final InputStream changelog = new FileInputStream(new File("src/test/resources/changelogs.zip"));
		final String changeLog = "db/changelogs/changelog-main.xml";
		final String dataSet = "dataset";
		final ConfigRest config = new ConfigRest("municipios", dataSet, changeLog);
		when(defaultMigrator.getConfig()).thenReturn(config);
		final Migrator migrator = new MigratorUnzip(changelog, defaultMigrator, config);
		migrator.run();
	}
}
