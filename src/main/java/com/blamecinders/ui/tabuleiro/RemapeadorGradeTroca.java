package com.blamecinders.ui.tabuleiro;

import com.blamecinders.aplicacao.MovimentoTabuleiro;

import java.util.Objects;

/** Troca os atores da origem e do destino sem deslocar o restante da grade. */
public final class RemapeadorGradeTroca {

    private RemapeadorGradeTroca() {
    }

    public static <T> void remapear(T[][] grade, MovimentoTabuleiro movimento) {
        Objects.requireNonNull(grade, "grade");
        Objects.requireNonNull(movimento, "movimento");
        if (!movimento.isValido()) {
            throw new IllegalArgumentException("O remapeamento exige um movimento válido.");
        }

        int linhaOrigem = movimento.getLinhaOrigem();
        int colunaOrigem = movimento.getColunaOrigem();
        int linhaDestino = movimento.getLinhaDestino();
        int colunaDestino = movimento.getColunaDestino();
        int distancia = Math.abs(linhaDestino - linhaOrigem)
            + Math.abs(colunaDestino - colunaOrigem);
        if (distancia != 1) {
            throw new IllegalArgumentException("A troca exige posições adjacentes.");
        }

        T origem = grade[linhaOrigem][colunaOrigem];
        grade[linhaOrigem][colunaOrigem] = grade[linhaDestino][colunaDestino];
        grade[linhaDestino][colunaDestino] = origem;
    }
}
