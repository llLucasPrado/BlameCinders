package com.blamecinders.tabuleiro;

import com.blamecinders.combate.CatalogoInimigos;
import com.blamecinders.combate.Inimigo;
import com.blamecinders.item.CatalogoItens;
import com.blamecinders.item.ItemBau;
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
    public static final int OBJETIVO_CHAMAS = 3;

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

        this.grid = grid;
        this.jogadorLinha = jogadorLinha;
        this.jogadorColuna = jogadorColuna;
        this.random = Objects.requireNonNull(random, "random");
        this.grid[jogadorLinha][jogadorColuna] = null;
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

        int r = random.nextInt(100);

        if (r >= 10 && r < 30) {
            if (contarBausNoTabuleiro() < 3) {
                return TipoCarta.BAU;
            }
        }

        if (r >= 30 && r < 40) {
            return TipoCarta.PAREDE;
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

    public void gerarNovaChamaUnica() {

        for (int tentativas = 0; tentativas < 300; tentativas++) {

            int l = random.nextInt(LINHAS);
            int c = random.nextInt(COLUNAS);

            if (l == jogadorLinha && c == jogadorColuna) continue;
            if (getTipoSeguro(l, c) == TipoCarta.PAREDE) continue;
            if (getTipoSeguro(l, c) == TipoCarta.CHAMA) continue;
            if (cartaEstaRevelada(l, c)) continue;

            grid[l][c] = criarCarta(TipoCarta.CHAMA);
            return;
        }

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

        if (chamasColetadas < OBJETIVO_CHAMAS && !existeChama()) {
            garantirUmaChama();
        }
    }

    private boolean existeChama() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.CHAMA) {
                    return true;
                }
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

    // Mantém exatamente uma chama ativa enquanto o objetivo não foi concluído.
    private void garantirUmaChama() {
        int quantidade = 0;

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.CHAMA) {
                    quantidade++;
                }
            }
        }

        if (quantidade == 0) {
            gerarNovaChamaUnica();
            return;
        }

        if (quantidade > 1) {
            boolean manteveUma = false;

            for (int i = 0; i < LINHAS; i++) {
                for (int j = 0; j < COLUNAS; j++) {
                    if (getTipoSeguro(i, j) == TipoCarta.CHAMA) {
                        if (!manteveUma) {
                            manteveUma = true;
                        } else {
                            grid[i][j] = gerarCartaEsteiraSegura();
                        }
                    }
                }
            }
        }
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

    private boolean estaDentro(int linha, int coluna) {
        return linha >= 0 && linha < LINHAS && coluna >= 0 && coluna < COLUNAS;
    }
}
