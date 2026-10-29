# Roteiro de Demonstração — Sistema de Gestão de Planos de Voo

**Data de referência:** 2026-11-15  
**URL base:** http://localhost:8080  
**Interface:** http://localhost:8080/swagger-ui.html

---

## Bloco 1 — Dados iniciais (~2 min)

### 1.1 Listar aeroportos
```
GET /api/aeroportos
```
Resultado esperado: POA, FLN, CWB, GRU.

### 1.2 Listar aeronaves
```
GET /api/aeronaves
```
Resultado esperado: PR-ABC (PARTICULAR), PP-GLA e PR-XYZ (PASSAGEIROS), PT-CRG (CARGA).

### 1.3 Listar planos aprovados
```
GET /api/planos?situacao=APROVADO
```
Resultado esperado: 7 planos aprovados. Plano 7 com partida em 2026-12-01 (será cancelado depois).

---

## Bloco 2 — Verificar disponibilidade (~1 min)

### 2.1 Altitude ocupada em R1 às 08h (plano 1 já usa altitude 28000)
```
GET /api/aerovias/R1/altitudes-disponiveis?inicio=2026-11-15T08:00:00&fim=2026-11-15T09:00:00
```
Resultado esperado: **28000 não aparece** na lista.

---

## Bloco 3 — Ciclo completo de aprovação (~4 min)

### 3.1 Criar plano
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
Resultado esperado: situacao=EM_EDICAO. **Anotar o ID retornado.**

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
Resultado esperado: trecho adicionado, horaSaida calculada automaticamente (~15:25).

### 3.3 Validar (sem reservar recursos)
```
GET /api/planos/{id}/validar
```
Resultado esperado: `{ "valido": true, "erros": [] }`

### 3.4 Confirmar que altitude ainda está livre antes de aprovar
```
GET /api/aerovias/R1/altitudes-disponiveis?inicio=2026-11-15T15:00:00&fim=2026-11-15T16:00:00
```
Resultado esperado: **29000 aparece** (validação não reserva nada).

### 3.5 Aprovar (reserva tudo atomicamente)
```
POST /api/planos/{id}/aprovar
```
Resultado esperado: situacao=APROVADO.

### 3.6 Confirmar que altitude foi reservada
```
GET /api/aerovias/R1/altitudes-disponiveis?inicio=2026-11-15T15:00:00&fim=2026-11-15T16:00:00
```
Resultado esperado: **29000 não aparece** mais.

---

## Bloco 4 — Conflito de altitude (~1 min)

Tentar criar plano na mesma aerovia R1, mesmo horário 08:00, mesma altitude 28000 do plano 1:
```
POST /api/planos
{ "pilotoId": 3, "aeronavePrefixo": "PP-GLA", "origemCodigo": "POA", "destinoCodigo": "FLN", "dataHoraPartida": "2026-11-15T08:00:00" }

POST /api/planos/{id}/trechos
{ "aeroviaId": "R1", "altitude": 28000, "horaEntrada": "2026-11-15T08:00:00", "tipoPassagem": "POUSO", "numeroVaga": 3, "tempoPermanencia": 60 }

POST /api/planos/{id}/aprovar
```
Resultado esperado: **422** — "Altitude 28000 na aerovia R1 não está disponível". Plano continua EM_EDICAO, nenhuma reserva parcial criada.

---

## Bloco 5 — Cancelamento (~1 min)

### 5.1 Cancelar plano futuro (plano 7, partida 2026-12-01)
```
POST /api/planos/7/cancelar
```
Resultado esperado: situacao=CANCELADO. Reservas liberadas.

### 5.2 Confirmar liberação
```
GET /api/aerovias/R2/altitudes-disponiveis?inicio=2026-12-01T10:00:00&fim=2026-12-01T11:00:00
```
Resultado esperado: altitude 30000 disponível (era do plano 7 cancelado).

### 5.3 Tentar cancelar plano cujo voo já ocorreu (plano 1, partida 2026-11-15)
```
POST /api/planos/1/cancelar
```
Resultado esperado: **422** — "Plano não pode ser cancelado: não está aprovado ou a partida já ocorreu".
