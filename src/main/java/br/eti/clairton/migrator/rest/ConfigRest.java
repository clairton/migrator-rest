package br.eti.clairton.migrator.rest;

import javax.enterprise.inject.Vetoed;

import br.eti.clairton.migrator.Config;

@Vetoed
public class ConfigRest extends Config {
	private final String tenant;

	public ConfigRest(final String tenant, final String datasetPath, final String changelogPath) {
		this(tenant, datasetPath, changelogPath, null);
	}

	public ConfigRest(final String tenant, final String datasetPath, final String changelogPath, final String schema) {
		super(datasetPath, changelogPath, schema);
		this.tenant = tenant;
	}

	public String getTenant() {
		return tenant;
	}
}
