package br.eti.clairton.migrator.rest;

import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

import javax.enterprise.inject.Vetoed;

import br.eti.clairton.migrator.Inserter;
import br.eti.clairton.migrator.Migrator;
import br.eti.clairton.migrator.MigratorDefault;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.CompositeResourceAccessor;
import liquibase.resource.ResourceAccessor;

@Vetoed
public class MigratorUnzip implements Migrator {
	private static final Logger logger = getLogger(MigratorUnzip.class.getSimpleName());
	private final InputStream changelog;
	private final Migrator migrator;
	private final ConfigRest config;
	private final Compactor compactor;

	public MigratorUnzip(final InputStream changelog, final Migrator migrator, final ConfigRest config) {
		this(changelog, migrator, config, new Compactor());
	}

	public MigratorUnzip(final InputStream changelog, final Migrator migrator, final ConfigRest config, final Compactor compactor) {
		this.changelog = changelog;
		this.config = config;
		final MigratorDefault migratorDefault = (MigratorDefault) migrator;
		final Liquibase original = migratorDefault.getLiquibase();
		final Liquibase liquibase = getLiquibase(this.config, original);
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

	private static Liquibase getLiquibase(final ConfigRest config, final Liquibase original) {
		try {
			final File file = new File(config.getTenant());
			if (!file.exists()) {
				if (!file.mkdirs()) {
					throw new RuntimeException("Não foi possível criar o diretorio " + file.getAbsolutePath());
				}
			}
			final URL url = file.toURI().toURL();
			logger.log(INFO, "Adicionando {0} ao class loader para liquibase carregar changelogs", url);
			final URL[] urls = new URL[] { url };
			final ClassLoader classLoader = new URLClassLoader(urls);
			final Database connection = original.getDatabase();
			final ClassLoaderResourceAccessor additional = new ClassLoaderResourceAccessor(classLoader);
			final ResourceAccessor resourceAccessor = new CompositeResourceAccessor(original.getResourceAccessor(), additional);
			final Liquibase liquibase = new Liquibase(config.getChangelogPath(), resourceAccessor, connection);
			return liquibase;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
