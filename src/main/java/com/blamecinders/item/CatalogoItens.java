package com.blamecinders.item;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class CatalogoItens {

    private static final List<Comida> COMIDAS = Arrays.asList(
        new Comida("Ração de viagem", 8, "COMIDA: RAÇÃO"),
        new Comida("Sopa de cinzas", 12, "COMIDA: SOPA"),
        new Comida("Frasco rubro", 18, "COMIDA: FRASCO")
    );

    private CatalogoItens() {
    }

    public static ItemBau gerarItemAleatorio(Random random) {
        // Distribuição inicial para balanceamento: 55% arma, 45% comida.
        if (random.nextInt(100) < 55) {
            Arma arma = CatalogoArmas.gerarArmaAleatoria(random);
            return arma;
        }

        Comida modelo = COMIDAS.get(random.nextInt(COMIDAS.size()));
        return modelo.copiar();
    }
}
