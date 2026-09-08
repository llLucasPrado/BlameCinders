package com.blamecinders.ui.carta;

import com.blamecinders.tabuleiro.CartaInfo;

public final class IdentificadorVisualCarta {

    public static final String VERSO = "VERSO";
    public static final String HEROI = "HERÓI-TESTE";

    private IdentificadorVisualCarta() {
    }

    public static String obter(CartaInfo carta) {
        if (carta == null) return VERSO;

        switch (carta.getTipo()) {
            case INIMIGO:
                return carta.getInimigo() == null
                    ? "INIMIGO"
                    : carta.getInimigo().getIdentificadorVisual();
            case BAU:
                return "BAÚ";
            case CHAMA:
                return "CHAMA";
            case PAREDE:
                return "PAREDE";
            case VAZIO:
                return VERSO;
            default:
                throw new IllegalStateException("Tipo de carta sem representação visual.");
        }
    }
}
