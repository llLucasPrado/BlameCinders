# Mapeamento técnico

## Regras confirmadas

- O tabuleiro possui 4 linhas e 5 colunas.
- O herói ocupa uma célula vazia; nunca existe outra carta sob ele.
- Apenas cartas ortogonalmente adjacentes podem ser reveladas.
- Paredes bloqueiam movimento.
- Uma carta revelada pode ser consultada e acionada em um segundo clique.
- Inimigos oferecem combate, furtividade ou recuo.
- O dano de combate consome primeiro a durabilidade da arma e depois a vida.
- Baús podem conter arma ou comida.
- Três chamas coletadas encerram a fase.
- Ao mover, somente o segmento entre a borda de entrada e a antiga posição do
  herói desliza para preencher o espaço. A célula nova do herói continua vazia.

## Estrutura encontrada

- `BlameCindersGame`: controlador monolítico; mistura ciclo de vida libGDX, estado da
  partida, regras, criação da UI e coordenação de animações.
- `Tabuleiro`: modelo do grid, geração aleatória, chamas, movimento e esteira.
- `Combate`: entidades e resolução de combate, mas também contém `CartaInfo`,
  que pertence ao domínio do tabuleiro.
- `Fluxos`: coordena popups, zoom e combate diretamente com atores Scene2D.
- `Animacoes`: conhece o array concreto de cartas e a implementação do grid.
- `UI`: HUD e popups.
- `Utils.GerenciadorTexturas`: carrega caminhos de imagens usados inclusive pelo
  domínio (`Arma` e `Inimigo`).

## Problemas estruturais

1. `CartaInfo` depende de `Tabuleiro.TipoCarta`, enquanto `Tabuleiro` depende de
   `CartaInfo`, criando dependência circular.
2. O herói começa sobre uma carta e o preenchimento global de `null` recria uma
   carta sob ele depois de cada movimento.
3. A esteira desloca a linha ou coluna inteira, inclusive cartas do outro lado
   do herói.
4. Texturas e nomes de arquivos fazem parte das entidades de domínio.
5. `BlameCindersGame` possui mais de 1.300 linhas e conhece praticamente todas as classes.
6. A aleatoriedade global impede testes determinísticos.
7. Não existem testes automatizados.
8. Texturas procedurais temporárias não têm um proprietário único para descarte.

## Estrutura alvo

```text
com.blamecinders
├── domain
│   ├── board       Carta, TipoCarta, Tabuleiro, Posicao, Direcao
│   ├── combat      Jogador, Inimigo, SistemaCombate, ResultadoCombate
│   └── item        Item, Arma, Comida, CatalogoItens
├── application     Partida, Turno, ResultadoAcao
├── presentation
│   └── gdx         tela, atores, HUD, popups, animações e tema visual
└── desktop          DesktopLauncher
```

A migração será incremental para manter o jogo compilando entre as etapas.

## Identidade visual temporária

As cartas serão desenhadas em código e identificadas por texto (`VERSO`,
`HERÓI`, `INIMIGO`, `CHAMA`, `PAREDE`, `BAÚ`, nomes de armas e comidas).

Fonte dark fantasy escolhida: **Cinzel Decorative**. Enquanto o arquivo não
estiver disponível, a aplicação deve usar a fonte padrão do libGDX como fallback.

## Balanceamento provisório

- Vida inicial/máxima do herói: 50.
- Armas atuais: 5 e 15 de durabilidade.
- Comidas: 8, 12 e 18 de cura.
- Baús: 55% de chance de arma e 45% de comida.
- Furtividade: `70% - dificuldade do inimigo`, limitada entre 25% e 80%.
- Falha de furtividade fica registrada na carta; o jogador deve lutar ou recuar.

Esses números são uma linha de base para testes de partida, não o balanceamento
final.
