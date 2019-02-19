# migrator [![Build Status](https://travis-ci.org/clairton/migrator-rest.svg?branch=master)](https://travis-ci.org/clairton/migrator-rest)

Migração de dados, usando o projeto https://github.com/clairton/migrator com HTTP.

Para receber o changelog em uma aplicação:

```java
@Controller
@Path("/migrator")
public class MigratorController extends AbstractMigratorController {
	private static final long serialVersionUID = -5130409602054807205L;

	@Deprecated
	public MigratorController() {
		this(null, null, null);
	}

	@Inject
	public MigratorController(final Result result, final Migrator migrator, final Config config) {
		super(result, migrator, config);
	}
}
```

Para enviar o changelog para uma aplicação:

```java
@Controller
@Path("/migrator")
public class MigratorController extends AbstractMigratorController {
	private static final long serialVersionUID = -5130409602054807205L;

	@Deprecated
	public MigratorController() {
		this(null, null, null);
	}

	@Inject
	public MigratorController(final Result result, final Migrator migrator, final Config config) {
		super(result, migrator, config);
	}
}
```

Download através do maven, dependência:
```xml
<dependency>
  <groupId>br.eti.clairton</groupId>
  <artifactId>migrator-rest</artifactId>
  <version>X.X.X</version>
  <scope>compile</scope>
</dependency>
```