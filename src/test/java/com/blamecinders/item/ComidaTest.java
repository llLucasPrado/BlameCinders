package com.blamecinders.item;

import com.blamecinders.combate.Jogador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComidaTest {

    @Test
    void curaSemUltrapassarVidaMaxima() {
        Jogador jogador = new Jogador(50);
        jogador.setVida(30);
        Comida comida = new Comida("Frasco rubro", 18, "COMIDA: FRASCO");

        assertEquals(18, comida.consumir(jogador));
        assertEquals(48, jogador.getVida());
        assertEquals(2, comida.consumir(jogador));
        assertEquals(50, jogador.getVida());
        assertEquals(50, jogador.getVidaMaxima());
    }

    @Test
    void rejeitaDadosInvalidos() {
        assertThrows(NullPointerException.class, () -> new Comida(null, 8, "COMIDA"));
        assertThrows(NullPointerException.class, () -> new Comida("Ração", 8, null));
        assertThrows(IllegalArgumentException.class, () -> new Comida("Ração", 0, "COMIDA"));
    }

    @Test
    void rejeitaJogadorNuloAoConsumir() {
        Comida comida = new Comida("Ração", 8, "COMIDA");

        assertThrows(NullPointerException.class, () -> comida.consumir(null));
    }
}
