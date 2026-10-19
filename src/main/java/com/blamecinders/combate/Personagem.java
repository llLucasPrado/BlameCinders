package com.blamecinders.combate;

public enum Personagem {
    PERSONAGEM_Vieyra("Vieyra", "Arqueiro: Tem alcance maior de acerto a outras cartas em linha.", 35, 75),
    PERSONAGEM_Taiga("Taiga", "Assassino: Maior chance de sucesso em furtividade", 30, 85),
    PERSONAGEM_Nazaret("Nazaret", "Anão: Tem alcance maior de acerto a outras cartas adjacentes.", 70, 50),
    PERSONAGEM_Aquilla("Aquilla", "Cavaleiro: Não sei ainda...", 50, 70);

    public static final Personagem PADRAO = PERSONAGEM_Vieyra;

    private final String nome;
    private final String descricao;
    private final int vidaInicial;
    private final int chanceFurtividade;

    Personagem(String nome, String descricao, int vidaInicial, int chanceFurtividade) {
        this.nome = nome;
        this.descricao = descricao;
        this.vidaInicial = vidaInicial;
        this.chanceFurtividade = chanceFurtividade;
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public int getVidaInicial() { return vidaInicial; }

    public String getIdentificadorVisual() {
        return "PERSONAGEM_" + name();
    }

    public static Personagem porId(String id) {
        for (Personagem personagem : values()) {
            if (personagem.name().equals(id)) return personagem;
        }
        return PADRAO;
    }

    public int getChanceFurtividade() { return chanceFurtividade; }

}