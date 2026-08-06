package com.blamecinders.animacao;

import com.badlogic.gdx.scenes.scene2d.Actor;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimacaoCartaScene2DTest {

    @Test
    void revelarExecutaTrocaNoMeioDoFlipERestauraEscala() {
        Actor carta = new Actor();
        carta.setSize(108f, 144f);
        AtomicBoolean conteudoRevelado = new AtomicBoolean();

        new AnimacaoCarta().aplicarFlip(carta, () -> conteudoRevelado.set(true));
        avancar(carta, 10, 0.05f);

        assertTrue(conteudoRevelado.get());
        assertEquals(1f, carta.getScaleX(), 0.001f);
        assertEquals(1f, carta.getScaleY(), 0.001f);
    }

    @Test
    void agirFechaCartaZoomEExecutaCallback() {
        Actor carta = new Actor();
        carta.setScale(1f);
        carta.getColor().a = 1f;
        AtomicBoolean acaoConcluida = new AtomicBoolean();

        new AnimacaoCarta().dissolverCartaZoom(carta, () -> acaoConcluida.set(true));
        avancar(carta, 10, 0.05f);

        assertTrue(acaoConcluida.get());
        assertEquals(0f, carta.getColor().a, 0.001f);
        assertEquals(0.8f, carta.getScaleX(), 0.001f);
    }

    private void avancar(Actor ator, int passos, float delta) {
        for (int passo = 0; passo < passos; passo++) {
            ator.act(delta);
        }
    }
}
