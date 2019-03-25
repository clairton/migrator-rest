package br.eti.clairton.migrator.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;

import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;
import br.eti.clairton.migrator.MigratorDefault;

public class MigratorUnzipTest {
	@Test
	public void testRun() throws FileNotFoundException {
		final Migrator defaultMigrator = Mockito.mock(MigratorDefault.class);
		final InputStream changelog = new FileInputStream(new File("src/test/resources/changelogs.zip"));
		final String base = "target/changelog-" + new Date().getTime();
		final String changeLog = base + "/db/changelogs/changelog-main.xml";
		final String dataSet = base + "/dataset";
		final Config config = new Config(dataSet, changeLog);
		final Migrator migrator = new MigratorUnzip(changelog, defaultMigrator, config);
		migrator.run();
	}
}
