package com.blamecinders.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

final class AnimadorPopup {

    private AnimadorPopup() {
    }

    static void abrir(Actor actor) {
        if (actor == null) return;

        actor.clearActions();
        actor.setOrigin(Align.center);
        actor.setScale(0.01f, 1f);
        actor.getColor().a = 0f;
        actor.addAction(Actions.parallel(
            Actions.scaleTo(1f, 1f, 0.18f, Interpolation.fade),
            Actions.fadeIn(0.14f, Interpolation.fade)
        ));
    }

    static void fechar(Actor actor, Runnable aoFinalizar) {
        if (actor == null) {
            executar(aoFinalizar);
            return;
        }

        actor.clearActions();
        actor.setOrigin(Align.center);
        actor.addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(1f, 0.01f, 0.16f, Interpolation.fade),
                Actions.fadeOut(0.12f, Interpolation.fade)
            ),
            Actions.run(() -> {
                actor.remove();
                executar(aoFinalizar);
            })
        ));
    }

    static void fecharComOverlay(Actor popup, Actor overlay, Runnable aoFinalizar) {
        if (popup == null || overlay == null) {
            if (popup != null) popup.remove();
            if (overlay != null) overlay.remove();
            executar(aoFinalizar);
            return;
        }

        popup.clearActions();
        overlay.clearActions();
        popup.setOrigin(Align.center);
        popup.addAction(Actions.parallel(
            Actions.scaleTo(1f, 0.01f, 0.16f, Interpolation.fade),
            Actions.fadeOut(0.12f, Interpolation.fade)
        ));
        overlay.addAction(Actions.sequence(
            Actions.fadeOut(0.12f, Interpolation.fade),
            Actions.run(() -> {
                popup.remove();
                overlay.remove();
                if (aoFinalizar != null) Gdx.app.postRunnable(aoFinalizar);
            })
        ));
    }

    private static void executar(Runnable acao) {
        if (acao != null) acao.run();
    }
}
