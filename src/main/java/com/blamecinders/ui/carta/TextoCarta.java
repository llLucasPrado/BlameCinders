package com.blamecinders.ui.carta;

import com.blamecinders.configuracao.BalanceamentoJogo;
import com.blamecinders.tabuleiro.CartaInfo;

public final class TextoCarta {

    private TextoCarta() {
    }

    public static String montar(CartaInfo carta) {
        if (carta == null) return "Carta desconhecida.";

        switch (carta.getTipo()) {
            case INIMIGO:
                return carta.getInimigo() == null
                    ? "Inimigo desconhecido."
                    : carta.getInimigo().getNome() + "\nVida: " + carta.getInimigo().getVida();
            case BAU:
                return montarBau(carta);
            case CHAMA:
                return "Chama\nColete " + BalanceamentoJogo.OBJETIVO_CHAMAS + " para vencer.";
            case PAREDE:
                return "Parede\nNão é possível atravessar.";
            case VAZIO:
                return "Carta vazia.";
            default:
                throw new IllegalStateException("Tipo de carta sem descrição.");
        }
    }

    private static String montarBau(CartaInfo carta) {
        if (!carta.isBauAberto()) {
            return "Baú fechado.\nAbra para descobrir o que há dentro.";
        }
        if (carta.getArmaDentro() != null) {
            return "Baú já aberto.\nArma: " + carta.getArmaDentro().getNome()
                + "\nDurabilidade: " + carta.getArmaDentro().getDurabilidade();
        }
        if (carta.getComidaDentro() != null) {
            return "Baú já aberto.\nComida: " + carta.getComidaDentro().getNome()
                + "\nCura: " + carta.getComidaDentro().getCura();
        }
        return "Baú já aberto e vazio.";
    }
}
