package com.blamecinders.tabuleiro;

import com.blamecinders.combate.CatalogoInimigos;
import com.blamecinders.combate.Inimigo;
import com.blamecinders.configuracao.BalanceamentoJogo;
import com.blamecinders.item.CatalogoItens;
import com.blamecinders.item.ItemBau;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Random;

/**
 * Grade lógica da partida. A célula do herói permanece {@code null} e é lida
 * como {@link TipoCarta#VAZIO}; a esteira preserva as cartas existentes e cria
 * apenas a carta que entra pela borda.
 */
public class Tabuleiro {

    public static final int LINHAS = 4;
    public static final int COLUNAS = 5;
    public static final int OBJETIVO_CHAMAS = BalanceamentoJogo.OBJETIVO_CHAMAS;

    private final CartaInfo[][] grid;
    private final Random random;

    private int jogadorLinha = 0;
    private int jogadorColuna = 0;
    private int chamasColetadas = 0;

    public Tabuleiro() {
        this(new Random());
    }

    Tabuleiro(Random random) {
        this.random = Objects.requireNonNull(random, "random");
        grid = new CartaInfo[LINHAS][COLUNAS];
        inicializar();
    }

    Tabuleiro(CartaInfo[][] grid, int jogadorLinha, int jogadorColuna, Random random) {
        this(grid, jogadorLinha, jogadorColuna, 0, random);
    }

    private Tabuleiro(
        CartaInfo[][] grid,
        int jogadorLinha,
        int jogadorColuna,
        int chamasColetadas,
        Random random
    ) {
        if (grid == null || grid.length != LINHAS) {
            throw new IllegalArgumentException("Grid deve possuir " + LINHAS + " linhas.");
        }
        for (CartaInfo[] linha : grid) {
            if (linha == null || linha.length != COLUNAS) {
                throw new IllegalArgumentException("Grid deve possuir " + COLUNAS + " colunas.");
            }
        }
        if (!estaDentro(jogadorLinha, jogadorColuna)) {
            throw new IllegalArgumentException("Posição inicial do jogador fora do tabuleiro.");
        }
        if (chamasColetadas < 0 || chamasColetadas >= OBJETIVO_CHAMAS) {
            throw new IllegalArgumentException("Quantidade de chamas inválida.");
        }

        this.grid = grid;
        this.jogadorLinha = jogadorLinha;
        this.jogadorColuna = jogadorColuna;
        this.chamasColetadas = chamasColetadas;
        this.random = Objects.requireNonNull(random, "random");
        this.grid[jogadorLinha][jogadorColuna] = null;
    }

    public static Tabuleiro restaurar(
        CartaInfo[][] grid,
        int jogadorLinha,
        int jogadorColuna,
        int chamasColetadas
    ) {
        Tabuleiro tabuleiro = new Tabuleiro(
            grid,
            jogadorLinha,
            jogadorColuna,
            chamasColetadas,
            new Random()
        );
        if (!tabuleiro.existeMovimentoValido()) tabuleiro.gerarSaidaEmergencial();
        if (chamasColetadas < OBJETIVO_CHAMAS) tabuleiro.garantirUmaChama();
        return tabuleiro;
    }

    private void inicializar() {
        grid[0][0] = null;

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {

                if (i == 0 && j == 0) continue;

                if (i == 0 && j == 1) {
                    grid[i][j] = criarCarta(TipoCarta.INIMIGO);
                    continue;
                }

                if (i == 1 && j == 0) {
                    grid[i][j] = criarCarta(TipoCarta.INIMIGO);
                    continue;
                }

                grid[i][j] = criarCarta(gerarTipoCartaAleatoria());
            }
        }

        garantirUmaChama();
    }

    private CartaInfo criarCarta(TipoCarta tipo) {
        CartaInfo carta = new CartaInfo(tipo);

        if (tipo == TipoCarta.INIMIGO) {
            carta.setInimigo(gerarInimigoAleatorio());
        }

        if (tipo == TipoCarta.BAU) {
            carta.setItemDentro(gerarItemAleatorio());
        }

        return carta;
    }

    private Inimigo gerarInimigoAleatorio() {
        return CatalogoInimigos.gerarInimigoAleatorio(random);
    }

    private ItemBau gerarItemAleatorio() {
        return CatalogoItens.gerarItemAleatorio(random);
    }

    private TipoCarta gerarTipoCartaAleatoria() {

        int r = random.nextInt(BalanceamentoJogo.TOTAL_PERCENTUAL);

        if (r < BalanceamentoJogo.CHANCE_BAU_TABULEIRO) {
            return contarBausNoTabuleiro() < BalanceamentoJogo.LIMITE_BAUS_TABULEIRO
                ? TipoCarta.BAU
                : TipoCarta.INIMIGO;
        }

        if (r < BalanceamentoJogo.CHANCE_BAU_TABULEIRO
            + BalanceamentoJogo.CHANCE_PAREDE_TABULEIRO) {
            return contarParedesNoTabuleiro() < BalanceamentoJogo.LIMITE_PAREDES_TABULEIRO
                ? TipoCarta.PAREDE
                : TipoCarta.INIMIGO;
        }

        return TipoCarta.INIMIGO;
    }

    public boolean podeMover(int novaLinha, int novaColuna) {
        if (!estaDentro(novaLinha, novaColuna)) {
            return false;
        }

        int dx = Math.abs(novaColuna - jogadorColuna);
        int dy = Math.abs(novaLinha - jogadorLinha);

        if ((dx + dy) != 1) {
            return false;
        }

        if (getTipoSeguro(novaLinha, novaColuna) == TipoCarta.PAREDE) {
            return false;
        }

        return true;
    }

    public TipoCarta getCarta(int linha, int coluna) {
        return getTipoSeguro(linha, coluna);
    }

    public TipoCarta getTipoSeguro(int linha, int coluna) {
        if (grid[linha][coluna] == null) {
            return TipoCarta.VAZIO;
        }
        return grid[linha][coluna].getTipo();
    }

    public CartaInfo getCartaInfo(int linha, int coluna) {
        return grid[linha][coluna];
    }

    public int getJogadorLinha() {
        return jogadorLinha;
    }

    public int getJogadorColuna() {
        return jogadorColuna;
    }

    public int getChamasColetadas() {
        return chamasColetadas;
    }

    public void moverJogador(int novaLinha, int novaColuna) {
        if (!podeMover(novaLinha, novaColuna))
            return;

        jogadorLinha = novaLinha;
        jogadorColuna = novaColuna;
        grid[jogadorLinha][jogadorColuna] = null;
    }

    public void trocarJogadorComCarta(int novaLinha, int novaColuna) {
        if (!podeMover(novaLinha, novaColuna)) {
            throw new IllegalArgumentException("A troca exige uma carta adjacente acessível.");
        }

        CartaInfo cartaDestino = grid[novaLinha][novaColuna];
        if (cartaDestino == null) {
            throw new IllegalArgumentException("Não há carta para trocar de posição com o jogador.");
        }

        grid[jogadorLinha][jogadorColuna] = cartaDestino;
        jogadorLinha = novaLinha;
        jogadorColuna = novaColuna;
        grid[jogadorLinha][jogadorColuna] = null;
    }

    public void gerarNovaChamaUnica() {
        for (int tentativas = 0; tentativas < 300; tentativas++) {
            int l = random.nextInt(LINHAS);
            int c = random.nextInt(COLUNAS);

            if (podeReceberChama(l, c, true)) {
                grid[l][c] = criarCarta(TipoCarta.CHAMA);
                return;
            }
        }

        if (gerarChamaPorBusca(true)) return;
        gerarChamaPorBusca(false);
    }

    public boolean coletarChama(int linha, int coluna) {
        if (!estaDentro(linha, coluna) || getTipoSeguro(linha, coluna) != TipoCarta.CHAMA) {
            return false;
        }

        chamasColetadas++;
        grid[linha][coluna] = null;
        return true;
    }

    public void aplicarEsteira(int antigaLinha, int antigaColuna, int novaLinha, int novaColuna) {
        int dx = novaColuna - antigaColuna;
        int dy = novaLinha - antigaLinha;

        if (Math.abs(dx) + Math.abs(dy) != 1) {
            throw new IllegalArgumentException("A esteira exige um movimento ortogonal de uma célula.");
        }

        if (dx == 1) {
            preencherVazioPelaEsquerda(antigaLinha, antigaColuna);
        }
        else if (dx == -1) {
            preencherVazioPelaDireita(antigaLinha, antigaColuna);
        }
        else if (dy == 1) {
            preencherVazioPorCima(antigaColuna, antigaLinha);
        }
        else {
            preencherVazioPorBaixo(antigaColuna, antigaLinha);
        }

        preencherNulosExcetoJogador();

        if (!existeMovimentoValido()) {
            gerarSaidaEmergencial();
        }

        if (chamasColetadas < OBJETIVO_CHAMAS) {
            garantirUmaChama();
        }
    }

    private boolean gerarChamaPorBusca(boolean exigirCartaFechada) {
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                if (podeReceberChama(linha, coluna, exigirCartaFechada)) {
                    grid[linha][coluna] = criarCarta(TipoCarta.CHAMA);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean podeReceberChama(int linha, int coluna, boolean exigirCartaFechada) {
        if (linha == jogadorLinha && coluna == jogadorColuna) return false;
        TipoCarta tipo = getTipoSeguro(linha, coluna);
        if (tipo == TipoCarta.PAREDE || tipo == TipoCarta.CHAMA) return false;
        if (exigirCartaFechada && cartaEstaRevelada(linha, coluna)) return false;
        return posicaoAlcancavel(linha, coluna);
    }

    private boolean posicaoAlcancavel(int linhaDestino, int colunaDestino) {
        boolean[][] visitada = new boolean[LINHAS][COLUNAS];
        ArrayDeque<int[]> pendentes = new ArrayDeque<>();
        pendentes.add(new int[] { jogadorLinha, jogadorColuna });
        visitada[jogadorLinha][jogadorColuna] = true;

        int[] deslocamentoLinha = { -1, 1, 0, 0 };
        int[] deslocamentoColuna = { 0, 0, -1, 1 };
        while (!pendentes.isEmpty()) {
            int[] atual = pendentes.removeFirst();
            if (atual[0] == linhaDestino && atual[1] == colunaDestino) return true;

            for (int direcao = 0; direcao < deslocamentoLinha.length; direcao++) {
                int linha = atual[0] + deslocamentoLinha[direcao];
                int coluna = atual[1] + deslocamentoColuna[direcao];
                if (!estaDentro(linha, coluna)
                    || visitada[linha][coluna]
                    || getTipoSeguro(linha, coluna) == TipoCarta.PAREDE) {
                    continue;
                }
                visitada[linha][coluna] = true;
                pendentes.addLast(new int[] { linha, coluna });
            }
        }
        return false;
    }

    private void preencherVazioPelaEsquerda(int linha, int colunaVazia) {
        for (int c = colunaVazia; c > 0; c--) {
            grid[linha][c] = grid[linha][c - 1];
        }
        grid[linha][0] = gerarCartaEsteiraSegura();
    }

    private void preencherVazioPelaDireita(int linha, int colunaVazia) {
        for (int c = colunaVazia; c < COLUNAS - 1; c++) {
            grid[linha][c] = grid[linha][c + 1];
        }
        grid[linha][COLUNAS - 1] = gerarCartaEsteiraSegura();
    }

    private void preencherVazioPorCima(int coluna, int linhaVazia) {
        for (int l = linhaVazia; l > 0; l--) {
            grid[l][coluna] = grid[l - 1][coluna];
        }
        grid[0][coluna] = gerarCartaEsteiraSegura();
    }

    private void preencherVazioPorBaixo(int coluna, int linhaVazia) {
        for (int l = linhaVazia; l < LINHAS - 1; l++) {
            grid[l][coluna] = grid[l + 1][coluna];
        }
        grid[LINHAS - 1][coluna] = gerarCartaEsteiraSegura();
    }

    private CartaInfo gerarCartaEsteiraSegura() {
        TipoCarta tipo;

        do {
            tipo = gerarTipoCartaAleatoria();
        }
        while (tipo == TipoCarta.CHAMA);

        return criarCarta(tipo);
    }

    public boolean existeMovimentoValido() {
        int l = jogadorLinha;
        int c = jogadorColuna;

        if (l > 0 && getTipoSeguro(l - 1, c) != TipoCarta.PAREDE) return true;
        if (l < LINHAS - 1 && getTipoSeguro(l + 1, c) != TipoCarta.PAREDE) return true;
        if (c > 0 && getTipoSeguro(l, c - 1) != TipoCarta.PAREDE) return true;
        if (c < COLUNAS - 1 && getTipoSeguro(l, c + 1) != TipoCarta.PAREDE) return true;

        return false;
    }

    private void gerarSaidaEmergencial() {
        int l = jogadorLinha;
        int c = jogadorColuna;

        if (l > 0) grid[l - 1][c] = criarCarta(TipoCarta.INIMIGO);
        else grid[l + 1][c] = criarCarta(TipoCarta.INIMIGO);
    }

    private void garantirUmaChama() {
        boolean manteveChamaAlcancavel = false;
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.CHAMA) {
                    if (!manteveChamaAlcancavel && posicaoAlcancavel(i, j)) {
                        manteveChamaAlcancavel = true;
                    } else {
                        grid[i][j] = criarCarta(TipoCarta.INIMIGO);
                    }
                }
            }
        }

        if (!manteveChamaAlcancavel) gerarNovaChamaUnica();
    }

    public void revelarCarta(int linha, int coluna) {
        CartaInfo carta = grid[linha][coluna];
        if (carta != null) {
            carta.setEstado(EstadoCarta.REVELADA);
        }
    }

    public boolean cartaEstaRevelada(int linha, int coluna) {
        CartaInfo carta = grid[linha][coluna];
        return carta != null && carta.getEstado() == EstadoCarta.REVELADA;
    }

    public void consumirCarta(int linha, int coluna) {
        grid[linha][coluna] = null;
    }

    private void preencherNulosExcetoJogador() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (i == jogadorLinha && j == jogadorColuna) {
                    grid[i][j] = null;
                    continue;
                }
                if (grid[i][j] == null) {
                    grid[i][j] = gerarCartaEsteiraSegura();
                    grid[i][j].setEstado(EstadoCarta.FECHADA);
                }
            }
        }
    }

    private int contarBausNoTabuleiro() {
        int quantidade = 0;

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.BAU) {
                    quantidade++;
                }
            }
        }

        return quantidade;
    }

    private int contarParedesNoTabuleiro() {
        int quantidade = 0;

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.PAREDE) quantidade++;
            }
        }

        return quantidade;
    }

    private boolean estaDentro(int linha, int coluna) {
        return linha >= 0 && linha < LINHAS && coluna >= 0 && coluna < COLUNAS;
    }
}
