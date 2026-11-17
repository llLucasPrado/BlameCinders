package com.blamecinders.combate;

import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecompensaCinzasTest {

    @Test
    void defineQuantidadeEChancePorTipoDeInimigo() {
        RecompensaCinzas.Drop fraco = RecompensaCinzas.para(inimigo("Rato", 6));
        RecompensaCinzas.Drop medioBaixo = RecompensaCinzas.para(inimigo("Lobo", 10));
        RecompensaCinzas.Drop medioAlto = RecompensaCinzas.para(inimigo("Esqueleto", 15));
        RecompensaCinzas.Drop medioDificil = RecompensaCinzas.para(inimigo("Espírito", 20));

        assertEquals(2, fraco.getQuantidade());
        assertEquals(100, fraco.getChancePercentual());
        assertEquals(4, medioBaixo.getQuantidade());
        assertEquals(80, medioBaixo.getChancePercentual());
        assertEquals(7, medioAlto.getQuantidade());
        assertEquals(75, medioAlto.getChancePercentual());
        assertEquals(10, medioDificil.getQuantidade());
        assertEquals(65, medioDificil.getChancePercentual());
    }

    @Test
    void respeitaOLimitePercentualDoDrop() {
        Inimigo esqueleto = inimigo("Esqueleto", 15);

        assertEquals(7, RecompensaCinzas.calcular(esqueleto, new RandomFixo(74)));
        assertEquals(0, RecompensaCinzas.calcular(esqueleto, new RandomFixo(75)));
    }

    @Test
    void rejeitaDependenciasNulas() {
        assertThrows(NullPointerException.class, () -> RecompensaCinzas.para(null));
        assertThrows(
            NullPointerException.class,
            () -> RecompensaCinzas.calcular(inimigo("Rato", 6), null)
        );
    }

    private static Inimigo inimigo(String nome, int vida) {
        return new Inimigo(nome, vida, "INIMIGO 1");
    }

    private static final class RandomFixo extends Random {
        private final int valor;

        private RandomFixo(int valor) {
            this.valor = valor;
        }

        @Override
        public int nextInt(int limite) {
            if (valor < 0 || valor >= limite) {
                throw new IllegalArgumentException("Valor fora do limite do teste.");
            }
            return valor;
        }
    }
}
