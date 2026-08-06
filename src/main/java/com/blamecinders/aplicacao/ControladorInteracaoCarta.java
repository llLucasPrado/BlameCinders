package com.blamecinders.aplicacao;

import com.blamecinders.tabuleiro.Tabuleiro;

import java.util.Objects;

/** Decide a intenção de um clique sem abrir popups ou executar ações da carta. */
public final class ControladorInteracaoCarta {

    private final Tabuleiro tabuleiro;

    public ControladorInteracaoCarta(EstadoPartida partida) {
        this.tabuleiro = Objects.requireNonNull(partida, "partida").getTabuleiro();
    }

    public AcaoCliqueCarta decidir(int linha, int coluna) {
        if (linha < 0 || linha >= Tabuleiro.LINHAS || coluna < 0 || coluna >= Tabuleiro.COLUNAS) {
            throw new IllegalArgumentException("Posição fora do tabuleiro.");
        }

        boolean revelada = tabuleiro.cartaEstaRevelada(linha, coluna);
        boolean adjacente = Math.abs(linha - tabuleiro.getJogadorLinha())
            + Math.abs(coluna - tabuleiro.getJogadorColuna()) == 1;

        if (revelada) {
            return adjacente ? AcaoCliqueCarta.INTERAGIR : AcaoCliqueCarta.VISUALIZAR;
        }
        return adjacente ? AcaoCliqueCarta.REVELAR : AcaoCliqueCarta.BLOQUEAR;
    }
}
