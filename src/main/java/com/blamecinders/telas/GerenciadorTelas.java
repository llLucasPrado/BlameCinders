package com.blamecinders.telas;

public class GerenciadorTelas {

    private Tela telaAtual;

    public void trocarTela(Tela novaTela) {

        if (telaAtual != null) {
            telaAtual.esconder();
            telaAtual.destruir();
        }

        telaAtual = novaTela;
        telaAtual.mostrar();
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