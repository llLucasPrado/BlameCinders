package com.blamecinders.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PopupPause {

    private final Stage stage;
    private final Skin skin;
    private Actor fundoEscuro;

    private final Runnable continuar;
    private final Runnable opcoes;
    private final Runnable voltarMenu;

    private Window popup;

    private boolean fechando = false;

    public PopupPause(
        Stage stage,
        Skin skin,
        Runnable continuar,
        Runnable opcoes,
        Runnable voltarMenu
    ) {
        this.stage = stage;
        this.skin = skin;
        this.continuar = continuar;
        this.opcoes = opcoes;
        this.voltarMenu = voltarMenu;

        criar();
    }

    private void criar() {

        popup = new Window("", skin);

        popup.setName("popupPause");

        Label titulo = new Label("PAUSADO", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(2.2f);

        TextButton btnContinuar =
            new TextButton("Continuar", skin);

        TextButton btnOpcoes =
            new TextButton("Opções", skin);

        TextButton btnVoltar =
            new TextButton("Voltar ao menu principal", skin);

        btnContinuar.addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {

                fechar(continuar);
            }
        });

        btnOpcoes.addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {

                opcoes.run();
            }
        });

        btnVoltar.addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {

                fechar(voltarMenu);
            }
        });

        popup.add(titulo)
            .colspan(1)
            .width(420)
            .pad(20);

        popup.row();

        popup.add(btnContinuar)
            .width(220)
            .pad(8);

        popup.row();

        popup.add(btnOpcoes)
            .width(220)
            .pad(8);

        popup.row();

        popup.add(btnVoltar)
            .width(220)
            .pad(8);

        popup.pack();

        centralizar();

        stage.addActor(popup);

        animarAbertura();

        popup.toFront();
        
    }

    private void centralizar() {

        popup.setPosition(
            stage.getViewport().getWorldWidth() / 2f
                - popup.getWidth() / 2f,

            stage.getViewport().getWorldHeight() / 2f
                - popup.getHeight() / 2f
        );
    }

    private void animarAbertura() {

        popup.clearActions();

        popup.setOrigin(Align.center);

        popup.setScale(0.01f, 1f);

        popup.getColor().a = 0f;

        popup.addAction(
            Actions.parallel(
                Actions.scaleTo(
                    1f,
                    1f,
                    0.18f,
                    Interpolation.fade
                ),

                Actions.fadeIn(
                    0.14f,
                    Interpolation.fade
                )
            )
        );
    }

    private void fechar(Runnable aoFinalizar) {

        if (fechando) {
            return;
        }

        fechando = true;

        popup.clearActions();

        popup.setOrigin(Align.center);

        popup.addAction(
            Actions.sequence(

                Actions.parallel(

                    Actions.scaleTo(
                        1f,
                        0.01f,
                        0.16f,
                        Interpolation.fade
                    ),

                    Actions.fadeOut(
                        0.12f,
                        Interpolation.fade
                    )
                ),

                Actions.run(() -> {

                    popup.remove();

                    if (aoFinalizar != null) {
                        aoFinalizar.run();
                    }
                })
            )
        );
    }

    public void atualizar(float delta) {

        stage.act(delta);

    }

    public void remover() {

        if (fundoEscuro != null) {
            fundoEscuro.remove();
            fundoEscuro = null;
        }

        if (popup != null) {
            popup.remove();
            popup = null;
        }
    }
}