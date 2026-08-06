package com.blamecinders.aplicacao;

import com.blamecinders.combate.Jogador;
import com.blamecinders.tabuleiro.Tabuleiro;

import java.util.Objects;

/** Mantém o estado persistente de uma partida, sem depender do libGDX. */
public final class EstadoPartida {

    public static final int VIDA_INICIAL_HEROI = 50;

    private final Tabuleiro tabuleiro;
    private final Jogador jogador;
    private ResultadoPartida resultado = ResultadoPartida.EM_ANDAMENTO;

    public EstadoPartida() {
        this(new Tabuleiro(), new Jogador(VIDA_INICIAL_HEROI));
    }

    public EstadoPartida(Tabuleiro tabuleiro, Jogador jogador) {
        this.tabuleiro = Objects.requireNonNull(tabuleiro, "tabuleiro");
        this.jogador = Objects.requireNonNull(jogador, "jogador");
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public ResultadoPartida getResultado() {
        return resultado;
    }

    public boolean isFinalizada() {
        return resultado != ResultadoPartida.EM_ANDAMENTO;
    }

    public boolean verificarObjetivoConcluido() {
        if (isFinalizada()) {
            return resultado == ResultadoPartida.VITORIA;
        }

        if (tabuleiro.getChamasColetadas() < Tabuleiro.OBJETIVO_CHAMAS) {
            return false;
        }

        resultado = ResultadoPartida.VITORIA;
        return true;
    }

    public void registrarDerrota() {
        if (!isFinalizada()) {
            resultado = ResultadoPartida.DERROTA;
        }
    }
}
