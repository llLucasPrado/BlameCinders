package com.blamecinders.item;

import com.blamecinders.configuracao.BalanceamentoJogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class CatalogoArmas {

    private static final List<Arma> armas = new ArrayList<>();
    private static final Random random = new Random();

    static {
        armas.add(new Arma(
            "Claymore",
            BalanceamentoJogo.DURABILIDADE_CLAYMORE,
            "ARMA: CLAYMORE"
        ));

        armas.add(new Arma(
            "Espada Curta",
            BalanceamentoJogo.DURABILIDADE_ESPADA_CURTA,
            "ARMA: ESPADA_CURTA"
        ));

    }

    public static Arma gerarArmaAleatoria() {
        return gerarArmaAleatoria(random);
    }

    private CatalogoArmas() {
    }

    public static Arma gerarArmaAleatoria(Random fonteAleatoria) {
        Objects.requireNonNull(fonteAleatoria, "fonteAleatoria");
        Arma modelo = armas.get(fonteAleatoria.nextInt(armas.size()));
        return modelo.copiar();
    }
}
