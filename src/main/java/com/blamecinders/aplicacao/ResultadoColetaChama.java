package com.blamecinders.aplicacao;

public final class ResultadoColetaChama {

    private final int quantidadeColetada;
    private final boolean objetivoConcluido;

    ResultadoColetaChama(int quantidadeColetada, boolean objetivoConcluido) {
        this.quantidadeColetada = quantidadeColetada;
        this.objetivoConcluido = objetivoConcluido;
    }

    public int getQuantidadeColetada() {
        return quantidadeColetada;
    }

    public boolean isObjetivoConcluido() {
        return objetivoConcluido;
    }
}
