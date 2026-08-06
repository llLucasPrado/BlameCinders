# Plano incremental de refatoração

## Lote 1 — fundação e regras do grid

- testes de combate e tabuleiro;
- aleatoriedade injetável;
- célula do herói sempre vazia;
- esteira parcial;
- objetivo explícito de três chamas.

## Lote 2 — visual sem assets de cartas

- tema procedural reutilizável;
- cartas e zoom identificados por texto;
- fonte Cinzel Decorative com fallback;
- remoção de caminhos de imagem do domínio.

## Lote 3 — itens e combate

- tipo comum para itens de baú;
- comida e cura com limite de vida;
- furtividade com regra explícita e testável;
- tabela inicial de balanceamento.

## Lote 4 — separação de responsabilidades

- extrair estado e regras de partida de `BlameCindersGame`;
- mover tipos para os pacotes de domínio;
- impedir dependências do domínio para libGDX;
- adaptar UI e animações aos eventos da aplicação.

## Critério para o commit final

- `clean test build` aprovado;
- aplicação inicia sem imagens externas;
- movimento não deixa carta sob o herói;
- esteira parcial visual e lógica sincronizadas;
- paredes, chamas, baús, comida e combate cobertos por testes.
