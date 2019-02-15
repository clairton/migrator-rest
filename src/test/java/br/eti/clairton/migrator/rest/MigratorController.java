package br.eti.clairton.migrator.rest;

import static br.com.caelum.vraptor.view.Results.status;

import java.io.IOException;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
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
	private final Result result;

	@Deprecated
	public MigratorController() {
		this(null, null, null);
	}

	@Inject
	public MigratorController(final Result result, final Migrator migrator, final Config config) {
		super(result, migrator, config);
		this.result = result;
	}

	@Get({ "", "/" })
	public void ping() {
		result.use(status()).noContent();
	}

	@Post({ "", "/" })
	public void run(final UploadedFile file) throws IOException {
		super.run(file);
	}
}
