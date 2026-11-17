package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.ui.VisualMenu;

import java.util.Objects;

public final class TelaCreditos implements Tela {

    private final AcaoTela voltar;
    private final GerenciadorAudio audio;
    private final Skin skin;
    private Stage stage;
    private boolean encerrando;

    public TelaCreditos(AcaoTela voltar, GerenciadorAudio audio, Skin skin) {
        this.voltar = Objects.requireNonNull(voltar, "voltar");
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(VisualMenu.LARGURA, VisualMenu.ALTURA));
        VisualMenu.adicionarFundo(stage, VisualMenu.FUNDO_CAVALEIRO, 0.55f);

        Group painel = VisualMenu.criarPainel(300f, 150f, 680f, 440f);
        Label titulo = new Label("CRÉDITOS", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(2f);
        titulo.setBounds(40f, 320f, 600f, 70f);

        Label texto = new Label(
            "BLAME CINDERS\n\n"
            + "Projeto independente em desenvolvimento\n\n"
            + "Direção, arte e equipe serão registrados aqui\n"
            + "conforme os créditos forem definidos.",
            skin
        );
        texto.setAlignment(Align.center);
        texto.setColor(new Color(0.86f, 0.88f, 0.91f, 1f));
        texto.setBounds(50f, 105f, 580f, 210f);
        painel.addActor(titulo);
        painel.addActor(texto);
        stage.addActor(painel);

        Label voltarLabel = new Label("VOLTAR", skin);
        voltarLabel.setAlignment(Align.center);
        voltarLabel.setBounds(540f, 74f, 200f, 46f);
        voltarLabel.setColor(Color.LIGHT_GRAY);
        voltarLabel.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                voltarLabel.setColor(Color.WHITE);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                voltarLabel.setColor(Color.LIGHT_GRAY);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                sair();
            }
        });
        stage.addActor(voltarLabel);
        VisualMenu.animarEntrada(painel, 0f);
        VisualMenu.animarEntrada(voltarLabel, 0.15f);
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (!encerrando && (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))) {
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

    private void sair() {
        if (encerrando) return;
        encerrando = true;
        audio.tocarSelecionarOpcao();
        voltar.executar();
    }
}
