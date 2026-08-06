package com.root.game.Combate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CatalogoInimigos {

    private static final List<Inimigo> inimigos = new ArrayList<>();
    private static final Random random = new Random();

    static {
        inimigos.add(new Inimigo(
            "Esqueleto",
            20,
            "INIMIGO 1"
        ));

        inimigos.add(new Inimigo(
            "Esqueleto",
            20,
            "INIMIGO 2"
        ));

        inimigos.add(new Inimigo(
            "Lobo",
            12,
            "INIMIGO 3"
        ));

        inimigos.add(new Inimigo(
            "Espírito",
            23,
            "INIMIGO 4"
        ));

        inimigos.add(new Inimigo(
            "Zumbi",
            15,
            "INIMIGO 5"
        ));

        inimigos.add(new Inimigo(
            "Espirito",
            23,
            "INIMIGO 6"
        ));

        inimigos.add(new Inimigo(
            "Rato",
            6,
            "INIMIGO 7"
        ));

        inimigos.add(new Inimigo(
            "Lobo",
            12,
            "INIMIGO 8"
        ));

        inimigos.add(new Inimigo(
            "Rato",
            6,
            "INIMIGO 9"
        ));

        inimigos.add(new Inimigo(
            "Zumbi",
            15,
            "INIMIGO 10"
        ));

    }

    public static Inimigo gerarInimigoAleatorio() {
        return gerarInimigoAleatorio(random);
    }

    public static Inimigo gerarInimigoAleatorio(Random fonteAleatoria) {
        Inimigo modelo = inimigos.get(fonteAleatoria.nextInt(inimigos.size()));
        return modelo.copiar();
    }
}
