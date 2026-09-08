package com.blamecinders.persistencia;

import com.badlogic.gdx.utils.Json;
import com.blamecinders.aplicacao.EstadoPartida;

import java.util.Objects;

public final class RepositorioPartida {

    private final ArmazenamentoPartida armazenamento;
    private final Json json;

    public RepositorioPartida() {
        this(new ArmazenamentoPreferenciasPartida(), new Json());
    }

    RepositorioPartida(ArmazenamentoPartida armazenamento, Json json) {
        this.armazenamento = Objects.requireNonNull(armazenamento, "armazenamento");
        this.json = Objects.requireNonNull(json, "json");
    }

    public boolean existe() {
        if (armazenamento.ler().trim().isEmpty()) return false;
        try {
            return carregar() != null;
        } catch (IllegalStateException erro) {
            remover();
            return false;
        }
    }

    public void salvar(EstadoPartida partida) {
        if (Objects.requireNonNull(partida, "partida").isFinalizada()) {
            remover();
            return;
        }
        armazenamento.escrever(json.toJson(ConversorDadosPartida.paraDados(partida)));
    }

    public EstadoPartida carregar() {
        String conteudo = armazenamento.ler();
        if (conteudo.trim().isEmpty()) return null;
        try {
            DadosPartida dados = json.fromJson(DadosPartida.class, conteudo);
            return ConversorDadosPartida.paraPartida(dados);
        } catch (RuntimeException erro) {
            throw new IllegalStateException("Não foi possível carregar a partida salva.", erro);
        }
    }

    public void remover() {
        armazenamento.remover();
    }
}
