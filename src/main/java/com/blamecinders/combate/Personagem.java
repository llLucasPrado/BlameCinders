package com.blamecinders.combate;

import com.blamecinders.configuracao.BalanceamentoJogo;

import java.util.Locale;

public enum Personagem {
    VIEYRA(
        "Arqueira",
        "Ágil e precisa, prefere manter distância.",
        BalanceamentoJogo.VIDA_ARQUEIRA,
        BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE_ARQUEIRA
    ),
    TAIGA(
        "Assassino",
        "Frágil, porém furtivo e mortal nas sombras.",
        BalanceamentoJogo.VIDA_ASSASSINO,
        BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE_ASSASSINO
    ),
    NAZARET(
        "Anão",
        "Robusto e incansável, feito para resistir.",
        BalanceamentoJogo.VIDA_ANAO,
        0
    ),
    AQUILLA(
        "Cavaleiro",
        "Disciplinado e versátil, pronto para o perigo.",
        BalanceamentoJogo.VIDA_CAVALEIRO,
        BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE_CAVALEIRO
    );

    public static final Personagem PADRAO = AQUILLA;

    private static final String PREFIXO_PERSONAGEM = "PERSONAGEM_";

    private final String arquetipo;
    private final String descricao;
    private final int vidaInicial;
    private final int chanceBaseFurtividade;

    Personagem(
        String arquetipo,
        String descricao,
        int vidaInicial,
        int chanceBaseFurtividade
    ) {
        this.arquetipo = arquetipo;
        this.descricao = descricao;
        this.vidaInicial = vidaInicial;
        this.chanceBaseFurtividade = chanceBaseFurtividade;
    }

    public String getArquetipo() {
        return arquetipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getVidaInicial() {
        return vidaInicial;
    }

    public boolean podeUsarFurtividade() {
        return chanceBaseFurtividade > 0;
    }

    public int getChanceBaseFurtividade() {
        return chanceBaseFurtividade;
    }

    public String getIdentificadorVisual() {
        return PREFIXO_PERSONAGEM + name();
    }

    public static Personagem porId(String id) {
        if (id == null || id.trim().isEmpty()) return PADRAO;

        String normalizado = id.trim().toUpperCase(Locale.ROOT);
        while (normalizado.startsWith(PREFIXO_PERSONAGEM)) {
            normalizado = normalizado.substring(PREFIXO_PERSONAGEM.length());
        }

        for (Personagem personagem : values()) {
            if (personagem.name().equals(normalizado)) return personagem;
        }
        return PADRAO;
    }
}
