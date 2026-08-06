package com.blamecinders.aplicacao;

import com.blamecinders.combate.Jogador;
import com.blamecinders.tabuleiro.Tabuleiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoPartidaTest {

    @Test
    void iniciaEmAndamentoComHeroiEVidaPadrao() {
        EstadoPartida partida = new EstadoPartida();

        assertEquals(ResultadoPartida.EM_ANDAMENTO, partida.getResultado());
        assertEquals(EstadoPartida.VIDA_INICIAL_HEROI, partida.getJogador().getVida());
        assertFalse(partida.isFinalizada());
    }

    @Test
    void terminaEmVitoriaAoColetarTresChamas() {
        Tabuleiro tabuleiro = new Tabuleiro() {
            @Override
            public int getChamasColetadas() {
                return OBJETIVO_CHAMAS;
            }
        };
        EstadoPartida partida = new EstadoPartida(tabuleiro, new Jogador(50));

        assertTrue(partida.verificarObjetivoConcluido());
        assertTrue(partida.isFinalizada());
        assertEquals(ResultadoPartida.VITORIA, partida.getResultado());
    }

    @Test
    void registraDerrotaExplicitamente() {
        EstadoPartida partida = new EstadoPartida();

        partida.registrarDerrota();

        assertTrue(partida.isFinalizada());
        assertEquals(ResultadoPartida.DERROTA, partida.getResultado());
    }

}
