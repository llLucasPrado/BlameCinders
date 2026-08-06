package com.blamecinders.animacao;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.blamecinders.suporte.FonteTesteScene2D;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.ui.tabuleiro.CartaVisual;
import com.blamecinders.ui.tabuleiro.InteracaoCartaVisual;
import com.blamecinders.util.ProvedorPosicaoCarta;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnimacaoTabuleiroScene2DTest {

    private static final float PASSO_X = CartaVisual.LARGURA + 8f;
    private static final float PASSO_Y = CartaVisual.ALTURA + 8f;

    @Test
    void moverHeroiAnimaEsteiraEConcluiCallback() {
        BitmapFont fonte = FonteTesteScene2D.criar();
        CartaVisual[][] cartas = criarGrid(fonte);
        List<Actor> temporarios = new ArrayList<>();
        AtomicBoolean concluida = new AtomicBoolean();
        AnimacaoTabuleiro animacao = new AnimacaoTabuleiro(
            temporarios::add,
            cartas,
            CartaVisual.LARGURA,
            CartaVisual.ALTURA,
            8f,
            provedorPosicoes()
        );

        CartaVisual heroi = cartas[0][2];
        CartaVisual cartaDaEsteira = cartas[0][1];
        animacao.animarMovimentoJogadorComEsteira(0, 2, 0, 3, () -> concluida.set(true));

        assertFalse(concluida.get());
        avancar(cartas, 30, 0.05f);

        assertTrue(concluida.get());
        assertEquals(PASSO_X * 3f, heroi.getX(), 0.01f);
        assertEquals(PASSO_X * 2f, cartaDaEsteira.getX(), 0.01f);
        assertEquals(1, temporarios.size());
    }

    private CartaVisual[][] criarGrid(BitmapFont fonte) {
        CartaVisual[][] cartas = new CartaVisual[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        InteracaoCartaVisual interacao = new InteracaoCartaVisual() {
            @Override
            public boolean estaBloqueada() {
                return false;
            }

            @Override
            public void aoClicar(int linha, int coluna) {
            }
        };

        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                cartas[linha][coluna] = new CartaVisual(
                    "CARTA",
                    "VERSO",
                    coluna * PASSO_X,
                    (Tabuleiro.LINHAS - 1 - linha) * PASSO_Y,
                    linha,
                    coluna,
                    interacao,
                    fonte,
                    identificador -> new BaseDrawable()
                );
            }
        }
        return cartas;
    }

    private ProvedorPosicaoCarta provedorPosicoes() {
        return new ProvedorPosicaoCarta() {
            @Override
            public float getCartaX(int coluna) {
                return coluna * PASSO_X;
            }

            @Override
            public float getCartaY(int linha) {
                return (Tabuleiro.LINHAS - 1 - linha) * PASSO_Y;
            }
        };
    }

    private void avancar(CartaVisual[][] cartas, int passos, float delta) {
        for (int passo = 0; passo < passos; passo++) {
            for (CartaVisual[] linha : cartas) {
                for (CartaVisual carta : linha) {
                    carta.act(delta);
                }
            }
        }
    }
}
