package com.blamecinders.aplicacao;

import com.blamecinders.combate.ResultadoCombate;
import com.blamecinders.combate.ResultadoFurtividade;

import java.util.Objects;

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

    static ResultadoEncontroInimigo furtividade(ResultadoFurtividade resultado) {
        DesfechoInimigo desfecho = resultado.isSucesso()
            ? DesfechoInimigo.FURTIVIDADE_SUCESSO
            : DesfechoInimigo.FURTIVIDADE_FALHOU;
        String mensagem = resultado.isSucesso()
            ? "Furtividade bem-sucedida (" + resultado.getChancePercentual()
                + "% de chance). Você evitou o combate."
            : "Furtividade falhou (" + resultado.getChancePercentual()
                + "% de chance). Lute ou recue.";
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
