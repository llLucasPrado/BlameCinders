package com.blamecinders.ui.tabuleiro;

import com.blamecinders.util.ProvedorPosicaoCarta;

/** Calcula posições centralizadas do grid sem depender do Scene2D. */
public final class LayoutTabuleiro implements ProvedorPosicaoCarta {

    private final int linhas;
    private final int colunas;
    private final float larguraCarta;
    private final float alturaCarta;
    private final float espaco;
    private float larguraMundo;
    private float alturaMundo;

    public LayoutTabuleiro(
        int linhas,
        int colunas,
        float larguraCarta,
        float alturaCarta,
        float espaco,
        float larguraMundo,
        float alturaMundo
    ) {
        if (linhas <= 0 || colunas <= 0) {
            throw new IllegalArgumentException("O grid precisa ter linhas e colunas.");
        }
        if (larguraCarta <= 0f || alturaCarta <= 0f || espaco < 0f) {
            throw new IllegalArgumentException("Dimensões visuais inválidas.");
        }

        this.linhas = linhas;
        this.colunas = colunas;
        this.larguraCarta = larguraCarta;
        this.alturaCarta = alturaCarta;
        this.espaco = espaco;
        atualizarMundo(larguraMundo, alturaMundo);
    }

    public void atualizarMundo(float larguraMundo, float alturaMundo) {
        if (larguraMundo <= 0f || alturaMundo <= 0f) {
            throw new IllegalArgumentException("O mundo visual precisa ter dimensões positivas.");
        }
        this.larguraMundo = larguraMundo;
        this.alturaMundo = alturaMundo;
    }

    @Override
    public float getCartaX(int coluna) {
        validarIndice(coluna, colunas, "coluna");
        float larguraTotal = colunas * larguraCarta + (colunas - 1) * espaco;
        return (larguraMundo - larguraTotal) / 2f + coluna * (larguraCarta + espaco);
    }

    @Override
    public float getCartaY(int linha) {
        validarIndice(linha, linhas, "linha");
        float alturaTotal = linhas * alturaCarta + (linhas - 1) * espaco;
        return (alturaMundo - alturaTotal) / 2f
            + (linhas - 1 - linha) * (alturaCarta + espaco);
    }

    private static void validarIndice(int indice, int limite, String nome) {
        if (indice < 0 || indice >= limite) {
            throw new IndexOutOfBoundsException(nome + " fora do grid: " + indice);
        }
    }
}
