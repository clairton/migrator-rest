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

import br.eti.clairton.migrator.Config;

public class Compactor {
	private static final Logger logger = getLogger(Compactor.class.getSimpleName());

	public File zip(final Config config) {
		final String sourceFolder = removeFileName(config.getChangelogPath());
		final File file = new File(sourceFolder);
		if (!file.exists()) {
			throw new IllegalStateException("Changelog folder " + file.getAbsolutePath() + " not exist!");
		}
		return zip(file.getAbsolutePath());
	}

	public File zip(final String folder) {
		// http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
		try {
			logger.log(INFO, "Changelog folder to zip: {0}", folder);
			final File output = createTempFile("changelog", ".zip");
			logger.log(INFO, "Output to Zip: {0}", output.getAbsolutePath());
			final Collection<String> files = generateFileList(folder, new ArrayList<>(), new File(folder));
			if (files.isEmpty()) {
				throw new IllegalStateException("Folder  " + folder + " is empty!");
			}
			final FileOutputStream fileStream = new FileOutputStream(output);
			final ZipOutputStream zipStream = new ZipOutputStream(fileStream);
			logger.log(INFO, "Output to Zip: {0}", output.getAbsolutePath());
			byte[] buffer = new byte[1024];
			for (final String file : files) {
				logger.log(INFO, "File Added: {0}", file);
				final ZipEntry ze = new ZipEntry(file);
				zipStream.putNextEntry(ze);
				final String address = folder + separator + file;
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
