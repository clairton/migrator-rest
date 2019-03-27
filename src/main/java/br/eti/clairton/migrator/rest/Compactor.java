package br.eti.clairton.migrator.rest;

import static java.io.File.createTempFile;
import static java.io.File.separator;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import br.eti.clairton.migrator.Config;

public class Compactor {
	private static final Logger logger = getLogger(Compactor.class.getSimpleName());

	public File zip(final Config config) {
		final File file = removeFileName(config.getChangelogPath());
		return zip(file.getAbsolutePath());
	}

	public File zip(final String folder) {
		if (!new File(folder).exists()) {
			throw new IllegalStateException("Changelog folder " + folder + " not exist!");
		}
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

	public void unzip(final InputStream stream, final Config config) {
		final File folder = removeFileName(config.getChangelogPath());
		unzip(stream, folder.getAbsolutePath());
	}

	// https://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	public void unzip(final InputStream stream, final String outputFolder) {
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

	private String generateZipEntry(final String sourceFolder, final String file) {
		return file.substring(sourceFolder.length() + 1, file.length());
	}

	private File removeFileName(final String path) {
		final String parent = new File(path).getParentFile().getPath();
		return new File(parent);
	}
}
