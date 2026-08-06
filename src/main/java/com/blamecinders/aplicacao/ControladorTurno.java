package com.blamecinders.aplicacao;

import com.blamecinders.tabuleiro.Tabuleiro;

import java.util.Objects;

/** Coordena alterações atômicas do tabuleiro durante um turno. */
public final class ControladorTurno {

    private final EstadoPartida partida;

    public ControladorTurno(EstadoPartida partida) {
        this.partida = Objects.requireNonNull(partida, "partida");
    }

    public MovimentoTabuleiro prepararMovimento(int linhaDestino, int colunaDestino) {
        Tabuleiro tabuleiro = partida.getTabuleiro();
        return new MovimentoTabuleiro(
            tabuleiro.getJogadorLinha(),
            tabuleiro.getJogadorColuna(),
            linhaDestino,
            colunaDestino,
            tabuleiro.podeMover(linhaDestino, colunaDestino)
        );
    }

    public void concluirMovimento(MovimentoTabuleiro movimento) {
        Objects.requireNonNull(movimento, "movimento");
        if (!movimento.isValido()) {
            throw new IllegalArgumentException("Não é possível concluir um movimento inválido.");
        }

        Tabuleiro tabuleiro = partida.getTabuleiro();
        if (tabuleiro.getJogadorLinha() != movimento.getLinhaOrigem()
            || tabuleiro.getJogadorColuna() != movimento.getColunaOrigem()) {
            throw new IllegalStateException("O tabuleiro mudou depois que o movimento foi preparado.");
        }

        tabuleiro.moverJogador(movimento.getLinhaDestino(), movimento.getColunaDestino());
        tabuleiro.aplicarEsteira(
            movimento.getLinhaOrigem(),
            movimento.getColunaOrigem(),
            movimento.getLinhaDestino(),
            movimento.getColunaDestino()
        );
    }
}
