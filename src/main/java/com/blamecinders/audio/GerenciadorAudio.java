package com.blamecinders.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GerenciadorAudio {

    private static final String NOME_PREFERENCIAS = "blame-cinders-config";
    private static final String CHAVE_MUSICA = "musicaAtiva";
    private static final String CHAVE_EFEITOS = "efeitosAtivos";

    private final Sound pressEnter;
    private final Sound trocaOpcao;
    private final Sound selecionarOpcao;

    private final Music soundtrackMenu;
    private final Preferences preferencias;
    private boolean musicaAtiva;
    private boolean efeitosAtivos;
    private boolean musicaMenuSolicitada;
    private boolean descartado;

    public GerenciadorAudio() {

        preferencias = Gdx.app.getPreferences(NOME_PREFERENCIAS);
        musicaAtiva = preferencias.getBoolean(CHAVE_MUSICA, true);
        efeitosAtivos = preferencias.getBoolean(CHAVE_EFEITOS, true);

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
        if (efeitosAtivos) pressEnter.play();
    }

    public void tocarTrocaOpcao() {
        if (efeitosAtivos) trocaOpcao.play();
    }

    public void tocarSelecionarOpcao() {
        if (efeitosAtivos) selecionarOpcao.play();
    }

    public void iniciarMusicaMenu() {
        musicaMenuSolicitada = true;
        if (musicaAtiva && !soundtrackMenu.isPlaying()) {
            soundtrackMenu.play();
        }
    }

    public void pararMusicaMenu() {
        musicaMenuSolicitada = false;
        soundtrackMenu.stop();
    }

    public boolean isMusicaAtiva() {
        return musicaAtiva;
    }

    public boolean isEfeitosAtivos() {
        return efeitosAtivos;
    }

    public void setMusicaAtiva(boolean ativa) {
        musicaAtiva = ativa;
        preferencias.putBoolean(CHAVE_MUSICA, ativa).flush();
        if (!ativa) soundtrackMenu.stop();
        else if (musicaMenuSolicitada && !soundtrackMenu.isPlaying()) soundtrackMenu.play();
    }

    public void setEfeitosAtivos(boolean ativos) {
        efeitosAtivos = ativos;
        preferencias.putBoolean(CHAVE_EFEITOS, ativos).flush();
    }

    public void dispose() {
        if (descartado) return;
        descartado = true;
        pressEnter.dispose();
        trocaOpcao.dispose();
        selecionarOpcao.dispose();
        soundtrackMenu.dispose();
    }
}
