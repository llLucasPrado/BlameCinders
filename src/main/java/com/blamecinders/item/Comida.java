package com.blamecinders.item;

import com.blamecinders.combate.Jogador;

import java.util.Objects;

public class Comida implements ItemBau {

    private final String nome;
    private final int cura;
    private final String identificadorVisual;

    public Comida(String nome, int cura, String identificadorVisual) {
        if (cura <= 0) {
            throw new IllegalArgumentException("A cura da comida deve ser positiva.");
        }
        this.nome = Objects.requireNonNull(nome, "nome");
        this.cura = cura;
        this.identificadorVisual = Objects.requireNonNull(identificadorVisual, "identificadorVisual");
    }

    @Override
    public String getNome() {
        return nome;
    }

    public int getCura() {
        return cura;
    }

    @Override
    public String getIdentificadorVisual() {
        return identificadorVisual;
    }

    public int consumir(Jogador jogador) {
        return Objects.requireNonNull(jogador, "jogador").curar(cura);
    }

    public Comida copiar() {
        return new Comida(nome, cura, identificadorVisual);
    }
}
