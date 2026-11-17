package com.blamecinders.combate;

public enum CategoriaInimigo {
    FRACO("Fraco", 5, 8),
    MEDIO_BAIXO("Médio-baixo", 9, 12),
    MEDIO_ALTO("Médio-alto", 13, 16),
    MEDIO_DIFICIL("Médio-difícil", 17, 20);

    private final String nomeExibicao;
    private final int vidaMinima;
    private final int vidaMaxima;

    CategoriaInimigo(String nomeExibicao, int vidaMinima, int vidaMaxima) {
        this.nomeExibicao = nomeExibicao;
        this.vidaMinima = vidaMinima;
        this.vidaMaxima = vidaMaxima;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    public int getVidaMinima() {
        return vidaMinima;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public boolean contem(int vida) {
        return vida >= vidaMinima && vida <= vidaMaxima;
    }

    public static CategoriaInimigo porVida(int vida) {
        if (vida <= 0) {
            throw new IllegalArgumentException("A vida do inimigo deve ser positiva.");
        }
        if (vida <= FRACO.vidaMaxima) return FRACO;
        if (vida <= MEDIO_BAIXO.vidaMaxima) return MEDIO_BAIXO;
        if (vida <= MEDIO_ALTO.vidaMaxima) return MEDIO_ALTO;
        return MEDIO_DIFICIL;
    }
}
