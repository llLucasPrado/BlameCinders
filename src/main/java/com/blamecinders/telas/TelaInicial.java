package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.ui.VisualMenu;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class TelaInicial implements Tela {

    private static final float LARGURA_MUNDO = 1280f;
    private static final float ALTURA_MUNDO = 720f;
    private static final float DURACAO_TRANSICAO = 1.83f;

    private final GerenciadorTelas gerenciadorTelas;
    private final AcaoTela acaoNovoJogo;
    private final AcaoTela acaoContinuar;
    private final AcaoTela acaoOpcoes;
    private final AcaoTela acaoCreditos;
    private final BooleanSupplier partidaSalvaDisponivel;
    private final GerenciadorAudio audio;
    private final Skin skin;

    private Stage stage;
    private ShapeRenderer fadeRenderer;
    private boolean transicionando;
    private boolean transicaoConcluida;
    private float tempoTransicao;

    public TelaInicial(
        GerenciadorTelas gerenciadorTelas,
        AcaoTela acaoNovoJogo,
        AcaoTela acaoContinuar,
        AcaoTela acaoOpcoes,
        AcaoTela acaoCreditos,
        BooleanSupplier partidaSalvaDisponivel,
        GerenciadorAudio audio,
        Skin skin
    ) {
        this.gerenciadorTelas = Objects.requireNonNull(gerenciadorTelas, "gerenciadorTelas");
        this.acaoNovoJogo = Objects.requireNonNull(acaoNovoJogo, "acaoNovoJogo");
        this.acaoContinuar = Objects.requireNonNull(acaoContinuar, "acaoContinuar");
        this.acaoOpcoes = Objects.requireNonNull(acaoOpcoes, "acaoOpcoes");
        this.acaoCreditos = Objects.requireNonNull(acaoCreditos, "acaoCreditos");
        this.partidaSalvaDisponivel = Objects.requireNonNull(
            partidaSalvaDisponivel,
            "partidaSalvaDisponivel"
        );
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(LARGURA_MUNDO, ALTURA_MUNDO));
        VisualMenu.adicionarFundo(stage, VisualMenu.FUNDO_CEMITERIO, 0.20f);

        Label titulo = new Label("B L A M E   C I N D E R S", skin);
        titulo.setFontScale(2.05f);
        titulo.setAlignment(Align.center);
        titulo.setSize(LARGURA_MUNDO, 100f);
        titulo.setPosition(0f, 360f);

        Label pressionarEnter = new Label("PRESSIONE QUALQUER TECLA", skin);
        pressionarEnter.setFontScale(0.9f);
        pressionarEnter.setAlignment(Align.center);
        pressionarEnter.setSize(LARGURA_MUNDO, 60f);
        pressionarEnter.setPosition(0f, 270f);

        stage.addActor(titulo);
        stage.addActor(pressionarEnter);
        VisualMenu.animarEntrada(titulo, 0.1f);
        VisualMenu.animarEntrada(pressionarEnter, 0.35f);
        fadeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (!transicionando) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)
                || Gdx.input.justTouched()) {
                transicionando = true;
                audio.tocarPressEnter();
            }
            return;
        }

        tempoTransicao += delta;
        float progresso = Math.min(tempoTransicao / DURACAO_TRANSICAO, 1f);
        desenharFade(progresso);

        if (!transicaoConcluida && progresso >= 1f) {
            transicaoConcluida = true;
            gerenciadorTelas.trocarTela(new MenuPrincipal(
                acaoNovoJogo,
                acaoContinuar,
                acaoOpcoes,
                acaoCreditos,
                partidaSalvaDisponivel,
                audio,
                skin
            ));
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
        if (fadeRenderer != null) {
            fadeRenderer.dispose();
            fadeRenderer = null;
        }
    }

    private void desenharFade(float progresso) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        fadeRenderer.setProjectionMatrix(stage.getCamera().combined);
        fadeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        fadeRenderer.setColor(0f, 0f, 0f, progresso);
        fadeRenderer.rect(0f, 0f, LARGURA_MUNDO, ALTURA_MUNDO);
        fadeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
