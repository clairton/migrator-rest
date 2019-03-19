package br.eti.clairton.migrator.rest;

import java.io.File;

import org.junit.Test;

import br.com.caelum.vraptor.test.VRaptorIntegration;
import br.com.caelum.vraptor.test.http.Parameters;

public class MigratorControllerIntegrationTest extends VRaptorIntegration{
	@Test
	public void test() {
		final Parameters parameters = new Parameters();
		final File changelog = new File("src/test/resources/changelogs.zip");
		parameters.add("file", changelog);
		navigate()
			.post("/migrator", parameters)
			.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
			.execute()
			.wasStatus(200);
	}
}
