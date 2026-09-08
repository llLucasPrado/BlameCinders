# Assets

As imagens das cartas ficam em `Cartas/`. O jogo procura uma imagem pelo
identificador visual da carta e, se ela não existir, gera um fundo procedural
com o nome da carta visível.

As imagens de origem podem ter resolução alta. Em execução, elas são reduzidas
para no máximo 600x800 antes da criação da textura e dos mipmaps. A frente de
uma carta fechada só é carregada quando ela for revelada.

## Fonte

Fonte dark fantasy escolhida: **Cinzel Decorative Bold**.

- arquivo esperado: `Fonts/CinzelDecorative-Bold.ttf`;
- licença: SIL Open Font License 1.1 em `Fonts/OFL.txt`;
- fallback: fonte bitmap interna do libGDX.

A geração da fonte inclui explicitamente os caracteres acentuados usados nos
textos em português.

As telas iniciais, menus, HUD, cartas e pop-ups usam o mesmo tema compartilhado.
