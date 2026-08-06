package com.blamecinders.tabuleiro;

import com.blamecinders.combate.Inimigo;
import com.blamecinders.item.Arma;
import com.blamecinders.item.Comida;
import com.blamecinders.item.ItemBau;

public class CartaInfo {

    private final TipoCarta tipo;
    private EstadoCarta estado;
    private Inimigo inimigo;
    private ItemBau itemDentro;
    private boolean furtividadeTentada;

    public CartaInfo(TipoCarta tipo) {
        this.tipo = tipo;
        this.estado = EstadoCarta.FECHADA;
    }

    public TipoCarta getTipo() {
        return tipo;
    }

    public EstadoCarta getEstado() {
        return estado;
    }

    public void setEstado(EstadoCarta estado) {
        this.estado = estado;
    }

    public Inimigo getInimigo() {
        return inimigo;
    }

    public void setInimigo(Inimigo inimigo) {
        this.inimigo = inimigo;
    }

    public Arma getArmaDentro() {
        return itemDentro instanceof Arma ? (Arma) itemDentro : null;
    }

    public void setArmaDentro(Arma armaDentro) {
        this.itemDentro = armaDentro;
    }

    public Comida getComidaDentro() {
        return itemDentro instanceof Comida ? (Comida) itemDentro : null;
    }

    public ItemBau getItemDentro() {
        return itemDentro;
    }

    public void setItemDentro(ItemBau itemDentro) {
        this.itemDentro = itemDentro;
    }

    public boolean isFurtividadeTentada() {
        return furtividadeTentada;
    }

    public void registrarTentativaFurtividade() {
        this.furtividadeTentada = true;
    }
}
