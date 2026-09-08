# Mapeamento técnico

## Regras confirmadas

- o tabuleiro possui 4 linhas e 5 colunas;
- o herói ocupa uma célula vazia e nunca fica sobre outra carta;
- apenas cartas ortogonalmente adjacentes podem ser reveladas;
- o primeiro clique revela e o segundo permite interagir;
- paredes bloqueiam movimento;
- inimigos oferecem combate, furtividade ou recuo;
- dano de combate consome primeiro a arma e depois a vida do herói;
- furtividade troca as posições do herói e do inimigo sem acionar a esteira;
- sucesso furtivo não causa dano; falha causa metade do dano diretamente na
  vida, ignorando arma e qualquer proteção;
- baús podem conter arma ou comida;
- três chamas coletadas encerram a fase;
- somente o segmento entre a borda de entrada e a posição anterior do herói
  desliza para preencher o espaço vazio.

## Estrutura

```text
com.blamecinders
├── BlameCindersGame    composição e coordenação dos fluxos da partida
├── aplicacao           estado, turnos, interações e encontros
├── tabuleiro           grid, cartas, tipos e revelação
├── combate             herói, inimigos, combate e furtividade
├── configuracao        valores centralizados de balanceamento
├── item                armas, comidas e geração de itens
├── fluxo               revelação, interação de cartas e combate
├── persistencia        conversão, validação e armazenamento do save
├── telas               tela inicial, menu, opções e ciclo de vida
├── audio               música e efeitos sonoros
├── ui                  tema, HUD, pop-ups e componentes visuais
│   └── tabuleiro       stage, layout, atores e remapeamento do grid
├── animacao            animações Scene2D
├── util                cache, fallback e carga de texturas
└── desktop             inicialização LWJGL3
```

O domínio (`aplicacao`, `tabuleiro`, `combate` e `item`) não depende do libGDX.
As telas de entrada são administradas por `GerenciadorTelas`. Durante uma
partida, `BlameCindersGame` compõe os controladores e delega as interações
visuais a `FluxoInteracaoCarta`. `ui.tabuleiro.TelaTabuleiro` é somente a
representação Scene2D do grid; não existe uma segunda classe de tabuleiro no
pacote `telas`.

## Recursos visuais

`GerenciadorTexturas` mantém um cache por identificador. Imagens de alta
resolução são reduzidas em memória para até 600x800 antes da criação da textura.
Cartas fechadas carregam apenas o verso; a frente é resolvida quando necessária.
Se o arquivo não existir, um fundo procedural com rótulo visível é usado.

Fonte dark fantasy registrada: **Cinzel Decorative Bold** em
`assets/Fonts/CinzelDecorative-Bold.ttf`.

## Estado técnico

- ciclo de telas encerra cada tela uma única vez;
- fechar a aplicação antes de iniciar uma partida é seguro;
- Enter executa somente uma opção do menu;
- `Continuar` só inicia quando existe um save local válido;
- música e efeitos possuem configuração persistente no menu e no pause;
- pause preserva a modal anterior e congela tabuleiro, zoom, animações e timers;
- reiniciar após voltar ao menu recria HUD e mensagens;
- revelação aguarda o flip terminar antes de abrir a informação;
- existe um único mapeamento entre o tipo lógico e o identificador visual da carta;
- modelos e serviços rejeitam dependências e estados nulos na entrada;
- regras numéricas estão reunidas em `BalanceamentoJogo`;
- consultas de existência de imagens e atores do HUD são reutilizadas;
- existem 67 testes automatizados no código-fonte.

## Pendências deliberadas

- simular o balanceamento de furtividade em partidas completas;
- adicionar resolução, modo de tela e controles de volume às opções;
- medir memória e FPS durante uma partida completa, além do menu;
- adicionar testes de integração para os fluxos Scene2D extraídos.
