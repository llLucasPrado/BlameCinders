package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import java.util.function.BooleanSupplier;

public final class MenuPrincipal implements Tela {

    private static final float LARGURA_MUNDO = 1280f;
    private static final float ALTURA_MUNDO = 720f;
    private static final float DURACAO_FADE = 0.9f;

    private final AcaoTela acaoNovoJogo;
    private final AcaoTela acaoContinuar;
    private final AcaoTela acaoOpcoes;
    private final BooleanSupplier partidaSalvaDisponivel;
    private final GerenciadorAudio audio;
    private final Skin skin;

    private Stage stage;
    private ShapeRenderer fadeRenderer;
    private Label[] opcoes;
    private Label aviso;
    private int opcaoSelecionada;
    private float tempoFade;
    private boolean acaoEmExecucao;

    public MenuPrincipal(
        AcaoTela acaoNovoJogo,
        AcaoTela acaoContinuar,
        AcaoTela acaoOpcoes,
        BooleanSupplier partidaSalvaDisponivel,
        GerenciadorAudio audio,
        Skin skin
    ) {
        this.acaoNovoJogo = Objects.requireNonNull(acaoNovoJogo, "acaoNovoJogo");
        this.acaoContinuar = Objects.requireNonNull(acaoContinuar, "acaoContinuar");
        this.acaoOpcoes = Objects.requireNonNull(acaoOpcoes, "acaoOpcoes");
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

        Label titulo = new Label("Blame Cinders", skin);
        titulo.setFontScale(2.5f);
        titulo.setAlignment(Align.center);
        titulo.setSize(LARGURA_MUNDO, 100f);
        titulo.setPosition(0f, 500f);

        opcoes = new Label[] {
            criarOpcao("NOVO JOGO", 360f, 0),
            criarOpcao("CONTINUAR", 300f, 1),
            criarOpcao("OPÇÕES", 240f, 2),
            criarOpcao("SAIR", 180f, 3)
        };

        aviso = new Label("", skin);
        aviso.setColor(Color.LIGHT_GRAY);
        aviso.setAlignment(Align.center);
        aviso.setSize(LARGURA_MUNDO, 40f);
        aviso.setPosition(0f, 115f);

        stage.addActor(titulo);
        for (Label opcao : opcoes) {
            stage.addActor(opcao);
        }
        stage.addActor(aviso);

        atualizarSelecao();
        fadeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (tempoFade < DURACAO_FADE) {
            tempoFade = Math.min(tempoFade + delta, DURACAO_FADE);
            desenharFade(1f - tempoFade / DURACAO_FADE);
        }

        if (acaoEmExecucao) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selecionar((opcaoSelecionada + 1) % opcoes.length, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selecionar((opcaoSelecionada - 1 + opcoes.length) % opcoes.length, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            audio.tocarSelecionarOpcao();
            executarOpcao();
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
        audio.pararMusicaMenu();
        if (stage != null) {
            if (Gdx.input.getInputProcessor() == stage) {
                Gdx.input.setInputProcessor(null);
            }
            stage.dispose();
            stage = null;
        }
        if (fadeRenderer != null) {
            fadeRenderer.dispose();
            fadeRenderer = null;
        }
    }

    private Label criarOpcao(String texto, float y, int indice) {
        Label opcao = new Label(texto, skin);
        opcao.setFontScale(1.3f);
        opcao.setAlignment(Align.center);
        opcao.setSize(LARGURA_MUNDO, 50f);
        opcao.setPosition(0f, y);
        opcao.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!acaoEmExecucao && opcaoSelecionada != indice) {
                    selecionar(indice, true);
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (acaoEmExecucao) return;
                selecionar(indice, false);
                audio.tocarSelecionarOpcao();
                executarOpcao();
            }
        });
        return opcao;
    }

    private void selecionar(int indice, boolean tocarSom) {
        opcaoSelecionada = indice;
        atualizarSelecao();
        aviso.setText("");
        if (tocarSom) {
            audio.tocarTrocaOpcao();
        }
    }

    private void atualizarSelecao() {
        for (int i = 0; i < opcoes.length; i++) {
            opcoes[i].setFontScale(i == opcaoSelecionada ? 1.5f : 1.3f);
        }
    }

    private void executarOpcao() {
        switch (opcaoSelecionada) {
            case 0:
                acaoEmExecucao = true;
                acaoNovoJogo.executar();
                break;
            case 1:
                if (!partidaSalvaDisponivel.getAsBoolean()) {
                    aviso.setText("Não há partida salva disponível.");
                    break;
                }
                acaoEmExecucao = true;
                acaoContinuar.executar();
                break;
            case 2:
                acaoEmExecucao = true;
                acaoOpcoes.executar();
                break;
            case 3:
                acaoEmExecucao = true;
                Gdx.app.exit();
                break;
            default:
                throw new IllegalStateException("Opção de menu desconhecida.");
        }
    }

    private void desenharFade(float alpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        fadeRenderer.setProjectionMatrix(stage.getCamera().combined);
        fadeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        fadeRenderer.setColor(0f, 0f, 0f, alpha);
        fadeRenderer.rect(0f, 0f, LARGURA_MUNDO, ALTURA_MUNDO);
        fadeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
