package br.eti.clairton.migrator.rest;

import static br.com.caelum.vraptor.view.Results.status;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

public abstract class AbstractMigratorController implements Serializable {
	private static final Logger logger = getLogger(AbstractMigratorController.class.getSimpleName());
	private static final long serialVersionUID = -5726623316637519190L;
	private final Result result;
	private final Migrator migrator;
	private final Config config;

	public AbstractMigratorController(final Result result, final Migrator migrator, final Config config) {
		this.result = result;
		this.migrator = migrator;
		if (config != null) {
			this.config = new Config(config.getDataSetPath(), config.getChangelogPath(), config.getSchema()) {
				@Override
				public Boolean isMigrate() {
					return true;
				}
			};
		} else {
			this.config = config;
		}
	}

	@Post({ "", "/" })
	public void run(final UploadedFile file) throws IOException {
		final InputStream changelog = file.getFile();
		final Object[] params = new Object[] { file.getFileName(), file.getSize() };
		logger.log(INFO, "Run migration for file {0} with {1} kbs", params);
		final Migrator migrator = new MigratorUnzip(changelog, this.migrator, config);
		migrator.run();
		result.use(status()).ok();
	}
}
