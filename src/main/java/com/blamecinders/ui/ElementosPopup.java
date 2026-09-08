package com.blamecinders.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;

final class ElementosPopup {

    private ElementosPopup() {
    }

    static Image criarOverlay(Stage stage, float alpha) {
        Objects.requireNonNull(stage, "stage");
        Image overlay = new Image(new TextureRegionDrawable(
            GerenciadorTexturas.getSolid(Color.BLACK)
        ));
        overlay.setSize(
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight()
        );
        overlay.setColor(0f, 0f, 0f, Math.max(0f, Math.min(1f, alpha)));
        overlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
            }
        });
        return overlay;
    }

    static void centralizar(Stage stage, Actor actor) {
        Objects.requireNonNull(stage, "stage");
        Objects.requireNonNull(actor, "actor");
        actor.setPosition(
            stage.getViewport().getWorldWidth() / 2f - actor.getWidth() / 2f,
            stage.getViewport().getWorldHeight() / 2f - actor.getHeight() / 2f
        );
    }
}
