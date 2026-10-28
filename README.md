# Sistema de Gestão de Planos de Voo

Backend REST em Java 17 + Spring Boot 3 + PostgreSQL.

## Data de Referência

**2026-11-15** — use esta data para demonstração de disponibilidade, conflitos, aprovação e cancelamento.

Aerovias com ocupação nessa data: **R1, R2, R3, R4, R6, R7** (6 de 8).  
Aerovias livres: R5 (CWB→FLN), R8 (FLN→POA).

## Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 14+

## Configuração do Banco

```sql
CREATE USER voo_user WITH PASSWORD 'voo_pass';
CREATE DATABASE voo OWNER voo_user;
```

Ou com Docker:

```bash
docker run -d \
  --name voo-postgres \
  -e POSTGRES_USER=voo_user \
  -e POSTGRES_PASSWORD=voo_pass \
  -e POSTGRES_DB=voo \
  -p 5432:5432 \
  postgres:16
```

## Executar a Aplicação

```bash
cd Documents/voo
mvn spring-boot:run
```

O Liquibase criará o schema e carregará os dados iniciais automaticamente na primeira execução.

## Acessar a API

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Endpoints Principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | /api/aeroportos | Listar aeroportos |
| POST | /api/aeroportos | Cadastrar aeroporto |
| GET | /api/aeroportos/{codigo}/vagas-disponiveis?inicio=&fim= | Consultar vagas disponíveis |
| GET | /api/aerovias | Listar aerovias |
| POST | /api/aerovias | Cadastrar aerovia |
| GET | /api/aerovias/{id}/altitudes-disponiveis?inicio=&fim= | Consultar altitudes disponíveis |
| GET | /api/pilotos | Listar pilotos |
| POST | /api/pilotos | Cadastrar piloto |
| GET | /api/aeronaves | Listar aeronaves |
| POST | /api/aeronaves | Cadastrar aeronave |
| POST | /api/planos | Criar plano de voo |
| GET | /api/planos/{id} | Buscar plano |
| GET | /api/planos?situacao= | Listar por situação |
| POST | /api/planos/{id}/trechos | Adicionar trecho |
| DELETE | /api/planos/{id}/trechos | Remover todos os trechos |
| GET | /api/planos/{id}/validar | Validar plano (sem reservar) |
| POST | /api/planos/{id}/aprovar | Aprovar e reservar recursos |
| POST | /api/planos/{id}/abandonar | Abandonar plano em edição |
| POST | /api/planos/{id}/cancelar | Cancelar plano aprovado futuro |

## Dados Iniciais

### Aeronaves
| Prefixo | Tipo | Velocidade | Autonomia |
|---------|------|-----------|-----------|
| PR-ABC | PARTICULAR | 500 km/h | 2000 km |
| PP-GLA | PASSAGEIROS | 850 km/h | 5000 km |
| PR-XYZ | PASSAGEIROS | 900 km/h | 6000 km |
| PT-CRG | CARGA | 750 km/h | 4000 km |

### Pilotos
| ID | Registro | Nome |
|----|----------|------|
| 1 | PIL001 | Carlos Alberto Silva |
| 2 | PIL002 | Ana Paula Souza |
| 3 | PIL003 | Roberto Mendes |
| 4 | PIL004 | Fernanda Lima |

### Planos Aprovados (data ref. 2026-11-15)
| ID | Rota | Aerovia | Partida | Aeronave |
|----|------|---------|---------|---------|
| 1 | POA→FLN | R1 | 08:00 | PP-GLA |
| 2 | POA→GRU | R2 | 09:00 | PR-XYZ |
| 3 | GRU→POA | R3 | 13:00 | PP-GLA |
| 4 | FLN→CWB | R4 | 10:00 | PR-XYZ |
| 5 | CWB→GRU | R6 | 14:00 | PR-XYZ |
| 6 | GRU→CWB | R7 | 16:00 | PP-GLA |
| 7 | POA→GRU | R2 | 2026-12-01 10:00 | PR-XYZ (futuro, cancelável) |

## Exemplos de Requisições

### Criar plano em edição
```json
POST /api/planos
{
  "pilotoId": 1,
  "aeronavePrefixo": "PP-GLA",
  "origemCodigo": "POA",
  "destinoCodigo": "FLN",
  "dataHoraPartida": "2026-11-15T15:00:00"
}
```

### Adicionar trecho com pouso
```json
POST /api/planos/{id}/trechos
{
  "aeroviaId": "R1",
  "altitude": 28000,
  "horaEntrada": "2026-11-15T15:00:00",
  "tipoPassagem": "POUSO",
  "numeroVaga": 2,
  "tempoPermanencia": 60
}
```

### Consultar altitudes disponíveis em R1 na data de referência
```
GET /api/aerovias/R1/altitudes-disponiveis?inicio=2026-11-15T08:00:00&fim=2026-11-15T09:00:00
```
> Resultado: 28000 estará ocupado (plano 1). Demais altitudes livres.

### Cadastrar aeronave de carga
```json
POST /api/aeronaves
{
  "tipo": "CARGA",
  "prefixo": "PT-NEW",
  "velocidadeCruzeiro": 700,
  "autonomia": 3500,
  "companhiaAerea": "Minha Carga",
  "capacidadeCargaToneladas": 30
}
```
