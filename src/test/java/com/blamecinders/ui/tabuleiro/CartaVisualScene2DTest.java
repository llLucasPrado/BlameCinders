package com.blamecinders.ui.tabuleiro;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.blamecinders.suporte.FonteTesteScene2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartaVisualScene2DTest {

    @Test
    void revelarTrocaVersoPeloNomeDaCarta() {
        BitmapFont fonte = FonteTesteScene2D.criar();
        InteracaoFalsa interacao = new InteracaoFalsa();
        CartaVisual carta = criarCarta(fonte, interacao);

        assertEquals("VERSO", carta.getTexto());

        carta.setRevelada(true);

        assertEquals("CHAMA", carta.getTexto());
    }

    @Test
    void agirEncaminhaPosicaoAtualEObedeceBloqueio() {
        BitmapFont fonte = FonteTesteScene2D.criar();
        InteracaoFalsa interacao = new InteracaoFalsa();
        CartaVisual carta = criarCarta(fonte, interacao);
        ClickListener clique = (ClickListener) carta.getListeners().first();

        carta.setPosicaoGrid(2, 3);
        clique.clicked(new InputEvent(), 20f, 20f);

        assertEquals(2, interacao.linhaClicada);
        assertEquals(3, interacao.colunaClicada);

        interacao.bloqueada = true;
        carta.setPosicaoGrid(1, 1);
        clique.clicked(new InputEvent(), 20f, 20f);

        assertEquals(2, interacao.linhaClicada);
        assertEquals(3, interacao.colunaClicada);
    }

    @Test
    void carregaFrenteSomenteQuandoCartaForRevelada() {
        BitmapFont fonte = FonteTesteScene2D.criar();
        List<String> fundosSolicitados = new ArrayList<>();
        CartaVisual carta = new CartaVisual(
            "CHAMA",
            "VERSO",
            0f,
            0f,
            0,
            1,
            new InteracaoFalsa(),
            fonte,
            identificador -> {
                fundosSolicitados.add(identificador);
                return new BaseDrawable();
            }
        );

        assertEquals(Arrays.asList("VERSO"), fundosSolicitados);

        carta.setRevelada(true);

        assertEquals(Arrays.asList("VERSO", "CHAMA"), fundosSolicitados);
    }

    @Test
    void sincronizacaoFechadaTrocaIdentificadorSemCarregarFrente() {
        BitmapFont fonte = FonteTesteScene2D.criar();
        List<String> fundosSolicitados = new ArrayList<>();
        CartaVisual carta = new CartaVisual(
            "CHAMA",
            "VERSO",
            0f,
            0f,
            0,
            1,
            new InteracaoFalsa(),
            fonte,
            identificador -> {
                fundosSolicitados.add(identificador);
                return new BaseDrawable();
            }
        );

        carta.atualizarEstadoVisual("INIMIGO 1", false);
        assertEquals(Arrays.asList("VERSO"), fundosSolicitados);

        carta.atualizarEstadoVisual("INIMIGO 1", true);
        assertEquals(Arrays.asList("VERSO", "INIMIGO 1"), fundosSolicitados);
    }

    private CartaVisual criarCarta(BitmapFont fonte, InteracaoCartaVisual interacao) {
        return new CartaVisual(
            "CHAMA",
            "VERSO",
            0f,
            0f,
            0,
            1,
            interacao,
            fonte,
            identificador -> new BaseDrawable()
        );
    }

    private static final class InteracaoFalsa implements InteracaoCartaVisual {
        private boolean bloqueada;
        private int linhaClicada = -1;
        private int colunaClicada = -1;

        @Override
        public boolean estaBloqueada() {
            return bloqueada;
        }

        @Override
        public void aoClicar(int linha, int coluna) {
            linhaClicada = linha;
            colunaClicada = coluna;
        }
    }
}
