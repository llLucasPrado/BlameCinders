package com.blamecinders.aplicacao;

import com.blamecinders.tabuleiro.Tabuleiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControladorTurnoTest {

    @Test
    void preparaEConcluiMovimentoComEsteira() {
        EstadoPartida partida = new EstadoPartida();
        ControladorTurno controlador = new ControladorTurno(partida);

        MovimentoTabuleiro movimento = controlador.prepararMovimento(0, 1);
        controlador.concluirMovimento(movimento);

        Tabuleiro tabuleiro = partida.getTabuleiro();
        assertTrue(movimento.isValido());
        assertEquals(0, tabuleiro.getJogadorLinha());
        assertEquals(1, tabuleiro.getJogadorColuna());
        assertNull(tabuleiro.getCartaInfo(0, 1));
        assertNotNull(tabuleiro.getCartaInfo(0, 0));
    }

    @Test
    void rejeitaDestinoNaoAdjacente() {
        ControladorTurno controlador = new ControladorTurno(new EstadoPartida());

        MovimentoTabuleiro movimento = controlador.prepararMovimento(3, 4);

        assertFalse(movimento.isValido());
        assertThrows(IllegalArgumentException.class, () -> controlador.concluirMovimento(movimento));
    }

    @Test
    void rejeitaMovimentoPreparadoSobreEstadoAntigo() {
        ControladorTurno controlador = new ControladorTurno(new EstadoPartida());
        MovimentoTabuleiro primeiro = controlador.prepararMovimento(0, 1);
        MovimentoTabuleiro obsoleto = controlador.prepararMovimento(1, 0);

        controlador.concluirMovimento(primeiro);

        assertThrows(IllegalStateException.class, () -> controlador.concluirMovimento(obsoleto));
    }
}
