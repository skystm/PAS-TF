# Decisões Arquiteturais — Sistema de Gestão de Planos de Voo

## 1. Estilo Arquitetural: Arquitetura em Camadas

**Decisão:** Organização em três camadas — Controller, Service e Repository.

**Alternativas consideradas:**
- Arquitetura hexagonal (ports & adapters)
- CQRS (Command Query Responsibility Segregation)

**Justificativa:** A arquitetura em camadas é adequada ao escopo do sistema: as regras de negócio são centralizadas na camada de serviço, os controladores tratam apenas de entrada/saída HTTP, e os repositórios isolam o acesso ao banco. O sistema não possui alta complexidade de consultas que justificaria CQRS, e a arquitetura hexagonal adicionaria indireções desnecessárias para um projeto com um único adaptador de persistência.

---

## 2. Linguagem e Framework: Java 21 + Spring Boot 3.3

**Decisão:** Backend implementado em Java 21 com Spring Boot 3.3.

**Justificativa:** Requisito do enunciado. O Spring Boot reduz configuração boilerplate, fornece injeção de dependência, gerenciamento de transações e integração com JPA/Liquibase de forma transparente. Java 21 é a versão LTS mais recente, com suporte garantido.

---

## 3. Persistência: Spring Data JPA + PostgreSQL

**Decisão:** Mapeamento objeto-relacional via Hibernate/JPA com banco PostgreSQL.

**Alternativas consideradas:**
- JDBC puro
- MongoDB (NoSQL)

**Justificativa:** O domínio é fortemente relacional — planos referenciam aeronaves, pilotos e aerovias; reservas referenciam planos e slots de tempo. JPA permite mapear esses relacionamentos naturalmente. PostgreSQL é robusto, suporta transações ACID essenciais para a aprovação atômica de planos, e é amplamente utilizado em produção.

---

## 4. Herança de Aeronaves: Single Table Inheritance

**Decisão:** As três categorias de aeronave (Particular, Passageiros, Carga) são mapeadas em uma única tabela com discriminador `tipo`.

**Alternativas consideradas:**
- Table Per Class (uma tabela por subclasse)
- Joined Table (tabela base + tabelas de extensão)

**Justificativa:** O número de campos específicos por categoria é pequeno (1-2 colunas). Single Table evita JOINs na consulta de aeronaves e simplifica o mapeamento. O polimorfismo comportamental (regras de altitude e horário) é implementado via métodos abstratos em `Aeronave`, permitindo adicionar novas categorias sem alterar a lógica de validação existente.

---

## 5. Controle de Disponibilidade: Slots de Uma Hora

**Decisão:** A ocupação de aerovias e vagas é controlada em slots de uma hora alinhados à hora cheia, conforme especificado no enunciado.

**Justificativa:** O modelo de slots simplifica as consultas de conflito — basta verificar se dois registros compartilham o mesmo slot (aerovia + altitude + hora). A granularidade de uma hora é suficiente para o domínio de aviação civil. Qualquer utilização parcial ocupa o slot inteiro, o que é conservador e garante segurança operacional.

---

## 6. Aprovação Atômica: Transação Única

**Decisão:** A aprovação de um plano verifica e reserva todos os recursos em uma única transação `@Transactional`. Em caso de qualquer falha, toda a transação é revertida.

**Justificativa:** O enunciado exige que a aprovação seja integral — ou todos os recursos são reservados ou nenhum. A anotação `@Transactional` do Spring garante esse comportamento automaticamente com o suporte ACID do PostgreSQL. Não há necessidade de lógica de compensação manual.

---

## 7. Migrações de Banco: Liquibase

**Decisão:** Schema e dados iniciais gerenciados via Liquibase com changelogs XML.

**Alternativas consideradas:**
- Flyway
- `spring.jpa.hibernate.ddl-auto=create`

**Justificativa:** Liquibase permite reproduzir o estado exato do banco a partir do zero, o que é requisito do enunciado ("a carga inicial deverá ser reproduzível a partir dos artefatos entregues"). O `ddl-auto=create` não é adequado para produção pois destrói dados a cada reinicialização. Liquibase foi preferido ao Flyway por ter suporte nativo a XML, facilitando a inserção de dados iniciais complexos.

---

## 8. Documentação da API: OpenAPI / Swagger

**Decisão:** API documentada automaticamente via SpringDoc OpenAPI (Swagger UI em `/swagger-ui.html`).

**Justificativa:** O enunciado exige documentação da API com operações, parâmetros, requisições e respostas. O SpringDoc gera essa documentação automaticamente a partir das anotações do código, eliminando duplicação e garantindo que a documentação esteja sempre sincronizada com a implementação.

---

## 9. Evolução do Sistema

O sistema foi projetado para acomodar as evoluções previstas no enunciado:

- **Novos aeroportos e aerovias:** cadastro via API REST, sem alteração de código.
- **Novas categorias de aeronave:** basta criar nova subclasse de `Aeronave` e implementar `altitudePermitida()` e `horarioPermitido()`. Nenhuma outra classe precisa mudar.
- **Novas regras de operação por categoria:** encapsuladas nos métodos abstratos de cada subclasse.
- **Nova malha aérea:** inserção via API, persistida no banco.
