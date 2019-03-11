package br.eti.clairton.migrator.rest;

import static br.eti.clairton.migrator.rest.Utils.removeFileName;
import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.enterprise.inject.Vetoed;

import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

@Vetoed
public class MigratorUnzip implements Migrator {
	private static final Logger logger = getLogger(MigratorUnzip.class.getSimpleName());
	private final InputStream changelog;
	private final Migrator migrator;
	private final Config config;

	public MigratorUnzip(final InputStream changelog, final Migrator migrator, final Config config) {
		this.changelog = changelog;
		this.migrator = migrator;
		final String temp = getProperty("java.io.tmpdir") + separator + new Date().getTime() + separator;
		this.config = new Config(temp + config.getDataSetPath(), temp + config.getChangelogPath(), config.getSchema());
	}

	@Override
	public void run() {
		final String folder = removeFileName(config.getChangelogPath());
		unzip(changelog, folder);
		migrator.run();
	}

	// https://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	private void unzip(final InputStream stream, final String outputFolder) {
		logger.log(INFO, "Iniciando descompactação para {0}", outputFolder);
		byte[] buffer = new byte[1024];
		try {
			// create output directory is not exists
			final File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			// get the zip file content
			final ZipInputStream zis = new ZipInputStream(stream);
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();
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
