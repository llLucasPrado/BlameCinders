package com.blamecinders.persistencia;

interface ArmazenamentoPartida {
    String ler();

    default String lerBackup() {
        return "";
    }

    default void restaurarBackup() {
    }

    void escrever(String conteudo);

    void remover();
}
