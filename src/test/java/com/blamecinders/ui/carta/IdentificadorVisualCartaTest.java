package com.blamecinders.ui.carta;

import com.blamecinders.combate.Inimigo;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.TipoCarta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdentificadorVisualCartaTest {

    @Test
    void usaVersoQuandoNaoExisteCarta() {
        assertEquals(IdentificadorVisualCarta.VERSO, IdentificadorVisualCarta.obter(null));
    }

    @Test
    void usaIdentificadorEspecificoDoInimigo() {
        CartaInfo carta = new CartaInfo(TipoCarta.INIMIGO);
        carta.setInimigo(new Inimigo("Lobo", 12, "INIMIGO 3"));

        assertEquals("INIMIGO 3", IdentificadorVisualCarta.obter(carta));
    }

    @Test
    void mapeiaTiposFixos() {
        assertEquals("BAÚ", IdentificadorVisualCarta.obter(new CartaInfo(TipoCarta.BAU)));
        assertEquals("CHAMA", IdentificadorVisualCarta.obter(new CartaInfo(TipoCarta.CHAMA)));
        assertEquals("PAREDE", IdentificadorVisualCarta.obter(new CartaInfo(TipoCarta.PAREDE)));
    }
}
