package com.blamecinders.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;

/** Popup compartilhado para controlar música e efeitos sonoros. */
public final class PopupAudio {

    private final Stage stage;
    private final Skin skin;
    private final GerenciadorAudio audio;
    private final Slider.SliderStyle estiloSlider;

    public PopupAudio(Stage stage, Skin skin, GerenciadorAudio audio) {
        this.stage = Objects.requireNonNull(stage, "stage");
        this.skin = Objects.requireNonNull(skin, "skin");
        this.audio = Objects.requireNonNull(audio, "audio");
        estiloSlider = criarEstiloSlider();
    }

    public void mostrar() {
        Image overlay = ElementosPopup.criarOverlay(stage, 0.75f);
        Window popup = new Window("", skin);
        Label titulo = new Label("ÁUDIO", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(2f);

        TextButton btnMusica = new TextButton(textoMusica(), skin);
        TextButton btnEfeitos = new TextButton(textoEfeitos(), skin);
        TextButton btnVoltar = new TextButton("Voltar", skin);

        btnMusica.addListener(aoClicar(() -> {
            audio.setMusicaAtiva(!audio.isMusicaAtiva());
            btnMusica.setText(textoMusica());
        }));
        btnEfeitos.addListener(aoClicar(() -> {
            audio.setEfeitosAtivos(!audio.isEfeitosAtivos());
            btnEfeitos.setText(textoEfeitos());
        }));
        btnVoltar.addListener(aoClicar(() -> AnimadorPopup.fecharComOverlay(
            popup,
            overlay,
            null
        )));

        popup.add(titulo).width(440).pad(18);
        popup.row();
        popup.add(btnMusica).width(260).pad(6);
        popup.row();
        adicionarControleVolume(popup, true);
        popup.add(btnEfeitos).width(260).pad(6);
        popup.row();
        adicionarControleVolume(popup, false);
        popup.add(btnVoltar).width(220).pad(12);

        popup.pack();
        ElementosPopup.centralizar(stage, popup);
        stage.addActor(overlay);
        stage.addActor(popup);
        AnimadorPopup.abrir(overlay);
        AnimadorPopup.abrir(popup);
        overlay.toFront();
        popup.toFront();
    }

    private void adicionarControleVolume(Window popup, boolean musica) {
        Label valor = new Label(textoVolume(musica), skin);
        valor.setAlignment(Align.center);
        Slider barra = new Slider(0f, 1f, 0.01f, false, estiloSlider);
        barra.setValue(musica ? audio.getVolumeMusica() : audio.getVolumeEfeitos());
        barra.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (musica) audio.setVolumeMusica(barra.getValue());
                else audio.setVolumeEfeitos(barra.getValue());
                valor.setText(textoVolume(musica));
            }
        });

        popup.add(barra).width(360).height(26).pad(4).center();
        popup.row();
        popup.add(valor).width(360).pad(2);
        popup.row();
    }

    private String textoMusica() {
        return "Música: " + (audio.isMusicaAtiva() ? "ligada" : "desligada");
    }

    private String textoEfeitos() {
        return "Efeitos: " + (audio.isEfeitosAtivos() ? "ligados" : "desligados");
    }

    private String textoVolume(boolean musica) {
        float volume = musica ? audio.getVolumeMusica() : audio.getVolumeEfeitos();
        return Math.round(volume * 100f) + "%";
    }

    private ClickListener aoClicar(Runnable acao) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                acao.run();
            }
        };
    }

    private Slider.SliderStyle criarEstiloSlider() {
        Drawable fundo = new TextureRegionDrawable(new TextureRegion(
            GerenciadorTexturas.getSolid(new Color(0.20f, 0.20f, 0.20f, 1f))
        ));
        fundo.setMinHeight(8f);

        Drawable controle = new TextureRegionDrawable(new TextureRegion(
            GerenciadorTexturas.getSolid(new Color(0.78f, 0.64f, 0.39f, 1f))
        ));
        controle.setMinWidth(20f);
        controle.setMinHeight(20f);

        Slider.SliderStyle estilo = new Slider.SliderStyle();
        estilo.background = fundo;
        estilo.knob = controle;
        estilo.knobOver = controle;
        estilo.knobDown = controle;
        return estilo;
    }
}
