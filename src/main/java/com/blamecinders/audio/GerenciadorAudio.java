package com.blamecinders.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GerenciadorAudio {

    private final Sound pressEnter;
    private final Sound trocaOpcao;
    private final Sound selecionarOpcao;

    private final Music soundtrackMenu;

    public GerenciadorAudio() {

        pressEnter = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/PressEnter.ogg")
        );

        trocaOpcao = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/TrocaOpcao.ogg")
        );

        selecionarOpcao = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/SelecionarOpcao.ogg")
        );

        soundtrackMenu = Gdx.audio.newMusic(
            Gdx.files.internal("Sounds/SoundTrackMenu.ogg")
        );

        soundtrackMenu.setLooping(true);
    }

    public void tocarPressEnter() {
        pressEnter.play();
    }

    public void tocarTrocaOpcao() {
        trocaOpcao.play();
    }

    public void tocarSelecionarOpcao() {
        selecionarOpcao.play();
    }

    public void iniciarMusicaMenu() {
        if (!soundtrackMenu.isPlaying()) {
            soundtrackMenu.play();
        }
    }

    public void pararMusicaMenu() {
        soundtrackMenu.stop();
    }

    public void dispose() {
        pressEnter.dispose();
        trocaOpcao.dispose();
        selecionarOpcao.dispose();
        soundtrackMenu.dispose();
    }
}
