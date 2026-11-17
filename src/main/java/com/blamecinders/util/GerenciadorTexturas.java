package com.blamecinders.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class GerenciadorTexturas {

    private static final int LARGURA_MAXIMA_CARTA = 600;
    private static final int ALTURA_MAXIMA_CARTA = 800;
    private static final HashMap<String, Texture> cache = new HashMap<>();
    private static final HashMap<String, Boolean> cacheDisponibilidade = new HashMap<>();

    private GerenciadorTexturas() {
    }

    public static Texture get(String identificador) {

        if (!cache.containsKey(identificador)) {
            Texture tex = carregarImagemCarta(identificador);
            boolean possuiMipmaps = tex != null;
            if (tex == null) {
                tex = criarFundoCarta(identificador);
            }
            tex.setFilter(
                possuiMipmaps
                    ? Texture.TextureFilter.MipMapLinearLinear
                    : Texture.TextureFilter.Linear,
                Texture.TextureFilter.Linear
            );
            cache.put(identificador, tex);
        }

        return cache.get(identificador);

    }

    public static void disposeAll() {
        for (Texture tex : cache.values()) {
            tex.dispose();
        }
        cache.clear();
        cacheDisponibilidade.clear();
    }

    public static Texture getSolid(Color cor) {
        Objects.requireNonNull(cor, "cor");
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

    public static Texture getArquivo(String caminho) {
        Objects.requireNonNull(caminho, "caminho");
        String chave = "ARQUIVO:" + caminho;
        Texture existente = cache.get(chave);
        if (existente != null) return existente;

        if (Gdx.files == null || !Gdx.files.internal(caminho).exists()) {
            throw new IllegalArgumentException("Imagem não encontrada: " + caminho);
        }

        Texture textura = new Texture(Gdx.files.internal(caminho), true);
        textura.setFilter(
            Texture.TextureFilter.MipMapLinearLinear,
            Texture.TextureFilter.Linear
        );
        cache.put(chave, textura);
        return textura;
    }

    public static boolean possuiImagem(String identificador) {
        Boolean resultadoEmCache = cacheDisponibilidade.get(identificador);
        if (resultadoEmCache != null) return resultadoEmCache;

        String caminho = obterCaminhoImagem(identificador);
        boolean possuiImagem = caminho != null
            && Gdx.files != null
            && Gdx.files.internal(caminho).exists();
        cacheDisponibilidade.put(identificador, possuiImagem);
        return possuiImagem;
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

    private static Texture carregarImagemCarta(String identificador) {
        String caminho = obterCaminhoImagem(identificador);
        if (caminho == null || Gdx.files == null) return null;

        FileHandle arquivo = Gdx.files.internal(caminho);
        if (!arquivo.exists()) return null;

        Pixmap original = new Pixmap(arquivo);
        Pixmap texturaPixmap = original;
        try {
            float escala = Math.min(
                1f,
                Math.min(
                    (float) LARGURA_MAXIMA_CARTA / original.getWidth(),
                    (float) ALTURA_MAXIMA_CARTA / original.getHeight()
                )
            );

            if (escala < 1f) {
                int largura = Math.max(1, Math.round(original.getWidth() * escala));
                int altura = Math.max(1, Math.round(original.getHeight() * escala));
                texturaPixmap = new Pixmap(largura, altura, Pixmap.Format.RGBA8888);
                texturaPixmap.setFilter(Pixmap.Filter.BiLinear);
                texturaPixmap.drawPixmap(
                    original,
                    0, 0, original.getWidth(), original.getHeight(),
                    0, 0, largura, altura
                );
            }

            return new Texture(texturaPixmap, true);
        } finally {
            if (texturaPixmap != original) texturaPixmap.dispose();
            original.dispose();
        }
    }

    private static String obterCaminhoImagem(String identificador) {
        String id = identificador == null ? "" : identificador.toUpperCase(Locale.ROOT);

        if (id.contains("VERSO")) return "Cartas/Versos/versoTeste.jpg";
        if (id.startsWith("PERSONAGEM_")) {
            return "Cartas/Frente/Jogador/"
                + id.substring("PERSONAGEM_".length()).toLowerCase(Locale.ROOT) + ".png";
        }
        if (id.startsWith("HER")) return "Cartas/Frente/Jogador/jogador-teste.png";
        if (id.startsWith("BA")) return "Cartas/Frente/Bau/frenteTeste7.jpg";
        if (id.contains("CHAMA")) return "Cartas/Frente/Chama/frenteTeste4.jpg";
        if (id.contains("PAREDE")) return "Cartas/Frente/Parede/paredeTeste1.png";
        if (id.contains("CLAYMORE")) return "Cartas/Frente/Armas/claymore.png";
        if (id.contains("PUNHAL") || id.contains("ESPADA_CURTA")) {
            return "Cartas/Frente/Armas/espada-curta.png";
        }

        String caminhoInimigo = resolverCaminhoInimigo(id);
        if (caminhoInimigo != null) return caminhoInimigo;
        return null;
    }

    private static String resolverCaminhoInimigo(String identificador) {
        switch (identificador) {
            case "INIMIGO 1":
                return "Cartas/Frente/Inimigo/esqueleto-sentinela.png";
            case "INIMIGO 2":
                return "Cartas/Frente/Inimigo/esqueleto-rachado.png";
            case "INIMIGO 3":
                return "Cartas/Frente/Inimigo/lobo-faminto.png";
            case "INIMIGO 4":
                return "Cartas/Frente/Inimigo/espirito-errante.png";
            case "INIMIGO 5":
                return "Cartas/Frente/Inimigo/zumbi-decaido.png";
            case "INIMIGO 6":
                return "Cartas/Frente/Inimigo/aparicao.png";
            case "INIMIGO 7":
                return "Cartas/Frente/Inimigo/rato-da-cripta.png";
            case "INIMIGO 8":
                return "Cartas/Frente/Inimigo/lobo-doente.png";
            case "INIMIGO 9":
                return "Cartas/Frente/Inimigo/rato-voraz.png";
            case "INIMIGO 10":
                return "Cartas/Frente/Inimigo/cadaver-vacilante.png";
            case "INIMIGO_CORVO_VIGIA":
                return "Cartas/Frente/Inimigo/corvo-vigia.png";
            case "INIMIGO_RATO_COURACADO":
                return "Cartas/Frente/Inimigo/rato-couracado.png";
            case "INIMIGO_ESPIRITO_ACORRENTADO":
                return "Cartas/Frente/Inimigo/espirito-acorrentado.png";
            default:
                return null;
        }
    }

    private static Color escolherCor(String identificador) {
        String id = identificador == null ? "" : identificador.toUpperCase(Locale.ROOT);
        if (id.contains("VERSO")) return new Color(0.10f, 0.08f, 0.14f, 1f);
        if (id.contains("HERÓI") || id.contains("HEROI")) return new Color(0.24f, 0.10f, 0.16f, 1f);
        if (id.contains("INIMIGO")) return new Color(0.30f, 0.07f, 0.07f, 1f);
        if (id.contains("CHAMA")) return new Color(0.48f, 0.17f, 0.03f, 1f);
        if (id.contains("BAÚ") || id.contains("BAU")) return new Color(0.30f, 0.20f, 0.07f, 1f);
        if (id.contains("PAREDE")) return new Color(0.18f, 0.19f, 0.22f, 1f);
        if (id.contains("COMIDA")) return new Color(0.10f, 0.28f, 0.14f, 1f);
        if (
            id.contains("ARMA")
                || id.contains("CLAYMORE")
                || id.contains("PUNHAL")
                || id.contains("ESPADA_CURTA")
        ) {
            return new Color(0.10f, 0.18f, 0.28f, 1f);
        }
        return new Color(0.20f, 0.12f, 0.24f, 1f);
    }
}
