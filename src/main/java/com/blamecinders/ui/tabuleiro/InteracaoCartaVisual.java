package com.blamecinders.ui.tabuleiro;

/** Porta de interação entre uma carta Scene2D e o coordenador da partida. */
public interface InteracaoCartaVisual {

    boolean estaBloqueada();

    void aoClicar(int linha, int coluna);
}
