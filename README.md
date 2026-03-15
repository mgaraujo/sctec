# Empreendedorismo (API de Empreendimentos)


## Vídeo Pitch
 - https://www.youtube.com/watch?v=-YgIenH0t4I

## Descricao da solucao desenvolvida
API REST para cadastro e gerenciamento (CRUD) de **Empreendimentos**.

- Entidade principal: `Empreendimento` (codigo, nome, nomeResponsavel, municipio, segmento, contato, status).
- Persistencia: Spring Data JPA com banco **H2 em arquivo** .
- Municipios de SC: carregados no startup via API do IBGE, com fallback local em `src/main/resources/municipios_sc.json` caso a API esteja offline.
- Normalizacao de texto: acentos sao removidos ao carregar municipios e ao salvar input do usuario.

## Tecnologias utilizadas
- Java `21`
- Spring Boot `4.0.3`
- Maven (wrapper `./mvnw` / `mvnw.cmd`)
- Spring Web
- Spring Data JPA
- H2 Database
- Spring Validation
- Springdoc OpenAPI (Swagger UI) `2.8.0`

## Estrutura geral do projeto
- `pom.xml`
- `src/main/java/com/marciotech/sctech/EmpreendedorismoApplication.java` (bootstrap)
- `src/main/java/com/marciotech/sctech/config/H2ConsoleConfig.java` (H2 Console)
- `src/main/java/com/marciotech/sctech/config/SpringDataWebConfig.java` (serializacao estavel de paginacao)
- `src/main/java/com/marciotech/sctech/controllers/EmpreendimentoController.java` (endpoints REST)
- `src/main/java/com/marciotech/sctech/services/EmpreendimentoService.java` (servicos/regras)
- `src/main/java/com/marciotech/sctech/repositories/EmpreendimentoRepository.java` (repositorio JPA)
- `src/main/java/com/marciotech/sctech/entities/Empreendimento.java` e `Segmento.java` (modelo)
- `src/main/resources/application.yaml` (configuracoes H2/JPA)
- `src/main/resources/municipios_sc.json` (fallback offline dos municipios de SC)
- `scripts/` (scripts para popular dados via API)

## Instrucoes para execucao
### Pre-requisitos
- JDK 21 instalado e configurado no `PATH`.

### Subir a aplicacao
Linux/macOS:
```bash
./mvnw spring-boot:run	
```

Windows:
```bat
mvnw.cmd spring-boot:run
```

### Rodar testes
Linux/macOS:
```bash
./mvnw test
```

Windows:
```bat
mvnw.cmd test
```

### Acessos locais
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console/`
  - JDBC URL: `jdbc:h2:file:./data/empreendedorismodb`
  - User: `sa`
  - Password: (vazio)

## Endpoints
- `GET /empreendimentos` (lista todos)
- `GET /empreendimentos/{codigo}` (busca por id)
- `POST /empreendimentos` (cria)
- `PUT /empreendimentos/{codigo}` (atualiza)
- `DELETE /empreendimentos/{codigo}` (remove)

### Filtros e paginacao (GET /empreendimentos)
Filtros opcionais (query params): `nome`, `nomeResponsavel`, `municipio`, `segmento`, `contato`, `status`.

Paginacao/ordenacao (Spring Data): `page`, `size`, `sort` (ex.: `sort=nome,asc`).

### Exemplo de payload (POST/PUT)
```json
{
  "nome": "Loja Exemplo",
  "nomeResponsavel": "Maria Silva",
  "municipio": "Joinville",
  "segmento": "COMERCIO",
  "contato": "maria@example.com",
  "status": true
}
```

### Validacao
Se o municipio informado nao existir na tabela de municipios de SC, a API retorna `400 Bad Request` com uma mensagem de validacao.

### Exemplos de curl
Criar:
```bash
curl -i -X POST "http://localhost:8080/empreendimentos" \
  -H "Content-Type: application/json" \
  -d '{"nome":"Loja Exemplo","nomeResponsavel":"Maria Silva","municipio":"Joinville","segmento":"COMERCIO","contato":"maria@example.com","status":true}'
```

Listar:
```bash
curl -i "http://localhost:8080/empreendimentos"
```

Listar com filtro e paginacao:
```bash
curl -i "http://localhost:8080/empreendimentos?municipio=Florianopolis&status=true&page=0&size=10&sort=nome,asc"
```

## Popular dados via script
Com a aplicacao rodando, voce pode criar 50 empreendimentos ficticios chamando a propria API:

Linux/macOS:
```bash
./scripts/populate_empreendimentos.sh
```

Windows (PowerShell):
```powershell
./scripts/populate_empreendimentos.ps1
```

Opcional: defina `BASE_URL`/`BaseUrl` se a API nao estiver em `http://localhost:8080`.
