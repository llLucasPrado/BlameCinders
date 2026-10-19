package com.blamecinders.telas;

import java.util.Objects;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.combate.Personagem;
import com.blamecinders.configuracao.BalanceamentoJogo;
import com.blamecinders.util.GerenciadorTexturas;

public final class TelaEscolhaPersonagem implements Tela {

    private static final float LARGURA_MUNDO = 1280f;
    private static final float ALTURA_MUNDO = 720f;

    private final Consumer<Personagem> aoEscolher;
    private final AcaoTela aoVoltar;
    private final GerenciadorAudio audio;
    private final Skin skin;

    private Stage stage;
    private Label[] opcoes;
    private Label descricao;
    private Image retrato;
    private int selecionado;
    private boolean acaoEmExecucao;

    public TelaEscolhaPersonagem(
        Consumer<Personagem> aoEscolher,
        AcaoTela aoVoltar,
        GerenciadorAudio audio,
        Skin skin
    ) {
        this.aoEscolher = Objects.requireNonNull(aoEscolher, "aoEscolher");
        this.aoVoltar = Objects.requireNonNull(aoVoltar, "aoVoltar");
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(LARGURA_MUNDO, ALTURA_MUNDO));

        Label titulo = criarTexto("Escolha seu personagem", 2f, 560f, 100f);
        stage.addActor(titulo);

        Personagem[] personagens = Personagem.values();
        opcoes = new Label[personagens.length];
        for (int i = 0; i < personagens.length; i++) {
            opcoes[i] = criarOpcao(personagens[i].getNome(), 440f - i * 60f, i);
            stage.addActor(opcoes[i]);
        }

        descricao = criarTexto("", 1f, 130f, 80f);
        Label dica = criarTexto("ENTER confirma  -  ESC volta", 1f, 50f, 40f);
        dica.setColor(Color.LIGHT_GRAY);
        stage.addActor(descricao);
        stage.addActor(dica);

        retrato = new Image();
        retrato.setScaling(Scaling.fit);
        retrato.setSize(240f, 320f);
        retrato.setPosition(LARGURA_MUNDO - 340f, 200f);
        stage.addActor(retrato);

        atualizarSelecao();

        retrato = new Image();
        retrato.setSize(240f, 320f);
        retrato.setPosition(LARGURA_MUNDO - 340f, 200f);
        stage.addActor(retrato);

        atualizarSelecao();
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (acaoEmExecucao) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selecionar((selecionado + 1) % opcoes.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selecionar((selecionado - 1 + opcoes.length) % opcoes.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            confirmar();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            acaoEmExecucao = true;
            aoVoltar.executar();
        }
    }

    @Override
    public void redimensionar(int largura, int altura) {
        if (stage != null) stage.getViewport().update(largura, altura, true);
    }

    @Override
    public void esconder() {
        if (stage != null) stage.getRoot().setVisible(false);
    }

    @Override
    public void destruir() {
        audio.pararMusicaMenu();
        if (stage != null) {
            if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null);
            stage.dispose();
            stage = null;
        }
    }

    private Label criarTexto(String texto, float escala, float y, float altura) {
        Label label = new Label(texto, skin);
        label.setFontScale(escala);
        label.setAlignment(Align.center);
        label.setSize(LARGURA_MUNDO, altura);
        label.setPosition(0f, y);
        return label;
    }

    private Label criarOpcao(String texto, float y, int indice) {
        Label opcao = criarTexto(texto, 1.3f, y, 50f);
        opcao.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!acaoEmExecucao && selecionado != indice) selecionar(indice);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (acaoEmExecucao) return;
                selecionado = indice;
                confirmar();
            }
        });
        return opcao;
    }

    private void selecionar(int indice) {
        selecionado = indice;
        atualizarSelecao();
        audio.tocarTrocaOpcao();
    }

    private void atualizarSelecao() {
        for (int i = 0; i < opcoes.length; i++) {
            opcoes[i].setFontScale(i == selecionado ? 1.5f : 1.3f);
        }
        Personagem atual = Personagem.values()[selecionado];
        int minimo = BalanceamentoJogo.CHANCE_MINIMA_FURTIVIDADE;
        int maximo = BalanceamentoJogo.CHANCE_MAXIMA_FURTIVIDADE;
        int base = atual.getChanceFurtividade();
        int melhor = Math.max(minimo, Math.min(maximo, base - BalanceamentoJogo.DIFICULDADE_BASE_FURTIVIDADE));
        int pior = Math.max(minimo, Math.min(maximo, base - BalanceamentoJogo.DIFICULDADE_PADRAO_MAXIMA));

        descricao.setText(atual.getDescricao()
            + "\nVida: " + atual.getVidaInicial()
            + "   Furtividade: " + pior + "% a " + melhor + "%");
        retrato.setDrawable(new TextureRegionDrawable(new TextureRegion(
            GerenciadorTexturas.get(atual.getIdentificadorVisual())
        )));
    }

    private void confirmar() {
        acaoEmExecucao = true;
        audio.tocarSelecionarOpcao();
        aoEscolher.accept(Personagem.values()[selecionado]);
    }
}