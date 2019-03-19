package br.eti.clairton.migrator.rest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

@Controller
@Path("/migrator")
public class MigratorController extends AbstractMigratorController {
	private static final long serialVersionUID = -5130409602054807205L;

	@Deprecated
	public MigratorController() {
		this(null, null, null, null);
	}

	@Inject
	public MigratorController(final HttpServletRequest request, final Result result, final Migrator migrator,
			final Config config) {
		super(request, result, migrator, config);
	}

	@Override
	@Post({ "", "/" })
	public void run() {
		super.run();
	}
}
