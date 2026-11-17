# Blame Cinders

Protótipo de jogo de tabuleiro em Java com libGDX. A aplicação possui tela
inicial, menu, opções de áudio, save local, partida, cartas, combate, baús,
chamas, animações e interface para desktop.

## Executar

Requisito: JDK 17. O Gradle é fornecido pelo wrapper.

No Windows:

```powershell
.\gradlew.bat run
```

Para validar o projeto:

```powershell
.\gradlew.bat clean test build
```

O launcher abre o jogo em tela cheia. Os `FitViewport` mantêm o canvas lógico
de 1280x720 e preservam a proporção das cartas.

## Estado atual

- o primeiro clique em uma carta adjacente apenas a revela;
- o segundo clique permite interagir com inimigo, chama, baú ou parede;
- o herói não atravessa paredes;
- a esteira move somente o segmento necessário para preencher o espaço vazio;
- baús podem conter armas ou comida;
- três chamas encerram a fase;
- o menu de pause congela tabuleiro, animações e timers da partida;
- a partida pode ser salva manualmente pelo menu de pause e retomada por
  `Continuar`;
- o save é um JSON legível em `%USERPROFILE%\BlameCinders\saves\slot-001.json`;
- a gravação usa arquivo temporário e mantém `slot-001.backup.json` para
  recuperação;
- `Salvar / Sair` oferece as opções de apenas salvar ou salvar e retornar ao
  menu principal;
- um indicador animado de pena e livro confirma visualmente o salvamento;
- música e efeitos podem ser ligados ou desligados no menu e no pause;
- a furtividade sempre troca o herói e o inimigo de posição, sem acionar a
  esteira; no sucesso não há dano e, na falha, metade do dano vai direto à vida;
- assassino possui a maior chance de furtividade, seguido pela arqueira;
  cavaleiro possui chance baixa e o anão não pode escolher furtividade;
- texturas de cartas são preparadas antes do flip e possuem fallback procedural
  identificado por texto.

O mapeamento técnico está em [`docs/ARQUITETURA.md`](docs/ARQUITETURA.md), e as
sugestões de commits ficam em
[`docs/COMMITS_PENDENTES.md`](docs/COMMITS_PENDENTES.md).
