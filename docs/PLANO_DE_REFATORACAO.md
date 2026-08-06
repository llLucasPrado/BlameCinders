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
- [x] cobrir as regras centrais com 32 testes.

## Próximos lotes

### Apresentação

- extrair criação de tema/fonte de `BlameCindersGame`;
- criar uma tela própria para o tabuleiro;
- criar testes de integração Scene2D para revelar, agir e mover;
- revisar o término da animação ao coletar a terceira chama.

### Balanceamento

- centralizar números em uma configuração de balanceamento;
- simular partidas com sementes determinísticas;
- ajustar vida, dano, durabilidade, cura e furtividade com dados de partidas.

## Critério da próxima entrega

- `clean test build` aprovado;
- aplicação desktop inicia sem exceções;
- nenhuma carta fica sob o herói;
- ator visual e carta lógica permanecem sincronizados após a esteira;
- paredes, chamas, baús, comida, combate e furtividade continuam cobertos.
