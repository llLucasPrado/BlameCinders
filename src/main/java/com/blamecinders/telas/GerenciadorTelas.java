package com.blamecinders.telas;

import java.util.Objects;

public final class GerenciadorTelas {

    private Tela telaAtual;

    public void trocarTela(Tela novaTela) {
        Objects.requireNonNull(novaTela, "novaTela");
        encerrarTelaAtual();
        telaAtual = novaTela;
        telaAtual.mostrar();
    }

    public void encerrarTelaAtual() {
        if (telaAtual == null) return;

        Tela telaEncerrada = telaAtual;
        telaAtual = null;
        telaEncerrada.esconder();
        telaEncerrada.destruir();
    }

    public void render(float delta) {
        if (telaAtual != null) {
            telaAtual.render(delta);
        }
    }

    public void redimensionar(int largura, int altura) {
        if (telaAtual != null) {
            telaAtual.redimensionar(largura, altura);
        }
    }
}
