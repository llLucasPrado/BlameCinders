# Plano incremental de refatoração

## Concluído

- [x] configurar JDK 17, Gradle Wrapper e dependências libGDX;
- [x] adotar o layout `src/main` e `src/test`;
- [x] normalizar pacotes e nomes de classes;
- [x] manter a célula do herói vazia;
- [x] bloquear paredes e limitar a esteira ao segmento correto;
- [x] implementar objetivo de três chamas;
- [x] criar cartas textuais procedurais e fonte dark fantasy;
- [x] adicionar comida, cura, armas e furtividade;
- [x] extrair estado da partida e controlador de turno;
- [x] sincronizar identidade dos atores com a esteira;
- [x] agrupar fundo e rótulo em um único ator visual;
- [x] substituir callbacks de vitória/derrota por resultados explícitos;
- [x] separar resolução de baú, chama e inimigo da montagem visual;
- [x] preservar corretamente os resultados e mensagens de furtividade;
- [x] separar o primeiro clique de revelação do segundo clique de interação;
- [x] desacoplar as cartas visuais da aplicação;
- [x] extrair criação de tema/fonte de `BlameCindersGame`;
- [x] criar uma tela própria para o tabuleiro;
- [x] testar o cálculo de layout do grid;
- [x] criar testes Scene2D para revelar, agir e mover;
- [x] concluir a esteira antes de apresentar a vitória da terceira chama;
- [x] cobrir regras, layout e apresentação com 41 testes.

## Próximos lotes

### Balanceamento

- centralizar números em uma configuração de balanceamento;
- simular partidas com sementes determinísticas;
- ajustar vida, dano, durabilidade, cura e furtividade com dados de partidas.

## Critério da próxima entrega de balanceamento

- números de vida, dano, durabilidade, cura e furtividade centralizados;
- simulações reproduzíveis por semente;
- relatório simples dos resultados das simulações;
- `clean test build` e smoke test desktop aprovados.
