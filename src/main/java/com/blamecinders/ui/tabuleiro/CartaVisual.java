package com.blamecinders.ui.tabuleiro;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;
import java.util.function.Function;

/** Ator interativo que representa uma célula do tabuleiro. */
public class CartaVisual extends CartaExibida {

    public static final float LARGURA = 108;
    public static final float ALTURA = 144;

    private Drawable frente;
    private final Drawable verso;
    private final Function<String, Drawable> provedorFundos;
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
        InteracaoCartaVisual interacao,
        BitmapFont fonte
    ) {
        this(
            nomeFrente,
            nomeVerso,
            x,
            y,
            linha,
            coluna,
            interacao,
            fonte,
            CartaVisual::criarFundo
        );
    }

    public CartaVisual(
        String nomeFrente,
        String nomeVerso,
        float x,
        float y,
        int linha,
        int coluna,
        InteracaoCartaVisual interacao,
        BitmapFont fonte,
        Function<String, Drawable> provedorFundos
    ) {
        super(obterFundo(provedorFundos, nomeVerso), nomeVerso, fonte);
        this.nomeFrente = nomeFrente;
        this.nomeVerso = nomeVerso;
        this.provedorFundos = Objects.requireNonNull(provedorFundos, "provedorFundos");
        this.verso = getImagem().getDrawable();
        this.linha = linha;
        this.coluna = coluna;

        setBounds(x, y, LARGURA, ALTURA);
        setOrigin(Align.center);

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (interacao.estaBloqueada() || bloqueandoAnimacaoClique) {
                    return;
                }

                clearActions();
                addAction(Actions.scaleTo(1.1f, 1.1f, 0.12f, Interpolation.fade));
                toFront();
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (interacao.estaBloqueada() || bloqueandoAnimacaoClique) {
                    return;
                }

                clearActions();
                addAction(Actions.scaleTo(1f, 1f, 0.12f, Interpolation.fade));
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (interacao.estaBloqueada() || bloqueandoAnimacaoClique) {
                    return;
                }

                interacao.aoClicar(CartaVisual.this.linha, CartaVisual.this.coluna);
            }
        });

        atualizarFace();
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
        if (!Objects.equals(nomeFrente, nome)) {
            frente = null;
        }
        nomeFrente = nome;
        atualizarFace();
    }

    public void atualizarEstadoVisual(String nome, boolean revelada) {
        if (!Objects.equals(nomeFrente, nome)) {
            frente = null;
        }
        nomeFrente = nome;
        this.revelada = revelada;
        atualizarFace();
    }

    public Drawable getFundoAtual() {
        return revelada ? obterFrente() : verso;
    }

    public void setBloqueandoAnimacaoClique(boolean bloqueando) {
        bloqueandoAnimacaoClique = bloqueando;
    }

    public Drawable getFundoVerso() {
        return verso;
    }

    private void atualizarFace() {
        String identificador = revelada ? nomeFrente : nomeVerso;
        setConteudo(
            revelada ? obterFrente() : verso,
            identificador
        );
        setRotuloVisivel(!GerenciadorTexturas.possuiImagem(identificador));
    }

    private Drawable obterFrente() {
        if (frente == null) {
            frente = obterFundo(provedorFundos, nomeFrente);
        }
        return frente;
    }

    private static Drawable criarFundo(String identificador) {
        return new TextureRegionDrawable(new TextureRegion(GerenciadorTexturas.get(identificador)));
    }

    private static Drawable obterFundo(
        Function<String, Drawable> provedorFundos,
        String identificador
    ) {
        Drawable fundo = Objects.requireNonNull(provedorFundos, "provedorFundos")
            .apply(identificador);
        return Objects.requireNonNull(fundo, "Fundo não encontrado: " + identificador);
    }
}
