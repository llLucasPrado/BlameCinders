package com.blamecinders.persistencia;

interface ArmazenamentoPartida {
    String ler();
    void escrever(String conteudo);
    void remover();
}
