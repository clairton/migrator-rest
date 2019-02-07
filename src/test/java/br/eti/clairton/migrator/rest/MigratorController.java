package br.eti.clairton.migrator.rest;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Patch;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

@Controller
@Patch("/migrator")
public class MigratorController extends AbstractMigratorController {
	private static final long serialVersionUID = -5130409602054807205L;

	@Inject
	public MigratorController(final Result result, final Migrator migrator, final Config config) {
		super(result, migrator, config);
	}
}
