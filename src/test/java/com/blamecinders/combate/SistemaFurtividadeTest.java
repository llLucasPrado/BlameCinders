package com.blamecinders.combate;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SistemaFurtividadeTest {

    @Test
    void sucessoQuandoRolagemEstaDentroDaChance() {
        SistemaFurtividade sistema = new SistemaFurtividade(randomQueRetorna(19));
        Inimigo inimigo = new Inimigo("Esqueleto", 20, "INIMIGO 1", 25);

        ResultadoFurtividade resultado = sistema.tentar(inimigo);

        assertTrue(resultado.isSucesso());
        assertTrue(resultado.getRolagem() <= resultado.getChancePercentual());
    }

    @Test
    void falhaQuandoRolagemUltrapassaChance() {
        SistemaFurtividade sistema = new SistemaFurtividade(randomQueRetorna(79));
        Inimigo inimigo = new Inimigo("Esqueleto", 20, "INIMIGO 1", 25);

        ResultadoFurtividade resultado = sistema.tentar(inimigo);

        assertFalse(resultado.isSucesso());
        assertTrue(resultado.getRolagem() > resultado.getChancePercentual());
    }

    private static Random randomQueRetorna(final int valor) {
        return new Random() {
            @Override
            public int nextInt(int limite) {
                return Math.min(valor, limite - 1);
            }
        };
    }
}
