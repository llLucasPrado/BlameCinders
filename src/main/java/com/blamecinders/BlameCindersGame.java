package com.blamecinders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.aplicacao.AcaoCliqueCarta;
import com.blamecinders.aplicacao.ControladorEncontro;
import com.blamecinders.aplicacao.ControladorInteracaoCarta;
import com.blamecinders.aplicacao.ControladorTurno;
import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.aplicacao.MovimentoTabuleiro;
import com.blamecinders.aplicacao.ResultadoColetaBau;
import com.blamecinders.aplicacao.ResultadoColetaChama;
import com.blamecinders.aplicacao.ResultadoEncontroInimigo;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.combate.Jogador;
import com.blamecinders.fluxo.FluxoCarta;
import com.blamecinders.fluxo.FluxoCombate;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.tabuleiro.TipoCarta;
import com.blamecinders.telas.AcaoTela;
import com.blamecinders.telas.GerenciadorTelas;
import com.blamecinders.telas.MenuPrincipal;
import com.blamecinders.telas.TelaInicial;
import com.blamecinders.ui.ControladorHUD;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.ui.TemaJogo;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.ui.tabuleiro.CartaVisual;
import com.blamecinders.ui.tabuleiro.InteracaoCartaVisual;
import com.blamecinders.ui.tabuleiro.TelaTabuleiro;
import com.blamecinders.util.GerenciadorTexturas;


/** Coordena o ciclo de vida libGDX e os fluxos visuais da partida. */
public class BlameCindersGame extends ApplicationAdapter implements InteracaoCartaVisual {

    private Stage stageCartaZoom;
    private TelaTabuleiro telaTabuleiro;
    private Stage stageUI;
    private Stage stageAnimacao;

    private BitmapFont fonte;
    private BitmapFont fonteCarta;
    private Label labelMensagem;
    private Skin skin;
    private TemaJogo tema;
    private Timer.Task tarefaLimparMensagem;

    private boolean animandoTabuleiro = false;
    private boolean telaModalAberta = false;
    private boolean pauseAberto = false;
    private boolean modalAbertaAntesDoPause = false;

    private EstadoPartida partida;
    private ControladorTurno controladorTurno;
    private ControladorEncontro controladorEncontro;
    private ControladorInteracaoCarta controladorInteracaoCarta;

    private AnimacaoCarta animacaoCarta;
    private GerenciadorPopups popupManager;
    private ControladorHUD hudController;
    private FluxoCombate fluxoCombate;
    private FluxoCarta fluxoCarta;
    private InputMultiplexer multiplexer;

    private boolean jogoIniciado = false;
    private GerenciadorTelas gerenciadorTelas;
    private GerenciadorAudio gerenciadorAudio;

    public boolean isFinalizado() {
        return partida != null && partida.isFinalizada();
    }

    public boolean isAnimandoTabuleiro() {
        return animandoTabuleiro;
    }

    @Override
    public boolean estaBloqueada() {
        return isFinalizado()
            || animandoTabuleiro
            || telaModalAberta;
    }

    private Tabuleiro tabuleiro() {
        return partida.getTabuleiro();
    }

    private Jogador jogador() {
        return partida.getJogador();
    }

    @Override
    public void create() {

        gerenciadorTelas = new GerenciadorTelas();

        stageUI = new Stage(
            new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720)
        );

        stageCartaZoom = new Stage(
            new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720)
        );

        stageAnimacao = new Stage(
            new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720)
        );

        tema = TemaJogo.criar();
        skin = tema.getSkin();
        fonte = tema.getFonteInterface();
        fonteCarta = tema.getFonteCarta();

        criarMensagemUI();

        gerenciadorAudio = new GerenciadorAudio();

        animacaoCarta = new AnimacaoCarta();

        popupManager = new GerenciadorPopups(
            stageUI,
            stageCartaZoom,
            skin
        );

        hudController = new ControladorHUD(
            stageUI,
            skin
        );

        gerenciadorTelas.trocarTela(
            new TelaInicial(
                gerenciadorTelas,
                new AcaoTela() {

                    @Override
                    public void executar() {
                        iniciarNovoJogo();
                    }
                },
                gerenciadorAudio
            )
        );
    }

    @Override
    public void resize(int width, int height) {

        gerenciadorTelas.redimensionar(width, height);

        stageUI.getViewport().update(width, height, true);
        stageCartaZoom.getViewport().update(width, height, true);
        stageAnimacao.getViewport().update(width, height, true);
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        if (telaTabuleiro != null) {

            // ESC controla o pause somente durante o jogo
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                if (pauseAberto) {
                    fecharPause();
                } else {
                    abrirPause();
                }
            }

            telaTabuleiro.act(delta);

            stageUI.act(delta);
            stageCartaZoom.act(delta);
            stageAnimacao.act(delta);

            if (telaModalAberta) {

                // Durante o pause, o stageUI fica por último
                // para manter o pause acima de tudo.
                telaTabuleiro.draw();
                stageCartaZoom.draw();
                stageAnimacao.draw();
                stageUI.draw();

            } else {

                // Ordem normal da partida
                telaTabuleiro.draw();
                stageUI.draw();
                stageCartaZoom.draw();
                stageAnimacao.draw();
            }

        } else {

            // Menus
            gerenciadorTelas.render(delta);
        }
    }

    @Override
    public void dispose() {
        telaTabuleiro.dispose();
        stageUI.dispose();
        stageCartaZoom.dispose();
        stageAnimacao.dispose();
        tema.dispose();
        GerenciadorTexturas.disposeAll();
        if (gerenciadorAudio != null) {
            gerenciadorAudio.dispose();
        }
    }

    private void criarMensagemUI() {
        labelMensagem = new Label("", skin);
        labelMensagem.setPosition(20, 20);
        stageUI.addActor(labelMensagem);
    }

    @Override
    public void aoClicar(int linha, int coluna) {

        if (isFinalizado() || animandoTabuleiro || telaModalAberta) return;

        CartaVisual cartaOriginal = telaTabuleiro.getCarta(linha, coluna);
        AcaoCliqueCarta acao = controladorInteracaoCarta.decidir(linha, coluna);

        switch (acao) {
            case INTERAGIR:
                telaModalAberta = true;
                popupManager.mostrarConfirmacaoVisualizarCarta(
                    () -> mostrarOpcoesCartaReveladaAdjacente(linha, coluna, cartaOriginal),
                    () -> telaModalAberta = false
                );
                break;

            case VISUALIZAR:
                telaModalAberta = true;
                popupManager.mostrarConfirmacaoVisualizarCarta(
                    () -> visualizarInformacoesCarta(linha, coluna),
                    () -> telaModalAberta = false
                );
                break;

            case BLOQUEAR:
                mostrarMensagem("Você só pode revelar cartas adjacentes.");
                telaTabuleiro.animarMovimentoInvalido(linha, coluna);
                break;

            case REVELAR:
                telaModalAberta = true;
                popupManager.mostrarConfirmacaoCarta(
                    () -> executarFluxoCarta(linha, coluna, cartaOriginal),
                    () -> telaModalAberta = false
                );
                break;

            default:
                throw new IllegalStateException("Ação de clique desconhecida.");
        }
    }

    private void iniciarNovoJogo() {

        partida = new EstadoPartida();

        controladorTurno = new ControladorTurno(partida);
        controladorEncontro = new ControladorEncontro(partida);
        controladorInteracaoCarta = new ControladorInteracaoCarta(partida);

        telaTabuleiro = new TelaTabuleiro(
            tabuleiro(),
            fonteCarta,
            this
        );

        multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(stageCartaZoom);
        multiplexer.addProcessor(stageUI);
        multiplexer.addProcessor(stageAnimacao);
        multiplexer.addProcessor(telaTabuleiro.getStage());

        Gdx.input.setInputProcessor(multiplexer);

        fluxoCombate = new FluxoCombate(
            stageCartaZoom,
            skin,
            animacaoCarta,
            popupManager,
            controladorEncontro
        );

        fluxoCarta = new FluxoCarta(
            stageCartaZoom,
            tabuleiro(),
            animacaoCarta,
            popupManager,
            skin
        );

        hudController.criarHUD();
        atualizarHUDCompleto();

        telaTabuleiro.sincronizar();
        telaTabuleiro.atualizarDestaques();

        gerenciadorTelas.trocarTela(telaTabuleiro);

        jogoIniciado = true;
    }

    private void coletarChama(int linha, int coluna) {
        ResultadoColetaChama resultado = controladorEncontro.coletarChama(linha, coluna);

        atualizarHUDCompleto();
        moverJogadorPara(linha, coluna, () -> {
            if (resultado.isObjetivoConcluido()) {
                mostrarMensagemFinal("Você venceu! 3/3 chamas coletadas.");
            }
        });
    }

    private void coletarBau(int linha, int coluna) {
        ResultadoColetaBau resultado = controladorEncontro.coletarBau(linha, coluna);
        mostrarMensagem(resultado.getMensagem());

        atualizarHUDCompleto();

        moverJogadorPara(linha, coluna);
    }

    private void moverJogadorPara(int linha, int coluna) {
        moverJogadorPara(linha, coluna, null);
    }

    private void moverJogadorPara(int linha, int coluna, Runnable aoFinalizar) {
        MovimentoTabuleiro movimento = controladorTurno.prepararMovimento(linha, coluna);
        if (!movimento.isValido()) {
            mostrarMensagem("Movimento inválido.");
            return;
        }

        atualizarTabuleiroComAnimacao(movimento, aoFinalizar);
    }

    private void atualizarHUDCompleto() {
        hudController.atualizarHUD(jogador(), tabuleiro().getChamasColetadas());
        hudController.setClickArmaListener(this::mostrarPopupDetalheArmaHUD);
    }

    private void mostrarMensagem(String texto) {
        cancelarLimpezaMensagem();
        labelMensagem.setText(texto);

        tarefaLimparMensagem = new Timer.Task() {
            @Override
            public void run() {
                labelMensagem.setText("");
                tarefaLimparMensagem = null;
            }
        };
        Timer.schedule(tarefaLimparMensagem, 2f);
    }

    private void mostrarMensagemFinal(String texto) {
        cancelarLimpezaMensagem();
        labelMensagem.setText(texto);
    }

    private void cancelarLimpezaMensagem() {
        if (tarefaLimparMensagem != null) {
            tarefaLimparMensagem.cancel();
            tarefaLimparMensagem = null;
        }
    }

    private void sincronizarTabuleiroVisual() {
        telaTabuleiro.sincronizar();
    }

    private void restaurarCartaOriginal(int linha, int coluna, CartaVisual cartaOriginal) {
        telaTabuleiro.restaurarCarta(linha, coluna, cartaOriginal);
    }

    private void atualizarDestaqueCartas() {
        telaTabuleiro.atualizarDestaques();
    }

    private void atualizarTabuleiroComAnimacao(
        MovimentoTabuleiro movimento,
        Runnable aoFinalizar
    ) {
        if (animandoTabuleiro) return;

        animandoTabuleiro = true;

        telaTabuleiro.animarMovimento(
            movimento,
            () -> {
                controladorTurno.concluirMovimento(movimento);
                telaTabuleiro.remapearAposEsteira(movimento);

                sincronizarTabuleiroVisual();

                // Evita destacar a carta real antes de a temporária terminar de entrar.
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        atualizarDestaqueCartas();
                    }
                }, 0.12f);

                animandoTabuleiro = false;
                if (aoFinalizar != null) {
                    aoFinalizar.run();
                }
            }
        );
    }

    private void mostrarPopupDetalheArmaHUD() {
        if (jogador() == null || jogador().getArmaEquipada() == null) {
            mostrarMensagem("Nenhuma arma equipada.");
            return;
        }

        telaModalAberta = true;

        popupManager.mostrarDetalheArmaEquipada(
            jogador().getArmaEquipada().getNome(),
            jogador().getArmaEquipada().getDurabilidade(),
            jogador().getArmaEquipada().getIdentificadorVisual(),
            animacaoCarta,
            () -> telaModalAberta = false
        );
    }

    private void recolocarCartaConsumidaComoPlaceholder(int linha, int coluna, CartaVisual cartaOriginal) {
        telaTabuleiro.recolocarComoPlaceholder(linha, coluna, cartaOriginal);
    }

    public boolean isTelaModalAberta() {
        return telaModalAberta;
    }

    private void executarFluxoCarta(int linha, int coluna, CartaVisual cartaOriginal) {
        fluxoCarta.revelarCarta(
            linha,
            coluna,
            cartaOriginal,
            tipo -> concluirRevelacao(linha, coluna, cartaOriginal, tipo)
        );
    }

    private void concluirRevelacao(int linha, int coluna, CartaVisual cartaOriginal, TipoCarta tipo) {
        if (tipo == TipoCarta.VAZIO) {
            stageCartaZoom.clear();
            telaModalAberta = false;
            restaurarCartaOriginal(linha, coluna, cartaOriginal);
            mostrarMensagem("Não há nada nesta posição.");
            return;
        }

        CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);

        if (tipo == TipoCarta.INIMIGO) {
            popupManager.mostrarPopupCartaReveladaComAcao(
                montarTextoInformacoesCarta(cartaInfo),
                "Combater",
                () -> abrirCombate(linha, coluna, cartaOriginal),
                () -> {
                    CartaExibida cartaZoomAtual = fluxoCarta.getCartaZoomAtual();
                    Runnable finalizar = () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                    };
                    if (cartaZoomAtual != null) {
                        animacaoCarta.dissolverCartaZoom(cartaZoomAtual, finalizar);
                    } else {
                        finalizar.run();
                    }
                }
            );
            return;
        }

        if (tipo == TipoCarta.CHAMA) {
            popupManager.mostrarPopupCartaReveladaComAcao(
                montarTextoInformacoesCarta(cartaInfo),
                "Coletar",
                () -> {
                    CartaExibida cartaZoomAtual = fluxoCarta.getCartaZoomAtual();
                    Runnable coletar = () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);
                        coletarChama(linha, coluna);
                    };
                    if (cartaZoomAtual != null) {
                        animacaoCarta.dissolverCartaZoom(cartaZoomAtual, coletar);
                    } else {
                        coletar.run();
                    }
                },
                () -> {
                    CartaExibida cartaZoomAtual = fluxoCarta.getCartaZoomAtual();
                    Runnable finalizar = () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                    };
                    if (cartaZoomAtual != null) {
                        animacaoCarta.dissolverCartaZoom(cartaZoomAtual, finalizar);
                    } else {
                        finalizar.run();
                    }
                }
            );
            return;
        }

        if (tipo == TipoCarta.BAU) {
            popupManager.mostrarPopupCartaReveladaComAcao(
                montarTextoInformacoesCarta(cartaInfo),
                "Abrir baú",
                () -> executarFluxoBauJaRevelado(linha, coluna, cartaOriginal, fluxoCarta.getCartaZoomAtual()),
                () -> {
                    CartaExibida cartaZoomAtual = fluxoCarta.getCartaZoomAtual();
                    Runnable finalizar = () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                    };
                    if (cartaZoomAtual != null) {
                        animacaoCarta.dissolverCartaZoom(cartaZoomAtual, finalizar);
                    } else {
                        finalizar.run();
                    }
                }
            );
            return;
        }

        String informacoes = tipo == TipoCarta.PAREDE
            ? "Parede encontrada.\nNão é possível avançar."
            : montarTextoInformacoesCarta(cartaInfo);

        popupManager.mostrarPopupMensagem(informacoes, () -> {
            CartaExibida cartaZoomAtual = fluxoCarta.getCartaZoomAtual();
            Runnable finalizar = () -> {
                stageCartaZoom.clear();
                telaModalAberta = false;
                restaurarCartaOriginal(linha, coluna, cartaOriginal);
                sincronizarTabuleiroVisual();
                atualizarDestaqueCartas();
                mostrarMensagem("Carta revelada. Clique novamente para interagir.");
            };
            if (cartaZoomAtual != null) {
                animacaoCarta.dissolverCartaZoom(cartaZoomAtual, finalizar);
            } else {
                finalizar.run();
            }
        });
    }

    private void visualizarInformacoesCarta(int linha, int coluna) {
        CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);

        if (cartaInfo == null) {
            telaModalAberta = false;
            mostrarMensagem("Não há carta nesta posição.");
            return;
        }

        CartaVisual cartaOriginal = telaTabuleiro.getCarta(linha, coluna);

        if (cartaOriginal == null) {
            telaModalAberta = false;
            return;
        }

        String textura = telaTabuleiro.getIdentificador(linha, coluna);

        CartaVisual cartaZoom = prepararCartaZoom(cartaOriginal);

        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoom);

        animacaoCarta.aplicarFlip(
            cartaZoom,
            () -> cartaZoom.setConteudo(GerenciadorTexturas.get(textura), textura)
        );

        animacaoCarta.aplicarIdleFlutuacao(cartaZoom);

        popupManager.mostrarPopupMensagem(
            montarTextoInformacoesCarta(cartaInfo),
            () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                stageCartaZoom.clear();
                telaModalAberta = false;
                restaurarCartaOriginal(linha, coluna, cartaOriginal);
                sincronizarTabuleiroVisual();
                atualizarDestaqueCartas();
            })
        );
    }

    private String montarTextoInformacoesCarta(CartaInfo cartaInfo) {
        if (cartaInfo == null) {
            return "Carta desconhecida.";
        }

        if (null != cartaInfo.getTipo()) switch (cartaInfo.getTipo()) {
            case INIMIGO:
                if (cartaInfo.getInimigo() != null) {
                    return cartaInfo.getInimigo().getNome()
                            + "\nVida: " + cartaInfo.getInimigo().getVida();
                }
                return "Inimigo desconhecido.";
                
            case BAU:
            if (cartaInfo.isBauAberto()) {
                if (cartaInfo.getArmaDentro() != null) {
                    return "Baú já aberto."
                        + "\nArma: " + cartaInfo.getArmaDentro().getNome()
                        + "\nDurabilidade: " + cartaInfo.getArmaDentro().getDurabilidade();
                }
                if (cartaInfo.getComidaDentro() != null) {
                    return "Baú já aberto."
                        + "\nComida: " + cartaInfo.getComidaDentro().getNome()
                        + "\nCura: " + cartaInfo.getComidaDentro().getCura();
                }
                return "Baú já aberto e vazio.";
            }
            return "Baú fechado."
                + "\nAbra para descobrir o que há dentro.";

            case CHAMA:
                return "Chama"
                        + "\nColete 3 para vencer.";
            case PAREDE:
                return "Parede"
                        + "\nNão é possível atravessar.";
            default:
                break;
        }
        return "Carta vazia.";
    }

    private void abrirCombate(int linha, int coluna, CartaVisual cartaOriginal) {
        fluxoCombate.mostrarTelaCombate(
            tabuleiro().getCartaInfo(linha, coluna),
            jogador(),
            resultado -> finalizarEncontroInimigo(
                linha,
                coluna,
                cartaOriginal,
                resultado
            ),
            this::mostrarMensagem
        );
    }

    private void finalizarEncontroInimigo(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        ResultadoEncontroInimigo resultado
    ) {
        controladorEncontro.concluirInimigo(linha, coluna, resultado);
        switch (resultado.getDesfecho()) {
            case FURTIVIDADE_SUCESSO:
            case FURTIVIDADE_FALHOU:
            case COMBATE_VENCIDO:
                stageCartaZoom.clear();
                telaModalAberta = false;
                recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);
                moverJogadorPara(linha, coluna);
                atualizarHUDCompleto();
                mostrarMensagem(resultado.getMensagem());
                break;

            case JOGADOR_DERROTADO:
                stageCartaZoom.clear();
                telaModalAberta = false;
                restaurarCartaOriginal(linha, coluna, cartaOriginal);
                mostrarMensagem(resultado.getMensagem());
                popupManager.mostrarGameOver();
                break;

            case RECUO:
                stageCartaZoom.clear();
                telaModalAberta = false;
                restaurarCartaOriginal(linha, coluna, cartaOriginal);
                sincronizarTabuleiroVisual();
                atualizarDestaqueCartas();
                mostrarMensagem(resultado.getMensagem());
                break;

            default:
                throw new IllegalStateException("Desfecho de inimigo desconhecido.");
        }
    }

    private void mostrarOpcoesCartaReveladaAdjacente(int linha, int coluna, CartaVisual cartaOriginal) {
        CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);

        if (cartaInfo == null) {
            telaModalAberta = false;
            mostrarMensagem("Não há carta nesta posição.");
            return;
        }

        if (cartaOriginal == null) {
            telaModalAberta = false;
            return;
        }

        String textura = telaTabuleiro.getIdentificador(linha, coluna);

        CartaVisual cartaZoom = prepararCartaZoom(cartaOriginal);

        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoom);

        animacaoCarta.aplicarFlip(
            cartaZoom,
            () -> cartaZoom.setConteudo(GerenciadorTexturas.get(textura), textura)
        );

        animacaoCarta.aplicarIdleFlutuacao(cartaZoom);

        switch (cartaInfo.getTipo()) {

            case INIMIGO:
                popupManager.mostrarPopupCartaReveladaComAcao(
                    montarTextoInformacoesCarta(cartaInfo),
                    "Combater",

                    () -> abrirCombate(linha, coluna, cartaOriginal),

                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                    })
                );
                break;

            case BAU:
                popupManager.mostrarPopupCartaReveladaComAcao(
                    montarTextoInformacoesCarta(cartaInfo),
                    "Abrir baú",

                    () -> executarFluxoBauJaRevelado(linha, coluna, cartaOriginal, cartaZoom),

                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                    })
                );
                break;

            case CHAMA:
                popupManager.mostrarPopupCartaReveladaComAcao(
                    montarTextoInformacoesCarta(cartaInfo),
                    "Coletar",
                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);
                        coletarChama(linha, coluna);
                    }),
                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                    })
                );
                break;

            case PAREDE:
                popupManager.mostrarPopupMensagem(
                    montarTextoInformacoesCarta(cartaInfo),
                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                    })
                );
                break;

            case VAZIO:
            default:
                popupManager.mostrarPopupMensagem(
                    "Carta vazia.",
                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                    })
                );
                break;
        }
    }

    private void executarFluxoBauJaRevelado(int linha, int coluna, CartaVisual cartaOriginal, CartaExibida cartaZoom) {
        CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);

        if (cartaInfo == null || cartaInfo.getItemDentro() == null) {
            popupManager.mostrarPopupMensagem("Baú vazio.", () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                stageCartaZoom.clear();
                telaModalAberta = false;
                restaurarCartaOriginal(linha, coluna, cartaOriginal);
                sincronizarTabuleiroVisual();
                atualizarDestaqueCartas();
            }));
            return;
        }

        cartaInfo.registrarAberturaBau(); 

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
                boolean jogadorJaTemArma =
                    jogador() != null && jogador().getArmaEquipada() != null;

                popupManager.mostrarDecisaoItemBau(
                    cartaInfo,
                    jogadorJaTemArma,

                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);
                        coletarBau(linha, coluna);
                    }),

                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;

                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                        mostrarMensagem("Você deixou o item no baú.");
                    })
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

    private void abrirPause() {
        if (pauseAberto) {
            return;
        }
        modalAbertaAntesDoPause = telaModalAberta;
        telaModalAberta = true;
        pauseAberto = true;
        priorizarPause();
        popupManager.mostrarPause(
            () -> fecharPause(),
            () -> {
                System.out.println("Opções do pause");
            },
            () -> {
                voltarAoMenuPrincipal();
            }
        );
    }

    private void fecharPause() {
        if (!pauseAberto) {
            return;
        }
        popupManager.fecharPause(() -> {
            pauseAberto = false;
            telaModalAberta = modalAbertaAntesDoPause;
            restaurarInputJogo();
        });
    }

    private void voltarAoMenuPrincipal() {

        telaModalAberta = false;

        // Remove o tabuleiro atual
        if (telaTabuleiro != null) {
            telaTabuleiro.destruir();
            telaTabuleiro = null;
        }

        // Limpa os elementos da partida
        if (stageUI != null) {
            stageUI.clear();
        }

        if (stageCartaZoom != null) {
            stageCartaZoom.clear();
        }

        if (stageAnimacao != null) {
            stageAnimacao.clear();
        }

        // A partida deixa de estar ativa
        jogoIniciado = false;

        // Volta para o menu principal
        gerenciadorTelas.trocarTela(
            new MenuPrincipal(
                gerenciadorTelas,
                this::iniciarNovoJogo,
                gerenciadorAudio
            )
        );
    }

    private void priorizarPause() {

        multiplexer.clear();

        multiplexer.addProcessor(stageUI);
        multiplexer.addProcessor(stageCartaZoom);
        multiplexer.addProcessor(stageAnimacao);
        multiplexer.addProcessor(telaTabuleiro.getStage());
    }

    private void restaurarInputJogo() {

        multiplexer.clear();

        multiplexer.addProcessor(stageCartaZoom);
        multiplexer.addProcessor(stageUI);
        multiplexer.addProcessor(stageAnimacao);
        multiplexer.addProcessor(telaTabuleiro.getStage());
    }

}
