package br.eti.clairton.migrator.rest;

import static br.eti.clairton.migrator.rest.Utils.removeFileName;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

	public MigratorUnzip(final InputStream changelog, final Migrator migrator, final Config config) {
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
	}

	@Override
	public void run() {
		final String folder = removeFileName(config.getChangelogPath());
		unzip(changelog, folder);
		final Object[] params = new Object[] { config.getChangelogPath(), config.getDataSetPath() };
		logger.log(INFO, "Rodando migrator, changelog path: {0}, dataset path: {1}", params);
		migrator.run();
	}

	// https://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	private void unzip(final InputStream stream, final String outputFolder) {
		if (stream == null) {
			logger.log(WARNING, "Stream esta nulo");
			throw new IllegalStateException();
		}
		logger.log(INFO, "Iniciando descompactação para {0}", outputFolder);
		byte[] buffer = new byte[1024];
		try {
			// create output directory is not exists
			final File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdirs();
				logger.log(INFO, "Criado pasta {0}", folder.getAbsolutePath());
			} else {
				logger.log(INFO, "Já existe pasta {0}", folder.getAbsolutePath());
			}
			final ZipInputStream zis = new ZipInputStream(stream);
			ZipEntry ze = zis.getNextEntry();
			if (stream == null || ze == null) {
				logger.log(WARNING, "Não foi possível recuperar o arquivo do .zip");
				throw new NullPointerException();
			}
			while (ze != null) {
				final String fileName = ze.getName();
				final File newFile = new File(outputFolder + File.separator + fileName);
				logger.log(INFO, "Descompactando {0}", newFile.getAbsoluteFile());
				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();
				final FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				ze = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
			logger.log(INFO, "Finalizado descompatação");
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
