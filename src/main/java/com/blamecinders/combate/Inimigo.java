package com.blamecinders.combate;

import com.blamecinders.configuracao.BalanceamentoJogo;

import java.util.Objects;

public class Inimigo {

    private final String nome;
    private final String identificadorVisual;
    private final int dificuldadeFurtividade;
    private final CategoriaInimigo categoria;
    private int vida;

    public Inimigo(String nome, int vida, String identificadorVisual) {
        this(
            nome,
            vida,
            identificadorVisual,
            CategoriaInimigo.porVida(vida),
            Math.min(
                BalanceamentoJogo.DIFICULDADE_PADRAO_MAXIMA,
                BalanceamentoJogo.DIFICULDADE_BASE_FURTIVIDADE + vida
            )
        );
    }

    public Inimigo(String nome, int vida, String identificadorVisual, int dificuldadeFurtividade) {
        this(
            nome,
            vida,
            identificadorVisual,
            CategoriaInimigo.porVida(vida),
            dificuldadeFurtividade
        );
    }

    public Inimigo(
        String nome,
        int vida,
        String identificadorVisual,
        CategoriaInimigo categoria,
        int dificuldadeFurtividade
    ) {
        if (vida <= 0) {
            throw new IllegalArgumentException("A vida do inimigo deve ser positiva.");
        }
        this.nome = textoObrigatorio(nome, "nome");
        this.identificadorVisual = textoObrigatorio(
            identificadorVisual,
            "identificadorVisual"
        );
        this.categoria = Objects.requireNonNull(categoria, "categoria");
        this.vida = vida;
        this.dificuldadeFurtividade = Math.max(
            0,
            Math.min(BalanceamentoJogo.DIFICULDADE_ABSOLUTA_MAXIMA, dificuldadeFurtividade)
        );
    }

    public String getNome() {
        return nome;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = Math.max(0, vida);
    }

    public String getIdentificadorVisual() {
        return identificadorVisual;
    }

    public int getDificuldadeFurtividade() {
        return dificuldadeFurtividade;
    }

    public CategoriaInimigo getCategoria() {
        return categoria;
    }

    public Inimigo copiar() {
        return new Inimigo(
            nome,
            vida,
            identificadorVisual,
            categoria,
            dificuldadeFurtividade
        );
    }

    private static String textoObrigatorio(String texto, String campo) {
        Objects.requireNonNull(texto, campo);
        if (texto.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio.");
        }
        return texto;
    }
}
