package com.blamecinders.aplicacao;

import com.blamecinders.ui.tabuleiro.RemapeadorGradeEsteira;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RemapeadorGradeEsteiraTest {

    @Test
    void acompanhaEsteiraAoMoverParaDireita() {
        String[][] grade = grade();

        RemapeadorGradeEsteira.remapear(
            grade,
            new MovimentoTabuleiro(1, 2, 1, 3, true)
        );

        assertArrayEquals(new String[]{"D", "A", "B", "H", "E"}, grade[1]);
    }

    @Test
    void acompanhaEsteiraAoMoverParaEsquerda() {
        String[][] grade = grade();

        RemapeadorGradeEsteira.remapear(
            grade,
            new MovimentoTabuleiro(1, 2, 1, 1, true)
        );

        assertArrayEquals(new String[]{"A", "H", "D", "E", "B"}, grade[1]);
    }

    @Test
    void acompanhaEsteiraAoMoverParaBaixo() {
        String[][] grade = grade();

        RemapeadorGradeEsteira.remapear(
            grade,
            new MovimentoTabuleiro(1, 2, 2, 2, true)
        );

        assertArrayEquals(new String[]{"M", "A", "H", "R"}, coluna(grade, 2));
    }

    @Test
    void acompanhaEsteiraAoMoverParaCima() {
        String[][] grade = grade();

        RemapeadorGradeEsteira.remapear(
            grade,
            new MovimentoTabuleiro(2, 2, 1, 2, true)
        );

        assertArrayEquals(new String[]{"A", "M", "R", "H"}, coluna(grade, 2));
    }

    private String[][] grade() {
        return new String[][]{
            {"0", "1", "A", "3", "4"},
            {"A", "B", "H", "D", "E"},
            {"K", "L", "M", "N", "O"},
            {"P", "Q", "R", "S", "T"}
        };
    }

    private String[] coluna(String[][] grade, int coluna) {
        String[] valores = new String[grade.length];
        for (int linha = 0; linha < grade.length; linha++) {
            valores[linha] = grade[linha][coluna];
        }
        return valores;
    }
}
