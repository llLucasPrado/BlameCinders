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
import com.blamecinders.configuracao.BalanceamentoJogo;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;

/** Apresenta vida, chamas e arma equipada durante a partida. */
public class ControladorHUD {

    private final Stage stageUI;
    private final Skin skin;

    private Label labelHUD;
    private Image imagemArmaHUD;
    private String identificadorArmaAtual;

    public ControladorHUD(Stage stageUI, Skin skin) {
        this.stageUI = Objects.requireNonNull(stageUI, "stageUI");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    public void criarHUD() {
        imagemArmaHUD = null;
        identificadorArmaAtual = null;
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
                "Chamas: " + chamasColetadas + " / " + BalanceamentoJogo.OBJETIVO_CHAMAS
                    + "    Vida: " + jogador.getVida()
                    + "    Arma: " + textoArma
            );
        }

        atualizarMiniaturaArma(jogador);
    }

    private void atualizarMiniaturaArma(Jogador jogador) {

        if (imagemArmaHUD != null) {
            imagemArmaHUD.setVisible(jogador != null && jogador.getArmaEquipada() != null);
        }
        if (jogador == null || jogador.getArmaEquipada() == null) {
            identificadorArmaAtual = null;
            return;
        }

        String textura = jogador.getArmaEquipada().getIdentificadorVisual();
        if (imagemArmaHUD == null) {
            imagemArmaHUD = new Image();
            imagemArmaHUD.setSize(64, 86);
            stageUI.addActor(imagemArmaHUD);
        }
        if (!textura.equals(identificadorArmaAtual)) {
            imagemArmaHUD.setDrawable(new TextureRegionDrawable(
                new TextureRegion(GerenciadorTexturas.get(textura))
            ));
            identificadorArmaAtual = textura;
        }
        imagemArmaHUD.setVisible(true);
        imagemArmaHUD.setPosition(
            stageUI.getViewport().getWorldWidth() - 90,
            stageUI.getViewport().getWorldHeight() - 110
        );
    }

    public void setClickArmaListener(Runnable aoClicar) {

        if (imagemArmaHUD == null) return;
        Objects.requireNonNull(aoClicar, "aoClicar");

        imagemArmaHUD.clearListeners();

        imagemArmaHUD.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                aoClicar.run();
            }
        });
    }

}
