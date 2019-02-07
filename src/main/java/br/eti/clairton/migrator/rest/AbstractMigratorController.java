package br.eti.clairton.migrator.rest;

import static br.com.caelum.vraptor.view.Results.status;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import br.com.caelum.vraptor.Patch;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

public abstract class AbstractMigratorController implements Serializable {
	private static final long serialVersionUID = -5726623316637519190L;
	private final Result result;
	private final Migrator migrator;
	private final Config config;

	public AbstractMigratorController(final Result result, final Migrator migrator, final Config config) {
		this.result = result;
		this.migrator = migrator;
		this.config = config;
	}

	@Patch("")
	public void run(final UploadedFile upload) throws IOException {
		final InputStream changelog = upload.getFile();
		final Migrator migrator = new MigratorUnzip(changelog, this.migrator, config);
		migrator.run();
		result.use(status()).ok();
	}
}
