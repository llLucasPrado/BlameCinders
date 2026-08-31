package com.blamecinders.aplicacao;

import java.util.Objects;

import com.blamecinders.combate.ResultadoCombate;
import com.blamecinders.combate.ResultadoFurtividade;

public final class ResultadoEncontroInimigo {

    private final DesfechoInimigo desfecho;
    private final String mensagem;
    private final ResultadoCombate combate;
    private final ResultadoFurtividade furtividade;

    private ResultadoEncontroInimigo(
        DesfechoInimigo desfecho,
        String mensagem,
        ResultadoCombate combate,
        ResultadoFurtividade furtividade
    ) {
        this.desfecho = Objects.requireNonNull(desfecho, "desfecho");
        this.mensagem = Objects.requireNonNull(mensagem, "mensagem");
        this.combate = combate;
        this.furtividade = furtividade;
    }

    public static ResultadoEncontroInimigo recuo() {
        return new ResultadoEncontroInimigo(
            DesfechoInimigo.RECUO,
            "Você recuou.",
            null,
            null
        );
    }

    static ResultadoEncontroInimigo furtividade(ResultadoFurtividade resultado, int dano) {
        DesfechoInimigo desfecho = resultado.isSucesso()
            ? DesfechoInimigo.FURTIVIDADE_SUCESSO
            : DesfechoInimigo.FURTIVIDADE_FALHOU;
        String mensagem = resultado.isSucesso()
            ? "Furtividade bem-sucedida (" + resultado.getChancePercentual()
                + "% de chance). Você sofreu " + dano + " de dano."
            : "Furtividade falhou (" + resultado.getChancePercentual()
                + "% de chance). Você sofreu " + dano + " de dano. Lute ou recue.";
        return new ResultadoEncontroInimigo(desfecho, mensagem, null, resultado);
    }

    static ResultadoEncontroInimigo combate(ResultadoCombate resultado) {
        return new ResultadoEncontroInimigo(
            resultado.isJogadorVenceu()
                ? DesfechoInimigo.COMBATE_VENCIDO
                : DesfechoInimigo.JOGADOR_DERROTADO,
            resultado.getMensagemResultado(),
            resultado,
            null
        );
    }

    static ResultadoEncontroInimigo derrotaPorFurtividade(ResultadoFurtividade resultado, int dano) {
        return new ResultadoEncontroInimigo(
            DesfechoInimigo.JOGADOR_DERROTADO,
            "Furtividade falhou (" + resultado.getChancePercentual()
                + "% de chance). Você sofreu " + dano + " de dano e foi derrotado.",
            null,
            resultado
        );
    }

    public DesfechoInimigo getDesfecho() {
        return desfecho;
    }

    public String getMensagem() {
        return mensagem;
    }

    public ResultadoCombate getCombate() {
        return combate;
    }

    public ResultadoFurtividade getFurtividade() {
        return furtividade;
    }

    public boolean isTerminal() {
        return desfecho != DesfechoInimigo.FURTIVIDADE_FALHOU;
    }
}
