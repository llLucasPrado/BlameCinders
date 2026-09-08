package com.blamecinders.persistencia;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

final class ArmazenamentoPreferenciasPartida implements ArmazenamentoPartida {

    private static final String NOME_PREFERENCIAS = "blame-cinders-save";
    private static final String CHAVE_PARTIDA = "partida";

    private final Preferences preferencias;

    ArmazenamentoPreferenciasPartida() {
        preferencias = Gdx.app.getPreferences(NOME_PREFERENCIAS);
    }

    @Override
    public String ler() {
        return preferencias.getString(CHAVE_PARTIDA, "");
    }

    @Override
    public void escrever(String conteudo) {
        preferencias.putString(CHAVE_PARTIDA, conteudo).flush();
    }

    @Override
    public void remover() {
        preferencias.remove(CHAVE_PARTIDA);
        preferencias.flush();
    }
}
