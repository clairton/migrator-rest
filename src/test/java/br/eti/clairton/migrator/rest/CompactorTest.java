package br.eti.clairton.migrator.rest;

import static java.nio.file.Files.exists;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

public class CompactorTest {
	@Test
	public void testRun() throws IOException {
		final String path = "src/test/resources/db/changelogs/changelog-main.xml";
		final Config config = new Config(null, path) {
			@Override
			public Boolean isMigrate() {
				return true;
			}
		};
		final String address = "";
		final String token = "";
		final Uploader uploader = mock(Uploader.class);
		final Compactor compactor = mock(Compactor.class);
		when(compactor.zip(config)).thenReturn(File.createTempFile("changelog", ".zip"));
		final Migrator migrator = new MigratorRemote(config, address, token, uploader, compactor);
		migrator.run();
		final ArgumentCaptor<File> file = ArgumentCaptor.forClass(File.class);
		final ArgumentCaptor<String> string = ArgumentCaptor.forClass(String.class);
		verify(uploader).run(file.capture(), string.capture(), string.capture());
		assertTrue(exists(file.getValue().toPath()));
	}
}
