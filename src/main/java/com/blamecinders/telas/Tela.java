package com.blamecinders.telas;

public interface Tela {

    void mostrar();

    void render(float delta);

    void redimensionar(int largura, int altura);

    void esconder();

    void destruir();
}