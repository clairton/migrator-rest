package br.eti.clairton.migrator.rest;

import static java.nio.file.Files.readAllBytes;
import static java.util.logging.Level.INFO;
import static java.util.logging.Logger.getLogger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

class Uploader {
	private static final Logger logger = getLogger(Uploader.class.getSimpleName());

	boolean run(final File file, final String url) {
		// https://blog.morizyun.com/blog/android-httpurlconnection-post-multipart/index.html
		final String crlf = "\r\n";
		final String twoHyphens = "--";
		try {
			final String boundary = "*****";
			final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			final DataOutputStream output = new DataOutputStream(connection.getOutputStream());
			output.writeBytes(twoHyphens + boundary + crlf);
			output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"changelog.zip\"");
			output.writeBytes(crlf + crlf);
			byte[] bytes = readAllBytes(file.toPath());
			output.write(bytes);
			output.writeBytes(crlf);
			output.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
			output.flush();
			output.close();
			int status = connection.getResponseCode();
			logger.log(INFO, "Http status {0} for {1} address", new Object[] { status, url });
			return status == 200;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
