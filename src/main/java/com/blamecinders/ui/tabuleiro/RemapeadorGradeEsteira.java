package com.blamecinders.ui.tabuleiro;

import com.blamecinders.aplicacao.MovimentoTabuleiro;

import java.util.Objects;

/** Mantém a identidade dos atores visuais enquanto a esteira muda suas células. */
public final class RemapeadorGradeEsteira {

    private RemapeadorGradeEsteira() {
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
        int deltaLinha = linhaDestino - linhaOrigem;
        int deltaColuna = colunaDestino - colunaOrigem;

        T atorHeroi = grade[linhaOrigem][colunaOrigem];
        T atorReciclado = grade[linhaDestino][colunaDestino];

        if (deltaColuna == 1) {
            for (int coluna = colunaOrigem; coluna > 0; coluna--) {
                grade[linhaOrigem][coluna] = grade[linhaOrigem][coluna - 1];
            }
            grade[linhaOrigem][0] = atorReciclado;
        } else if (deltaColuna == -1) {
            for (int coluna = colunaOrigem; coluna < grade[linhaOrigem].length - 1; coluna++) {
                grade[linhaOrigem][coluna] = grade[linhaOrigem][coluna + 1];
            }
            grade[linhaOrigem][grade[linhaOrigem].length - 1] = atorReciclado;
        } else if (deltaLinha == 1) {
            for (int linha = linhaOrigem; linha > 0; linha--) {
                grade[linha][colunaOrigem] = grade[linha - 1][colunaOrigem];
            }
            grade[0][colunaOrigem] = atorReciclado;
        } else if (deltaLinha == -1) {
            for (int linha = linhaOrigem; linha < grade.length - 1; linha++) {
                grade[linha][colunaOrigem] = grade[linha + 1][colunaOrigem];
            }
            grade[grade.length - 1][colunaOrigem] = atorReciclado;
        } else {
            throw new IllegalArgumentException("A esteira exige um movimento ortogonal de uma célula.");
        }

        grade[linhaDestino][colunaDestino] = atorHeroi;
    }
}
