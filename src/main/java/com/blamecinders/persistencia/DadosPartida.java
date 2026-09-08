package com.blamecinders.persistencia;

import com.blamecinders.tabuleiro.EstadoCarta;
import com.blamecinders.tabuleiro.TipoCarta;

public final class DadosPartida {

    public int versao;
    public int jogadorLinha;
    public int jogadorColuna;
    public int chamasColetadas;
    public int vidaAtual;
    public int vidaMaxima;
    public ItemSalvo armaEquipada;
    public CartaSalva[] cartas;

    public static final class CartaSalva {
        public TipoCarta tipo;
        public EstadoCarta estado;
        public InimigoSalvo inimigo;
        public ItemSalvo item;
        public boolean furtividadeTentada;
        public boolean bauAberto;
    }

    public static final class InimigoSalvo {
        public String nome;
        public String identificadorVisual;
        public int vida;
        public int dificuldadeFurtividade;
    }

    public static final class ItemSalvo {
        public TipoItemSalvo tipo;
        public String nome;
        public String identificadorVisual;
        public int valor;
    }

    public enum TipoItemSalvo {
        ARMA,
        COMIDA
    }
}
