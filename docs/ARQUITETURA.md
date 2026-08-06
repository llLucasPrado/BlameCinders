# Mapeamento técnico

## Regras confirmadas

- O tabuleiro possui 4 linhas e 5 colunas.
- O herói ocupa uma célula vazia; nunca existe outra carta sob ele.
- Apenas cartas ortogonalmente adjacentes podem ser reveladas.
- Paredes bloqueiam movimento.
- Uma carta revelada pode ser consultada e acionada em um segundo clique.
- Inimigos oferecem combate, furtividade ou recuo.
- O dano consome primeiro a durabilidade da arma e depois a vida do herói.
- Baús podem conter arma ou comida.
- Três chamas coletadas encerram a fase.
- Ao mover, somente o segmento entre a borda de entrada e a antiga posição do
  herói desliza para preencher o espaço.

## Estrutura atual

```text
com.blamecinders
├── BlameCindersGame       ciclo de vida libGDX e composição da apresentação
├── aplicacao              EstadoPartida, ControladorTurno e comandos
├── tabuleiro              grid, cartas, tipos e estado de revelação
├── combate                herói, inimigos, combate e furtividade
├── item                   armas, comidas e geração de itens de baú
├── fluxo                  coordenação das telas de carta e combate
├── ui                     HUD, popups e atores visuais compostos
├── animacao               animações Scene2D
├── util                   texturas procedurais e cálculo de posições
└── desktop                DesktopLauncher
```

O código de domínio (`aplicacao`, `tabuleiro`, `combate` e `item`) não depende
do libGDX. A camada visual depende do domínio, nunca o contrário.

## Problemas já corrigidos

1. Projeto convertido para o layout Java/Gradle padrão e inicialização desktop.
2. Pacote provisório `com.root.game` e nomes `TCC_0_01`/`Cartas` removidos.
3. Herói mantido em uma única célula vazia, sem carta sobreposta.
4. Esteira limitada ao segmento que preenche a antiga célula do herói.
5. Referências dos atores remapeadas junto com a esteira, evitando teleporte.
6. Fundo e rótulo agrupados; texto acompanha flip, escala e movimento.
7. Cartas procedurais identificadas por texto, sem imagens externas obrigatórias.
8. Armas separadas de entidades de combate; caminhos viraram identificadores visuais.
9. Estado da partida e conclusão do turno extraídos da aplicação libGDX.
10. Regras centrais cobertas por 22 testes automatizados.

## Pendências conhecidas

- `BlameCindersGame` ainda concentra criação da UI e fluxos de encontros; caiu de
  mais de 1.300 para cerca de 1.100 linhas, mas deve ser dividida em telas e
  coordenadores menores.
- `FluxoCarta` e `FluxoCombate` ainda recebem callbacks numerosos; devem retornar
  resultados de encontro explícitos.
- O balanceamento atual é provisório e precisa de sessões de jogo/simulações.
- Ainda falta um teste automatizado de interação Scene2D; hoje o smoke test apenas
  confirma inicialização real da janela sem exceções.

## Identidade visual temporária

As cartas são desenhadas em código e identificadas por texto (`VERSO`,
`HERÓI-TESTE`, `INIMIGO`, `CHAMA`, `PAREDE`, `BAÚ`, armas e comidas).

Fonte dark fantasy: **Cinzel Decorative Bold**, armazenada em
`assets/Fonts/CinzelDecorative-Bold.ttf`, com a licença em `assets/Fonts/OFL.txt`.
O jogo mantém a fonte padrão do libGDX como fallback.

## Balanceamento provisório

- Vida inicial/máxima do herói: 50.
- Armas: 5 e 15 de durabilidade.
- Comidas: 8, 12 e 18 de cura.
- Baús: 55% de chance de arma e 45% de comida.
- Furtividade: `70% - dificuldade do inimigo`, limitada entre 25% e 80%.
- Falha de furtividade fica registrada na carta; o jogador deve lutar ou recuar.

Esses números são uma linha de base testável, não o balanceamento final.
