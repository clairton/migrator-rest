package br.eti.clairton.migrator.rest;

import static br.eti.clairton.migrator.rest.Utils.removeFileName;
import static java.io.File.createTempFile;
import static java.io.File.separator;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.enterprise.inject.Vetoed;

import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

@Vetoed
public class MigratorRemote implements Migrator {
	private static final Logger logger = getLogger(MigratorRemote.class.getSimpleName());
	private final Config config;
	private final Uploader uploader;
	private final String url;

	public MigratorRemote(final Config config, final String url, final Uploader uploader) {
		this.config = config;
		this.uploader = uploader;
		this.url = url;
	}

	@Override
	public void run() {
		final String sourceFolder = removeFileName(config.getChangelogPath());
		final File changelog = zip(new File(sourceFolder).getAbsolutePath());
		uploader.run(changelog, url);
	}

	// http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
	private File zip(final String sourceFolder) {
		try {
			final File output = createTempFile("changelog", ".zip");
			final Collection<String> files = generateFileList(sourceFolder, new ArrayList<>(), new File(sourceFolder));
			final FileOutputStream fileStream = new FileOutputStream(output);
			final ZipOutputStream zipStream = new ZipOutputStream(fileStream);
			logger.log(INFO, "Output to Zip: {0}", output);
			byte[] buffer = new byte[1024];
			for (final String file : files) {
				logger.log(INFO, "File Added: {0}", file);
				final ZipEntry ze = new ZipEntry(file);
				zipStream.putNextEntry(ze);
				final String address = sourceFolder + separator + file;
				final FileInputStream in = new FileInputStream(address);
				int len;
				while ((len = in.read(buffer)) > 0) {
					zipStream.write(buffer, 0, len);
				}
				in.close();
			}
			zipStream.closeEntry();
			zipStream.close();
			logger.log(INFO, "Success {0} files added", files.size());
			return output;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Collection<String> generateFileList(final String folder, final Collection<String> files, final File node) {
		if (node.isFile()) {
			files.add(generateZipEntry(folder, node.getAbsoluteFile().toString()));
		} else if (node.isDirectory()) {
			final String[] subNode = node.list();
			for (final String filename : subNode) {
				generateFileList(folder, files, new File(node, filename));
			}
		}
		return files;
	}

	private String generateZipEntry(String sourceFolder, String file) {
		return file.substring(sourceFolder.length() + 1, file.length());
	}
}
