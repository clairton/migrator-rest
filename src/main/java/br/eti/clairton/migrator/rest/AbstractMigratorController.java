package br.eti.clairton.migrator.rest;

import static br.com.caelum.vraptor.view.Results.status;
import static java.io.File.createTempFile;
import static java.nio.file.Files.copy;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Logger.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;

import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.observer.upload.DefaultUploadedFile;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.eti.clairton.migrator.Config;
import br.eti.clairton.migrator.Migrator;

public abstract class AbstractMigratorController implements Serializable {
	private static final Logger logger = getLogger(AbstractMigratorController.class.getSimpleName());
	private static final long serialVersionUID = -5726623316637519190L;
	private final Result result;
	private final Migrator migrator;
	private final Config config;
	private final ServletRequest request;

	public AbstractMigratorController(final ServletRequest request, final Result result, final Migrator migrator,
			final Config config) {
		this.request = request;
		this.result = result;
		this.migrator = migrator;
		if (config != null) {
			this.config = new Config(config.getDataSetPath(), config.getChangelogPath(), config.getSchema()) {
				@Override
				public Boolean isMigrate() {
					return true;
				}
			};
		} else {
			this.config = config;
		}
	}

	@Post({ "", "/" })
	public void run(final UploadedFile file) {
		try {
			final InputStream changelog;
			if (file == null || file.getFile() == null) {
				logger.log(WARNING, "UploadedFile is null, try load from 5request");
				final Object param = request.getAttribute("file");
				if (DefaultUploadedFile.class.isInstance(param)) {
					changelog = ((UploadedFile) param).getFile();
				} else {
					final File temp = new File(param.toString());
					changelog = new FileInputStream(temp);
				}
			} else {
				final File temp = createTempFile("changelog", ".zip");
				copy(file.getFile(), temp.toPath(), REPLACE_EXISTING);
				logger.log(INFO, "File is {0}", temp.getAbsolutePath());
				changelog = new FileInputStream(temp);
			}
			if (changelog == null) {
				logger.log(WARNING, "File not found, stream is null");
				result.use(status()).badRequest("Needs a changelog zip file");
			} else {
				final Object[] params = new Object[] { file.getFileName(), file.getSize() };
				logger.log(INFO, "Run migration for file {0} with {1} kbs", params);
				final Migrator migrator = new MigratorUnzip(changelog, this.migrator, config);
				migrator.run();
				result.use(status()).ok();
			}
		} catch (final Exception e) {
			logger.log(WARNING, "Houve um erro ao rodar as migrações", e);
			final String mensagem = "Houve um erro ao rodar as migrações, verifique os logs para maiores detalhes";
			result.use(status()).badRequest(mensagem);
		}
	}
}
