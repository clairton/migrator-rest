package br.eti.clairton.migrator.rest;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

public class CompactorTest {
	@Test
	public void testRun() {
		final String path = new File("src/test/resources/db/changelogs").getAbsolutePath();
		final Compactor compactor = new Compactor();
		final File zip = compactor.zip(path);
		assertNotNull(zip.length());
	}
}
