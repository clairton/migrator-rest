package br.eti.clairton.migrator.rest;

import static br.com.caelum.vraptor.view.Results.status;

import java.io.Serializable;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Patch;
import br.com.caelum.vraptor.Result;

@Controller
@Patch("/migrator")
public class MigratorController implements Serializable {
	private static final long serialVersionUID = -5726623316637519190L;

	@Inject
	private Result result;

	@Patch("")
	public void run() {
		result.use(status()).ok();
	}

}
