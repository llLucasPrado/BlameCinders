package com.blamecinders.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Locale;

public class GerenciadorTexturas {


    private static final HashMap<String, Texture> cache = new HashMap<>();

    public static Texture get(String identificador) {

        if (!cache.containsKey(identificador)) {
            Texture tex = criarFundoCarta(identificador);
            tex.setFilter(Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear);
            cache.put(identificador, tex);
        }

        return cache.get(identificador);

    }

    public static void disposeAll() {
        for (Texture tex : cache.values()) {
            tex.dispose();
        }
        cache.clear();
    }

    public static Texture getSolid(Color cor) {
        String chave = String.format(
            Locale.ROOT,
            "SOLID:%.3f:%.3f:%.3f:%.3f",
            cor.r, cor.g, cor.b, cor.a
        );
        Texture existente = cache.get(chave);
        if (existente != null) return existente;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(cor);
        pixmap.fill();
        Texture textura = new Texture(pixmap);
        pixmap.dispose();
        cache.put(chave, textura);
        return textura;
    }

    private static Texture criarFundoCarta(String identificador) {
        final int largura = 108;
        final int altura = 144;
        Color fundo = escolherCor(identificador);

        Pixmap pixmap = new Pixmap(largura, altura, Pixmap.Format.RGBA8888);
        pixmap.setColor(fundo);
        pixmap.fill();

        pixmap.setColor(new Color(0.72f, 0.59f, 0.34f, 1f));
        for (int margem = 0; margem < 4; margem++) {
            pixmap.drawRectangle(margem, margem, largura - margem * 2, altura - margem * 2);
        }

        pixmap.setColor(new Color(0.05f, 0.04f, 0.06f, 0.75f));
        pixmap.fillRectangle(8, altura / 2 - 22, largura - 16, 44);

        Texture textura = new Texture(pixmap);
        pixmap.dispose();
        return textura;
    }

    private static Color escolherCor(String identificador) {
        String id = identificador == null ? "" : identificador.toUpperCase();
        if (id.contains("VERSO")) return new Color(0.10f, 0.08f, 0.14f, 1f);
        if (id.contains("HERÓI") || id.contains("HEROI")) return new Color(0.24f, 0.10f, 0.16f, 1f);
        if (id.contains("INIMIGO")) return new Color(0.30f, 0.07f, 0.07f, 1f);
        if (id.contains("CHAMA")) return new Color(0.48f, 0.17f, 0.03f, 1f);
        if (id.contains("BAÚ") || id.contains("BAU")) return new Color(0.30f, 0.20f, 0.07f, 1f);
        if (id.contains("PAREDE")) return new Color(0.18f, 0.19f, 0.22f, 1f);
        if (id.contains("COMIDA")) return new Color(0.10f, 0.28f, 0.14f, 1f);
        if (id.contains("ARMA") || id.contains("CLAYMORE") || id.contains("PUNHAL")) {
            return new Color(0.10f, 0.18f, 0.28f, 1f);
        }
        return new Color(0.20f, 0.12f, 0.24f, 1f);
    }
}
