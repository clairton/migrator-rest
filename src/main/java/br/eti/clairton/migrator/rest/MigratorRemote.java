package br.eti.clairton.migrator.rest;

import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.util.logging.Logger;

import javax.enterprise.inject.Vetoed;

import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

@Vetoed
public class MigratorRemote implements Migrator {
	private static final Logger logger = getLogger(MigratorRemote.class.getSimpleName());
	private final Config config;
	private final Uploader uploader;
	private final Compactor compactor;
	private final String url;
	private final String token;

	public MigratorRemote(final Config config, final String url, final String token) {
		this(config, url, token, new Uploader(), new Compactor());
	}

	public MigratorRemote(
			final Config config, 
			final String url, 
			final String token, 
			final Uploader uploader,
			final Compactor compactor) {
		this.config = config;
		this.uploader = uploader;
		this.url = url;
		this.token = token;
		this.compactor = compactor;
	}

	@Override
	public void run() {
		if (config.isMigrate()) {
			final File changelog = compactor.zip(config);
			final Object[] params = new Object[] { changelog.getAbsoluteFile(), changelog.length() };
			logger.log(INFO, "Uploading file {0} with size {1} kb", params);
			uploader.run(changelog, url, token);
		}
	}
}
