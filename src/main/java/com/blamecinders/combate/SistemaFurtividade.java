package com.blamecinders.combate;

import java.util.Objects;
import java.util.Random;

import com.blamecinders.configuracao.BalanceamentoJogo;

public class SistemaFurtividade {

    static final int CHANCE_BASE = BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE;

    private final Random random;

    public SistemaFurtividade() {
        this(new Random());
    }

    public SistemaFurtividade(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }
    
    public ResultadoFurtividade tentar(Inimigo inimigo) {
    return tentar(inimigo, CHANCE_BASE);
}

    public ResultadoFurtividade tentar(Inimigo inimigo, int chanceBase) {
        Objects.requireNonNull(inimigo, "inimigo");
        int chance = Math.max(
            BalanceamentoJogo.CHANCE_MINIMA_FURTIVIDADE,
            Math.min(
                BalanceamentoJogo.CHANCE_MAXIMA_FURTIVIDADE,
                chanceBase - inimigo.getDificuldadeFurtividade()
            )
        );
        int rolagem = random.nextInt(BalanceamentoJogo.TOTAL_PERCENTUAL) + 1;
        return new ResultadoFurtividade(rolagem <= chance, chance, rolagem);
    }
}
