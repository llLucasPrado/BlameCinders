package com.blamecinders.combate;

import com.blamecinders.configuracao.BalanceamentoJogo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public final class CatalogoInimigos {

    private static final List<Inimigo> INIMIGOS = Arrays.asList(
        criar("Rato da cripta", 5, "INIMIGO 7", CategoriaInimigo.FRACO),
        criar("Rato voraz", 6, "INIMIGO 9", CategoriaInimigo.FRACO),
        criar(
            "Corvo da Vigília",
            7,
            "INIMIGO_CORVO_VIGIA",
            CategoriaInimigo.FRACO
        ),
        criar("Lobo faminto", 8, "INIMIGO 3", CategoriaInimigo.FRACO),
        criar("Cadáver vacilante", 8, "INIMIGO 10", CategoriaInimigo.FRACO),
        criar("Lobo doente", 10, "INIMIGO 8", CategoriaInimigo.MEDIO_BAIXO),
        criar("Zumbi decaído", 11, "INIMIGO 5", CategoriaInimigo.MEDIO_BAIXO),
        criar("Esqueleto rachado", 12, "INIMIGO 2", CategoriaInimigo.MEDIO_BAIXO),
        criar(
            "Rato Couraçado",
            14,
            "INIMIGO_RATO_COURACADO",
            CategoriaInimigo.MEDIO_ALTO
        ),
        criar("Esqueleto sentinela", 15, "INIMIGO 1", CategoriaInimigo.MEDIO_ALTO),
        criar("Aparição", 16, "INIMIGO 6", CategoriaInimigo.MEDIO_ALTO),
        criar(
            "Espírito Acorrentado",
            18,
            "INIMIGO_ESPIRITO_ACORRENTADO",
            CategoriaInimigo.MEDIO_DIFICIL
        ),
        criar("Espírito errante", 20, "INIMIGO 4", CategoriaInimigo.MEDIO_DIFICIL)
    );

    private static final Map<CategoriaInimigo, List<Inimigo>> INIMIGOS_POR_CATEGORIA =
        agruparPorCategoria();
    private static final Random ALEATORIO = new Random();

    private CatalogoInimigos() {
    }

    public static Inimigo gerarInimigoAleatorio() {
        return gerarInimigoAleatorio(ALEATORIO);
    }

    public static Inimigo gerarInimigoAleatorio(Random fonteAleatoria) {
        Objects.requireNonNull(fonteAleatoria, "fonteAleatoria");
        CategoriaInimigo categoria = sortearCategoria(
            fonteAleatoria.nextInt(BalanceamentoJogo.TOTAL_PERCENTUAL)
        );
        List<Inimigo> modelos = INIMIGOS_POR_CATEGORIA.get(categoria);
        Inimigo modelo = modelos.get(fonteAleatoria.nextInt(modelos.size()));
        return modelo.copiar();
    }

    public static List<Inimigo> listarModelos() {
        List<Inimigo> copias = new ArrayList<>();
        for (Inimigo inimigo : INIMIGOS) copias.add(inimigo.copiar());
        return Collections.unmodifiableList(copias);
    }

    private static Map<CategoriaInimigo, List<Inimigo>> agruparPorCategoria() {
        Map<CategoriaInimigo, List<Inimigo>> modelos = new EnumMap<>(CategoriaInimigo.class);
        for (CategoriaInimigo categoria : CategoriaInimigo.values()) {
            modelos.put(categoria, new ArrayList<>());
        }
        for (Inimigo inimigo : INIMIGOS) {
            modelos.get(inimigo.getCategoria()).add(inimigo);
        }
        return modelos;
    }

    private static CategoriaInimigo sortearCategoria(int sorteio) {
        int limiteFracos = BalanceamentoJogo.CHANCE_INIMIGO_FRACO;
        if (sorteio < limiteFracos) return CategoriaInimigo.FRACO;

        int limiteMedioBaixo = limiteFracos
            + BalanceamentoJogo.CHANCE_INIMIGO_MEDIO_BAIXO;
        if (sorteio < limiteMedioBaixo) return CategoriaInimigo.MEDIO_BAIXO;

        int limiteMedioAlto = limiteMedioBaixo
            + BalanceamentoJogo.CHANCE_INIMIGO_MEDIO_ALTO;
        if (sorteio < limiteMedioAlto) return CategoriaInimigo.MEDIO_ALTO;
        return CategoriaInimigo.MEDIO_DIFICIL;
    }

    private static Inimigo criar(
        String nome,
        int vida,
        String identificadorVisual,
        CategoriaInimigo categoria
    ) {
        return new Inimigo(
            nome,
            vida,
            identificadorVisual,
            categoria,
            Math.min(
                BalanceamentoJogo.DIFICULDADE_PADRAO_MAXIMA,
                BalanceamentoJogo.DIFICULDADE_BASE_FURTIVIDADE + vida
            )
        );
    }
}
