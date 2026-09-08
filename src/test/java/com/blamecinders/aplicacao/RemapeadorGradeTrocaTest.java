package com.blamecinders.aplicacao;

import com.blamecinders.ui.tabuleiro.RemapeadorGradeTroca;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class RemapeadorGradeTrocaTest {

    @Test
    void trocaSomenteHeroiEAlvo() {
        String[][] grade = {
            {"A", "H", "I"},
            {"B", "C", "D"}
        };

        RemapeadorGradeTroca.remapear(
            grade,
            new MovimentoTabuleiro(0, 1, 0, 2, true)
        );

        assertArrayEquals(new String[]{"A", "I", "H"}, grade[0]);
        assertArrayEquals(new String[]{"B", "C", "D"}, grade[1]);
    }
}
