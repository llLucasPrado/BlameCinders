package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;

public class TelaInicial implements Tela {

    private static final float LARGURA_MUNDO = 1280f;
    private static final float ALTURA_MUNDO = 720f;

    private final GerenciadorTelas gerenciadorTelas;
    private final AcaoTela acaoNovoJogo;

    private Stage stage;
    private BitmapFont fonte;

    private final GerenciadorAudio gerenciadorAudio;

    private ShapeRenderer fadeRenderer;

    private boolean transicionando = false;
    private float tempoTransicao = 0f;

    private static final float DURACAO_TRANSICAO = 1.83f;

    public TelaInicial(
        GerenciadorTelas gerenciadorTelas,
        AcaoTela acaoNovoJogo,
        GerenciadorAudio gerenciadorAudio
    ) {
        this.gerenciadorTelas = gerenciadorTelas;
        this.acaoNovoJogo = acaoNovoJogo;
        this.gerenciadorAudio = gerenciadorAudio;
    }

    @Override
    public void mostrar() {

        stage = new Stage(
            new FitViewport(LARGURA_MUNDO, ALTURA_MUNDO)
        );

        fonte = new BitmapFont();

        LabelStyle estilo = new LabelStyle();
        estilo.font = fonte;

        Label titulo = new Label("Blame Cinders", estilo);
        titulo.setFontScale(2.5f);
        titulo.setAlignment(Align.center);
        titulo.setSize(LARGURA_MUNDO, 100f);
        titulo.setPosition(0f, 430f);

        Label pressionarEnter = new Label("PRESSIONE ENTER", estilo);
        pressionarEnter.setFontScale(1.2f);
        pressionarEnter.setAlignment(Align.center);
        pressionarEnter.setSize(LARGURA_MUNDO, 60f);
        pressionarEnter.setPosition(0f, 250f);

        stage.addActor(titulo);
        stage.addActor(pressionarEnter);

        fadeRenderer = new ShapeRenderer();

    }
    
    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

        if (!transicionando) {

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                transicionando = true;
                tempoTransicao = 0f;

                gerenciadorAudio.tocarPressEnter();
            }

            return;
        }

        tempoTransicao += delta;

        float progresso =
            Math.min(tempoTransicao / DURACAO_TRANSICAO, 1f);

        desenharFade(progresso);

        if (tempoTransicao >= DURACAO_TRANSICAO) {

            gerenciadorTelas.trocarTela(
                new MenuPrincipal(
                    gerenciadorTelas,
                    acaoNovoJogo,
                    gerenciadorAudio
                )
            );
        }
    }

    @Override
    public void redimensionar(int largura, int altura) {

        if (stage != null) {
            stage.getViewport().update(largura, altura, true);
        }
    }

    @Override
    public void esconder() {

        if (stage != null) {
            stage.getRoot().setVisible(false);
        }
    }

    @Override
    public void destruir() {

        if (stage != null) {
            stage.dispose();
            stage = null;
        }

        if (fonte != null) {
            fonte.dispose();
            fonte = null;
        }

        if (fadeRenderer != null) {
            fadeRenderer.dispose();
            fadeRenderer = null;
        }
    }

    private void desenharFade(float progresso) {

        Gdx.gl.glEnable(GL20.GL_BLEND);

        fadeRenderer.setProjectionMatrix(stage.getCamera().combined);

        fadeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        fadeRenderer.setColor(
            0f,
            0f,
            0f,
            progresso
        );

        fadeRenderer.rect(
            0f,
            0f,
            LARGURA_MUNDO,
            ALTURA_MUNDO
        );

        fadeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}