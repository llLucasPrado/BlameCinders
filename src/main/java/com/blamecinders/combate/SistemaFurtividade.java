package com.blamecinders.combate;

import java.util.Objects;
import java.util.Random;

public class SistemaFurtividade {

    static final int CHANCE_BASE = 70;

    private final Random random;

    public SistemaFurtividade() {
        this(new Random());
    }

    public SistemaFurtividade(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public ResultadoFurtividade tentar(Inimigo inimigo) {
        int chance = Math.max(25, Math.min(80,
            CHANCE_BASE - inimigo.getDificuldadeFurtividade()));
        int rolagem = random.nextInt(100) + 1;
        return new ResultadoFurtividade(rolagem <= chance, chance, rolagem);
    }
}
