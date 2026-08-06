package com.blamecinders.aplicacao;

import com.blamecinders.item.ItemBau;

public final class ResultadoColetaBau {

    private final ItemBau item;
    private final int vidaCurada;
    private final String mensagem;

    ResultadoColetaBau(ItemBau item, int vidaCurada, String mensagem) {
        this.item = item;
        this.vidaCurada = vidaCurada;
        this.mensagem = mensagem;
    }

    public ItemBau getItem() {
        return item;
    }

    public int getVidaCurada() {
        return vidaCurada;
    }

    public String getMensagem() {
        return mensagem;
    }
}
