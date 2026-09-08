package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;

import java.util.Objects;

public final class TelaOpcoes implements Tela {

    private static final float LARGURA_MUNDO = 1280f;
    private static final float ALTURA_MUNDO = 720f;

    private final AcaoTela voltar;
    private final GerenciadorAudio audio;
    private final Skin skin;

    private Stage stage;
    private Label[] opcoes;
    private int opcaoSelecionada;
    private boolean encerrando;

    public TelaOpcoes(AcaoTela voltar, GerenciadorAudio audio, Skin skin) {
        this.voltar = Objects.requireNonNull(voltar, "voltar");
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(LARGURA_MUNDO, ALTURA_MUNDO));

        Label titulo = new Label("OPÇÕES", skin);
        titulo.setFontScale(2.2f);
        titulo.setAlignment(Align.center);
        titulo.setSize(LARGURA_MUNDO, 80f);
        titulo.setPosition(0f, 500f);
        stage.addActor(titulo);

        opcoes = new Label[] {
            criarOpcao(360f, 0),
            criarOpcao(300f, 1),
            criarOpcao(210f, 2)
        };
        for (Label opcao : opcoes) stage.addActor(opcao);

        atualizarTextos();
        atualizarSelecao();
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (encerrando) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selecionar((opcaoSelecionada + 1) % opcoes.length, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selecionar((opcaoSelecionada - 1 + opcoes.length) % opcoes.length, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executarOpcao();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sair();
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
        if (stage == null) return;
        if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null);
        stage.dispose();
        stage = null;
    }

    private Label criarOpcao(float y, int indice) {
        Label opcao = new Label("", skin);
        opcao.setAlignment(Align.center);
        opcao.setSize(LARGURA_MUNDO, 50f);
        opcao.setPosition(0f, y);
        opcao.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!encerrando && opcaoSelecionada != indice) selecionar(indice, true);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (encerrando) return;
                selecionar(indice, false);
                executarOpcao();
            }
        });
        return opcao;
    }

    private void selecionar(int indice, boolean tocarSom) {
        opcaoSelecionada = indice;
        atualizarSelecao();
        if (tocarSom) audio.tocarTrocaOpcao();
    }

    private void executarOpcao() {
        audio.tocarSelecionarOpcao();
        if (opcaoSelecionada == 0) {
            audio.setMusicaAtiva(!audio.isMusicaAtiva());
        } else if (opcaoSelecionada == 1) {
            audio.setEfeitosAtivos(!audio.isEfeitosAtivos());
        } else {
            sair();
            return;
        }
        atualizarTextos();
    }

    private void sair() {
        if (encerrando) return;
        encerrando = true;
        voltar.executar();
    }

    private void atualizarTextos() {
        opcoes[0].setText("MÚSICA: " + (audio.isMusicaAtiva() ? "LIGADA" : "DESLIGADA"));
        opcoes[1].setText(
            "EFEITOS: " + (audio.isEfeitosAtivos() ? "LIGADOS" : "DESLIGADOS")
        );
        opcoes[2].setText("VOLTAR");
    }

    private void atualizarSelecao() {
        for (int i = 0; i < opcoes.length; i++) {
            opcoes[i].setFontScale(i == opcaoSelecionada ? 1.5f : 1.3f);
            opcoes[i].setColor(i == opcaoSelecionada ? Color.WHITE : Color.LIGHT_GRAY);
        }
    }

}
