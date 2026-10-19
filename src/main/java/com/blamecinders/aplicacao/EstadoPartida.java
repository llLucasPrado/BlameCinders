package com.blamecinders.aplicacao;

import java.util.Objects;

import com.blamecinders.combate.Jogador;
import com.blamecinders.combate.Personagem;
import com.blamecinders.configuracao.BalanceamentoJogo;
import com.blamecinders.tabuleiro.Tabuleiro;

/** Mantém o estado persistente de uma partida, sem depender do libGDX. */
public final class EstadoPartida {

    public static final int VIDA_INICIAL_HEROI = BalanceamentoJogo.VIDA_INICIAL_HEROI;

    private final Tabuleiro tabuleiro;
    private final Jogador jogador;
    private final Personagem personagem;
    private int cinzas;
    private ResultadoPartida resultado = ResultadoPartida.EM_ANDAMENTO;

    public EstadoPartida() {
    this(Personagem.PADRAO);
}

    public EstadoPartida(Personagem personagem) {
        this(new Tabuleiro(), new Jogador(personagem.getVidaInicial()), personagem);
    }

    public EstadoPartida(Tabuleiro tabuleiro, Jogador jogador) {
        this(tabuleiro, jogador, Personagem.PADRAO);
    }

    public EstadoPartida(Tabuleiro tabuleiro, Jogador jogador, Personagem personagem) {
        this.tabuleiro = Objects.requireNonNull(tabuleiro, "tabuleiro");
        this.jogador = Objects.requireNonNull(jogador, "jogador");
        this.personagem = Objects.requireNonNull(personagem, "personagem");
    }

    public Personagem getPersonagem() {
        return personagem;
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

    public int getCinzas() {
        return cinzas;
    }

    public void adicionarCinzas(int quantidade) {
        validarQuantidadeCinzas(quantidade);
        cinzas += quantidade;
    }

    public void restaurarCinzas(int quantidade) {
        validarQuantidadeCinzas(quantidade);
        cinzas = quantidade;
    }

    private void validarQuantidadeCinzas(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade de cinzas não pode ser negativa.");
        }
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
            cinzas = 0;
            resultado = ResultadoPartida.DERROTA;
        }
    }
}
