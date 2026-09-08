package com.blamecinders.aplicacao;

import java.util.Objects;

import com.blamecinders.combate.ResultadoCombate;
import com.blamecinders.combate.ResultadoFurtividade;

public final class ResultadoEncontroInimigo {

    private final DesfechoInimigo desfecho;
    private final String mensagem;
    private final ResultadoCombate combate;
    private final ResultadoFurtividade furtividade;
    private final int danoRecebido;

    private ResultadoEncontroInimigo(
        DesfechoInimigo desfecho,
        String mensagem,
        ResultadoCombate combate,
        ResultadoFurtividade furtividade,
        int danoRecebido
    ) {
        this.desfecho = Objects.requireNonNull(desfecho, "desfecho");
        this.mensagem = Objects.requireNonNull(mensagem, "mensagem");
        this.combate = combate;
        this.furtividade = furtividade;
        this.danoRecebido = Math.max(0, danoRecebido);
    }

    public static ResultadoEncontroInimigo recuo() {
        return new ResultadoEncontroInimigo(
            DesfechoInimigo.RECUO,
            "Você recuou.",
            null,
            null,
            0
        );
    }

    static ResultadoEncontroInimigo furtividade(ResultadoFurtividade resultado, int dano) {
        DesfechoInimigo desfecho = resultado.isSucesso()
            ? DesfechoInimigo.FURTIVIDADE_SUCESSO
            : DesfechoInimigo.FURTIVIDADE_FALHOU;
        String mensagem = resultado.isSucesso()
            ? "Furtividade bem-sucedida (" + resultado.getChancePercentual()
                + "% de chance). Você trocou de posição sem sofrer dano."
            : "Furtividade falhou (" + resultado.getChancePercentual()
                + "% de chance). Você trocou de posição e sofreu " + dano
                + " de dano direto.";
        return new ResultadoEncontroInimigo(desfecho, mensagem, null, resultado, dano);
    }

    static ResultadoEncontroInimigo combate(ResultadoCombate resultado) {
        return new ResultadoEncontroInimigo(
            resultado.isJogadorVenceu()
                ? DesfechoInimigo.COMBATE_VENCIDO
                : DesfechoInimigo.JOGADOR_DERROTADO,
            resultado.getMensagemResultado(),
            resultado,
            null,
            resultado.getVidaInicialJogador() - resultado.getVidaFinalJogador()
        );
    }

    static ResultadoEncontroInimigo derrotaPorFurtividade(ResultadoFurtividade resultado, int dano) {
        return new ResultadoEncontroInimigo(
            DesfechoInimigo.JOGADOR_DERROTADO,
            "Furtividade falhou (" + resultado.getChancePercentual()
                + "% de chance). Você trocou de posição, sofreu " + dano
                + " de dano direto e foi derrotado.",
            null,
            resultado,
            dano
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

    public int getDanoRecebido() {
        return danoRecebido;
    }

    public boolean isFurtividade() {
        return furtividade != null;
    }

    public boolean isTerminal() {
        return true;
    }
}
