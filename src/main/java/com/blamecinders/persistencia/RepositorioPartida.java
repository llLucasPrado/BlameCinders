package com.blamecinders.persistencia;

import com.badlogic.gdx.utils.Json;
import com.blamecinders.aplicacao.EstadoPartida;

import java.util.Objects;

public final class RepositorioPartida {

    private final ArmazenamentoPartida armazenamento;
    private final Json json;

    public RepositorioPartida() {
        this(
            new ArmazenamentoArquivoPartida(new ArmazenamentoPreferenciasPartida()),
            new Json()
        );
    }

    RepositorioPartida(ArmazenamentoPartida armazenamento, Json json) {
        this.armazenamento = Objects.requireNonNull(armazenamento, "armazenamento");
        this.json = Objects.requireNonNull(json, "json");
    }

    public boolean existe() {
        try {
            if (armazenamento.ler().trim().isEmpty()) return false;
            return carregar() != null;
        } catch (RuntimeException erro) {
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
            return converter(conteudo);
        } catch (RuntimeException erroPrincipal) {
            String conteudoBackup = armazenamento.lerBackup();
            if (!conteudoBackup.trim().isEmpty()) {
                EstadoPartida partidaRecuperada;
                try {
                    partidaRecuperada = converter(conteudoBackup);
                } catch (RuntimeException erroBackup) {
                    erroPrincipal.addSuppressed(erroBackup);
                    throw new IllegalStateException(
                        "Não foi possível carregar a partida salva.",
                        erroPrincipal
                    );
                }
                try {
                    armazenamento.restaurarBackup();
                } catch (RuntimeException erroRestauracao) {
                    erroPrincipal.addSuppressed(erroRestauracao);
                }
                return partidaRecuperada;
            }
            throw new IllegalStateException(
                "Não foi possível carregar a partida salva.",
                erroPrincipal
            );
        }
    }

    public void remover() {
        armazenamento.remover();
    }

    private EstadoPartida converter(String conteudo) {
        DadosPartida dados = json.fromJson(DadosPartida.class, conteudo);
        return ConversorDadosPartida.paraPartida(dados);
    }
}
