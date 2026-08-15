package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TelaInicial implements Tela {

    private static final float LARGURA_MUNDO = 1280f;
    private static final float ALTURA_MUNDO = 720f;

    private final GerenciadorTelas gerenciadorTelas;
    private final AcaoTela acaoNovoJogo;

    private Stage stage;
    private BitmapFont fonte;

    public TelaInicial(
        GerenciadorTelas gerenciadorTelas,
        AcaoTela acaoNovoJogo
    ) {
        this.gerenciadorTelas = gerenciadorTelas;
        this.acaoNovoJogo = acaoNovoJogo;
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


    }
    
    @Override
    public void render(float delta) {

        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            gerenciadorTelas.trocarTela(
                new MenuPrincipal(
                    gerenciadorTelas,
                    acaoNovoJogo
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
    }
}