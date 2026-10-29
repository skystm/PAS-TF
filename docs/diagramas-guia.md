# Guia para Diagramas — UML e C4

Use **draw.io** (https://draw.io) para criar todos os diagramas. É gratuito e exporta PNG/PDF.

---

## Diagrama 1 — Modelo de Domínio (UML)

Crie um diagrama de classes com os seguintes elementos:

### Classes e atributos

**Aeroporto**
- codigo: String (PK)
- nome: String
- vagasEstacionamento: int

**Aerovia**
- id: String (PK)
- distancia: double
- → origem: Aeroporto
- → destino: Aeroporto

**Piloto**
- id: Long (PK)
- registro: String
- nome: String

**Aeronave** *(abstrata)*
- prefixo: String (PK)
- velocidadeCruzeiro: double
- autonomia: double
- + altitudePermitida(altitude): boolean
- + horarioPermitido(inicio, fim): boolean
- + autonomiaEfetiva(): double

**AeronaveParticular** *(extends Aeronave)*
- empresaManutencao: String

**AeronavePassageiros** *(extends Aeronave)*
- companhiaAerea: String
- capacidadePassageiros: int

**AeronaveCarga** *(extends Aeronave)*
- companhiaAerea: String
- capacidadeCargaToneladas: double

**PlanoVoo**
- id: Long (PK)
- dataHoraPartida: LocalDateTime
- situacao: SituacaoPlano {EM_EDICAO, APROVADO, CANCELADO, ABANDONADO}
- → piloto: Piloto
- → aeronave: Aeronave
- → origem: Aeroporto
- → destino: Aeroporto

**TrechoVoo**
- id: Long (PK)
- ordem: int
- altitude: int
- horaEntrada: LocalDateTime
- horaSaida: LocalDateTime
- tipoPassagem: TipoPassagem {SOBREVOO, POUSO}
- numeroVaga: Integer (nullable)
- tempoPermanencia: Integer (nullable)
- → aerovia: Aerovia

**ReservaAerovia**
- id: Long (PK)
- altitude: int
- slotInicio: LocalDateTime
- slotFim: LocalDateTime
- → aerovia: Aerovia

**ReservaVaga**
- id: Long (PK)
- numeroVaga: int
- slotInicio: LocalDateTime
- slotFim: LocalDateTime
- → aeroporto: Aeroporto

### Relacionamentos
- PlanoVoo "1" → "0..*" TrechoVoo (composição)
- PlanoVoo "1" → "0..*" ReservaAerovia (composição)
- PlanoVoo "1" → "0..*" ReservaVaga (composição)
- TrechoVoo "*" → "1" Aerovia
- Aerovia "*" → "1" Aeroporto (origem)
- Aerovia "*" → "1" Aeroporto (destino)
- Aeronave ← AeronaveParticular (herança)
- Aeronave ← AeronavePassageiros (herança)
- Aeronave ← AeronaveCarga (herança)

---

## Diagrama C4 — Nível 1: Contexto

Elementos:
- **[Pessoa]** Usuário (operador do sistema, faz requisições HTTP)
- **[Sistema]** Sistema de Gestão de Planos de Voo (backend REST)

Relação:
- Usuário → Sistema: "Cria e gerencia planos de voo via API REST"

Não há sistemas externos neste projeto.

---

## Diagrama C4 — Nível 2: Contêineres

Elementos:
- **[Pessoa]** Usuário
- **[Contêiner: Aplicação]** Backend Spring Boot
  - Tecnologia: Java 21 + Spring Boot 3.3
  - Responsabilidade: Expõe API REST, aplica regras de negócio, gerencia planos e reservas
- **[Contêiner: Banco de Dados]** PostgreSQL
  - Tecnologia: PostgreSQL 16
  - Responsabilidade: Persiste aeroportos, aerovias, aeronaves, pilotos, planos, trechos e reservas

Relações:
- Usuário → Backend: "HTTP/JSON (porta 8080)"
- Backend → PostgreSQL: "JDBC/JPA (porta 5432)"

---

## Diagrama C4 — Nível 3: Componentes (dentro do Backend)

Elementos (componentes):

**Controllers (REST)**
- AeroportoController
- AeroviaController
- PilotoController
- AeronaveController
- PlanoVooController

**Services (Regras de Negócio)**
- AeroportoService
- AeroviaService
- PilotoService
- AeronaveService
- PlanoVooService → orquestra criação, validação, aprovação e cancelamento
- DisponibilidadeService → calcula slots e verifica conflitos

**Repositories (Acesso a Dados)**
- AeroportoRepository
- AeroviaRepository
- PilotoRepository
- AeronaveRepository
- PlanoVooRepository
- ReservaAeroviaRepository
- ReservaVagaRepository

**Domínio (Modelo)**
- Aeroporto, Aerovia, Piloto
- Aeronave (abstrata) + subclasses
- PlanoVoo, TrechoVoo
- ReservaAerovia, ReservaVaga

Relações:
- Controllers → Services (chamam métodos de negócio)
- Services → Repositories (persistência)
- Services → Domain Model (regras encapsuladas nas entidades)
- Repositories → PostgreSQL

---

## Como criar no draw.io

1. Acesse https://draw.io
2. Clique em "Create New Diagram"
3. Para o UML: use shapes "UML" na barra lateral esquerda
4. Para C4: use retângulos simples com cores diferentes por tipo:
   - Pessoa: azul claro
   - Sistema/Contêiner externo: cinza
   - Contêiner interno: azul escuro
   - Componente: azul médio
5. Salve como `.drawio` e exporte como PNG
6. Coloque os arquivos em `docs/diagramas/`
