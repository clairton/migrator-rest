package br.eti.clairton.migrator.rest;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class MigratorRemoteTest {
	@Test
	public void testRun() {
		final String path = new File("src/test/resources/db/changelogs").getAbsolutePath();
		final Compactor compactor = new Compactor();
		final File zip = compactor.zip(path);
		assertEquals(1408l, zip.length());
	}
}
