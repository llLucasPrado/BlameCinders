# Evidências de validação

## Execução automatizada

- Identificação: `VALIDACAO-20261116-2158-PRECOMMIT`
- Data e hora do relatório: 16/11/2026, 21:58:15 (UTC-03:00)
- Comando: `.\gradlew.bat test --no-daemon`
- Resultado: 107 testes executados, 107 aprovados, 0 falhas e 0 erros
- Classes/suítes de teste: 25
- Tempo acumulado informado nos arquivos XML do JUnit: 0,416 s
- Resultado do Gradle: `BUILD SUCCESSFUL in 12s`
- Relatório HTML: `build/reports/tests/test/index.html`
- SHA-256 do relatório HTML: `E8D23EBFF4DD3CF101995EB30420A67D7D01CDBEF4084F05F623AB71E4D2C475`

Ambiente da execução:

- Windows 11, arquitetura AMD64
- Eclipse Temurin OpenJDK 17.0.20+8
- Gradle 8.14.5
- JUnit Jupiter 5.13.4

A execução foi realizada na branch `main`, sobre o commit-base
`b0d3441d6600ea38c0f12f35f8b1ac763bd7ea8e`, imediatamente antes da
consolidação das alterações. O estado validado corresponde ao commit que inclui
este registro.

## Simulação da geração de tabuleiros

O teste
`TabuleiroTest.distribuicaoMediaDoTabuleiroPermaneceDentroDaFaixaPlanejada`
gerou 2.000 tabuleiros de forma determinística, usando as sementes inteiras de
0 a 1.999. Cada tabuleiro possui uma grade de 4 por 5 posições.

| Elemento | Total nas 2.000 gerações | Média por tabuleiro | Faixa aceita pelo teste |
| --- | ---: | ---: | ---: |
| Baús | 4.546 | 2,2730 | 1,5 a 2,8 |
| Paredes | 4.901 | 2,4505 | 1,7 a 3,2 |
| Inimigos | 26.553 | 13,2765 | 12,0 a 15,0 |
| Chamas iniciais | 2.000 | 1,0000 | exatamente 1 |

O teste específico foi concluído em 0,103 s no ambiente descrito acima. Esse
tempo representa apenas essa execução local e não deve ser apresentado como
benchmark geral de desempenho.

## Commits relacionados às correções

| Commit | Alteração principal |
| --- | --- |
| `e74f603` | Reorganização do projeto e correções de movimentação, esteira, paredes, sobreposição, objetivo de chamas, itens, HUD e testes. |
| `e9f4e8f` | Correção da ordem dos pop-ups de cartas de inimigo. |
| `b13e5fd` | Correção da ordem dos pop-ups de chama e baú. |
| `ed4a4e6` | Aplicação da nova lógica de furtividade. |
| `311dea4` | Polimento da animação de furtividade. |
| `ff03b1f` | Correção do funcionamento do menu de pausa. |
| `b3fb00b` | Implementação do salvamento manual no menu de pausa. |
| `f326a43` | Nova correção da ordem dos pop-ups. |

## Limitações do registro

- Não foi configurada nem apurada uma métrica formal de cobertura de código.
- O tempo total do Gradle inclui compilação, inicialização e geração do relatório; ele não equivale ao tempo de execução isolado dos testes.
- Os testes manuais e a experiência externa foram informais e não possuem medição estatística de cobertura, duração ou satisfação.
