package br.eti.clairton.migrator.rest;

import static br.com.caelum.vraptor.controller.HttpMethod.PATCH;

import org.junit.Test;

import br.com.caelum.vraptor.test.VRaptorIntegration;
import br.com.caelum.vraptor.test.http.Parameters;

public class MigratrorControllerIntegrationTest extends VRaptorIntegration {
//	@ClassRule
//	public static TestRule ruleFixture = new FixtureRule();

	@Test
	public void test() {
		navigate().to("/migrator", PATCH, new Parameters()).execute().wasStatus(200).isValid();
	}
}
