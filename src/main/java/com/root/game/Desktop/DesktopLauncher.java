package com.root.game.Desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.root.game.CorpoPrincipal.TCC_0_01;

/** Inicializador da versão desktop do jogo. */
public final class DesktopLauncher {

    private DesktopLauncher() {
    }

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuracao = new Lwjgl3ApplicationConfiguration();
        configuracao.setTitle("Blame Cinders");
        configuracao.setWindowedMode(960, 720);
        configuracao.useVsync(true);
        configuracao.setForegroundFPS(60);

        new Lwjgl3Application(new TCC_0_01(), configuracao);
    }
}
