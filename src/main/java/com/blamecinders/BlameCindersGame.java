package com.blamecinders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.aplicacao.AcaoCliqueCarta;
import com.blamecinders.aplicacao.ControladorEncontro;
import com.blamecinders.aplicacao.ControladorInteracaoCarta;
import com.blamecinders.aplicacao.ControladorTurno;
import com.blamecinders.aplicacao.DesfechoInimigo;
import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.aplicacao.MovimentoTabuleiro;
import com.blamecinders.aplicacao.ResultadoColetaBau;
import com.blamecinders.aplicacao.ResultadoColetaChama;
import com.blamecinders.aplicacao.ResultadoEncontroInimigo;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.combate.Jogador;
import com.blamecinders.configuracao.BalanceamentoJogo;
import com.blamecinders.fluxo.FluxoCarta;
import com.blamecinders.fluxo.FluxoCombate;
import com.blamecinders.fluxo.FluxoInteracaoCarta;
import com.blamecinders.persistencia.RepositorioPartida;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.telas.GerenciadorTelas;
import com.blamecinders.telas.MenuPrincipal;
import com.blamecinders.telas.TelaInicial;
import com.blamecinders.telas.TelaOpcoes;
import com.blamecinders.ui.ControladorHUD;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.ui.TemaJogo;
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
    private FluxoInteracaoCarta fluxoInteracaoCarta;
    private InputMultiplexer multiplexer;

    private GerenciadorTelas gerenciadorTelas;
    private GerenciadorAudio gerenciadorAudio;
    private RepositorioPartida repositorioPartida;

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
        fonteCarta = tema.getFonteCarta();

        gerenciadorAudio = new GerenciadorAudio();
        repositorioPartida = new RepositorioPartida();

        animacaoCarta = new AnimacaoCarta();

        popupManager = new GerenciadorPopups(
            stageUI,
            stageCartaZoom,
            skin,
            gerenciadorAudio
        );

        hudController = new ControladorHUD(
            stageUI,
            skin
        );

        gerenciadorTelas.trocarTela(
            new TelaInicial(
                gerenciadorTelas,
                this::iniciarNovoJogo,
                this::continuarPartida,
                this::abrirOpcoes,
                repositorioPartida::existe,
                gerenciadorAudio,
                skin
            )
        );
    }

    @Override
    public void resize(int width, int height) {

        gerenciadorTelas.redimensionar(width, height);

        if (telaTabuleiro != null) {
            telaTabuleiro.resize(width, height);
        }

        stageUI.getViewport().update(width, height, true);
        stageCartaZoom.getViewport().update(width, height, true);
        stageAnimacao.getViewport().update(width, height, true);
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        if (telaTabuleiro != null) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                if (pauseAberto) {
                    fecharPause();
                } else {
                    abrirPause();
                }
            }

            if (pauseAberto) {
                stageUI.act(delta);
            } else {
                telaTabuleiro.act(delta);
                stageUI.act(delta);
                stageCartaZoom.act(delta);
                stageAnimacao.act(delta);
            }

            if (telaModalAberta) {

                telaTabuleiro.draw();
                stageCartaZoom.draw();
                stageAnimacao.draw();
                stageUI.draw();

            } else {

                telaTabuleiro.draw();
                stageUI.draw();
                stageCartaZoom.draw();
                stageAnimacao.draw();
            }

        } else {

            gerenciadorTelas.render(delta);
        }
    }

    @Override
    public void dispose() {
        salvarPartidaAtual();
        cancelarLimpezaMensagem();
        Timer.instance().clear();
        if (gerenciadorTelas != null) {
            gerenciadorTelas.encerrarTelaAtual();
        }
        if (telaTabuleiro != null) {
            telaTabuleiro.dispose();
            telaTabuleiro = null;
        }
        if (stageUI != null) stageUI.dispose();
        if (stageCartaZoom != null) stageCartaZoom.dispose();
        if (stageAnimacao != null) stageAnimacao.dispose();
        if (tema != null) tema.dispose();
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
                    () -> fluxoInteracaoCarta.mostrarOpcoes(linha, coluna, cartaOriginal),
                    () -> telaModalAberta = false
                );
                break;

            case VISUALIZAR:
                telaModalAberta = true;
                popupManager.mostrarConfirmacaoVisualizarCarta(
                    () -> fluxoInteracaoCarta.visualizar(linha, coluna),
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
                    () -> fluxoInteracaoCarta.revelar(linha, coluna, cartaOriginal),
                    () -> telaModalAberta = false
                );
                break;

            default:
                throw new IllegalStateException("Ação de clique desconhecida.");
        }
    }

    private void iniciarNovoJogo() {
        repositorioPartida.remover();
        prepararPartida(new EstadoPartida());
    }

    private void continuarPartida() {
        EstadoPartida partidaSalva = repositorioPartida.carregar();
        if (partidaSalva != null) prepararPartida(partidaSalva);
    }

    private void prepararPartida(EstadoPartida partidaInicial) {
        gerenciadorTelas.encerrarTelaAtual();
        limparCamadasPartida();
        criarMensagemUI();
        pauseAberto = false;
        modalAbertaAntesDoPause = false;
        telaModalAberta = false;
        animandoTabuleiro = false;

        partida = partidaInicial;

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
            popupManager
        );

        fluxoInteracaoCarta = new FluxoInteracaoCarta(
            stageCartaZoom,
            tabuleiro(),
            jogador(),
            telaTabuleiro,
            animacaoCarta,
            popupManager,
            fluxoCarta,
            this::abrirCombate,
            (linha, coluna, carta) -> coletarChama(linha, coluna),
            (linha, coluna, carta) -> coletarBau(linha, coluna),
            this::mostrarMensagem,
            () -> telaModalAberta = false,
            this::salvarPartidaAtual
        );

        hudController.criarHUD();
        atualizarHUDCompleto();

        telaTabuleiro.sincronizar();
        telaTabuleiro.atualizarDestaques();
        salvarPartidaAtual();
    }

    private void coletarChama(int linha, int coluna) {
        ResultadoColetaChama resultado = controladorEncontro.coletarChama(linha, coluna);

        atualizarHUDCompleto();
        moverJogadorPara(linha, coluna, () -> {
            if (resultado.isObjetivoConcluido()) {
                int objetivo = BalanceamentoJogo.OBJETIVO_CHAMAS;
                mostrarMensagemFinal(
                    "Você venceu! " + objetivo + "/" + objetivo + " chamas coletadas."
                );
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
                animandoTabuleiro = false;
                salvarPartidaAtual();

                sincronizarTabuleiroVisual();

                // Evita destacar a carta real antes de a temporária terminar de entrar.
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        atualizarDestaqueCartas();
                    }
                }, 0.12f);

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
        if (resultado.isFurtividade()) {
            finalizarFurtividade(linha, coluna, cartaOriginal, resultado);
            return;
        }

        controladorEncontro.concluirInimigo(linha, coluna, resultado);
        switch (resultado.getDesfecho()) {
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
                salvarPartidaAtual();
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

    private void finalizarFurtividade(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        ResultadoEncontroInimigo resultado
    ) {
        stageCartaZoom.clear();
        telaModalAberta = false;
        restaurarCartaOriginal(linha, coluna, cartaOriginal);

        MovimentoTabuleiro movimento = controladorTurno.prepararMovimento(linha, coluna);
        if (!movimento.isValido()) {
            throw new IllegalStateException("A furtividade terminou com uma troca inválida.");
        }

        animandoTabuleiro = true;
        telaTabuleiro.animarTroca(movimento, () -> {
            controladorEncontro.concluirInimigo(linha, coluna, resultado);
            telaTabuleiro.remapearAposTroca(movimento);
            animandoTabuleiro = false;

            sincronizarTabuleiroVisual();
            atualizarHUDCompleto();
            salvarPartidaAtual();
            mostrarMensagem(resultado.getMensagem());

            if (resultado.getDesfecho() == DesfechoInimigo.JOGADOR_DERROTADO) {
                popupManager.mostrarGameOver();
            } else {
                atualizarDestaqueCartas();
            }
        });
    }

    private void abrirPause() {
        if (pauseAberto) {
            return;
        }
        modalAbertaAntesDoPause = telaModalAberta;
        telaModalAberta = true;
        pauseAberto = true;
        Timer.instance().stop();
        priorizarPause();
        popupManager.mostrarPause(
            this::concluirFechamentoPause,
            this::voltarAoMenuPrincipal
        );
    }

    private void fecharPause() {
        if (!pauseAberto) {
            return;
        }
        popupManager.fecharPause(this::concluirFechamentoPause);
    }

    private void concluirFechamentoPause() {
        pauseAberto = false;
        telaModalAberta = modalAbertaAntesDoPause;
        Timer.instance().start();
        restaurarInputJogo();
    }

    private void voltarAoMenuPrincipal() {
        salvarPartidaAtual();
        cancelarLimpezaMensagem();
        Timer.instance().clear();
        Timer.instance().start();
        telaModalAberta = false;
        pauseAberto = false;
        modalAbertaAntesDoPause = false;
        animandoTabuleiro = false;

        if (telaTabuleiro != null) {
            telaTabuleiro.dispose();
            telaTabuleiro = null;
        }

        limparCamadasPartida();
        partida = null;
        controladorTurno = null;
        controladorEncontro = null;
        controladorInteracaoCarta = null;
        fluxoCombate = null;
        fluxoCarta = null;
        fluxoInteracaoCarta = null;
        multiplexer = null;

        abrirMenuPrincipal();
    }

    private void abrirOpcoes() {
        gerenciadorTelas.trocarTela(
            new TelaOpcoes(this::abrirMenuPrincipal, gerenciadorAudio, skin)
        );
    }

    private void abrirMenuPrincipal() {
        gerenciadorTelas.trocarTela(new MenuPrincipal(
            this::iniciarNovoJogo,
            this::continuarPartida,
            this::abrirOpcoes,
            repositorioPartida::existe,
            gerenciadorAudio,
            skin
        ));
    }

    private void limparCamadasPartida() {
        stageUI.clear();
        stageCartaZoom.clear();
        stageAnimacao.clear();
        labelMensagem = null;
    }

    private void salvarPartidaAtual() {
        if (!animandoTabuleiro && repositorioPartida != null && partida != null) {
            repositorioPartida.salvar(partida);
        }
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
