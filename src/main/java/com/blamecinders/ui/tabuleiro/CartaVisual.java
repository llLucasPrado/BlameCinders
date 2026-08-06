package com.blamecinders.ui.tabuleiro;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.blamecinders.BlameCindersGame;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.util.GerenciadorTexturas;

/** Ator interativo que representa uma célula do tabuleiro. */
public class CartaVisual extends CartaExibida {

    public static final float LARGURA = 108;
    public static final float ALTURA = 144;

    private Texture frente;
    private final Texture verso;
    private String nomeFrente;
    private final String nomeVerso;
    private int linha;
    private int coluna;
    private boolean revelada;
    private boolean bloqueandoAnimacaoClique;

    public CartaVisual(
        String nomeFrente,
        String nomeVerso,
        float x,
        float y,
        int linha,
        int coluna,
        BlameCindersGame jogo,
        BitmapFont fonte
    ) {
        super(GerenciadorTexturas.get(nomeVerso), nomeVerso, fonte);
        this.nomeFrente = nomeFrente;
        this.nomeVerso = nomeVerso;
        this.frente = GerenciadorTexturas.get(nomeFrente);
        this.verso = GerenciadorTexturas.get(nomeVerso);
        this.linha = linha;
        this.coluna = coluna;

        setBounds(x, y, LARGURA, ALTURA);
        setOrigin(Align.center);

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (jogo.isFinalizado()
                    || jogo.isAnimandoTabuleiro()
                    || jogo.isTelaModalAberta()
                    || bloqueandoAnimacaoClique) {
                    return;
                }

                clearActions();
                addAction(Actions.scaleTo(1.1f, 1.1f, 0.12f, Interpolation.fade));
                toFront();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (jogo.isAnimandoTabuleiro()
                    || jogo.isTelaModalAberta()
                    || bloqueandoAnimacaoClique) {
                    return;
                }

                clearActions();
                addAction(Actions.scaleTo(1f, 1f, 0.12f, Interpolation.fade));
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (jogo.isFinalizado()
                    || jogo.isAnimandoTabuleiro()
                    || jogo.isTelaModalAberta()
                    || bloqueandoAnimacaoClique) {
                    return;
                }

                jogo.clicarCarta(CartaVisual.this.linha, CartaVisual.this.coluna);
            }
        });
    }

    public void setPosicaoGrid(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public void setRevelada(boolean revelada) {
        this.revelada = revelada;
        atualizarFace();
    }

    public void setFrente(String nome) {
        nomeFrente = nome;
        frente = GerenciadorTexturas.get(nome);
        atualizarFace();
    }

    public Texture getTexturaAtual() {
        return revelada ? frente : verso;
    }

    public void setBloqueandoAnimacaoClique(boolean bloqueando) {
        bloqueandoAnimacaoClique = bloqueando;
    }

    public Texture getTexturaVerso() {
        return verso;
    }

    private void atualizarFace() {
        setConteudo(
            revelada ? frente : verso,
            revelada ? nomeFrente : nomeVerso
        );
    }
}
