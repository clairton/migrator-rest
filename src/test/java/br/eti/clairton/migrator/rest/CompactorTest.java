package br.eti.clairton.migrator.rest;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import br.eti.clairton.migrator.Config;

public class CompactorTest {
	@Test
	public void testZip() {
		final Config config = new ConfigRest(null, null, "db/changelogs/changelog-1.0.0.xml");
		final Compactor compactor = new Compactor();
		final File zip = compactor.zip(config);
		assertNotNull(zip.length());
	}
}
