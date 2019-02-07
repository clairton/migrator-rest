package br.eti.clairton.migrator.rest;

import java.io.File;

class Utils {
	public static String removeFileName(final String path) {
		return new File(path).getParentFile().getPath();
	}
}
