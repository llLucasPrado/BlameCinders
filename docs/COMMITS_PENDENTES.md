# Pendências e próximos lotes

Este arquivo registra as alterações consolidadas no lote e o que ainda deve ser
desenvolvido ou validado manualmente.

## Alterações locais atuais

- remover do Git a pasta `bin/`, que contém classes e recursos gerados;
- manter `bin/` ignorada pelo Git;
- manter `.vscode/` ignorada, pois suas configurações contêm caminhos locais;
- substituir os autosaves durante ações por salvamento manual no pause;
- adicionar o submenu `Salvar / Sair`, com `Salvar` e `Salvar e sair`;
- adicionar o indicador animado de livro e pena após um save bem-sucedido;
- compilação e suíte completa validadas: 107 testes, sem falhas.

Sugestão de commit após a validação:

`feat(save): adiciona salvamento manual pelo menu de pause`

## Melhorias técnicas deste lote

- save manual transferido para arquivo JSON com escrita temporária e backup;
- recuperação pelo backup também restaura o arquivo principal;
- migração automática do save antigo de `Preferences`;
- formato de save atualizado para preservar a categoria dos inimigos;
- saves antigos com herói cercado ou chama isolada são normalizados ao carregar;
- popup de áudio não pode mais ser empilhado e fecha antes do pause ao pressionar
  `Esc`;
- tela de opções fica bloqueada enquanto o popup de áudio está aberto;
- texturas são preparadas antes da troca de face para evitar travada no meio do
  flip;
- nova chama continua sendo gerada mesmo quando todas as cartas foram reveladas;
- chama inalcançável por uma barreira de paredes é reposicionada;
- teste instável de troca furtiva deixou de depender da posição aleatória da
  chama;
- simulação determinística cobre 2.000 tabuleiros iniciais;
- suíte completa validada: 107 testes, sem falhas.

Sugestões de commits separados após a validação:

`feat(save): persiste partida em JSON com backup e migração`

`fix(interface): corrige modal de áudio e travada durante o flip`

`fix(tabuleiro): garante chama alcançável até o fim da fase`

## Novas criaturas

- adicionadas artes limpas e verticais para Corvo da Vigília, Rato Couraçado e
  Espírito Acorrentado;
- refeitas também as dez artes antigas de esqueletos, lobos, espíritos, zumbis e
  ratos, agora sem títulos, vida, números, ícones ou molduras;
- Lobo Faminto redesenhado como uma criatura quadrúpede, magra e realista, sem
  efeitos mágicos, para comunicar melhor sua categoria de inimigo fraco;
- arquivos antigos `frenteTeste*.jpg` removidos e substituídos por PNGs com nomes
  semânticos, como `esqueleto-sentinela.png` e `lobo-faminto.png`;
- removidos títulos, números, indicadores de vida, ícones e molduras das imagens;
- assets armazenados em `assets/Cartas/Frente/Inimigo`, sem dependência dos
  arquivos originais da Área de Trabalho;
- identificadores visuais semânticos adicionados ao carregador de texturas;
- catálogo ampliado de 10 para 13 criaturas;
- distribuição das categorias preservada em 40%, 30%, 20% e 10% por sorteio
  ponderado, independentemente da quantidade de artes em cada categoria;
- documentação de balanceamento atualizada;
- suíte completa validada: 107 testes, sem falhas.

Sugestão de commit após a validação:

`feat(inimigos): padroniza artes e preserva pesos de dificuldade`

## Revelação direta e artes das armas

- após confirmar a revelação de inimigo, baú ou chama, as opções da carta são
  exibidas imediatamente, sem fechar o zoom nem exigir um segundo clique;
- inimigos oferecem `Combater`, baús oferecem `Abrir baú` e chamas oferecem
  `Coletar` logo depois da confirmação;
- ao encontrar comida no baú, a ação foi simplificada para `Consumir`;
- claymore e espada curta receberam artes limpas, sem nome, durabilidade,
  coração, números ou moldura;
- a antiga arte de punhal foi substituída por uma espada curta coerente com o
  nome e com o equipamento inicial do jogo;
- identificador novo corrigido para `ARMA: ESPADA_CURTA`, mantendo `PUNHAL`
  somente como compatibilidade com saves antigos;
- JPGs antigos das armas removidos e caminhos atualizados para os novos PNGs.

Sugestão de commit após a validação:

`feat(interacao): abre ações após revelar e padroniza artes das armas`

## Sistema de saves em arquivos

O armazenamento principal já foi substituído por um arquivo JSON legível em
`%USERPROFILE%\BlameCinders\saves\slot-001.json`.

- save armazenado fora de `assets/` e do diretório de instalação;
- escrita feita primeiro em `slot-001.json.tmp`;
- backup anterior mantido em `slot-001.backup.json`;
- recuperação pelo backup quando o arquivo principal está corrompido;
- migração automática do save antigo armazenado em `Preferences`;
- categoria dos inimigos preservada na versão 3 do formato;
- saves corrompidos não são apagados pela consulta de existência;
- testes cobrem gravação, backup, remoção, migração e corrupção;

Próximas extensões:

- oferecer vários slots de salvamento;
- gravar metadados para a tela de seleção: nome, data, tempo jogado, vida,
  chamas coletadas e versão do formato;
- validar versão, campos obrigatórios e integridade ao carregar;
- impedir que um arquivo corrompido apague os outros slots;
- adicionar opções `Salvar jogo`, `Carregar jogo` e `Continuar` na interface;
- criar testes para listagem, seleção, exclusão e isolamento dos vários slots.

Formato sugerido: `slot-001.json`, `slot-002.json` e `autosave.json`. Apesar de
ser um arquivo de texto legível, a extensão `.json` deixa explícita a estrutura
dos dados e facilita validação e manutenção.

## Furtividade por arquétipo

- assassino possui a maior chance, com faixa inicial de 50% a 80%;
- arqueira possui a segunda maior chance, de 35% a 65%;
- cavaleiro possui chance baixa, de 5% a 30%;
- anão não pode selecionar nem executar furtividade;
- a dificuldade de cada inimigo continua reduzindo a chance dentro dessas faixas;
- a restrição do anão é validada pela interface, pelo controlador e pelo sistema;
- suíte completa validada: 107 testes, sem falhas.

Sugestão de commit após a validação:

`feat(furtividade): diferencia chance por arquétipo`

## Validação geral da partida

- executar toda a suíte automatizada;
- iniciar uma nova partida e carregar uma partida salva;
- conferir revelação e interação em dois cliques;
- conferir movimentação localizada da esteira e geração da nova carta;
- conferir paredes e impedir sobreposição de cartas;
- conferir coleta de três chamas e encerramento da fase;
- conferir baús, armas, comida, cura, combate, recuo e game over;
- conferir furtividade bem-sucedida e malsucedida;
- conferir pause, retorno ao menu, reinício e descarte de recursos.

## Balanceamento

- primeira base aplicada: assassino 32, arqueira 38, cavaleiro 50 e anão 60 de vida;
- inimigos separados em fraco, médio-baixo, médio-alto e médio-difícil;
- catálogo distribuído em 40%, 30%, 20% e 10%, com dano médio aproximado de 10,9;
- espada curta com 12 e claymore com 22 de durabilidade;
- alimentos curam 8, 14 ou 20 pontos;
- baús têm 18% de chance e limite de 3; paredes têm 15% e limite de 4;
- baús entregam 45% de armas e 55% de alimentos;
- regras e metas de validação registradas em `docs/BALANCEAMENTO.md`;
- suíte completa validada: 107 testes, sem falhas;
- revisar dificuldade e probabilidade de furtividade após validar a vida dos inimigos;
- validar em partidas completas as faixas iniciais: assassino 50%–80%,
  arqueira 35%–65%, cavaleiro 5%–30% e anão sem furtividade;
- simular partidas completas para avaliar progressão e duração;
- confirmar a regra de falha furtiva: troca de posições e metade do dano direto
  na vida, ignorando arma ou proteção.

Sugestão de commit após a validação:

`feat(balanceamento): define progressão inicial de personagens e encontros`

## Opções e interface

- botão `Tentar novamente` ampliado e texto reajustado para permanecer dentro
  da moldura do painel de derrota;
- confirmação de saída sem salvar recebeu fundo de brasas escurecido, mensagem
  em duas linhas e botões alinhados lado a lado dentro do quadro;
- adicionar controles separados de volume de música e efeitos;
- adicionar seleção de resolução;
- adicionar alternância entre janela e tela cheia;
- criar tela de gerenciamento dos slots de save;
- substituir gradualmente os nomes provisórios e recursos temporários das
  cartas pela arte final.

## Qualidade e desempenho

- adicionar testes de integração para os fluxos Scene2D;
- medir FPS e memória no menu e durante uma partida completa;
- verificar descarte de texturas, fontes, sons, stages e listeners;
- revisar o comportamento em resoluções e proporções de tela diferentes;
- fazer uma última revisão de organização, nomes e responsabilidades depois de
  estabilizar as mecânicas.
