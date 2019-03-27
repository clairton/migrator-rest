package br.eti.clairton.migrator.rest;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.enterprise.inject.Vetoed;

import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Inserter;
import br.eti.clairton.migrator.Migrator;
import br.eti.clairton.migrator.MigratorDefault;
import liquibase.Liquibase;

@Vetoed
public class MigratorUnzip implements Migrator {
	private static final Logger logger = getLogger(MigratorUnzip.class.getSimpleName());
	private final InputStream changelog;
	private final Migrator migrator;
	private final Config config;
	private final Compactor compactor;
	
	public MigratorUnzip(final InputStream changelog, final Migrator migrator, final Config config) {
		this(changelog, migrator, config, new Compactor());
	}

	public MigratorUnzip(final InputStream changelog, final Migrator migrator, final Config config, final Compactor compactor) {
		this.changelog = changelog;
		this.config = new Config(config.getDataSetPath(), config.getChangelogPath(), config.getSchema()) {
			@Override
			public Boolean isDrop() {
				return FALSE;
			}

			@Override
			public Boolean isPopulate() {
				return FALSE;
			}

			@Override
			public Boolean isMigrate() {
				return TRUE;
			}
		};
		final MigratorDefault migratorDefault = (MigratorDefault) migrator;
		final Liquibase liquibase = migratorDefault.getLiquibase();
		final Inserter inserter = migratorDefault.getInserter();
		this.migrator = new MigratorDefault(liquibase, this.config, inserter);
		this.compactor = compactor;
	}

	@Override
	public void run() {
		compactor.unzip(changelog, config);
		final Object[] params = new Object[] { config.getChangelogPath(), config.getDataSetPath() };
		logger.log(INFO, "Rodando migrator, changelog path: {0}, dataset path: {1}", params);
		migrator.run();
	}
}
