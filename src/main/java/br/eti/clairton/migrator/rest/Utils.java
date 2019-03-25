package br.eti.clairton.migrator.rest;

import java.io.File;

class Utils {
	public static String removeFileName(final String path) {
		final String parent = new File(path).getParentFile().getPath();
		return new File(parent).getAbsolutePath();
	}
}
