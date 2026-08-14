package com.blamecinders.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import java.util.Locale;

public class GerenciadorTexturas {


    private static final HashMap<String, Texture> cache = new HashMap<>();

    public static Texture get(String identificador) {

        if (!cache.containsKey(identificador)) {
            Texture tex = carregarImagemCarta(identificador);
            if (tex == null) {
                tex = criarFundoCarta(identificador);
            }
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

    /** Carrega a imagem correspondente em assets/Cartas, quando disponÃ­vel. */
    private static Texture carregarImagemCarta(String identificador) {
        String caminho = obterCaminhoImagem(identificador);
        if (caminho == null || Gdx.files == null) return null;

        FileHandle arquivo = Gdx.files.internal(caminho);
        return arquivo.exists() ? new Texture(arquivo) : null;
    }

    private static String obterCaminhoImagem(String identificador) {
        String id = identificador == null ? "" : identificador.toUpperCase(Locale.ROOT);

        if (id.contains("VERSO")) return "Cartas/Versos/versoTeste.jpg";
        if (id.startsWith("HER")) return "Cartas/Frente/Jogador/jogadorTeste.png";
        if (id.startsWith("BA")) return "Cartas/Frente/Bau/frenteTeste7.jpg";
        if (id.contains("HERÃ“I") || id.contains("HEROI")) {
            return "Cartas/Frente/Jogador/jogadorTeste.png";
        }
        if (id.contains("BAÃš") || id.contains("BAU")) {
            return "Cartas/Frente/Bau/frenteTeste7.jpg";
        }
        if (id.contains("CHAMA")) return "Cartas/Frente/Chama/frenteTeste4.jpg";
        if (id.contains("PAREDE")) return "Cartas/Frente/Parede/paredeTeste1.png";
        if (id.contains("CLAYMORE")) return "Cartas/Frente/Armas/claymore.jpeg";
        if (id.contains("PUNHAL")) return "Cartas/Frente/Armas/punhal.jpeg";

        if (id.startsWith("INIMIGO")) {
            return "Cartas/Frente/Inimigo/frenteTeste"
                + extrairNumeroInimigo(id) + ".jpg";
        }
        return null;
    }

    private static int extrairNumeroInimigo(String identificador) {
        String somenteDigitos = identificador.replaceAll("\\D+", "");
        if (somenteDigitos.isEmpty()) return 0;

        switch (Integer.parseInt(somenteDigitos)) {
            case 1: return 0;
            case 2: return 1;
            case 3: return 2;
            case 4: return 3;
            case 5: return 5;
            case 6: return 6;
            case 7: return 8;
            case 8: return 9;
            case 9: return 10;
            case 10: return 11;
            default: return 0;
        }
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
