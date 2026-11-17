package com.blamecinders.combate;

import java.util.Objects;
import java.util.Random;

import com.blamecinders.configuracao.BalanceamentoJogo;

public class SistemaFurtividade {

    private final Random random;

    public SistemaFurtividade() {
        this(new Random());
    }

    public SistemaFurtividade(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public ResultadoFurtividade tentar(Personagem personagem, Inimigo inimigo) {
        Objects.requireNonNull(personagem, "personagem");
        Objects.requireNonNull(inimigo, "inimigo");
        if (!personagem.podeUsarFurtividade()) {
            throw new IllegalStateException(
                personagem.getArquetipo() + " não pode usar furtividade."
            );
        }

        int chance = calcularChance(personagem, inimigo.getDificuldadeFurtividade());
        int rolagem = random.nextInt(BalanceamentoJogo.TOTAL_PERCENTUAL) + 1;
        return new ResultadoFurtividade(rolagem <= chance, chance, rolagem);
    }

    public static int calcularChance(Personagem personagem, int dificuldadeInimigo) {
        Objects.requireNonNull(personagem, "personagem");
        if (!personagem.podeUsarFurtividade()) return 0;

        return Math.max(
            BalanceamentoJogo.CHANCE_MINIMA_FURTIVIDADE,
            Math.min(
                BalanceamentoJogo.CHANCE_MAXIMA_FURTIVIDADE,
                personagem.getChanceBaseFurtividade() - dificuldadeInimigo
            )
        );
    }
}
