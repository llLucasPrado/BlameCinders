package com.blamecinders.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class GerenciadorAudio {

    private static final float DURACAO_FADE_MUSICA = 1.5f;

    private static final String NOME_PREFERENCIAS = "blame-cinders-config";
    private static final String CHAVE_MUSICA = "musicaAtiva";
    private static final String CHAVE_EFEITOS = "efeitosAtivos";
    private static final String CHAVE_VOLUME_MUSICA = "volumeMusica";
    private static final String CHAVE_VOLUME_EFEITOS = "volumeEfeitos";

    private final Sound pressEnter;
    private final Sound trocaOpcao;
    private final Sound selecionarOpcao;
    private final Sound clickCartasErradas;
    private final Sound flipCartasEntradaSaida;
    private final Sound hoverCartas;
    private final Sound derrotaCarta;
    private final Sound gameOver;
    private final Sound furtividade;
    private final Sound derrotaFurtividade;
    private final Sound combate;
    private final Sound chama;

    private final Music soundtrackMenu;
    private final Music soundtrackFase1;
    private final Preferences preferencias;
    private boolean musicaAtiva;
    private boolean efeitosAtivos;
    private float volumeMusica;
    private float volumeEfeitos;
    private boolean musicaMenuSolicitada;
    private boolean musicaFaseSolicitada;
    private float volumeMenu;
    private float volumeFase;
    private boolean furtividadeEmAndamento;
    private boolean combateEmAndamento;
    private long somFurtividadeId = -1L;
    private long somCombateId = -1L;
    private boolean descartado;

    public GerenciadorAudio() {

        preferencias = Gdx.app.getPreferences(NOME_PREFERENCIAS);
        musicaAtiva = preferencias.getBoolean(CHAVE_MUSICA, true);
        efeitosAtivos = preferencias.getBoolean(CHAVE_EFEITOS, true);
        volumeMusica = preferencias.getFloat(CHAVE_VOLUME_MUSICA, 1f);
        volumeEfeitos = preferencias.getFloat(CHAVE_VOLUME_EFEITOS, 1f);

        pressEnter = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/PressEnter.ogg")
        );

        trocaOpcao = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/TrocaOpcao.ogg")
        );

        selecionarOpcao = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/SelecionarOpcao.ogg")
        );

        clickCartasErradas = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/ClickCartasErradas.ogg")
        );

        flipCartasEntradaSaida = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/FlipCartasEntradaSaida.ogg")
        );

        hoverCartas = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/HoverCartas.ogg")
        );

        derrotaCarta = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/DerrotaCarta.m4a.ogg")
        );

        gameOver = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/GameOver.m4a.ogg")
        );

        furtividade = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/Furtividade.m4a.ogg")
        );

        derrotaFurtividade = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/DerrotaFurtividade.m4a.ogg")
        );

        combate = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/Combate.ogg")
        );

        chama = Gdx.audio.newSound(
            Gdx.files.internal("Sounds/Chama.ogg")
        );

        soundtrackMenu = Gdx.audio.newMusic(
            Gdx.files.internal("Sounds/SoundTrackMenu.ogg")
        );

        soundtrackMenu.setLooping(true);

        soundtrackFase1 = Gdx.audio.newMusic(
            Gdx.files.internal("Sounds/TrilhaSonoraF1.ogg")
        );
        soundtrackFase1.setLooping(true);
    }

    public void tocarPressEnter() {
        if (efeitosAtivos) pressEnter.play(volumeEfeitos);
    }

    public void tocarTrocaOpcao() {
        if (efeitosAtivos) trocaOpcao.play(volumeEfeitos);
    }

    public void tocarSelecionarOpcao() {
        if (efeitosAtivos) selecionarOpcao.play(volumeEfeitos);
    }

    public void tocarClickCartaErrada() {
        if (efeitosAtivos) clickCartasErradas.play(volumeEfeitos);
    }

    public void tocarFlipCartas() {
        if (efeitosAtivos) flipCartasEntradaSaida.play(volumeEfeitos);
    }

    public void tocarHoverCarta() {
        if (efeitosAtivos) hoverCartas.play(volumeEfeitos);
    }

    public void tocarDerrotaCarta() {
        if (efeitosAtivos) derrotaCarta.play(volumeEfeitos);
    }

    public void tocarGameOver() {
        if (efeitosAtivos) gameOver.play(volumeEfeitos);
    }

    public void tocarChama() {
        if (efeitosAtivos) chama.play(volumeEfeitos);
    }

    public void iniciarSomCombate() {
        combateEmAndamento = true;
        if (efeitosAtivos && somCombateId < 0L) {
            somCombateId = combate.loop(volumeEfeitos);
        }
    }

    public void finalizarSomCombate() {
        if (somCombateId >= 0L) {
            combate.stop(somCombateId);
            somCombateId = -1L;
        }
        combateEmAndamento = false;
    }

    public void iniciarSomFurtividade() {
        furtividadeEmAndamento = true;
        if (efeitosAtivos && somFurtividadeId < 0L) {
            somFurtividadeId = furtividade.loop(volumeEfeitos);
        }
    }

    public void finalizarSomFurtividade(boolean sucesso) {
        if (somFurtividadeId >= 0L) {
            furtividade.stop(somFurtividadeId);
            somFurtividadeId = -1L;
        }
        furtividadeEmAndamento = false;
        if (!sucesso && efeitosAtivos) derrotaFurtividade.play(volumeEfeitos);
    }

    public void iniciarMusicaMenu() {
        musicaMenuSolicitada = true;
        pararMusicaFase();
        if (musicaAtiva && !soundtrackMenu.isPlaying()) {
            volumeMenu = 0f;
            soundtrackMenu.setVolume(volumeMenu);
            soundtrackMenu.play();
        }
    }

    public void pararMusicaMenu() {
        musicaMenuSolicitada = false;
        soundtrackMenu.stop();
    }

    public void iniciarMusicaFase() {
        musicaFaseSolicitada = true;
        pararMusicaMenu();
        if (musicaAtiva && !soundtrackFase1.isPlaying()) {
            volumeFase = 0f;
            soundtrackFase1.setVolume(volumeFase);
            soundtrackFase1.play();
        }
    }

    public void pararMusicaFase() {
        musicaFaseSolicitada = false;
        soundtrackFase1.stop();
    }

    public void atualizar(float delta) {
        if (!musicaAtiva) return;
        if (musicaMenuSolicitada && soundtrackMenu.isPlaying() && volumeMenu < 1f) {
            volumeMenu = Math.min(1f, volumeMenu + delta / DURACAO_FADE_MUSICA);
            soundtrackMenu.setVolume(volumeMenu * volumeMusica);
        }
        if (musicaFaseSolicitada && soundtrackFase1.isPlaying()) {
            boolean reduzirVolume = furtividadeEmAndamento || combateEmAndamento;
            float volumeAlvo = reduzirVolume ? 0.06f : 1f;
            float duracaoFade = reduzirVolume ? 0.35f : DURACAO_FADE_MUSICA;
            if (volumeFase < volumeAlvo) {
                volumeFase = Math.min(volumeAlvo, volumeFase + delta / duracaoFade);
            } else if (volumeFase > volumeAlvo) {
                volumeFase = Math.max(volumeAlvo, volumeFase - delta / duracaoFade);
            }
            soundtrackFase1.setVolume(volumeFase * volumeMusica);
        }
    }

    public boolean isMusicaAtiva() {
        return musicaAtiva;
    }

    public boolean isEfeitosAtivos() {
        return efeitosAtivos;
    }

    public float getVolumeMusica() {
        return volumeMusica;
    }

    public void setVolumeMusica(float volume) {
        volumeMusica = limitarVolume(volume);
        preferencias.putFloat(CHAVE_VOLUME_MUSICA, volumeMusica).flush();
        soundtrackMenu.setVolume(volumeMenu * volumeMusica);
        soundtrackFase1.setVolume(volumeFase * volumeMusica);
    }

    public float getVolumeEfeitos() {
        return volumeEfeitos;
    }

    public void setVolumeEfeitos(float volume) {
        volumeEfeitos = limitarVolume(volume);
        preferencias.putFloat(CHAVE_VOLUME_EFEITOS, volumeEfeitos).flush();
        if (somCombateId >= 0L) combate.setVolume(somCombateId, volumeEfeitos);
        if (somFurtividadeId >= 0L) furtividade.setVolume(somFurtividadeId, volumeEfeitos);
    }

    public void setMusicaAtiva(boolean ativa) {
        musicaAtiva = ativa;
        preferencias.putBoolean(CHAVE_MUSICA, ativa).flush();
        if (!ativa) {
            soundtrackMenu.stop();
            soundtrackFase1.stop();
        } else {
            if (musicaMenuSolicitada && !soundtrackMenu.isPlaying()) {
                volumeMenu = 0f;
                soundtrackMenu.setVolume(volumeMenu * volumeMusica);
                soundtrackMenu.play();
            }
            if (musicaFaseSolicitada && !soundtrackFase1.isPlaying()) {
                volumeFase = 0f;
                soundtrackFase1.setVolume(volumeFase * volumeMusica);
                soundtrackFase1.play();
            }
        }
    }

    public void setEfeitosAtivos(boolean ativos) {
        efeitosAtivos = ativos;
        preferencias.putBoolean(CHAVE_EFEITOS, ativos).flush();
        if (!ativos && somFurtividadeId >= 0L) {
            furtividade.stop(somFurtividadeId);
            somFurtividadeId = -1L;
        } else if (ativos && furtividadeEmAndamento && somFurtividadeId < 0L) {
            somFurtividadeId = furtividade.loop(volumeEfeitos);
        }
        if (!ativos && somCombateId >= 0L) {
            combate.stop(somCombateId);
            somCombateId = -1L;
        } else if (ativos && combateEmAndamento && somCombateId < 0L) {
            somCombateId = combate.loop(volumeEfeitos);
        }
    }

    public void dispose() {
        if (descartado) return;
        descartado = true;
        pressEnter.dispose();
        trocaOpcao.dispose();
        selecionarOpcao.dispose();
        clickCartasErradas.dispose();
        flipCartasEntradaSaida.dispose();
        hoverCartas.dispose();
        derrotaCarta.dispose();
        gameOver.dispose();
        if (somFurtividadeId >= 0L) furtividade.stop(somFurtividadeId);
        if (somCombateId >= 0L) combate.stop(somCombateId);
        furtividade.dispose();
        derrotaFurtividade.dispose();
        combate.dispose();
        chama.dispose();
        soundtrackMenu.dispose();
        soundtrackFase1.dispose();
    }

    private static float limitarVolume(float volume) {
        return Math.max(0f, Math.min(1f, volume));
    }
}
