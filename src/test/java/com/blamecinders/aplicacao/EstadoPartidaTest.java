package com.blamecinders.aplicacao;

import com.blamecinders.combate.Jogador;
import com.blamecinders.combate.Personagem;
import com.blamecinders.tabuleiro.Tabuleiro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoPartidaTest {

    @Test
    void iniciaEmAndamentoComHeroiEVidaPadrao() {
        EstadoPartida partida = new EstadoPartida();

        assertEquals(ResultadoPartida.EM_ANDAMENTO, partida.getResultado());
        assertEquals(EstadoPartida.VIDA_INICIAL_HEROI, partida.getJogador().getVida());
        assertSame(Personagem.PADRAO, partida.getPersonagem());
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
        partida.adicionarCinzas(12);

        partida.registrarDerrota();

        assertTrue(partida.isFinalizada());
        assertEquals(ResultadoPartida.DERROTA, partida.getResultado());
        assertEquals(0, partida.getCinzas());
    }

    @Test
    void usaAtributosDoPersonagemEscolhido() {
        EstadoPartida partida = new EstadoPartida(Personagem.NAZARET);

        assertSame(Personagem.NAZARET, partida.getPersonagem());
        assertEquals(Personagem.NAZARET.getVidaInicial(), partida.getJogador().getVida());
        assertEquals(
            Personagem.NAZARET.getVidaInicial(),
            partida.getJogador().getVidaMaxima()
        );
    }

    @Test
    void acumulaERestauraCinzasComValidacao() {
        EstadoPartida partida = new EstadoPartida();

        partida.adicionarCinzas(4);
        partida.adicionarCinzas(6);
        assertEquals(10, partida.getCinzas());

        partida.restaurarCinzas(3);
        assertEquals(3, partida.getCinzas());

        assertThrows(IllegalArgumentException.class, () -> partida.adicionarCinzas(-1));
        assertThrows(IllegalArgumentException.class, () -> partida.restaurarCinzas(-1));

        partida.restaurarCinzas(Integer.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> partida.adicionarCinzas(1));
    }

}
