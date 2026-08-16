package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;

public class MenuPrincipal implements Tela {

    private static final float LARGURA_MUNDO = 1280f;
    private static final float ALTURA_MUNDO = 720f;

    private int opcaoSelecionada = 0;
    private Label[] opcoes;

    private final GerenciadorTelas gerenciadorTelas;
    private final AcaoTela acaoNovoJogo;
    private final GerenciadorAudio gerenciadorAudio;

    private ShapeRenderer fadeRenderer;
    private float tempoFade = 0f;
    private static final float DURACAO_FADE = 0.9f;

    private Stage stage;
    private BitmapFont fonte;

    //private final TelaTabuleiro telaTabuleiro;

    public MenuPrincipal(
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
        titulo.setPosition(0f, 500f);

        opcoes = new Label[4];

        opcoes[0] = criarOpcao("NOVO JOGO", 360f, 0);
        opcoes[1] = criarOpcao("CONTINUAR", 300f, 1);
        opcoes[2] = criarOpcao("OPÇÕES", 240f, 2);
        opcoes[3] = criarOpcao("SAIR", 180f, 3);

        for (Label opcao : opcoes) {
            stage.addActor(opcao);
        }

        Gdx.input.setInputProcessor(stage);

        atualizarSelecao();

        fadeRenderer = new ShapeRenderer();

        gerenciadorAudio.iniciarMusicaMenu();

    }

    private Label criarOpcao(String texto, float y, int indice) {

        LabelStyle estilo = new LabelStyle();
        estilo.font = fonte;

        Label opcao = new Label(texto, estilo);

        opcao.setFontScale(1.3f);
        opcao.setAlignment(Align.center);
        opcao.setSize(LARGURA_MUNDO, 50f);
        opcao.setPosition(0f, y);

        opcao.addListener(new ClickListener() {

            @Override
            public void enter(
                InputEvent event,
                float x,
                float y,
                int pointer,
                com.badlogic.gdx.scenes.scene2d.Actor fromActor
            ) {
                opcaoSelecionada = indice;
                atualizarSelecao();
                gerenciadorAudio.tocarTrocaOpcao();
            }

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {
                opcaoSelecionada = indice;
                atualizarSelecao();
                gerenciadorAudio.tocarSelecionarOpcao();
                executarOpcao();
            }
        });

        return opcao;
    }

    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

        if (tempoFade < DURACAO_FADE) {

            tempoFade += delta;

            float progresso =
                Math.min(tempoFade / DURACAO_FADE, 1f);

            desenharFade(progresso);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {

            opcaoSelecionada++;

            if (opcaoSelecionada >= opcoes.length) {
                opcaoSelecionada = 0;
            }

            atualizarSelecao();

            gerenciadorAudio.tocarTrocaOpcao();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {

            opcaoSelecionada--;

            if (opcaoSelecionada < 0) {
                opcaoSelecionada = opcoes.length - 1;
            }

            atualizarSelecao();

            gerenciadorAudio.tocarTrocaOpcao();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            executarOpcao();

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

                gerenciadorAudio.tocarSelecionarOpcao();

                executarOpcao();
            }
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

        gerenciadorAudio.pararMusicaMenu();

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

    private void atualizarSelecao() {

        for (int i = 0; i < opcoes.length; i++) {

            if (i == opcaoSelecionada) {
                opcoes[i].setFontScale(1.5f);
            } else {
                opcoes[i].setFontScale(1.3f);
            }
        }
    }

    private void executarOpcao() {

        switch (opcaoSelecionada) {

            case 0:
                acaoNovoJogo.executar();
                break;

            case 1:
                // CONTINUAR
                System.out.println("Continuar");
                break;

            case 2:
                // OPÇÕES
                System.out.println("Opções");
                break;

            case 3:
                // SAIR
                Gdx.app.exit();
                break;
        }
    }
    
    private void desenharFade(float progresso) {

        Gdx.gl.glEnable(GL20.GL_BLEND);

        fadeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        fadeRenderer.setColor(
            0f,
            0f,
            0f,
            1f - progresso
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
