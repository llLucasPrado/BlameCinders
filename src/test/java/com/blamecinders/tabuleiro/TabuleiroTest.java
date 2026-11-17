package com.blamecinders.tabuleiro;

import com.blamecinders.combate.Inimigo;
import com.blamecinders.configuracao.BalanceamentoJogo;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static com.blamecinders.tabuleiro.TipoCarta.CHAMA;
import static com.blamecinders.tabuleiro.TipoCarta.BAU;
import static com.blamecinders.tabuleiro.TipoCarta.INIMIGO;
import static com.blamecinders.tabuleiro.TipoCarta.PAREDE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TabuleiroTest {

    @Test
    void geraChamaMesmoQuandoTodasAsCartasEstaoReveladas() {
        CartaInfo[][] grid = gridPreenchido();
        grid[0][0] = null;
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                if (grid[linha][coluna] != null) {
                    grid[linha][coluna].setInimigo(
                        new Inimigo("Teste", 5, "INIMIGO 1")
                    );
                    grid[linha][coluna].setEstado(EstadoCarta.REVELADA);
                }
            }
        }
        Tabuleiro tabuleiro = new Tabuleiro(grid, 0, 0, new Random(1));

        tabuleiro.gerarNovaChamaUnica();

        assertEquals(1, contarTipo(tabuleiro, TipoCarta.CHAMA));
    }

    @Test
    void reposicionaChamaQueFicouIsoladaPorParedes() {
        CartaInfo[][] grid = gridPreenchido();
        grid[0][0] = null;
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            grid[linha][1] = new CartaInfo(PAREDE);
        }
        grid[0][2] = new CartaInfo(CHAMA);
        Tabuleiro tabuleiro = new Tabuleiro(grid, 0, 0, new Random(4));

        tabuleiro.moverJogador(1, 0);
        tabuleiro.aplicarEsteira(0, 0, 1, 0);

        assertEquals(1, contarTipo(tabuleiro, CHAMA));
        assertEquals(0, colunaDoTipo(tabuleiro, CHAMA));
        assertTrue(tabuleiro.existeMovimentoValido());
    }

    @Test
    void restauraSaveAntigoCercadoComChamaAlcancavel() {
        CartaInfo[][] grid = gridPreenchido();
        grid[0][0] = null;
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            grid[linha][1] = new CartaInfo(PAREDE);
        }
        grid[1][0] = new CartaInfo(PAREDE);
        grid[0][2] = new CartaInfo(CHAMA);

        Tabuleiro tabuleiro = Tabuleiro.restaurar(grid, 0, 0, 0);

        assertTrue(tabuleiro.existeMovimentoValido());
        assertEquals(1, contarTipo(tabuleiro, CHAMA));
        assertEquals(0, colunaDoTipo(tabuleiro, CHAMA));
    }

    @Test
    void iniciaComUmaUnicaChamaESemCartaSobHeroi() {
        Tabuleiro tabuleiro = new Tabuleiro(new Random(7));

        assertNull(tabuleiro.getCartaInfo(0, 0));
        assertEquals(1, contarTipo(tabuleiro, CHAMA));
        assertEquals(1, contarCelulasVazias(tabuleiro));
        assertEquals(3, Tabuleiro.OBJETIVO_CHAMAS);
    }

    @Test
    void respeitaLimitesDeBausEParedesNaGeracaoInicial() {
        for (int semente = 0; semente < 300; semente++) {
            Tabuleiro tabuleiro = new Tabuleiro(new Random(semente));
            assertTrue(
                contarTipo(tabuleiro, BAU) <= BalanceamentoJogo.LIMITE_BAUS_TABULEIRO
            );
            assertTrue(
                contarTipo(tabuleiro, PAREDE)
                    <= BalanceamentoJogo.LIMITE_PAREDES_TABULEIRO
            );
        }
    }

    @Test
    void distribuicaoMediaDoTabuleiroPermaneceDentroDaFaixaPlanejada() {
        int amostras = 2_000;
        int totalBaus = 0;
        int totalParedes = 0;
        int totalInimigos = 0;

        for (int semente = 0; semente < amostras; semente++) {
            Tabuleiro tabuleiro = new Tabuleiro(new Random(semente));
            totalBaus += contarTipo(tabuleiro, BAU);
            totalParedes += contarTipo(tabuleiro, PAREDE);
            totalInimigos += contarTipo(tabuleiro, INIMIGO);
            assertEquals(1, contarTipo(tabuleiro, CHAMA));
        }

        double mediaBaus = (double) totalBaus / amostras;
        double mediaParedes = (double) totalParedes / amostras;
        double mediaInimigos = (double) totalInimigos / amostras;
        assertTrue(mediaBaus >= 1.5 && mediaBaus <= 2.8, "Média de baús: " + mediaBaus);
        assertTrue(
            mediaParedes >= 1.7 && mediaParedes <= 3.2,
            "Média de paredes: " + mediaParedes
        );
        assertTrue(
            mediaInimigos >= 12.0 && mediaInimigos <= 15.0,
            "Média de inimigos: " + mediaInimigos
        );
    }

    @Test
    void moverParaDireitaDeslocaSomenteSegmentoDaEsquerda() {
        CartaInfo[][] grid = gridPreenchido();
        grid[0][0] = new CartaInfo(CHAMA);
        CartaInfo primeira = grid[1][0];
        CartaInfo segunda = grid[1][1];
        CartaInfo ultima = grid[1][4];
        Tabuleiro tabuleiro = new Tabuleiro(grid, 1, 2, new Random(11));

        assertTrue(tabuleiro.podeMover(1, 3));
        tabuleiro.moverJogador(1, 3);
        tabuleiro.aplicarEsteira(1, 2, 1, 3);

        assertSame(primeira, tabuleiro.getCartaInfo(1, 1));
        assertSame(segunda, tabuleiro.getCartaInfo(1, 2));
        assertSame(ultima, tabuleiro.getCartaInfo(1, 4));
        assertNull(tabuleiro.getCartaInfo(1, 3));
        assertEquals(1, contarCelulasVazias(tabuleiro));
    }

    @Test
    void moverParaCimaDeslocaSomenteSegmentoDeBaixo() {
        CartaInfo[][] grid = gridPreenchido();
        grid[0][0] = new CartaInfo(CHAMA);
        CartaInfo bordaInferior = grid[3][3];
        CartaInfo acimaDoDestino = grid[0][3];
        Tabuleiro tabuleiro = new Tabuleiro(grid, 2, 3, new Random(13));

        assertTrue(tabuleiro.podeMover(1, 3));
        tabuleiro.moverJogador(1, 3);
        tabuleiro.aplicarEsteira(2, 3, 1, 3);

        assertSame(bordaInferior, tabuleiro.getCartaInfo(2, 3));
        assertSame(acimaDoDestino, tabuleiro.getCartaInfo(0, 3));
        assertNull(tabuleiro.getCartaInfo(1, 3), "A posição do herói deve permanecer vazia");
        assertEquals(1, contarCelulasVazias(tabuleiro));
    }

    @Test
    void paredeBloqueiaMovimento() {
        CartaInfo[][] grid = gridPreenchido();
        grid[2][3] = new CartaInfo(PAREDE);
        Tabuleiro tabuleiro = new Tabuleiro(grid, 2, 2, new Random(17));

        assertFalse(tabuleiro.podeMover(2, 3));
        tabuleiro.moverJogador(2, 3);

        assertEquals(2, tabuleiro.getJogadorLinha());
        assertEquals(2, tabuleiro.getJogadorColuna());
        assertSame(grid[2][3], tabuleiro.getCartaInfo(2, 3));
    }

    @Test
    void chamaSoPodeSerContabilizadaUmaVez() {
        CartaInfo[][] grid = gridPreenchido();
        grid[0][1] = new CartaInfo(CHAMA);
        Tabuleiro tabuleiro = new Tabuleiro(grid, 0, 0, new Random(19));

        assertTrue(tabuleiro.coletarChama(0, 1));
        assertFalse(tabuleiro.coletarChama(0, 1));
        assertEquals(1, tabuleiro.getChamasColetadas());
        assertNull(tabuleiro.getCartaInfo(0, 1));
    }

    @Test
    void trocaHeroiComCartaSemAcionarEsteira() {
        CartaInfo[][] grid = gridPreenchido();
        CartaInfo alvo = grid[1][3];
        CartaInfo cartaDistante = grid[1][0];
        Tabuleiro tabuleiro = new Tabuleiro(grid, 1, 2, new Random(23));

        tabuleiro.trocarJogadorComCarta(1, 3);

        assertEquals(1, tabuleiro.getJogadorLinha());
        assertEquals(3, tabuleiro.getJogadorColuna());
        assertSame(alvo, tabuleiro.getCartaInfo(1, 2));
        assertSame(cartaDistante, tabuleiro.getCartaInfo(1, 0));
        assertNull(tabuleiro.getCartaInfo(1, 3));
        assertEquals(1, contarCelulasVazias(tabuleiro));
    }

    private static CartaInfo[][] gridPreenchido() {
        CartaInfo[][] grid = new CartaInfo[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                grid[linha][coluna] = new CartaInfo(INIMIGO);
            }
        }
        return grid;
    }

    private static int contarTipo(Tabuleiro tabuleiro, TipoCarta tipo) {
        int total = 0;
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                if (tabuleiro.getTipoSeguro(linha, coluna) == tipo) total++;
            }
        }
        return total;
    }

    private static int contarCelulasVazias(Tabuleiro tabuleiro) {
        return contarTipo(tabuleiro, TipoCarta.VAZIO);
    }

    private static int colunaDoTipo(Tabuleiro tabuleiro, TipoCarta tipo) {
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                if (tabuleiro.getTipoSeguro(linha, coluna) == tipo) return coluna;
            }
        }
        return -1;
    }
}
