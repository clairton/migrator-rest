package br.eti.clairton.migrator.rest;

import static br.com.caelum.vraptor.view.Results.status;
import static java.io.File.createTempFile;
import static java.io.File.separator;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.System.getProperty;
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

	public AbstractMigratorController(final ServletRequest request, final Result result, final Migrator migrator, final Config config) {
		this.request = request;
		this.result = result;
		this.migrator = migrator;
		this.config = config;
	}

	@Post({ "", "/" })
	public void run(final UploadedFile file, final String tenant) {
		try {
			final InputStream changelog;
			if (file == null || file.getFile() == null) {
				logger.log(WARNING, "UploadedFile is null, try load from servlet request");
				final Object param = request.getAttribute("file");
				if (DefaultUploadedFile.class.isInstance(param)) {
					changelog = ((UploadedFile) param).getFile();
				} else if (param != null){
					final File temp = new File(param.toString());
					changelog = new FileInputStream(temp);
				} else {
					logger.log(WARNING, "File from servlet request is null");
					changelog = null;
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
				final String path = getProperty("java.io.tmpdir") + separator + tenant;
				logger.log(INFO, "Class path tenant for changelogs {0}", path);
				final ConfigRest config = new ConfigRest(path, this.config.getDataSetPath(), this.config.getChangelogPath(), this.config.getSchema()) {
					@Override
					public Boolean isDrop() {
						return FALSE;
					}

					@Override
					public Boolean isPopulate() {
						return FALSE;
					}

					@Override
					public Boolean isMigrate() {
						return TRUE;
					}
				};
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
