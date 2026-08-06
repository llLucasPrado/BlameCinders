# Blame Cinders

Protótipo de jogo em Java com libGDX. O projeto atualmente possui a lógica de
tabuleiro, combate, cartas, animações e interface para desktop.

## Executar

Requisitos: JDK 17. O Gradle é fornecido pelo wrapper e não precisa ser instalado
separadamente.

No Windows:

```powershell
.\gradlew.bat run
```

Para apenas compilar:

```powershell
.\gradlew.bat build
```

## Estado atual

O código Java compila com libGDX 1.14.1. As cartas são provisoriamente desenhadas
em código e identificadas por texto, sem depender de imagens externas. A fonte e
seu fallback estão documentados em [`assets/README.md`](assets/README.md).

A furtividade possui uma primeira regra funcional e os baús podem gerar armas ou
comidas. As regras de tabuleiro, combate, cura e furtividade possuem testes em
`src/test/java`.

O mapeamento e as próximas extrações do controlador principal estão em
[`docs/ARQUITETURA.md`](docs/ARQUITETURA.md) e
[`docs/PLANO_DE_REFATORACAO.md`](docs/PLANO_DE_REFATORACAO.md).
