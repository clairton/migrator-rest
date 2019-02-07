package br.eti.clairton.migrator.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.nio.file.Files;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

public class MigratorRemoteTest {
	@Test
	public void testRun() {
		final String path = "src/test/resources/db/changelogs/changelog-main.xml";
		final Config config = new Config(null, path);
		final String address = "";
		final Uploader uploader = mock(Uploader.class);
		final Migrator migrator = new MigratorRemote(config, address, uploader);
		migrator.run();
		final ArgumentCaptor<File> file = ArgumentCaptor.forClass(File.class);
		final ArgumentCaptor<String> string = ArgumentCaptor.forClass(String.class);
		verify(uploader).run(file.capture(), string.capture());
		assertTrue(Files.exists(file.getValue().toPath()));
	}
}
