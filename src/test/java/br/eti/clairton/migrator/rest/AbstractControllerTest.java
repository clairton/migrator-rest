package br.eti.clairton.migrator.rest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;
import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

public class AbstractControllerTest {
	private MockResult result;
	private AbstractMigratorController controller;
	private Migrator migrator;
	private Config config;

	@Before
	public void setUp() {
		result = new MockResult();
		migrator = mock(Migrator.class);
		final String path = "target/changelog-" + new Date().getTime() + "/db/changelogs/changelog-main.xml";
		config = new Config("", path);
		controller = new MigratorController(result, migrator, config);
	}

	@Test
	public void test() throws IOException {
		final File changelog = new File("src/test/resources/changelogs.zip");
		final UploadedFile file = mock(UploadedFile.class);
		when(file.getFile()).thenReturn(new FileInputStream(changelog));
		controller.run(file);
	}
}
