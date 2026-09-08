package com.blamecinders.fluxo;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.combate.Jogador;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.tabuleiro.TipoCarta;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.ui.carta.TextoCarta;
import com.blamecinders.ui.tabuleiro.CartaVisual;
import com.blamecinders.ui.tabuleiro.TelaTabuleiro;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;
import java.util.function.Consumer;

/** Coordena revelação, consulta e ações das cartas fora da classe principal. */
public final class FluxoInteracaoCarta {

    @FunctionalInterface
    public interface AcaoCarta {
        void executar(int linha, int coluna, CartaVisual cartaOriginal);
    }

    private final Stage stageCartaZoom;
    private final Tabuleiro tabuleiro;
    private final Jogador jogador;
    private final TelaTabuleiro telaTabuleiro;
    private final AnimacaoCarta animacaoCarta;
    private final GerenciadorPopups popupManager;
    private final FluxoCarta fluxoRevelacao;
    private final AcaoCarta abrirCombate;
    private final AcaoCarta coletarChama;
    private final AcaoCarta coletarBau;
    private final Consumer<String> mostrarMensagem;
    private final Runnable fecharModal;
    private final Runnable registrarAlteracao;

    public FluxoInteracaoCarta(
        Stage stageCartaZoom,
        Tabuleiro tabuleiro,
        Jogador jogador,
        TelaTabuleiro telaTabuleiro,
        AnimacaoCarta animacaoCarta,
        GerenciadorPopups popupManager,
        FluxoCarta fluxoRevelacao,
        AcaoCarta abrirCombate,
        AcaoCarta coletarChama,
        AcaoCarta coletarBau,
        Consumer<String> mostrarMensagem,
        Runnable fecharModal,
        Runnable registrarAlteracao
    ) {
        this.stageCartaZoom = Objects.requireNonNull(stageCartaZoom, "stageCartaZoom");
        this.tabuleiro = Objects.requireNonNull(tabuleiro, "tabuleiro");
        this.jogador = Objects.requireNonNull(jogador, "jogador");
        this.telaTabuleiro = Objects.requireNonNull(telaTabuleiro, "telaTabuleiro");
        this.animacaoCarta = Objects.requireNonNull(animacaoCarta, "animacaoCarta");
        this.popupManager = Objects.requireNonNull(popupManager, "popupManager");
        this.fluxoRevelacao = Objects.requireNonNull(fluxoRevelacao, "fluxoRevelacao");
        this.abrirCombate = Objects.requireNonNull(abrirCombate, "abrirCombate");
        this.coletarChama = Objects.requireNonNull(coletarChama, "coletarChama");
        this.coletarBau = Objects.requireNonNull(coletarBau, "coletarBau");
        this.mostrarMensagem = Objects.requireNonNull(mostrarMensagem, "mostrarMensagem");
        this.fecharModal = Objects.requireNonNull(fecharModal, "fecharModal");
        this.registrarAlteracao = Objects.requireNonNull(
            registrarAlteracao,
            "registrarAlteracao"
        );
    }

    public void revelar(int linha, int coluna, CartaVisual cartaOriginal) {
        fluxoRevelacao.revelarCarta(
            linha,
            coluna,
            cartaOriginal,
            tipo -> concluirRevelacao(linha, coluna, cartaOriginal, tipo)
        );
    }

    public void visualizar(int linha, int coluna) {
        CartaInfo cartaInfo = tabuleiro.getCartaInfo(linha, coluna);
        if (cartaInfo == null) {
            fecharModal.run();
            mostrarMensagem.accept("Não há carta nesta posição.");
            return;
        }

        CartaVisual cartaOriginal = telaTabuleiro.getCarta(linha, coluna);
        if (cartaOriginal == null) {
            fecharModal.run();
            return;
        }

        String textura = telaTabuleiro.getIdentificador(linha, coluna);
        CartaVisual cartaZoom = prepararCartaZoom(cartaOriginal);
        montarCamadaZoom(cartaZoom);
        animacaoCarta.aplicarIdleFlutuacao(cartaZoom);
        animacaoCarta.aplicarFlip(
            cartaZoom,
            () -> cartaZoom.setConteudo(GerenciadorTexturas.get(textura), textura),
            () -> popupManager.mostrarPopupMensagem(
                TextoCarta.montar(cartaInfo),
                () -> dissolverERestaurar(cartaZoom, linha, coluna, cartaOriginal, null)
            )
        );
    }

    public void mostrarOpcoes(int linha, int coluna, CartaVisual cartaOriginal) {
        CartaInfo cartaInfo = tabuleiro.getCartaInfo(linha, coluna);
        if (cartaInfo == null || cartaOriginal == null) {
            fecharModal.run();
            if (cartaInfo == null) mostrarMensagem.accept("Não há carta nesta posição.");
            return;
        }

        String textura = telaTabuleiro.getIdentificador(linha, coluna);
        CartaVisual cartaZoom = prepararCartaZoom(cartaOriginal);
        montarCamadaZoom(cartaZoom);
        animacaoCarta.aplicarIdleFlutuacao(cartaZoom);
        animacaoCarta.aplicarFlip(
            cartaZoom,
            () -> cartaZoom.setConteudo(GerenciadorTexturas.get(textura), textura),
            () -> mostrarOpcoesPorTipo(linha, coluna, cartaOriginal, cartaZoom, cartaInfo)
        );
    }

    private void concluirRevelacao(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        TipoCarta tipo
    ) {
        if (tipo == TipoCarta.VAZIO) {
            concluirModal(() -> restaurarCarta(linha, coluna, cartaOriginal));
            mostrarMensagem.accept("Não há nada nesta posição.");
            return;
        }

        registrarAlteracao.run();
        CartaInfo cartaInfo = tabuleiro.getCartaInfo(linha, coluna);
        popupManager.mostrarPopupMensagem(
            TextoCarta.montar(cartaInfo),
            () -> fecharCartaRevelada(linha, coluna, cartaOriginal)
        );
    }

    private void fecharCartaRevelada(int linha, int coluna, CartaVisual cartaOriginal) {
        CartaVisual cartaZoom = fluxoRevelacao.getCartaZoomAtual();
        Runnable finalizar = () -> concluirModal(() -> {
            restaurarCarta(linha, coluna, cartaOriginal);
            mostrarMensagem.accept("Carta revelada. Clique novamente para interagir.");
        });

        if (cartaZoom == null) {
            finalizar.run();
        } else {
            animacaoCarta.dissolverCartaZoom(cartaZoom, finalizar);
        }
    }

    private void mostrarOpcoesPorTipo(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        CartaVisual cartaZoom,
        CartaInfo cartaInfo
    ) {
        switch (cartaInfo.getTipo()) {
            case INIMIGO:
                popupManager.mostrarPopupCartaReveladaComAcao(
                    TextoCarta.montar(cartaInfo),
                    "Combater",
                    () -> abrirCombate.executar(linha, coluna, cartaOriginal),
                    () -> dissolverERestaurar(cartaZoom, linha, coluna, cartaOriginal, null)
                );
                break;
            case BAU:
                popupManager.mostrarPopupCartaReveladaComAcao(
                    TextoCarta.montar(cartaInfo),
                    "Abrir baú",
                    () -> abrirBau(linha, coluna, cartaOriginal, cartaZoom),
                    () -> dissolverERestaurar(cartaZoom, linha, coluna, cartaOriginal, null)
                );
                break;
            case CHAMA:
                popupManager.mostrarPopupCartaReveladaComAcao(
                    TextoCarta.montar(cartaInfo),
                    "Coletar",
                    () -> dissolverEExecutar(cartaZoom, () -> concluirModal(() -> {
                        telaTabuleiro.recolocarComoPlaceholder(linha, coluna, cartaOriginal);
                        coletarChama.executar(linha, coluna, cartaOriginal);
                    })),
                    () -> dissolverERestaurar(cartaZoom, linha, coluna, cartaOriginal, null)
                );
                break;
            case PAREDE:
                popupManager.mostrarPopupMensagem(
                    TextoCarta.montar(cartaInfo),
                    () -> dissolverERestaurar(cartaZoom, linha, coluna, cartaOriginal, null)
                );
                break;
            case VAZIO:
            default:
                popupManager.mostrarPopupMensagem(
                    "Carta vazia.",
                    () -> dissolverERestaurar(cartaZoom, linha, coluna, cartaOriginal, null)
                );
                break;
        }
    }

    private void abrirBau(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        CartaExibida cartaZoom
    ) {
        CartaInfo cartaInfo = tabuleiro.getCartaInfo(linha, coluna);
        if (cartaInfo == null) {
            concluirModal(() -> restaurarCarta(linha, coluna, cartaOriginal));
            return;
        }

        cartaInfo.registrarAberturaBau();
        registrarAlteracao.run();
        if (cartaInfo.getItemDentro() == null) {
            popupManager.mostrarPopupMensagem(
                "Baú vazio.",
                () -> dissolverERestaurar(cartaZoom, linha, coluna, cartaOriginal, null)
            );
            return;
        }

        String identificadorItem = cartaInfo.getItemDentro().getIdentificadorVisual();
        animacaoCarta.aplicarFlip(
            cartaZoom,
            () -> cartaZoom.setConteudo(
                GerenciadorTexturas.get(identificadorItem),
                identificadorItem
            )
        );

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                popupManager.mostrarDecisaoItemBau(
                    cartaInfo,
                    jogador.getArmaEquipada() != null,
                    () -> dissolverEExecutar(cartaZoom, () -> concluirModal(() -> {
                        telaTabuleiro.recolocarComoPlaceholder(linha, coluna, cartaOriginal);
                        coletarBau.executar(linha, coluna, cartaOriginal);
                    })),
                    () -> dissolverERestaurar(
                        cartaZoom,
                        linha,
                        coluna,
                        cartaOriginal,
                        () -> mostrarMensagem.accept("Você deixou o item no baú.")
                    )
                );
            }
        }, 0.26f);
    }

    private CartaVisual prepararCartaZoom(CartaVisual carta) {
        carta.remove();
        carta.clearActions();
        carta.setSize(300f, 400f);
        carta.setOrigin(Align.center);
        carta.setScale(0.01f);
        carta.setRotation(0f);
        carta.setConteudo(GerenciadorTexturas.get("VERSO"), "VERSO");
        carta.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - carta.getWidth() / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 120f
        );
        return carta;
    }

    private void montarCamadaZoom(CartaVisual cartaZoom) {
        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoom);
    }

    private void dissolverERestaurar(
        CartaExibida cartaZoom,
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        Runnable aoFinalizar
    ) {
        dissolverEExecutar(cartaZoom, () -> concluirModal(() -> {
            restaurarCarta(linha, coluna, cartaOriginal);
            if (aoFinalizar != null) aoFinalizar.run();
        }));
    }

    private void dissolverEExecutar(CartaExibida cartaZoom, Runnable aoFinalizar) {
        animacaoCarta.dissolverCartaZoom(cartaZoom, aoFinalizar);
    }

    private void concluirModal(Runnable aoFinalizar) {
        stageCartaZoom.clear();
        fluxoRevelacao.limparCartaZoomAtual();
        fecharModal.run();
        if (aoFinalizar != null) aoFinalizar.run();
    }

    private void restaurarCarta(int linha, int coluna, CartaVisual cartaOriginal) {
        telaTabuleiro.restaurarCarta(linha, coluna, cartaOriginal);
        telaTabuleiro.sincronizar();
        telaTabuleiro.atualizarDestaques();
    }
}
