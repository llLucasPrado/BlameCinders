package com.blamecinders.ui.tabuleiro;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LayoutTabuleiroTest {

    @Test
    void centralizaGridEInverteEixoVertical() {
        LayoutTabuleiro layout = new LayoutTabuleiro(4, 5, 108f, 144f, 8f, 1280f, 720f);

        assertEquals(354f, layout.getCartaX(0));
        assertEquals(818f, layout.getCartaX(4));
        assertEquals(516f, layout.getCartaY(0));
        assertEquals(60f, layout.getCartaY(3));
    }

    @Test
    void recalculaPosicoesQuandoMundoMuda() {
        LayoutTabuleiro layout = new LayoutTabuleiro(1, 1, 100f, 120f, 0f, 800f, 600f);

        assertEquals(350f, layout.getCartaX(0));
        assertEquals(240f, layout.getCartaY(0));

        layout.atualizarMundo(1000f, 800f);

        assertEquals(450f, layout.getCartaX(0));
        assertEquals(340f, layout.getCartaY(0));
    }

    @Test
    void rejeitaDimensoesEIndicesInvalidos() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new LayoutTabuleiro(0, 5, 108f, 144f, 8f, 1280f, 720f)
        );

        LayoutTabuleiro layout = new LayoutTabuleiro(4, 5, 108f, 144f, 8f, 1280f, 720f);
        assertThrows(IndexOutOfBoundsException.class, () -> layout.getCartaX(5));
        assertThrows(IndexOutOfBoundsException.class, () -> layout.getCartaY(-1));
    }
}
