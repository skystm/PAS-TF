# Roteiro de Demonstração — Sistema de Gestão de Planos de Voo

**Data de referência:** 2026-11-15  
**URL base:** http://localhost:8080  
**Interface:** http://localhost:8080/swagger-ui.html

---

## Bloco 1 — Consultar dados iniciais

### 1.1 Listar aeroportos
```
GET /api/aeroportos
```
**Resultado esperado:** POA, FLN, CWB, GRU com suas vagas.

### 1.2 Listar aerovias
```
GET /api/aerovias
```
**Resultado esperado:** R1 a R8 com origens, destinos e distâncias.

### 1.3 Listar aeronaves
```
GET /api/aeronaves
```
**Resultado esperado:** PR-ABC (PARTICULAR), PP-GLA e PR-XYZ (PASSAGEIROS), PT-CRG (CARGA).

### 1.4 Listar planos aprovados
```
GET /api/planos?situacao=APROVADO
```
**Resultado esperado:** 7 planos aprovados, incluindo o plano 7 com partida em 2026-12-01.

---

## Bloco 2 — Consultar disponibilidade na data de referência

### 2.1 Altitude ocupada em R1 às 08h
```
GET /api/aerovias/R1/altitudes-disponiveis?inicio=2026-11-15T08:00:00&fim=2026-11-15T09:00:00
```
**Resultado esperado:** 28000 **não aparece** na lista (ocupado pelo plano 1). As demais altitudes estão livres.

### 2.2 Altitude livre em R1 fora do horário ocupado
```
GET /api/aerovias/R1/altitudes-disponiveis?inicio=2026-11-15T10:00:00&fim=2026-11-15T11:00:00
```
**Resultado esperado:** Todas as altitudes disponíveis, incluindo 28000.

### 2.3 Vagas ocupadas em FLN
```
GET /api/aeroportos/FLN/vagas-disponiveis?inicio=2026-11-15T08:00:00&fim=2026-11-15T10:00:00
```
**Resultado esperado:** Vaga 1 **não aparece** (ocupada pelo plano 1). Vagas 2 a 16 disponíveis.

---

## Bloco 3 — Ciclo completo de um plano válido

### 3.1 Criar plano em edição
```
POST /api/planos
{
  "pilotoId": 2,
  "aeronavePrefixo": "PP-GLA",
  "origemCodigo": "POA",
  "destinoCodigo": "FLN",
  "dataHoraPartida": "2026-11-15T15:00:00"
}
```
**Resultado esperado:** Plano criado com situacao=EM_EDICAO. Anotar o ID retornado (usar como {id} abaixo).

### 3.2 Adicionar trecho
```
POST /api/planos/{id}/trechos
{
  "aeroviaId": "R1",
  "altitude": 29000,
  "horaEntrada": "2026-11-15T15:00:00",
  "tipoPassagem": "POUSO",
  "numeroVaga": 2,
  "tempoPermanencia": 60
}
```
**Resultado esperado:** Trecho adicionado. horaSaida calculada automaticamente (~15:25).

### 3.3 Validar plano (sem reservar recursos)
```
GET /api/planos/{id}/validar
```
**Resultado esperado:** `{ "valido": true, "erros": [] }`

### 3.4 Aprovar plano (reserva recursos atomicamente)
```
POST /api/planos/{id}/aprovar
```
**Resultado esperado:** situacao=APROVADO. Reservas efetivadas.

### 3.5 Confirmar que validação não reservou recursos
> Demonstrar que entre os passos 3.3 e 3.4, consultar disponibilidade mostra os recursos ainda livres.
```
GET /api/aerovias/R1/altitudes-disponiveis?inicio=2026-11-15T15:00:00&fim=2026-11-15T16:00:00
```
Antes da aprovação: 29000 aparece disponível.  
Após a aprovação: 29000 não aparece.

---

## Bloco 4 — Falhas de validação

### 4.1 Percurso inconsistente (aerovia errada)
```
POST /api/planos
{ "pilotoId": 1, "aeronavePrefixo": "PP-GLA", "origemCodigo": "POA", "destinoCodigo": "GRU", "dataHoraPartida": "2026-11-15T16:00:00" }

POST /api/planos/{id}/trechos
{ "aeroviaId": "R4", "altitude": 28000, "horaEntrada": "2026-11-15T16:00:00", "tipoPassagem": "POUSO", "numeroVaga": 1, "tempoPermanencia": 60 }

GET /api/planos/{id}/validar
```
**Resultado esperado:** erro "Origem da primeira aerovia deve ser igual à origem do plano" (R4 começa em FLN, não POA).

### 4.2 Altitude proibida para aeronave particular
```
POST /api/planos
{ "pilotoId": 1, "aeronavePrefixo": "PR-ABC", "origemCodigo": "POA", "destinoCodigo": "FLN", "dataHoraPartida": "2026-11-15T16:00:00" }

POST /api/planos/{id}/trechos
{ "aeroviaId": "R1", "altitude": 30000, "horaEntrada": "2026-11-15T16:00:00", "tipoPassagem": "POUSO", "numeroVaga": 3, "tempoPermanencia": 60 }

GET /api/planos/{id}/validar
```
**Resultado esperado:** erro de altitude — aeronave particular só pode usar 25000, 26000 ou 27000.

### 4.3 Conflito de altitude (aprovação rejeitada)
Criar plano na mesma aerovia R1, mesmo horário 08:00, mesma altitude 28000 do plano 1:
```
POST /api/planos
{ "pilotoId": 3, "aeronavePrefixo": "PP-GLA", "origemCodigo": "POA", "destinoCodigo": "FLN", "dataHoraPartida": "2026-11-15T08:00:00" }

POST /api/planos/{id}/trechos
{ "aeroviaId": "R1", "altitude": 28000, "horaEntrada": "2026-11-15T08:00:00", "tipoPassagem": "POUSO", "numeroVaga": 3, "tempoPermanencia": 60 }

POST /api/planos/{id}/aprovar
```
**Resultado esperado:** 422 — "Altitude 28000 na aerovia R1 não está disponível". Plano continua EM_EDICAO, nenhuma reserva parcial criada.

### 4.4 Autonomia insuficiente
Usar aeronave PR-ABC (autonomia 2000km, efetiva 1700km) em rota POA→GRU (865km) + GRU→POA (865km) = 1730km sem reabastecimento:
```
POST /api/planos
{ "pilotoId": 1, "aeronavePrefixo": "PR-ABC", "origemCodigo": "POA", "destinoCodigo": "POA", "dataHoraPartida": "2026-11-15T07:00:00" }
```
Adicionar trecho R2 (sobrevoo) + trecho R3 (pouso). Validar.
**Resultado esperado:** erro de autonomia insuficiente.

---

## Bloco 5 — Abandono e cancelamento

### 5.1 Abandonar plano em edição
```
POST /api/planos/{id_em_edicao}/abandonar
```
**Resultado esperado:** situacao=ABANDONADO.

### 5.2 Tentar modificar plano abandonado
```
POST /api/planos/{id_abandonado}/trechos
{ ... }
```
**Resultado esperado:** 422 — "Plano não está em edição".

### 5.3 Cancelar plano aprovado futuro (plano 7, partida 2026-12-01)
```
POST /api/planos/7/cancelar
```
**Resultado esperado:** situacao=CANCELADO. Reservas liberadas.

### 5.4 Confirmar que reservas foram liberadas
```
GET /api/aerovias/R2/altitudes-disponiveis?inicio=2026-12-01T10:00:00&fim=2026-12-01T11:00:00
```
**Resultado esperado:** altitude 30000 disponível (era do plano 7 cancelado).

### 5.5 Tentar cancelar plano cujo voo já ocorreu (plano 1, partida 2026-11-15)
```
POST /api/planos/1/cancelar
```
**Resultado esperado:** 422 — "Plano não pode ser cancelado: não está aprovado ou a partida já ocorreu".

### 5.6 Consultar planos cancelados e abandonados (ainda disponíveis)
```
GET /api/planos?situacao=CANCELADO
GET /api/planos?situacao=ABANDONADO
```
**Resultado esperado:** planos aparecem normalmente para consulta.

---

## Bloco 6 — Aeronave de carga (restrição de horário)

### 6.1 Carga fora do horário permitido
```
POST /api/planos
{ "pilotoId": 4, "aeronavePrefixo": "PT-CRG", "origemCodigo": "POA", "destinoCodigo": "FLN", "dataHoraPartida": "2026-11-15T08:00:00" }

POST /api/planos/{id}/trechos
{ "aeroviaId": "R1", "altitude": 28000, "horaEntrada": "2026-11-15T08:00:00", "tipoPassagem": "POUSO", "numeroVaga": 5, "tempoPermanencia": 60 }

GET /api/planos/{id}/validar
```
**Resultado esperado:** erro — aeronave de carga só pode operar entre 00:00 e 05:59.
