package com.blamecinders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.animacao.AnimacaoTabuleiro;
import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.combate.Arma;
import com.blamecinders.combate.Jogador;
import com.blamecinders.combate.SistemaCombate;
import com.blamecinders.combate.SistemaFurtividade;
import com.blamecinders.fluxo.FluxoCarta;
import com.blamecinders.fluxo.FluxoCombate;
import com.blamecinders.ui.ControladorHUD;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.util.ProvedorPosicaoCarta;
import com.blamecinders.util.GerenciadorTexturas;
import com.blamecinders.item.Comida;
import com.blamecinders.item.ItemBau;
import com.blamecinders.modelo.TipoCarta;
import com.blamecinders.modelo.CartaInfo;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.ui.tabuleiro.CartaVisual;

import java.util.Objects;

//Classe principal do jogo.

//RESPONSABILIDADE DESTA CLASSE:
//inicializar o jogo e os stages, criar o tabuleiro visual, orquestrar os controladores extraídos (HUD, popups, fluxo de cartas e combate);
//manter o estado global da run;
//sincronizar o grid lógico com o grid visual;
//iniciar animação de movimento/esteira.

public class BlameCindersGame extends ApplicationAdapter {

    //Stages
    private Stage stageCartaZoom;
    private Stage stageTabuleiro;
    private Stage stageUI;
    private Stage stageAnimacao;

    //UI base
    private BitmapFont fonte;
    private BitmapFont fonteCarta;
    private Label labelMensagem;
    private Skin skin;

    //Estado transitório da apresentação
    private boolean animandoTabuleiro = false;
    private boolean telaModalAberta = false;

    //Estado e regras persistentes da partida
    private EstadoPartida partida;

    //Visual do tabuleiro
    private CartaVisual[][] cartasVisuais;

    //Controladores / fluxos extraídos
    private AnimacaoTabuleiro animacaoTabuleiro;
    private AnimacaoCarta animacaoCarta;
    private GerenciadorPopups popupManager;
    private ControladorHUD hudController;
    private FluxoCombate fluxoCombate;
    private FluxoCarta fluxoCarta;

    //Constantes visuais das cartas
    private static final float CARTA_LARGURA = 108;
    private static final float CARTA_ALTURA = 144;
    private static final float ESPACO = 8;

    //Informa se o jogo terminou, usado pelas cartas visuais para bloquear interação.
    public boolean isFinalizado() {
        return partida != null && partida.isFinalizada();
    }

    //Informa se o tabuleiro está animando, usado pelas cartas visuais para bloquear clique durante esteira/movimento.
    public boolean isAnimandoTabuleiro() {
        return animandoTabuleiro;
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
        stageTabuleiro = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720));
        stageUI = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720));
        stageCartaZoom = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720));
        stageAnimacao = new Stage(new com.badlogic.gdx.utils.viewport.FitViewport(1280, 720));

        //Input multiplexer, Ordem importante: zoom > UI > animação > tabuleiro
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageCartaZoom);
        multiplexer.addProcessor(stageUI);
        multiplexer.addProcessor(stageAnimacao);
        multiplexer.addProcessor(stageTabuleiro);
        Gdx.input.setInputProcessor(multiplexer);

        //Fontes / skin base
        fonte = criarFonte(28);
        fonteCarta = criarFonte(18);
        criarUI();

        //Modelo principal
        partida = new EstadoPartida();
        SistemaCombate sistemaCombate = new SistemaCombate();
        SistemaFurtividade sistemaFurtividade = new SistemaFurtividade();

        //Controladores extraídos
        animacaoCarta = new AnimacaoCarta();

        popupManager = new GerenciadorPopups(stageUI, stageCartaZoom, skin);
        hudController = new ControladorHUD(stageUI, skin);

        fluxoCombate = new FluxoCombate(
            stageCartaZoom,
            skin,
            animacaoCarta,
            popupManager,
            sistemaCombate,
            sistemaFurtividade
        );

        fluxoCarta = new FluxoCarta(
            stageCartaZoom,
            tabuleiro(),
            animacaoCarta,
            popupManager,
            skin
        );

        //Grid visual
        cartasVisuais = new CartaVisual[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        criarTabuleiroVisual();

        //HUD
        hudController.criarHUD();
        atualizarHUDCompleto();

        //Estado visual inicial
        sincronizarTabuleiroVisual();
        atualizarDestaqueCartas();
    }

    //controla o tamanho e proporção da tela
    @Override
    public void resize(int width, int height) {
        stageTabuleiro.getViewport().update(width, height, true);
        stageUI.getViewport().update(width, height, true);
        stageCartaZoom.getViewport().update(width, height, true);
        stageAnimacao.getViewport().update(width, height, true);
    }

    //Renderiza os Stages, que define onde cada coisa é desenhada
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);

        stageTabuleiro.act(delta);
        stageUI.act(delta);
        stageCartaZoom.act(delta);
        stageAnimacao.act(delta);

        stageTabuleiro.draw();
        stageUI.draw();
        stageCartaZoom.draw();
        stageAnimacao.draw();
    }

    @Override
    public void dispose() {
        stageTabuleiro.dispose();
        stageUI.dispose();
        stageCartaZoom.dispose();
        stageAnimacao.dispose();
        skin.dispose();
        fonteCarta.dispose();
        GerenciadorTexturas.disposeAll();
    }

    //Criação de UI base
    private void criarUI() {
        skin = new Skin();

        // Fonte padrão
        skin.add("default-font", fonte);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = fonte;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        // Estilo de janela
        com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle windowStyle =
            new com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle();

        windowStyle.titleFont = fonte;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = criarDrawableCor(new Color(0f, 0f, 0f, 0.85f));

        skin.add("default", windowStyle);

        // Estilo de botão
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = fonte;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.GRAY;
        buttonStyle.up = criarDrawableCor(new Color(0.18f, 0.18f, 0.18f, 1f));
        buttonStyle.down = criarDrawableCor(new Color(0.10f, 0.10f, 0.10f, 1f));

        skin.add("default", buttonStyle);

        // Label de mensagem inferior
        labelMensagem = new Label("", skin);
        labelMensagem.setPosition(20, 20);
        stageUI.addActor(labelMensagem);
    }

    // TABULEIRO VISUAL
    //Cria as cartas visuais iniciais do tabuleiro, cada célula do grid lógico ganha um ator visual correspondente.
    //O jogador começa revelado na posição inicial.
    private void criarTabuleiroVisual() {
        float larguraTotal = Tabuleiro.COLUNAS * CARTA_LARGURA + (Tabuleiro.COLUNAS - 1) * ESPACO;
        float alturaTotal = Tabuleiro.LINHAS * CARTA_ALTURA + (Tabuleiro.LINHAS - 1) * ESPACO;

        float startX = (stageTabuleiro.getViewport().getWorldWidth() - larguraTotal) / 2f;
        float startY = (stageTabuleiro.getViewport().getWorldHeight() - alturaTotal) / 2f;

        for (int i = 0; i < Tabuleiro.LINHAS; i++) {
            for (int j = 0; j < Tabuleiro.COLUNAS; j++) {

                String frente = obterTextura(i, j);
                String verso = "VERSO";

                float x = startX + j * (CARTA_LARGURA + ESPACO);
                float y = startY + (Tabuleiro.LINHAS - 1 - i) * (CARTA_ALTURA + ESPACO);

                CartaVisual carta = new CartaVisual(frente, verso, x, y, i, j, this, fonteCarta);

                if (i == tabuleiro().getJogadorLinha() && j == tabuleiro().getJogadorColuna()) {
                    carta.setRevelada(true);
                }

                cartasVisuais[i][j] = carta;
                stageTabuleiro.addActor(carta);
            }
        }

        animacaoTabuleiro = new AnimacaoTabuleiro(
            stageTabuleiro,
            cartasVisuais,
            CARTA_LARGURA,
            CARTA_ALTURA,
            ESPACO,
            new ProvedorPosicaoCarta() {
                @Override
                public float getCartaX(int coluna) {
                    return BlameCindersGame.this.getCartaX(coluna);
                }

                @Override
                public float getCartaY(int linha) {
                    return BlameCindersGame.this.getCartaY(linha);
                }
            }
        );
    }

    //Traduz o conteúdo lógico da célula em caminho de textura.
    //PRIORIDADE: se a célula é a posição do jogador, usa textura do jogador, se a carta for null, usa verso;
    //caso contrário, escolhe pela CartaInfo/TipoCarta.
    private String obterTextura(int linha, int coluna) {
        if (linha == tabuleiro().getJogadorLinha() && coluna == tabuleiro().getJogadorColuna()) {
            return "HERÓI-TESTE";
        }

        CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);

        if (cartaInfo == null) {
            return "VERSO";
        }

        TipoCarta tipo = cartaInfo.getTipo();

        if (Objects.requireNonNull(tipo) == TipoCarta.INIMIGO) {
            if (cartaInfo.getInimigo() != null) {
                return cartaInfo.getInimigo().getTexturaPath();
            }
            return "INIMIGO";
        } else if (tipo == TipoCarta.BAU) {
            return "BAÚ";
        } else if (tipo == TipoCarta.CHAMA) {
            return "CHAMA";
        } else if (tipo == TipoCarta.PAREDE) {
            return "PAREDE";
        } else if (tipo == TipoCarta.VAZIO) {
            return "VERSO";
        }
        return "INIMIGO";
    }

    //Entrada principal de clique em carta.
    //Regras:
    //carta fechada adjacente: pergunta se deseja revelar;
    //carta fechada não adjacente: bloqueia;
    //carta revelada não adjacente: permite apenas visualizar;
    //carta revelada adjacente: permite visualizar e, se aplicável, interagir.
    public void clicarCarta(int linha, int coluna) {

        if (isFinalizado() || animandoTabuleiro || telaModalAberta) return;

        boolean cartaRevelada = tabuleiro().cartaEstaRevelada(linha, coluna);

        boolean adjacente = fluxoCarta.podeRevelar(
            tabuleiro().getJogadorLinha(),
            tabuleiro().getJogadorColuna(),
            linha,
            coluna
        );

        CartaVisual cartaOriginal = cartasVisuais[linha][coluna];

        if (cartaRevelada && adjacente) {
            telaModalAberta = true;

            //Carta já revelada e adjacente: primeiro pergunta se o jogador deseja visualizar
            //Só depois da confirmação aparecem as informações e ações possíveis.
            popupManager.mostrarConfirmacaoVisualizarCarta(
                () -> mostrarOpcoesCartaReveladaAdjacente(linha, coluna, cartaOriginal),
                () -> telaModalAberta = false
            );

            return;
        }

        //Carta revelada e distante: permite apenas visualizar informações.
        if (cartaRevelada) {
            telaModalAberta = true;

            popupManager.mostrarConfirmacaoVisualizarCarta(
                () -> visualizarInformacoesCarta(linha, coluna),
                () -> telaModalAberta = false
            );

            return;
        }

        //Carta fechada e distante: não pode revelar.
        if (!adjacente) {
            mostrarMensagem("Você só pode revelar cartas adjacentes.");

            if (animacaoTabuleiro != null) {
                animacaoTabuleiro.animarCartaMovimentoInvalido(linha, coluna);
            }

            return;
        }

        //Carta fechada e adjacente: fluxo normal de revelação.
        telaModalAberta = true;

        popupManager.mostrarConfirmacaoCarta(
            () -> executarFluxoCarta(linha, coluna, cartaOriginal),
            () -> telaModalAberta = false
        );
    }

    // FLUXOS DE COLETA / MOVIMENTO
    //Trata a coleta de chama.
    //Regras:
    //incrementa as chamas no tabuleiro, consome a carta da posição, atualiza HUD; verifica vitória; move jogador com animação/esteira.
    private void coletarChama(int linha, int coluna) {
        int antigaLinha = tabuleiro().getJogadorLinha();
        int antigaColuna = tabuleiro().getJogadorColuna();

        tabuleiro().coletarChama(linha, coluna);
        tabuleiro().consumirCarta(linha, coluna);

        atualizarHUDCompleto();

        if (partida.verificarObjetivoConcluido()) {
            mostrarMensagem("Você venceu!");
        }

        atualizarTabuleiroComAnimacao(antigaLinha, antigaColuna, linha, coluna);
    }

    //Trata o fluxo completo do baú, se houver arma, pergunta se deseja equipar/trocar, se equipar, consome o baú e move o jogador;
    //se não equipar, mantém o baú no tabuleiro
    //Etapa final do baú:
    //equipa a arma (se houver), consome a carta e move o jogador para a posição.
    private void coletarBau(int linha, int coluna) {
        CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);

        ItemBau item = cartaInfo != null ? cartaInfo.getItemDentro() : null;
        if (item instanceof Arma) {
            Arma arma = (Arma) item;
            jogador().setArmaEquipada(arma);
            mostrarMensagem("Você equipou: " + arma.getNome());
        } else if (item instanceof Comida) {
            Comida comida = (Comida) item;
            int vidaCurada = comida.consumir(jogador());
            mostrarMensagem(vidaCurada > 0
                ? "Você recuperou " + vidaCurada + " de vida."
                : "Sua vida já estava cheia.");
        } else {
            mostrarMensagem("Baú vazio.");
        }

        tabuleiro().consumirCarta(linha, coluna);

        atualizarHUDCompleto();

        int antigaLinha = tabuleiro().getJogadorLinha();
        int antigaColuna = tabuleiro().getJogadorColuna();

        atualizarTabuleiroComAnimacao(antigaLinha, antigaColuna, linha, coluna);
    }

    //Encapsula a movimentação do jogador para uma nova posição, sempre usando a animação de esteira.
    private void moverJogadorPara(int linha, int coluna) {
        int antigaLinha = tabuleiro().getJogadorLinha();
        int antigaColuna = tabuleiro().getJogadorColuna();

        atualizarTabuleiroComAnimacao(antigaLinha, antigaColuna, linha, coluna);
    }

    //Atualiza o HUD e reinstala o click da miniatura de arma.
    private void atualizarHUDCompleto() {
        hudController.atualizarHUD(jogador(), tabuleiro().getChamasColetadas());
        hudController.setClickArmaListener(this::mostrarPopupDetalheArmaHUD);
    }

    // MENSAGENS E FONTES
    private BitmapFont criarFonte(int tamanho) {
        String caminhoFonte = "Fonts/CinzelDecorative-Bold.ttf";
        if (!Gdx.files.internal(caminhoFonte).exists()) {
            BitmapFont fallback = new BitmapFont();
            fallback.getData().setScale(tamanho / 15f);
            return fallback;
        }

        FreeTypeFontGenerator gen =
            new FreeTypeFontGenerator(Gdx.files.internal(caminhoFonte));

        FreeTypeFontGenerator.FreeTypeFontParameter p =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        p.size = tamanho;
        p.color = Color.WHITE;

        BitmapFont f = gen.generateFont(p);
        gen.dispose();
        return f;
    }

    //Exibe mensagem temporária no canto inferior da UI.
    private void mostrarMensagem(String texto) {
        labelMensagem.setText(texto);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                labelMensagem.setText("");
            }
        }, 2f);
    }

    // SINCRONIZAÇÃO VISUAL
    //Sincroniza todas as cartas visuais com o estado lógico do tabuleiro, este é um dos métodos mais importantes da classe.
    //Ele garante, posição correta, textura correta, reset de transformações, revelação correta, prioridade visual do jogador.
    private void sincronizarTabuleiroVisual() {
        int jogadorLinha = tabuleiro().getJogadorLinha();
        int jogadorColuna = tabuleiro().getJogadorColuna();

        for (int i = 0; i < Tabuleiro.LINHAS; i++) {
            for (int j = 0; j < Tabuleiro.COLUNAS; j++) {

                CartaVisual carta = cartasVisuais[i][j];
                if (carta == null) continue;

                carta.setPosicaoGrid(i, j);

                carta.setVisible(true);
                carta.getColor().a = 1f;
                carta.setScale(1f, 1f);
                carta.setRotation(0f);
                carta.setPosition(getCartaX(j), getCartaY(i));

                if (i == jogadorLinha && j == jogadorColuna) {
                    carta.setFrente("HERÓI-TESTE");
                    carta.setRevelada(true);
                    carta.toFront();
                } else {
                    carta.setFrente(obterTextura(i, j));

                    if (tabuleiro().getCarta(i, j) == TipoCarta.VAZIO) {
                        carta.setRevelada(false);
                    } else {
                        carta.setRevelada(tabuleiro().cartaEstaRevelada(i, j));
                    }
                }
            }
        }
    }

    //Restaura uma carta visual removida temporariamente do stage durante o zoom/revelação.
    private void restaurarCartaOriginal(int linha, int coluna, CartaVisual cartaOriginal) {
        if (cartaOriginal.getStage() == null) {
            stageTabuleiro.addActor(cartaOriginal);
        }

        cartasVisuais[linha][coluna] = cartaOriginal;

        cartaOriginal.clearActions();
        cartaOriginal.setVisible(true);
        cartaOriginal.setScale(1f, 1f);
        cartaOriginal.setRotation(0f);
        cartaOriginal.getColor().a = 1f;
        cartaOriginal.setPosition(getCartaX(coluna), getCartaY(linha));
        cartaOriginal.setPosicaoGrid(linha, coluna);

        if (linha == tabuleiro().getJogadorLinha() && coluna == tabuleiro().getJogadorColuna()) {
            cartaOriginal.setFrente("HERÓI-TESTE");
            cartaOriginal.setRevelada(true);
            cartaOriginal.toFront();
        } else {
            cartaOriginal.setFrente(obterTextura(linha, coluna));

            if (tabuleiro().getCarta(linha, coluna) == TipoCarta.VAZIO) {
                cartaOriginal.setRevelada(false);
            } else {
                cartaOriginal.setRevelada(tabuleiro().cartaEstaRevelada(linha, coluna));
            }
        }
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
        int jLinha = tabuleiro().getJogadorLinha();
        int jColuna = tabuleiro().getJogadorColuna();

        for (int i = 0; i < cartasVisuais.length; i++) {
            for (int j = 0; j < cartasVisuais[i].length; j++) {

                CartaVisual carta = cartasVisuais[i][j];
                if (carta == null) continue;

                boolean jogador = (i == jLinha && j == jColuna);
                boolean adjacente =
                    Math.abs(i - jLinha) + Math.abs(j - jColuna) == 1;

                carta.clearActions();

                if (jogador) {
                    carta.addAction(
                        Actions.color(
                            new Color(1f, 1f, 1f, 1f),
                            0.18f,
                            Interpolation.fade
                        )
                    );
                    continue;
                }

                if (adjacente) {

                    //Primeiro entra no destaque de forma gradual, ó depois começa o pulso infinito.
                    carta.addAction(
                        Actions.sequence(
                            Actions.color(
                                new Color(1f, 1f, 1f, 0.90f),
                                0.28f,
                                Interpolation.fade
                            ),
                            Actions.forever(
                                Actions.sequence(
                                    Actions.color(
                                        new Color(0.86f, 0.92f, 1f, 1f),
                                        0.85f,
                                        Interpolation.sine
                                    ),
                                    Actions.color(
                                        new Color(1f, 1f, 1f, 0.90f),
                                        0.85f,
                                        Interpolation.sine
                                    )
                                )
                            )
                        )
                    );
                } else {
                    carta.addAction(
                        Actions.color(
                            new Color(0.8f, 0.85f, 1f, 0.65f),
                            0.25f,
                            Interpolation.fade
                        )
                    );
                }
            }
        }
    }

    // POSIÇÕES VISUAIS
    private float getCartaX(int coluna) {
        float larguraTotal = Tabuleiro.COLUNAS * CARTA_LARGURA + (Tabuleiro.COLUNAS - 1) * ESPACO;
        float startX = (stageTabuleiro.getViewport().getWorldWidth() - larguraTotal) / 2f;
        return startX + coluna * (CARTA_LARGURA + ESPACO);
    }

    private float getCartaY(int linha) {
        float alturaTotal = Tabuleiro.LINHAS * CARTA_ALTURA + (Tabuleiro.LINHAS - 1) * ESPACO;
        float startY = (stageTabuleiro.getViewport().getWorldHeight() - alturaTotal) / 2f;
        return startY + (Tabuleiro.LINHAS - 1 - linha) * (CARTA_ALTURA + ESPACO);
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
    private void atualizarTabuleiroComAnimacao(int antigaLinha, int antigaColuna, int novaLinha, int novaColuna) {
        if (animandoTabuleiro) return;

        animandoTabuleiro = true;

        animacaoTabuleiro.animarMovimentoJogadorComEsteira(
            antigaLinha,
            antigaColuna,
            novaLinha,
            novaColuna,
            () -> {
                tabuleiro().moverJogador(novaLinha, novaColuna);
                tabuleiro().aplicarEsteira(antigaLinha, antigaColuna, novaLinha, novaColuna);

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
            jogador().getArmaEquipada().getTexturaPath(),
            animacaoCarta,
            () -> telaModalAberta = false
        );
    }

    // UTILS
    //Cria um drawable sólido simples para janelas e botões.
    private Drawable criarDrawableCor(Color cor) {
        return new TextureRegionDrawable(new TextureRegion(GerenciadorTexturas.getSolid(cor)));
    }

    //Recoloca no stage uma carta removida pelo zoom, mas sem exibi-la visualmente.
    //Uso: inimigo derrotado, chama coletada, baú coletado.
    //Motivo: a carta precisa existir no array visual para a sincronização,
    //mas não deve reaparecer revelada por um instante antes da esteira.
    private void recolocarCartaConsumidaComoPlaceholder(int linha, int coluna, CartaVisual cartaOriginal) {
        if (cartaOriginal == null) return;

        if (cartaOriginal.getStage() == null) {
            stageTabuleiro.addActor(cartaOriginal);
        }

        cartasVisuais[linha][coluna] = cartaOriginal;

        cartaOriginal.clearActions();
        cartaOriginal.setPosicaoGrid(linha, coluna);
        cartaOriginal.setPosition(getCartaX(coluna), getCartaY(linha));
        cartaOriginal.setScale(1f, 1f);
        cartaOriginal.setRotation(0f);

        //Ponto principal: a carta volta fechada e invisível.
        //Assim ela não aparece revelada antes da esteira/sincronização.
        cartaOriginal.setRevelada(false);
        cartaOriginal.setVisible(false);
        cartaOriginal.getColor().a = 0f;
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

            // INIMIGO
            () -> executarFluxoInimigo(linha, coluna, cartaOriginal),

            // CHAMA
            () -> popupManager.mostrarPopupMensagem("Chama coletada!", () -> {
                Image cartaZoomAtual = fluxoCarta.getCartaZoomAtual();

                Runnable finalizar = () -> {
                    stageCartaZoom.clear();
                    telaModalAberta = false;
                    recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);
                    coletarChama(linha, coluna);
                };

                if (cartaZoomAtual != null) {
                    animacaoCarta.dissolverCartaZoom(cartaZoomAtual, finalizar);
                } else {
                    finalizar.run();
                }
            }),

            // BAÚ
            () -> executarFluxoBau(linha, coluna, cartaOriginal),

            // PAREDE
            () -> popupManager.mostrarPopupMensagem(
                "Parede encontrada.\nNão é possível avançar.",
                () -> {
                    Image cartaZoomAtual = fluxoCarta.getCartaZoomAtual();

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
            ),

            // VAZIO
            () -> {
                stageCartaZoom.clear();
                telaModalAberta = false;
                restaurarCartaOriginal(linha, coluna, cartaOriginal);
                mostrarMensagem("Não há nada nesta posição.");
            },

            // restauração genérica

            this::mostrarMensagem
        );
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

        CartaVisual cartaOriginal = cartasVisuais[linha][coluna];

        if (cartaOriginal == null) {
            telaModalAberta = false;
            return;
        }

        cartaOriginal.remove();

        String textura = obterTextura(linha, coluna);

        TextureRegion region = new TextureRegion(GerenciadorTexturas.get(textura));
        Image cartaZoom = new Image(new TextureRegionDrawable(region));

        float maxLargura = 300f;
        float maxAltura = 400f;

        float escala = Math.min(
            maxLargura / region.getRegionWidth(),
            maxAltura / region.getRegionHeight()
        );

        float larguraFinal = region.getRegionWidth() * escala;
        float alturaFinal = region.getRegionHeight() * escala;

        cartaZoom.setSize(larguraFinal, alturaFinal);
        cartaZoom.setOrigin(Align.center);
        cartaZoom.setScale(0.01f);

        cartaZoom.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - larguraFinal / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 120
        );

        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoom);
        adicionarRotuloCartaZoom(cartaZoom, textura);

        animacaoCarta.aplicarFlip(cartaZoom, () -> cartaZoom.setDrawable(
            new TextureRegionDrawable(
                new TextureRegion(GerenciadorTexturas.get(textura))
            )
        ));

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

        if (Objects.requireNonNull(cartaInfo.getTipo()) == TipoCarta.INIMIGO) {
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

    //Executa o fluxo de uma carta de inimigo.
    //Fluxo: mostra opções de lutar ou sair,
    //se lutar, abre a tela de combate;
    //se vencer, consome o inimigo e move o jogador;
    //se sair, mantém a carta revelada no tabuleiro.
    private void executarFluxoInimigo(int linha, int coluna, CartaVisual cartaOriginal) {

        Runnable acaoVoltar = () -> {
            stageCartaZoom.clear();
            telaModalAberta = false;
            restaurarCartaOriginal(linha, coluna, cartaOriginal);
            sincronizarTabuleiroVisual();
            atualizarDestaqueCartas();
        };

        Runnable acaoVitoria = () -> {
            stageCartaZoom.clear();
            telaModalAberta = false;

            tabuleiro().consumirCarta(linha, coluna);
            recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);

            moverJogadorPara(linha, coluna);
            atualizarHUDCompleto();
        };

        Runnable acaoDerrota = () -> {
            stageCartaZoom.clear();
            telaModalAberta = false;
            restaurarCartaOriginal(linha, coluna, cartaOriginal);

            partida.registrarDerrota();
            popupManager.mostrarGameOver();
        };

        popupManager.mostrarPopupInimigo(
            // LUTAR
            () -> fluxoCombate.mostrarTelaCombate(
                tabuleiro().getCartaInfo(linha, coluna),
                jogador(),
                acaoVoltar,
                acaoVitoria,
                acaoDerrota,
                this::mostrarMensagem
            ),

            // SAIR
            () -> {
                Image cartaZoomAtual = fluxoCarta.getCartaZoomAtual();

                Runnable finalizar = () -> {
                    stageCartaZoom.clear();
                    telaModalAberta = false;
                    restaurarCartaOriginal(linha, coluna, cartaOriginal);
                    sincronizarTabuleiroVisual();
                    atualizarDestaqueCartas();
                    mostrarMensagem("Você saiu.");
                };

                if (cartaZoomAtual != null) {
                    animacaoCarta.dissolverCartaZoom(cartaZoomAtual, finalizar);
                } else {
                    finalizar.run();
                }
            }
        );
    }

    //Executa o fluxo de uma carta de baú.
    //Fluxo: mostra "Baú encontrado", vira o zoom para mostrar a arma, pergunta se deseja equipar/trocar;
    //se aceitar, consome o baú e move o jogador;
    //se recusar, mantém o baú revelado no tabuleiro.
    private void executarFluxoBau(int linha, int coluna, CartaVisual cartaOriginal) {

        popupManager.mostrarPopupMensagem("Baú encontrado!", () -> {
            CartaInfo cartaInfo = tabuleiro().getCartaInfo(linha, coluna);

            if (cartaInfo == null || cartaInfo.getItemDentro() == null) {
                popupManager.mostrarPopupMensagem("Baú vazio.", () -> {
                    Image cartaZoomAtual = fluxoCarta.getCartaZoomAtual();

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
                });

                return;
            }

            Image cartaZoomAtual = fluxoCarta.getCartaZoomAtual();

            if (cartaZoomAtual != null) {
                animacaoCarta.aplicarFlip(cartaZoomAtual, () -> cartaZoomAtual.setDrawable(
                    new TextureRegionDrawable(
                        new TextureRegion(
                            GerenciadorTexturas.get(cartaInfo.getItemDentro().getIdentificadorVisual())
                        )
                    )
                ));
            }

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    boolean jogadorJaTemArma =
                        jogador() != null && jogador().getArmaEquipada() != null;

                    popupManager.mostrarPopupItemBau(
                        cartaInfo,
                        jogadorJaTemArma,

                        // EQUIPAR / TROCAR
                        () -> {
                            Image zoomAtual = fluxoCarta.getCartaZoomAtual();

                            Runnable finalizar = () -> {
                                stageCartaZoom.clear();
                                telaModalAberta = false;
                                recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);
                                coletarBau(linha, coluna);
                            };

                            if (zoomAtual != null) {
                                animacaoCarta.dissolverCartaZoom(zoomAtual, finalizar);
                            } else {
                                finalizar.run();
                            }
                        },

                        // NÃO EQUIPAR / NÃO TROCAR
                        () -> {
                            Image zoomAtual = fluxoCarta.getCartaZoomAtual();

                            Runnable finalizar = () -> {
                                stageCartaZoom.clear();
                                telaModalAberta = false;

                                // O baú permanece no tabuleiro e continua revelado.
                                restaurarCartaOriginal(linha, coluna, cartaOriginal);
                                sincronizarTabuleiroVisual();
                                atualizarDestaqueCartas();
                                mostrarMensagem("Você deixou o item no baú.");
                            };

                            if (zoomAtual != null) {
                                animacaoCarta.dissolverCartaZoom(zoomAtual, finalizar);
                            } else {
                                finalizar.run();
                            }
                        }
                    );
                }
            }, 0.26f);
        });
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

        String textura = obterTextura(linha, coluna);

        TextureRegion region = new TextureRegion(GerenciadorTexturas.get(textura));
        Image cartaZoom = new Image(new TextureRegionDrawable(region));

        float maxLargura = 300f;
        float maxAltura = 400f;

        float escala = Math.min(
            maxLargura / region.getRegionWidth(),
            maxAltura / region.getRegionHeight()
        );

        float larguraFinal = region.getRegionWidth() * escala;
        float alturaFinal = region.getRegionHeight() * escala;

        cartaZoom.setSize(larguraFinal, alturaFinal);
        cartaZoom.setOrigin(Align.center);
        cartaZoom.setScale(0.01f);

        cartaZoom.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - larguraFinal / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 120
        );

        stageCartaZoom.clear();
        stageCartaZoom.addActor(popupManager.criarOverlayBloqueador(0.65f));
        stageCartaZoom.addActor(cartaZoom);
        adicionarRotuloCartaZoom(cartaZoom, textura);

        //Usa o mesmo efeito visual de ampliação/flip das cartas reveladas.
        animacaoCarta.aplicarFlip(cartaZoom, () -> cartaZoom.setDrawable(
            new TextureRegionDrawable(
                new TextureRegion(GerenciadorTexturas.get(textura))
            )
        ));

        animacaoCarta.aplicarIdleFlutuacao(cartaZoom);

        switch (cartaInfo.getTipo()) {

            case INIMIGO:
                popupManager.mostrarPopupCartaReveladaComAcao(
                    montarTextoInformacoesCarta(cartaInfo),
                    "Combater",

                    // COMBATER DIRETO
                    () -> {
                        Runnable acaoVoltar = () -> {
                            stageCartaZoom.clear();
                            telaModalAberta = false;
                            restaurarCartaOriginal(linha, coluna, cartaOriginal);
                            sincronizarTabuleiroVisual();
                            atualizarDestaqueCartas();
                        };

                        Runnable acaoVitoria = () -> {
                            stageCartaZoom.clear();
                            telaModalAberta = false;

                            tabuleiro().consumirCarta(linha, coluna);
                            recolocarCartaConsumidaComoPlaceholder(linha, coluna, cartaOriginal);

                            moverJogadorPara(linha, coluna);
                            atualizarHUDCompleto();
                        };

                        Runnable acaoDerrota = () -> {
                            stageCartaZoom.clear();
                            telaModalAberta = false;
                            restaurarCartaOriginal(linha, coluna, cartaOriginal);

                            partida.registrarDerrota();
                            popupManager.mostrarGameOver();
                        };

                        fluxoCombate.mostrarTelaCombate(
                            tabuleiro().getCartaInfo(linha, coluna),
                            jogador(),
                            acaoVoltar,
                            acaoVitoria,
                            acaoDerrota,
                            this::mostrarMensagem
                        );
                    },

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
    private void executarFluxoBauJaRevelado(int linha, int coluna, CartaVisual cartaOriginal, Image cartaZoom) {
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
        animacaoCarta.aplicarFlip(cartaZoom, () -> cartaZoom.setDrawable(
            new TextureRegionDrawable(
                new TextureRegion(
                    GerenciadorTexturas.get(cartaInfo.getItemDentro().getIdentificadorVisual())
                )
            )
        ));

        //Aguarda o flip terminar antes de mostrar as opções.
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                boolean jogadorJaTemArma =
                    jogador() != null && jogador().getArmaEquipada() != null;

                popupManager.mostrarPopupItemBauRevelado(
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

    private void adicionarRotuloCartaZoom(Image carta, String texto) {
        Label rotulo = new Label(texto, skin);
        rotulo.setAlignment(Align.center);
        rotulo.setWrap(true);
        rotulo.setSize(carta.getWidth() - 24f, 100f);
        rotulo.setPosition(
            carta.getX() + 12f,
            carta.getY() + (carta.getHeight() - rotulo.getHeight()) / 2f
        );
        stageCartaZoom.addActor(rotulo);
    }

}
