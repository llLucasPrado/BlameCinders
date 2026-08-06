package com.blamecinders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.blamecinders.combate.Jogador;
import com.blamecinders.fluxo.FluxoCarta;
import com.blamecinders.fluxo.FluxoCombate;
import com.blamecinders.ui.ControladorHUD;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.ui.TemaJogo;
import com.blamecinders.util.GerenciadorTexturas;
import com.blamecinders.tabuleiro.TipoCarta;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.ui.tabuleiro.CartaVisual;
import com.blamecinders.ui.tabuleiro.InteracaoCartaVisual;
import com.blamecinders.ui.tabuleiro.TelaTabuleiro;


//Classe principal do jogo.

//RESPONSABILIDADE DESTA CLASSE:
//inicializar o jogo e os stages, criar o tabuleiro visual, orquestrar os controladores extraídos (HUD, popups, fluxo de cartas e combate);
//manter o estado global da run;
//sincronizar o grid lógico com o grid visual;
//iniciar animação de movimento/esteira.

public class BlameCindersGame extends ApplicationAdapter implements InteracaoCartaVisual {

    //Stages
    private Stage stageCartaZoom;
    private TelaTabuleiro telaTabuleiro;
    private Stage stageUI;
    private Stage stageAnimacao;

    //UI base
    private BitmapFont fonte;
    private BitmapFont fonteCarta;
    private Label labelMensagem;
    private Skin skin;
    private TemaJogo tema;
    private Timer.Task tarefaLimparMensagem;

    //Estado transitório da apresentação
    private boolean animandoTabuleiro = false;
    private boolean telaModalAberta = false;

    //Estado e regras persistentes da partida
    private EstadoPartida partida;
    private ControladorTurno controladorTurno;
    private ControladorEncontro controladorEncontro;
    private ControladorInteracaoCarta controladorInteracaoCarta;

    //Controladores / fluxos extraídos
    private AnimacaoCarta animacaoCarta;
    private GerenciadorPopups popupManager;
    private ControladorHUD hudController;
    private FluxoCombate fluxoCombate;
    private FluxoCarta fluxoCarta;

    //Informa se o jogo terminou, usado pelas cartas visuais para bloquear interação.
    public boolean isFinalizado() {
        return partida != null && partida.isFinalizada();
    }

    //Informa se o tabuleiro está animando, usado pelas cartas visuais para bloquear clique durante esteira/movimento.
    public boolean isAnimandoTabuleiro() {
        return animandoTabuleiro;
    }

    @Override
    public boolean estaBloqueada() {
        return isFinalizado() || animandoTabuleiro || telaModalAberta;
    }

    private Tabuleiro tabuleiro() {
        return partida.getTabuleiro();
    }

    private Jogador jogador() {
        return partida.getJogador();
    }

    //Ciclo de vida
    @Override
    public void create() {

        //Criação dos stages
        stageUI = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720));
        stageCartaZoom = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720));
        stageAnimacao = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720));

        //Fontes / skin base
        tema = TemaJogo.criar();
        skin = tema.getSkin();
        fonte = tema.getFonteInterface();
        fonteCarta = tema.getFonteCarta();
        criarMensagemUI();

        //Modelo principal
        partida = new EstadoPartida();
        controladorTurno = new ControladorTurno(partida);
        controladorEncontro = new ControladorEncontro(partida);
        controladorInteracaoCarta = new ControladorInteracaoCarta(partida);

        telaTabuleiro = new TelaTabuleiro(tabuleiro(), fonteCarta, this);

        //Input multiplexer, Ordem importante: zoom > UI > animação > tabuleiro
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageCartaZoom);
        multiplexer.addProcessor(stageUI);
        multiplexer.addProcessor(stageAnimacao);
        multiplexer.addProcessor(telaTabuleiro.getStage());
        Gdx.input.setInputProcessor(multiplexer);

        //Controladores extraídos
        animacaoCarta = new AnimacaoCarta();

        popupManager = new GerenciadorPopups(stageUI, stageCartaZoom, skin);
        hudController = new ControladorHUD(stageUI, skin);

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

        //HUD
        hudController.criarHUD();
        atualizarHUDCompleto();

        //Estado visual inicial
        telaTabuleiro.sincronizar();
        telaTabuleiro.atualizarDestaques();
    }

    //controla o tamanho e proporção da tela
    @Override
    public void resize(int width, int height) {
        telaTabuleiro.resize(width, height);
        stageUI.getViewport().update(width, height, true);
        stageCartaZoom.getViewport().update(width, height, true);
        stageAnimacao.getViewport().update(width, height, true);
    }

    //Renderiza os Stages, que define onde cada coisa é desenhada
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        telaTabuleiro.act(delta);
        stageUI.act(delta);
        stageCartaZoom.act(delta);
        stageAnimacao.act(delta);

        telaTabuleiro.draw();
        stageUI.draw();
        stageCartaZoom.draw();
        stageAnimacao.draw();
    }

    @Override
    public void dispose() {
        telaTabuleiro.dispose();
        stageUI.dispose();
        stageCartaZoom.dispose();
        stageAnimacao.dispose();
        tema.dispose();
        GerenciadorTexturas.disposeAll();
    }

    // A fonte e os estilos pertencem ao TemaJogo; a aplicação apenas posiciona a mensagem.
    private void criarMensagemUI() {
        labelMensagem = new Label("", skin);
        labelMensagem.setPosition(20, 20);
        stageUI.addActor(labelMensagem);
    }

    //Entrada principal de clique em carta.
    //Regras:
    //carta fechada adjacente: pergunta se deseja revelar;
    //carta fechada não adjacente: bloqueia;
    //carta revelada não adjacente: permite apenas visualizar;
    //carta revelada adjacente: permite visualizar e, se aplicável, interagir.
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

    // FLUXOS DE COLETA / MOVIMENTO
    //Trata a coleta de chama.
    //Regras:
    //incrementa as chamas no tabuleiro, consome a carta da posição, atualiza HUD; verifica vitória; move jogador com animação/esteira.
    private void coletarChama(int linha, int coluna) {
        ResultadoColetaChama resultado = controladorEncontro.coletarChama(linha, coluna);

        atualizarHUDCompleto();
        moverJogadorPara(linha, coluna, () -> {
            if (resultado.isObjetivoConcluido()) {
                mostrarMensagemFinal("Você venceu! 3/3 chamas coletadas.");
            }
        });
    }

    //Trata o fluxo completo do baú, se houver arma, pergunta se deseja equipar/trocar, se equipar, consome o baú e move o jogador;
    //se não equipar, mantém o baú no tabuleiro
    //Etapa final do baú:
    //equipa a arma (se houver), consome a carta e move o jogador para a posição.
    private void coletarBau(int linha, int coluna) {
        ResultadoColetaBau resultado = controladorEncontro.coletarBau(linha, coluna);
        mostrarMensagem(resultado.getMensagem());

        atualizarHUDCompleto();

        moverJogadorPara(linha, coluna);
    }

    //Encapsula a movimentação do jogador para uma nova posição, sempre usando a animação de esteira.
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

    //Atualiza o HUD e reinstala o click da miniatura de arma.
    private void atualizarHUDCompleto() {
        hudController.atualizarHUD(jogador(), tabuleiro().getChamasColetadas());
        hudController.setClickArmaListener(this::mostrarPopupDetalheArmaHUD);
    }

    // MENSAGENS E FONTES
    //Exibe mensagem temporária no canto inferior da UI.
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

    // SINCRONIZAÇÃO VISUAL
    //Sincroniza todas as cartas visuais com o estado lógico do tabuleiro, este é um dos métodos mais importantes da classe.
    //Ele garante, posição correta, textura correta, reset de transformações, revelação correta, prioridade visual do jogador.
    private void sincronizarTabuleiroVisual() {
        telaTabuleiro.sincronizar();
    }

    //Restaura uma carta visual removida temporariamente do stage durante o zoom/revelação.
    private void restaurarCartaOriginal(int linha, int coluna, CartaVisual cartaOriginal) {
        telaTabuleiro.restaurarCarta(linha, coluna, cartaOriginal);
    }

    //Atualiza o destaque das cartas adjacentes ao jogador.
    //Regras visuais:
    //o jogador fica normal;
    //cartas adjacentes entram gradualmente no destaque;
    //depois começam a pulsar suavemente;
    //cartas não adjacentes perdem destaque suavemente.
    //Deve ser chamado depois do movimento/sincronização.
    //Ele limpa ações antigas para evitar pulso preso em cartas antigas.
    private void atualizarDestaqueCartas() {
        telaTabuleiro.atualizarDestaques();
    }

    // MOVIMENTO + ESTEIRA
    //Executa a animação de movimento do jogador com esteira.
    //A ordem lógica é:
    //1. animação visual;
    //2. mover jogador no grid;
    //3. aplicar esteira;
    //4. sincronizar visual;
    //5. resetar estado visual;
    //6. reabilitar o jogo.
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

                /*
                 * Aguarda a carta temporária terminar de entrar antes de aplicar destaque.
                 * Isso evita a piscada brusca na carta nova.
                 */
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

    // POPUP DE DETALHE DA ARMA
    //Exibe a arma equipada em destaque.
    //Este popup continua local porque faz parte da integração direta com o clique da miniatura da HUD.
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

    // UTILS
    //Cria um drawable sólido simples para janelas e botões.
    //Recoloca no stage uma carta removida pelo zoom, mas sem exibi-la visualmente.
    //Uso: inimigo derrotado, chama coletada, baú coletado.
    //Motivo: a carta precisa existir no array visual para a sincronização,
    //mas não deve reaparecer revelada por um instante antes da esteira.
    private void recolocarCartaConsumidaComoPlaceholder(int linha, int coluna, CartaVisual cartaOriginal) {
        telaTabuleiro.recolocarComoPlaceholder(linha, coluna, cartaOriginal);
    }

    //Informa se existe alguma tela modal/popup aberta.
    //Usado pelas cartas para bloquear hover e clique enquanto overlays, popups, combate ou vitória estão ativos.
    public boolean isTelaModalAberta() {
        return telaModalAberta;
    }

    //Executa o fluxo de evento da carta.
    //Usado em dois casos: carta fechada após confirmar revelação, carta já revelada e adjacente, para permitir interação novamente
    private void executarFluxoCarta(int linha, int coluna, CartaVisual cartaOriginal) {
        fluxoCarta.revelarCarta(
            linha,
            coluna,
            cartaOriginal,
            tipo -> concluirRevelacao(linha, coluna, cartaOriginal, tipo)
        );
    }

    private void concluirRevelacao(
        int linha,
        int coluna,
        CartaVisual cartaOriginal,
        TipoCarta tipo
    ) {
        if (tipo == TipoCarta.VAZIO) {
            stageCartaZoom.clear();
            telaModalAberta = false;
            restaurarCartaOriginal(linha, coluna, cartaOriginal);
            mostrarMensagem("Não há nada nesta posição.");
            return;
        }

        CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);
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

    //Visualiza uma carta já revelada sem executar a sua ação.
    //Usado principalmente para cartas reveladas não adjacentes.
    //Exibe informações da carta, como nome, vida e futuramente furtividade.
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

        cartaOriginal.remove();

        String textura = telaTabuleiro.getIdentificador(linha, coluna);

        CartaExibida cartaZoom = criarCartaZoom(textura);

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

    //Monta o texto de informações de uma carta revelada.
    //Exibe apenas informações úteis no momento.
    //Para inimigos, mostra nome e vida atual.
    private String montarTextoInformacoesCarta(CartaInfo cartaInfo) {
        if (cartaInfo == null) {
            return "Carta desconhecida.";
        }

        if (cartaInfo.getTipo() == TipoCarta.INIMIGO) {
            if (cartaInfo.getInimigo() != null) {
                return cartaInfo.getInimigo().getNome()
                    + "\nVida: " + cartaInfo.getInimigo().getVida();
            }
            return "Inimigo desconhecido.";
        } else if (cartaInfo.getTipo() == TipoCarta.BAU) {
            if (cartaInfo.getArmaDentro() != null) {
                return "Baú"
                    + "\nArma: " + cartaInfo.getArmaDentro().getNome()
                    + "\nDurabilidade: " + cartaInfo.getArmaDentro().getDurabilidade();
            }
            if (cartaInfo.getComidaDentro() != null) {
                return "Baú"
                    + "\nComida: " + cartaInfo.getComidaDentro().getNome()
                    + "\nCura: " + cartaInfo.getComidaDentro().getCura();
            }
            return "Baú vazio.";
        } else if (cartaInfo.getTipo() == TipoCarta.CHAMA) {
            return "Chama"
                + "\nColete 3 para vencer.";
        } else if (cartaInfo.getTipo() == TipoCarta.PAREDE) {
            return "Parede"
                + "\nNão é possível atravessar.";
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

            case FURTIVIDADE_FALHOU:
                mostrarMensagem(resultado.getMensagem());
                break;

            default:
                throw new IllegalStateException("Desfecho de inimigo desconhecido.");
        }
    }

    //Mostra uma carta já revelada e adjacente em zoom.
    //1. remove temporariamente a carta do tabuleiro;
    //2. amplia a carta no stage de zoom;
    //3. mostra informações;
    //4. se houver ação possível, mostra o botão de ação.
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

        cartaOriginal.remove();

        String textura = telaTabuleiro.getIdentificador(linha, coluna);

        CartaExibida cartaZoom = criarCartaZoom(textura);

        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoom);

        //Usa o mesmo efeito visual de ampliação/flip das cartas reveladas.
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

                    // CANCELAR
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

                    //Baú já revelado:
                    //primeiro o jogador já viu as informações do baú.
                    //Ao abrir, fazemos flip para a arma e mostramos opções diretas.
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

    //Executa o fluxo de um baú que já estava revelado.
    //Fluxo: não mostra "Baú encontrado", a carta ampliada já está visível pela visualização;
    //ao abrir, faz flip para mostrar a arma mostra apenas nome/durabilidade da arma e opções.
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

        //Baú já revelado: ao abrir, apenas viramos a carta ampliada para mostrar a arma.
        String identificadorItem = cartaInfo.getItemDentro().getIdentificadorVisual();
        animacaoCarta.aplicarFlip(
            cartaZoom,
            () -> cartaZoom.setConteudo(
                GerenciadorTexturas.get(identificadorItem),
                identificadorItem
            )
        );

        //Aguarda o flip terminar antes de mostrar as opções.
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                boolean jogadorJaTemArma =
                    jogador() != null && jogador().getArmaEquipada() != null;

                popupManager.mostrarDecisaoItemBau(
                    cartaInfo,
                    jogadorJaTemArma,

                    // EQUIPAR / TROCAR
                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;
                        recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);
                        coletarBau(linha, coluna);
                    }),

                    // NÃO EQUIPAR / MANTER ATUAL
                    () -> animacaoCarta.dissolverCartaZoom(cartaZoom, () -> {
                        stageCartaZoom.clear();
                        telaModalAberta = false;

                        /*
                         * Baú permanece no tabuleiro e revelado.
                         */
                        restaurarCartaOriginal(linha, coluna, cartaOriginal);
                        sincronizarTabuleiroVisual();
                        atualizarDestaqueCartas();
                        mostrarMensagem("Você deixou o item no baú.");
                    })
                );
            }
        }, 0.26f);
    }

    private CartaExibida criarCartaZoom(String identificadorFrente) {
        TextureRegion regiao = new TextureRegion(GerenciadorTexturas.get(identificadorFrente));
        CartaExibida carta = new CartaExibida(
            GerenciadorTexturas.get("VERSO"),
            "VERSO",
            fonte
        );

        float escala = Math.min(
            300f / regiao.getRegionWidth(),
            400f / regiao.getRegionHeight()
        );
        float largura = regiao.getRegionWidth() * escala;
        float altura = regiao.getRegionHeight() * escala;

        carta.setSize(largura, altura);
        carta.setOrigin(Align.center);
        carta.setScale(0.01f);
        carta.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - largura / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 120f
        );
        return carta;
    }

}
