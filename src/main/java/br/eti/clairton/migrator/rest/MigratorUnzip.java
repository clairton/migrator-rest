package br.eti.clairton.migrator.rest;

import static java.util.logging.Level.INFO;
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
		this.config = config;
	}

	@Override
	public void run() {
		final String folder = removeFileName(config.getChangelogPath());
		unzip(changelog, folder);
		migrator.run();
	}

	private String removeFileName(final String path) {
		return new File(path).getParentFile().getPath();
	}

	// https://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	private void unzip(final InputStream stream, final String outputFolder) {
		byte[] buffer = new byte[1024];
		try {
			// create output directory is not exists
			final File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
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
