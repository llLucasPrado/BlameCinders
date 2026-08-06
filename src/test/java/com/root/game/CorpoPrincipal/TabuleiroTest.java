package com.root.game.CorpoPrincipal;

import com.root.game.Modelo.CartaInfo;
import org.junit.jupiter.api.Test;

import java.util.Random;

import com.root.game.Modelo.TipoCarta;

import static com.root.game.Modelo.TipoCarta.CHAMA;
import static com.root.game.Modelo.TipoCarta.INIMIGO;
import static com.root.game.Modelo.TipoCarta.PAREDE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TabuleiroTest {

    @Test
    void iniciaComUmaUnicaChamaESemCartaSobHeroi() {
        Tabuleiro tabuleiro = new Tabuleiro(new Random(7));

        assertNull(tabuleiro.getCartaInfo(0, 0));
        assertEquals(1, contarTipo(tabuleiro, CHAMA));
        assertEquals(1, contarCelulasVazias(tabuleiro));
        assertEquals(3, Tabuleiro.OBJETIVO_CHAMAS);
    }

    @Test
    void moverParaDireitaDeslocaSomenteSegmentoDaEsquerda() {
        CartaInfo[][] grid = gridPreenchido();
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
}
