package com.blamecinders.combate;

import java.util.Objects;
import java.util.Random;

/** Tabela de drops da run. A probabilidade é aplicada por inimigo derrotado. */
public final class RecompensaCinzas {
    private static final Random ALEATORIO = new Random();

    public static final class Drop {
        private final int quantidade;
        private final int chancePercentual;

        private Drop(int quantidade, int chancePercentual) {
            this.quantidade = quantidade;
            this.chancePercentual = chancePercentual;
        }

        public int getQuantidade() { return quantidade; }
        public int getChancePercentual() { return chancePercentual; }
    }

    private RecompensaCinzas() { }

    public static Drop para(Inimigo inimigo) {
        Objects.requireNonNull(inimigo, "inimigo");
        switch (inimigo.getNome().toLowerCase()) {
            case "rato": return new Drop(2, 100);
            case "lobo": return new Drop(4, 80);
            case "zumbi": return new Drop(6, 75);
            case "esqueleto": return new Drop(8, 70);
            case "espírito":
            case "espirito": return new Drop(10, 65);
            default: return new Drop(2, 50);
        }
    }

    public static int calcular(Inimigo inimigo) {
        return calcular(inimigo, ALEATORIO);
    }

    public static int calcular(Inimigo inimigo, Random aleatorio) {
        Objects.requireNonNull(aleatorio, "aleatorio");
        Drop drop = para(inimigo);
        return aleatorio.nextInt(100) < drop.chancePercentual ? drop.quantidade : 0;
    }
}
