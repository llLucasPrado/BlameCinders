package com.blamecinders.item;

import com.blamecinders.configuracao.BalanceamentoJogo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class CatalogoItens {

    private static final List<Comida> COMIDAS = Arrays.asList(
        new Comida("Ração de viagem", BalanceamentoJogo.CURA_RACAO, "COMIDA: RAÇÃO"),
        new Comida("Sopa de cinzas", BalanceamentoJogo.CURA_SOPA, "COMIDA: SOPA"),
        new Comida("Frasco rubro", BalanceamentoJogo.CURA_FRASCO, "COMIDA: FRASCO")
    );

    private CatalogoItens() {
    }

    public static ItemBau gerarItemAleatorio(Random random) {
        if (random.nextInt(BalanceamentoJogo.TOTAL_PERCENTUAL)
            < BalanceamentoJogo.CHANCE_ARMA_NO_BAU) {
            Arma arma = CatalogoArmas.gerarArmaAleatoria(random);
            return arma;
        }

        Comida modelo = COMIDAS.get(random.nextInt(COMIDAS.size()));
        return modelo.copiar();
    }
}
