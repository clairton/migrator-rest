package br.eti.clairton.migrator.rest;

import static java.io.File.createTempFile;
import static java.io.File.separator;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
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
		logger.log(INFO, "Changelog main path from configuration: {0}", config.getChangelogPath());
		final String folder = removeFileName(config.getChangelogPath());
		logger.log(INFO, "Changelog folder from configuration: {0}", folder);
		final URL url = getClass().getClassLoader().getResource(folder);
		if (url == null) {
			throw new IllegalStateException("Folder  " + config.getChangelogPath() + " is not loaded by class loader!");
		}
		return zip(url);
	}

	public File zip(final URL folder) {
//		if (!new File(folder).exists()) {
//			throw new IllegalStateException("Changelog folder " + folder + " not exist!");
//		}
		// http://www.mkyong.com/java/how-to-compress-files-in-zip-format/
		try {
			logger.log(INFO, "Changelog folder to zip: {0}", folder);
			final File output = createTempFile("changelog", ".zip");
			logger.log(INFO, "Output to Zip: {0}", output.getAbsolutePath());
			final Collection<URL> files = generateFileList(new ArrayList<>(), folder);
			if (files.isEmpty()) {
				throw new IllegalStateException("Folder  " + folder + " is empty!");
			}
			final FileOutputStream fileStream = new FileOutputStream(output);
			final ZipOutputStream zipStream = new ZipOutputStream(fileStream);
			logger.log(INFO, "Output to Zip: {0}", output.getAbsolutePath());
			byte[] buffer = new byte[1024];
			for (final URL file : files) {
				final String name = filename(file);
				logger.log(INFO, "File Added: {0}", name);
				final ZipEntry ze = new ZipEntry(name);
				zipStream.putNextEntry(ze);
				final InputStream in = file.openStream();
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
			logger.log(WARNING, "Error on zip changelog", e);
			throw new RuntimeException(e);
		}
	}

	public void unzip(final InputStream stream, final ConfigRest config) {
		final String folder = config.getTenant() + separator + removeFileName(config.getChangelogPath());
		unzip(stream, new File(folder).getAbsolutePath());
	}

	// https://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	public void unzip(final InputStream stream, final String outputFolder) {
		if (stream == null) {
			final String message = "Stream esta nulo";
			logger.log(WARNING, message);
			throw new IllegalStateException(message);
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
			logger.log(WARNING, "Error on unzip changelog", e);
			throw new RuntimeException(e);
		}
	}

	private Collection<URL> generateFileList(final Collection<URL> files, final URL node) {
		try {
			if (new File(node.getFile()).isFile()) {
				files.add(node);
			} else {
				final URLConnection conn = node.openConnection();
				final InputStream inputStream = conn.getInputStream();
				final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				final Collection<URL> children = new ArrayList<>();
				String line;
				while ((line = reader.readLine()) != null) {
					final String string = node.getProtocol() + ":" + node.getFile() + separator + line;
					logger.log(INFO, "Found url: " + string);
					final URL url = new URL(string);
					children.add(url);
				}
				reader.close();
				for (final URL file : children) {
					generateFileList(files, file);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return files;
	}

	private String filename(final URL uri) {
		final String[] parts = uri.toString().split("/");
		return parts[parts.length - 1];
	}

	private String removeFileName(final String path) {
		final String parent = new File(path).getParentFile().getPath();
		return parent;
	}
}
