package br.eti.clairton.migrator.rest;

import javax.inject.Inject;
import javax.servlet.ServletRequest;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
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
	public MigratorController(final ServletRequest request, final Result result, final Migrator migrator, @Rest final Config config) {
		super(request, result, migrator, config);
	}

	@Override
	@Post({ "", "/" })
	public void run(final UploadedFile file, final String tenant) {
		super.run(file, tenant);
	}
}
