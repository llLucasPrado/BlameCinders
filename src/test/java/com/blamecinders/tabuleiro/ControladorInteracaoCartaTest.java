package com.blamecinders.tabuleiro;

import com.blamecinders.aplicacao.AcaoCliqueCarta;
import com.blamecinders.aplicacao.ControladorInteracaoCarta;
import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.combate.Jogador;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControladorInteracaoCartaTest {

    @Test
    void primeiroCliqueRevelaESegundoInterage() {
        Tabuleiro tabuleiro = criarTabuleiro();
        ControladorInteracaoCarta controlador = new ControladorInteracaoCarta(
            new EstadoPartida(tabuleiro, new Jogador(50))
        );

        assertEquals(AcaoCliqueCarta.REVELAR, controlador.decidir(0, 1));

        tabuleiro.revelarCarta(0, 1);

        assertEquals(AcaoCliqueCarta.INTERAGIR, controlador.decidir(0, 1));
    }

    @Test
    void cartaDistanteFechadaBloqueiaEReveladaApenasVisualiza() {
        Tabuleiro tabuleiro = criarTabuleiro();
        ControladorInteracaoCarta controlador = new ControladorInteracaoCarta(
            new EstadoPartida(tabuleiro, new Jogador(50))
        );

        assertEquals(AcaoCliqueCarta.BLOQUEAR, controlador.decidir(3, 4));

        tabuleiro.revelarCarta(3, 4);

        assertEquals(AcaoCliqueCarta.VISUALIZAR, controlador.decidir(3, 4));
    }

    private Tabuleiro criarTabuleiro() {
        CartaInfo[][] grid = new CartaInfo[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                grid[linha][coluna] = new CartaInfo(TipoCarta.INIMIGO);
            }
        }
        return new Tabuleiro(grid, 0, 0, new Random(1));
    }
}
