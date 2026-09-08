package com.blamecinders.tabuleiro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CartaInfoTest {

    @Test
    void rejeitaTipoNulo() {
        assertThrows(NullPointerException.class, () -> new CartaInfo(null));
    }

    @Test
    void rejeitaEstadoNulo() {
        CartaInfo carta = new CartaInfo(TipoCarta.BAU);

        assertThrows(NullPointerException.class, () -> carta.setEstado(null));
    }
}
