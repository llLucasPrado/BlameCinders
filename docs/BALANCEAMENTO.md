# Balanceamento inicial

Este documento registra a primeira base numérica da fase do cemitério. Os valores
foram escolhidos considerando a regra atual: a vida do inimigo representa o dano
total do encontro, a arma absorve esse dano antes da vida do herói e o inimigo é
derrotado se o herói sobreviver.

Os números são uma base controlada para testes, não valores finais. Toda alteração
deve ser validada em partidas completas antes de aumentar a quantidade de armas,
comidas ou categorias.

## Personagens

| Arquétipo | Vida | Papel no equilíbrio |
| --- | ---: | --- |
| Assassino | 32 | Menor resistência, compensada pela melhor furtividade |
| Arqueira | 38 | Resistência baixa, com furtividade intermediária |
| Cavaleiro | 50 | Opção equilibrada e mais tolerante a combate direto |
| Anão | 60 | Maior resistência, compensando a ausência de furtividade |

Nenhum inimigo da primeira fase deve derrotar um personagem com a vida cheia em
um único encontro. O inimigo mais forte causa 20 de dano, antes da absorção da
arma.

## Categorias de inimigos

| Categoria | Vida/dano | Modelos no catálogo | Frequência aproximada |
| --- | ---: | ---: | ---: |
| Fraco | 5–8 | 5 | 40% |
| Médio-baixo | 9–12 | 3 | 30% |
| Médio-alto | 13–16 | 3 | 20% |
| Médio-difícil | 17–20 | 2 | 10% |

O catálogo possui treze modelos. Primeiro é sorteada a categoria com os pesos da
tabela e depois um modelo daquela categoria, portanto novas artes não alteram a
frequência de dificuldade. A média atual é aproximadamente 10,9 de dano por
encontro. As categorias também definem a recompensa de cinzas, evitando regras
dependentes do nome do inimigo.

Distribuição atual:

- fracos: 5, 6, 7, 8 e 8 de vida;
- médio-baixos: 10, 11 e 12 de vida;
- médio-altos: 14, 15 e 16 de vida;
- médio-difíceis: 18 e 20 de vida.

## Furtividade por categoria

Com a dificuldade atual de cada inimigo, as chances resultantes ficam nestas
faixas:

| Arquétipo | Fraco | Médio-baixo | Médio-alto | Médio-difícil |
| --- | ---: | ---: | ---: | ---: |
| Assassino | 72%–75% | 68%–70% | 64%–66% | 60%–62% |
| Arqueira | 57%–60% | 53%–55% | 49%–51% | 45%–47% |
| Cavaleiro | 22%–25% | 18%–20% | 14%–16% | 10%–12% |
| Anão | indisponível | indisponível | indisponível | indisponível |

Uma falha causa metade da vida do inimigo como dano direto, arredondada para
cima, e ignora a arma equipada. Independentemente do resultado, herói e inimigo
trocam de posição.

## Armas e alimentos

| Recurso | Durabilidade ou cura | Objetivo |
| --- | ---: | --- |
| Espada curta | 12 | Absorver aproximadamente um encontro médio |
| Claymore | 22 | Absorver dois encontros fracos ou um encontro forte |
| Ração de viagem | 8 | Recuperação pequena |
| Sopa de cinzas | 14 | Recuperação média |
| Frasco rubro | 20 | Recuperação alta |

Durabilidade é a "vida" da arma. Ela funciona como uma proteção consumível no
sistema atual; não aumenta o dano causado pelo herói. Um baú tem 45% de chance de
conter arma e 55% de chance de conter comida.

## Tabuleiro

Para cada nova carta gerada:

- baú: 18%, limitado a 3 simultâneos;
- parede: 15%, limitada a 4 simultâneas;
- inimigo: 67%, além das tentativas que excederem os limites anteriores;
- chama: controlada separadamente, mantendo uma chama ativa até o objetivo de 3.

O limite de paredes reduz a chance de corredores fechados e o limite de baús
impede acúmulo excessivo de cura e durabilidade. O tabuleiro continua garantindo
uma saída jogável caso a posição do herói fique bloqueada. A chama também é
reposicionada quando uma barreira a deixa em uma região inalcançável.

## Metas para os testes jogando

Durante a validação manual, registrar por personagem:

- quantidade de encontros enfrentados e evitados;
- vida restante ao encontrar cada chama;
- quantidade e tipo de itens encontrados;
- armas quebradas e comida desperdiçada por estar perto da vida máxima;
- mortes antes da primeira, segunda e terceira chama;
- duração total da fase.

Como referência inicial, uma partida vencida deve normalmente oferecer de 1 a 3
baús, exigir decisões de rota e permitir chegar à terceira chama sem depender de
um item específico. Se uma categoria morrer ou vencer quase sempre sem decisões,
o ajuste deve ser pequeno, normalmente de 2 a 4 pontos por vez.
