package com.blamecinders.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.blamecinders.audio.GerenciadorAudio;

import java.util.Objects;

final class PopupPause {

    private static final String NOME_POPUP = "popupPause";
    private static final String NOME_OVERLAY = "overlayPause";

    private final Stage stage;
    private final Skin skin;
    private final GerenciadorAudio audio;

    PopupPause(Stage stage, Skin skin, GerenciadorAudio audio) {
        this.stage = Objects.requireNonNull(stage, "stage");
        this.skin = Objects.requireNonNull(skin, "skin");
        this.audio = Objects.requireNonNull(audio, "audio");
    }

    void mostrar(Runnable continuar, Runnable voltarMenu) {
        Objects.requireNonNull(continuar, "continuar");
        Objects.requireNonNull(voltarMenu, "voltarMenu");
        if (stage.getRoot().findActor(NOME_POPUP) != null) return;

        Image overlay = ElementosPopup.criarOverlay(stage, 0.75f);
        overlay.setName(NOME_OVERLAY);
        stage.addActor(overlay);

        Window popup = new Window("", skin);
        popup.setName(NOME_POPUP);

        Label titulo = new Label("PAUSADO", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(2.2f);

        TextButton btnContinuar = new TextButton("Continuar", skin);
        TextButton btnMusica = new TextButton(textoMusica(), skin);
        TextButton btnEfeitos = new TextButton(textoEfeitos(), skin);
        TextButton btnVoltar = new TextButton("Menu Principal", skin);

        btnContinuar.addListener(aoClicar(() ->
            AnimadorPopup.fecharComOverlay(popup, overlay, continuar)
        ));
        btnVoltar.addListener(aoClicar(() ->
            AnimadorPopup.fecharComOverlay(popup, overlay, voltarMenu)
        ));
        btnMusica.addListener(aoClicar(() -> {
            audio.setMusicaAtiva(!audio.isMusicaAtiva());
            btnMusica.setText(textoMusica());
        }));
        btnEfeitos.addListener(aoClicar(() -> {
            audio.setEfeitosAtivos(!audio.isEfeitosAtivos());
            btnEfeitos.setText(textoEfeitos());
        }));

        popup.add(titulo).width(420).pad(20);
        popup.row();
        popup.add(btnContinuar).width(220).pad(8);
        popup.row();
        popup.add(btnMusica).width(220).pad(8);
        popup.row();
        popup.add(btnEfeitos).width(220).pad(8);
        popup.row();
        popup.add(btnVoltar).width(220).pad(8);
        popup.pack();

        ElementosPopup.centralizar(stage, popup);
        stage.addActor(popup);
        AnimadorPopup.abrir(overlay);
        AnimadorPopup.abrir(popup);
        overlay.toFront();
        popup.toFront();
    }

    void fechar(Runnable aoFinalizar) {
        Actor popup = stage.getRoot().findActor(NOME_POPUP);
        Actor overlay = stage.getRoot().findActor(NOME_OVERLAY);
        AnimadorPopup.fecharComOverlay(popup, overlay, aoFinalizar);
    }

    private static ClickListener aoClicar(Runnable acao) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                acao.run();
            }
        };
    }

    private String textoMusica() {
        return "Música: " + (audio.isMusicaAtiva() ? "ligada" : "desligada");
    }

    private String textoEfeitos() {
        return "Efeitos: " + (audio.isEfeitosAtivos() ? "ligados" : "desligados");
    }
}
