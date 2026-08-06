package com.blamecinders.fluxo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.ui.tabuleiro.CartaVisual;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.util.GerenciadorTexturas;
import com.blamecinders.tabuleiro.TipoCarta;
import com.blamecinders.tabuleiro.CartaInfo;
import java.util.Objects;

//Controla o fluxo de revelação visual da carta.

public class FluxoCarta {

    private final Stage stageCartaZoom;
    private final Tabuleiro tabuleiro;
    private final AnimacaoCarta animacaoCarta;
    private final GerenciadorPopups popupManager;
    private final Skin skin;
    private CartaExibida cartaZoomAtual;

    public FluxoCarta(
        Stage stageCartaZoom,
        Tabuleiro tabuleiro,
        AnimacaoCarta animacaoCarta,
        GerenciadorPopups popupManager,
        Skin skin
    ) {
        this.stageCartaZoom = stageCartaZoom;
        this.tabuleiro = tabuleiro;
        this.animacaoCarta = animacaoCarta;
        this.popupManager = popupManager;
        this.skin = skin;
    }

    public CartaExibida getCartaZoomAtual() {
        return cartaZoomAtual;
    }

    public boolean podeRevelar(int jLinha, int jColuna, int linha, int coluna) {
        int dLinha = Math.abs(linha - jLinha);
        int dColuna = Math.abs(coluna - jColuna);
        return (dLinha + dColuna) == 1;
    }

    public void revelarCarta(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        Runnable onInimigo,
        Runnable onChama,
        Runnable onBau,
        Runnable onParede,
        Runnable onVazio,
        java.util.function.Consumer<String> mensagem
    ) {
        TipoCarta tipo = tabuleiro.getCarta(linha, coluna);

        if (tipo == TipoCarta.VAZIO) {
            mensagem.accept("Não há nada nesta posição.");
            onVazio.run();
            return;
        }

        tabuleiro.revelarCarta(linha, coluna);
        cartaOriginal.remove();

        String textura = obterTextura(linha, coluna);
        cartaZoomAtual = criarCartaZoom(textura);

        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoomAtual);

        animacaoCarta.aplicarFlip(
            cartaZoomAtual,
            () -> cartaZoomAtual.setConteudo(GerenciadorTexturas.get(textura), textura)
        );

        animacaoCarta.aplicarIdleFlutuacao(cartaZoomAtual);

        switch (tipo) {
            case INIMIGO:
                mensagem.accept("Inimigo encontrado!");
                onInimigo.run();
                break;

            case CHAMA:
                mensagem.accept("Chama encontrada!");
                onChama.run();
                break;

            case BAU:
                mensagem.accept("Baú encontrado!");
                onBau.run();
                break;

            case PAREDE:
                mensagem.accept("Parede encontrada.");
                onParede.run();
                break;

            default:
                onVazio.run();
                break;
        }
    }

    private CartaExibida criarCartaZoom(String textura) {
        Texture texturaFrente = GerenciadorTexturas.get(textura);
        CartaExibida cartaZoom = new CartaExibida(
            GerenciadorTexturas.get("VERSO"),
            "VERSO",
            skin.getFont("default-font")
        );

        float maxLargura = 300f;
        float maxAltura = 400f;

        float escala = Math.min(
            maxLargura / texturaFrente.getWidth(),
            maxAltura / texturaFrente.getHeight()
        );

        cartaZoom.setSize(
            texturaFrente.getWidth() * escala,
            texturaFrente.getHeight() * escala
        );

        cartaZoom.setOrigin(Align.center);
        cartaZoom.setScale(0.01f);
        cartaZoom.setPosition(640 - cartaZoom.getWidth() / 2f, 360 - 120);

        return cartaZoom;
    }

    private String obterTextura(int linha, int coluna) {
        CartaInfo cartaInfo = tabuleiro.getCartaInfo(linha, coluna);

        if (cartaInfo == null) return "VERSO";

        if (Objects.requireNonNull(cartaInfo.getTipo()) == TipoCarta.INIMIGO) {
            return cartaInfo.getInimigo() != null
                ? cartaInfo.getInimigo().getIdentificadorVisual()
                : "INIMIGO";
        } else if (cartaInfo.getTipo() == TipoCarta.BAU) {
            return "BAÚ";
        } else if (cartaInfo.getTipo() == TipoCarta.CHAMA) {
            return "CHAMA";
        } else if (cartaInfo.getTipo() == TipoCarta.PAREDE) {
            return "PAREDE";
        }
        return "VERSO";
    }

}
