package com.blamecinders.ui.carta;

import com.blamecinders.combate.Inimigo;
import com.blamecinders.item.Comida;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.TipoCarta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextoCartaTest {

    @Test
    void descreveInimigoComNomeEVida() {
        CartaInfo carta = new CartaInfo(TipoCarta.INIMIGO);
        carta.setInimigo(new Inimigo("Lobo", 12, "INIMIGO 3"));

        assertEquals("Lobo\nVida: 12", TextoCarta.montar(carta));
    }

    @Test
    void bauFechadoNaoRevelaItem() {
        CartaInfo carta = new CartaInfo(TipoCarta.BAU);
        carta.setItemDentro(new Comida("Sopa", 12, "COMIDA: SOPA"));

        assertEquals(
            "Baú fechado.\nAbra para descobrir o que há dentro.",
            TextoCarta.montar(carta)
        );
    }

    @Test
    void bauAbertoDescreveItem() {
        CartaInfo carta = new CartaInfo(TipoCarta.BAU);
        carta.setItemDentro(new Comida("Sopa", 12, "COMIDA: SOPA"));
        carta.registrarAberturaBau();

        assertEquals("Baú já aberto.\nComida: Sopa\nCura: 12", TextoCarta.montar(carta));
    }
}
