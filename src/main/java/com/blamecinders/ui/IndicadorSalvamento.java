package com.blamecinders.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Disposable;

import java.util.Objects;

public final class IndicadorSalvamento implements Disposable {

    private static final float TAMANHO = 150f;
    private static final float MARGEM = 22f;

    private final Stage stage;
    private final Texture textura;
    private Image indicador;

    public IndicadorSalvamento(Stage stage) {
        this.stage = Objects.requireNonNull(stage, "stage");
        textura = new Texture(Gdx.files.internal("UI/indicador-save.png"));
        textura.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void mostrar() {
        removerAtual();

        indicador = new Image(textura);
        indicador.setTouchable(Touchable.disabled);
        indicador.setSize(TAMANHO, TAMANHO);
        indicador.setOrigin(TAMANHO / 2f, TAMANHO / 2f);
        indicador.setPosition(
            stage.getViewport().getWorldWidth() - TAMANHO - MARGEM,
            MARGEM
        );
        indicador.getColor().a = 0f;
        indicador.setScale(0.82f);

        indicador.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(0.2f),
                Actions.scaleTo(1f, 1f, 0.25f, Interpolation.swingOut),
                Actions.moveBy(0f, 8f, 0.25f, Interpolation.sineOut)
            ),
            Actions.repeat(3, Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(-4f, -2f, 0.12f, Interpolation.sine),
                    Actions.rotateBy(-1.6f, 0.12f, Interpolation.sine)
                ),
                Actions.parallel(
                    Actions.moveBy(4f, 2f, 0.12f, Interpolation.sine),
                    Actions.rotateBy(1.6f, 0.12f, Interpolation.sine)
                )
            )),
            Actions.delay(0.25f),
            Actions.parallel(
                Actions.fadeOut(0.35f),
                Actions.moveBy(0f, -8f, 0.35f, Interpolation.sineIn)
            ),
            Actions.run(() -> indicador = null),
            Actions.removeActor()
        ));

        stage.addActor(indicador);
        indicador.toFront();
    }

    @Override
    public void dispose() {
        removerAtual();
        textura.dispose();
    }

    private void removerAtual() {
        if (indicador != null) {
            indicador.remove();
            indicador = null;
        }
    }
}
