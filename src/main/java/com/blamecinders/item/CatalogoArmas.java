package com.blamecinders.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CatalogoArmas {

    private static final List<Arma> armas = new ArrayList<>();
    private static final Random random = new Random();

    static {
        armas.add(new Arma(
            "Claymore",
            15,
            "ARMA: CLAYMORE"
        ));

        armas.add(new Arma(
            "Espada Curta",
            5,
            "ARMA: PUNHAL"
        ));

    }

    public static Arma gerarArmaAleatoria() {
        return gerarArmaAleatoria(random);
    }

    public static Arma gerarArmaAleatoria(Random fonteAleatoria) {
        Arma modelo = armas.get(fonteAleatoria.nextInt(armas.size()));
        return modelo.copiar();
    }
}
