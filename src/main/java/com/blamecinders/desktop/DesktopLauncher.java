package com.blamecinders.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.blamecinders.BlameCindersGame;

/** Inicializador da versão desktop do jogo. */
public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuracao = new Lwjgl3ApplicationConfiguration();
        configuracao.setTitle("Blame Cinders");
        // Usa a resolução nativa do monitor. Os FitViewports do jogo preservam
        // o canvas lógico de 16:9 sem deformar as cartas.
        configuracao.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        configuracao.setHdpiMode(HdpiMode.Pixels);
        configuracao.useVsync(true);
        configuracao.setForegroundFPS(60);

        new Lwjgl3Application(new BlameCindersGame(), configuracao);
    }
}
