package com.blamecinders.fluxo;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.ui.tabuleiro.CartaVisual;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.ui.carta.IdentificadorVisualCarta;
import com.blamecinders.util.GerenciadorTexturas;
import com.blamecinders.tabuleiro.TipoCarta;
import java.util.Objects;
import java.util.function.Consumer;

/** Coordena a revelação visual de uma carta. */
public class FluxoCarta {

    private final Stage stageCartaZoom;
    private final Tabuleiro tabuleiro;
    private final AnimacaoCarta animacaoCarta;
    private final GerenciadorPopups popupManager;
    private CartaVisual cartaZoomAtual;

    public FluxoCarta(
        Stage stageCartaZoom,
        Tabuleiro tabuleiro,
        AnimacaoCarta animacaoCarta,
        GerenciadorPopups popupManager
    ) {
        this.stageCartaZoom = Objects.requireNonNull(stageCartaZoom, "stageCartaZoom");
        this.tabuleiro = Objects.requireNonNull(tabuleiro, "tabuleiro");
        this.animacaoCarta = Objects.requireNonNull(animacaoCarta, "animacaoCarta");
        this.popupManager = Objects.requireNonNull(popupManager, "popupManager");
    }

    public CartaVisual getCartaZoomAtual() {
        return cartaZoomAtual;
    }

    public void limparCartaZoomAtual() {
        cartaZoomAtual = null;
    }

    public void revelarCarta(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        Consumer<TipoCarta> aoRevelar
    ) {
        Objects.requireNonNull(cartaOriginal, "cartaOriginal");
        Objects.requireNonNull(aoRevelar, "aoRevelar");
        TipoCarta tipo = tabuleiro.getCarta(linha, coluna);

        if (tipo == TipoCarta.VAZIO) {
            aoRevelar.accept(tipo);
            return;
        }

        tabuleiro.revelarCarta(linha, coluna);
        String textura = IdentificadorVisualCarta.obter(tabuleiro.getCartaInfo(linha, coluna));
        cartaZoomAtual = cartaOriginal;
        prepararCartaZoom(cartaZoomAtual);

        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoomAtual);

        animacaoCarta.aplicarFlip(
            cartaZoomAtual,
            () -> cartaZoomAtual.setConteudo(GerenciadorTexturas.get(textura), textura),
            () -> aoRevelar.accept(tipo)
        );

        animacaoCarta.aplicarIdleFlutuacao(cartaZoomAtual);
    }

    /** Move a carta existente para a camada modal, sem criar outro ator visual. */
    private void prepararCartaZoom(CartaVisual carta) {
        carta.remove();
        carta.clearActions();
        carta.setSize(300f, 400f);
        carta.setOrigin(Align.center);
        carta.setScale(0.01f);
        carta.setRotation(0f);
        carta.setPosition(640f - carta.getWidth() / 2f, 360f - 120f);
        carta.setConteudo(GerenciadorTexturas.get("VERSO"), "VERSO");
    }

}
