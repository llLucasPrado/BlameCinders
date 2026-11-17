package com.blamecinders.combate;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SistemaFurtividadeTest {

    @Test
    void sucessoQuandoRolagemEstaDentroDaChance() {
        SistemaFurtividade sistema = new SistemaFurtividade(randomQueRetorna(19));
        Inimigo inimigo = new Inimigo("Esqueleto", 20, "INIMIGO 1", 25);

        ResultadoFurtividade resultado = sistema.tentar(Personagem.TAIGA, inimigo);

        assertTrue(resultado.isSucesso());
        assertEquals(65, resultado.getChancePercentual());
        assertTrue(resultado.getRolagem() <= resultado.getChancePercentual());
    }

    @Test
    void falhaQuandoRolagemUltrapassaChance() {
        SistemaFurtividade sistema = new SistemaFurtividade(randomQueRetorna(79));
        Inimigo inimigo = new Inimigo("Esqueleto", 20, "INIMIGO 1", 25);

        ResultadoFurtividade resultado = sistema.tentar(Personagem.VIEYRA, inimigo);

        assertFalse(resultado.isSucesso());
        assertTrue(resultado.getRolagem() > resultado.getChancePercentual());
    }

    @Test
    void rejeitaInimigoNulo() {
        SistemaFurtividade sistema = new SistemaFurtividade(new Random(1));

        assertThrows(
            NullPointerException.class,
            () -> sistema.tentar(Personagem.TAIGA, null)
        );
    }

    @Test
    void respeitaHierarquiaDeFurtividadeDosArquetipos() {
        int dificuldade = 25;

        int assassino = SistemaFurtividade.calcularChance(Personagem.TAIGA, dificuldade);
        int arqueira = SistemaFurtividade.calcularChance(Personagem.VIEYRA, dificuldade);
        int cavaleiro = SistemaFurtividade.calcularChance(Personagem.AQUILLA, dificuldade);
        int anao = SistemaFurtividade.calcularChance(Personagem.NAZARET, dificuldade);

        assertTrue(assassino > arqueira);
        assertTrue(arqueira > cavaleiro);
        assertEquals(0, anao);
    }

    @Test
    void impedeAnaoDeTentarFurtividade() {
        SistemaFurtividade sistema = new SistemaFurtividade(new Random(1));
        Inimigo inimigo = new Inimigo("Esqueleto", 20, "INIMIGO 1", 25);

        IllegalStateException erro = assertThrows(
            IllegalStateException.class,
            () -> sistema.tentar(Personagem.NAZARET, inimigo)
        );

        assertTrue(erro.getMessage().contains("não pode usar furtividade"));
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
