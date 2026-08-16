package com.blamecinders.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.blamecinders.combate.Jogador;
import com.blamecinders.util.GerenciadorTexturas;

/** Apresenta vida, chamas e arma equipada durante a partida. */
public class ControladorHUD {

    private final Stage stageUI;
    private final Skin skin;

    private Label labelHUD;
    private Image imagemArmaHUD;

    public ControladorHUD(Stage stageUI, Skin skin) {
        this.stageUI = stageUI;
        this.skin = skin;
    }

    public void criarHUD() {

        labelHUD = new Label("", skin);
        labelHUD.setPosition(20, stageUI.getViewport().getWorldHeight() - 25);

        stageUI.addActor(labelHUD);
    }

    public void atualizarHUD(Jogador jogador, int chamasColetadas) {

        if (labelHUD == null) return;

        String textoArma = "Sem arma";

        if (jogador != null && jogador.getArmaEquipada() != null) {
            textoArma = jogador.getArmaEquipada().getNome()
                + " (" + jogador.getArmaEquipada().getDurabilidade() + ")";
        }

        if (jogador != null) {
            labelHUD.setText(
                "Chamas: " + chamasColetadas + " / 3"
                    + "    Vida: " + jogador.getVida()
                    + "    Arma: " + textoArma
            );
        }

        atualizarMiniaturaArma(jogador);
    }

    /** Reposiciona os elementos presos Ã s bordas apÃ³s alterar o viewport. */
    private void atualizarMiniaturaArma(Jogador jogador) {

        if (imagemArmaHUD != null) {
            imagemArmaHUD.remove();
            imagemArmaHUD = null;
        }

        if (jogador == null || jogador.getArmaEquipada() == null) {
            return;
        }

        String textura = jogador.getArmaEquipada().getIdentificadorVisual();

        imagemArmaHUD = new Image(
            new TextureRegionDrawable(
                new TextureRegion(GerenciadorTexturas.get(textura))
            )
        );

        imagemArmaHUD.setSize(64, 86);
        imagemArmaHUD.setPosition(
            stageUI.getViewport().getWorldWidth() - 90,
            stageUI.getViewport().getWorldHeight() - 110
        );

        stageUI.addActor(imagemArmaHUD);
    }

    public void setClickArmaListener(Runnable aoClicar) {

        if (imagemArmaHUD == null) return;

        imagemArmaHUD.clearListeners();

        imagemArmaHUD.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                aoClicar.run();
            }
        });
    }

}
