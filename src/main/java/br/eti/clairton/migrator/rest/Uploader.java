package br.eti.clairton.migrator.rest;

import static java.lang.Long.toHexString;
import static java.net.URLConnection.guessContentTypeFromName;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.copy;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class Uploader {
	boolean run(final File file, final String url) {
		try {
			final String boundary = toHexString(System.currentTimeMillis()); // Just generate some unique random value.
			final String CRLF = "\r\n";
			final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PATCH");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			final OutputStream output = connection.getOutputStream();
			final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, UTF_8), true);
			writer.append("--" + boundary);
			writer.append(CRLF);
			writer.append("Content-Disposition: form-data; filename=\"changelog.zip\"");
			writer.append(CRLF);
			writer.append("Content-Type: " + guessContentTypeFromName(file.getName()));
			writer.append(CRLF);
			writer.append("Content-Transfer-Encoding: binary");
			writer.append(CRLF);
			writer.append(CRLF);
			writer.flush();
			copy(file.toPath(), output);
			output.flush(); // Important before continuing with writer!
			writer.append(CRLF);
			writer.flush();
			writer.append("--" + boundary + "--").append(CRLF).flush();
			writer.flush();
			int responseCode = connection.getResponseCode();
			return responseCode == 200;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
