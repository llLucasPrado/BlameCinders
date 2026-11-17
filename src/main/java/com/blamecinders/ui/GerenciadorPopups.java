package com.blamecinders.ui;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.item.Arma;
import com.blamecinders.item.Comida;
import com.blamecinders.item.ItemBau;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.util.GerenciadorTexturas;

/** Cria e anima os popups e overlays da partida. */
public class GerenciadorPopups {

    private final Stage stageUI;
    private final Stage stageCartaZoom;
    private final Skin skin;
    private final GerenciadorAudio audio;
    private final PopupPause popupPause;

    public GerenciadorPopups(
        Stage stageUI,
        Stage stageCartaZoom,
        Skin skin,
        GerenciadorAudio audio
    ) {
        this.stageUI = Objects.requireNonNull(stageUI, "stageUI");
        this.stageCartaZoom = Objects.requireNonNull(stageCartaZoom, "stageCartaZoom");
        this.skin = Objects.requireNonNull(skin, "skin");
        this.audio = Objects.requireNonNull(audio, "audio");
        this.popupPause = new PopupPause(stageUI, skin, audio);
    }

    public void mostrarPopupMensagem(String mensagem, Runnable confirmar) {
        Window popup = new Window("", skin);

        Label texto = new Label(mensagem, skin);
        texto.setAlignment(Align.center);

        TextButton btn = new TextButton("Confirmar", skin);

        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                AnimadorPopup.fechar(popup, confirmar);
            }
        });

        popup.add(texto).pad(20);
        popup.row();
        popup.add(btn).width(180).pad(15);

        popup.pack();
        centralizarZoom(popup);
        stageCartaZoom.addActor(popup);
        AnimadorPopup.abrir(popup);
        popup.toFront();
    }

    public void mostrarGameOver(Runnable tentarNovamente, Runnable voltarMenu) {
        mostrarResultado(
            VisualMenu.FUNDO_BRASAS,
            "VOCÊ CAIU",
            "As chamas se afastam, mas a jornada ainda pode recomeçar.",
            tentarNovamente,
            voltarMenu
        );
    }

    public void mostrarVitoria(int cinzas, Runnable tentarNovamente, Runnable voltarMenu) {
        mostrarResultado(
            VisualMenu.FUNDO_DRAGAO,
            "AS TRÊS CHAMAS",
            "A fase foi concluída. Cinzas carregadas: " + cinzas + ".",
            tentarNovamente,
            voltarMenu
        );
    }

    public Image criarOverlayBloqueador(float alpha) {
        return ElementosPopup.criarOverlay(stageCartaZoom, alpha);
    }

    private void centralizarZoom(Window popup) {
        popup.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 300
        );
    }

    public void mostrarPopupCartaReveladaComAcao(String mensagem, String textoAcao, Runnable acaoPrincipal, Runnable cancelar) {
        Window popup = new Window("", skin);
        popup.setName("popupCartaReveladaComAcao");

        Label texto = new Label(mensagem, skin);
        texto.setAlignment(Align.center);

        TextButton btnAcao = new TextButton(textoAcao, skin);
        TextButton btnCancelar = new TextButton("Cancelar", skin);

        btnAcao.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                AnimadorPopup.fechar(popup, acaoPrincipal);
            }
        });

        btnCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                AnimadorPopup.fechar(popup, cancelar);
            }
        });

        popup.add(texto).colspan(2).pad(20);
        popup.row();
        popup.add(btnAcao).width(180).pad(10);
        popup.add(btnCancelar).width(180).pad(10);

        popup.pack();

        popup.setPosition(
            stageUI.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stageUI.getViewport().getWorldHeight() / 5f - popup.getHeight() / 2f
        );

        stageCartaZoom.addActor(popup);
        AnimadorPopup.abrir(popup);
        popup.toFront();
    }

    public void mostrarDetalheArmaEquipada(
        String nomeArma,
        int durabilidade,
        String identificadorVisual,
        AnimacaoCarta animacaoCarta,
        Runnable aoFechar
    ) {
        Image overlay = criarOverlayBloqueador(0.75f);
        stageCartaZoom.addActor(overlay);

        Image cartaArma = new Image(new TextureRegionDrawable(
            new TextureRegion(GerenciadorTexturas.get(identificadorVisual))
        ));

        cartaArma.setSize(260, 360);
        cartaArma.setOrigin(Align.center);
        cartaArma.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - 130,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 140
        );

        Label labelInfo = new Label(
            nomeArma + "\nDurabilidade: " + durabilidade,
            skin
        );
        labelInfo.setAlignment(Align.center);
        labelInfo.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 1.93f - 120,
            120
        );

        TextButton btnFechar = new TextButton("Fechar", skin);
        btnFechar.setSize(160, 50);
        btnFechar.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - 80,
            50
        );

        stageCartaZoom.addActor(cartaArma);
        stageCartaZoom.addActor(labelInfo);
        stageCartaZoom.addActor(btnFechar);

        overlay.toFront();
        cartaArma.toFront();
        labelInfo.toFront();
        btnFechar.toFront();

        if (animacaoCarta != null) {
            animacaoCarta.aplicarIdleFlutuacao(cartaArma);
        }

        btnFechar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // O stage pode conter a tela de combate por baixo deste popup.
                overlay.remove();
                cartaArma.remove();
                labelInfo.remove();
                btnFechar.remove();

                if (aoFechar != null) {
                    aoFechar.run();
                }
            }
        });
    }

    public void mostrarDecisaoItemBau(
        CartaInfo cartaInfo,
        boolean jogadorTemArma,
        Runnable aoUsar,
        Runnable aoDeixar
    ) {
        ItemBau item = cartaInfo != null ? cartaInfo.getItemDentro() : null;
        String texto;
        String btnPrincipal;
        String btnSecundario;

        if (item instanceof Arma) {
            Arma arma = (Arma) item;
            texto = arma.getNome() + "\nDurabilidade: " + arma.getDurabilidade();
            btnPrincipal = jogadorTemArma ? "Trocar arma" : "Equipar";
            btnSecundario = jogadorTemArma ? "Manter atual" : "Deixar";
        } else if (item instanceof Comida) {
            Comida comida = (Comida) item;
            texto = comida.getNome() + "\nCura: " + comida.getCura();
            btnPrincipal = "Consumir";
            btnSecundario = "Deixar";
        } else {
            texto = "Baú vazio.";
            btnPrincipal = "Fechar";
            btnSecundario = "Deixar";
        }

        Window popup = new Window("", skin);
        popup.setName("popupArmaBauRevelado");

        Label label = new Label(texto, skin);
        label.setAlignment(Align.center);

        TextButton btn1 = new TextButton(btnPrincipal, skin);
        TextButton btn2 = new TextButton(btnSecundario, skin);

        btn1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                AnimadorPopup.fechar(popup, aoUsar);
            }
        });

        btn2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                AnimadorPopup.fechar(popup, aoDeixar);
            }
        });

        popup.add(label).colspan(2).pad(20);
        popup.row();
        popup.add(btn1).width(190).pad(10);
        popup.add(btn2).width(190).pad(10);

        popup.pack();

        popup.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 300
        );

        stageCartaZoom.addActor(popup);
        AnimadorPopup.abrir(popup);
        popup.toFront();
    }

    public void mostrarPause(
        BooleanSupplier salvar,
        Runnable continuar,
        Runnable voltarMenu
    ) {
        popupPause.mostrar(salvar, continuar, voltarMenu);
    }

    public void fecharPause(Runnable aoFinalizar) {
        popupPause.fechar(aoFinalizar);
    }

    private void mostrarResultado(
        String caminhoFundo,
        String textoTitulo,
        String mensagem,
        Runnable tentarNovamente,
        Runnable voltarMenu
    ) {
        Objects.requireNonNull(tentarNovamente, "tentarNovamente");
        Objects.requireNonNull(voltarMenu, "voltarMenu");
        stageCartaZoom.clear();

        Image fundo = new Image(new TextureRegionDrawable(new TextureRegion(
            GerenciadorTexturas.getArquivo(caminhoFundo)
        )));
        fundo.setScaling(Scaling.fill);
        fundo.setFillParent(true);
        stageCartaZoom.addActor(fundo);

        Image overlay = criarOverlayBloqueador(0.48f);
        stageCartaZoom.addActor(overlay);

        Group painel = VisualMenu.criarPainel(340f, 190f, 600f, 340f);
        Label titulo = new Label(textoTitulo, skin);
        titulo.setFontScale(1.85f);
        titulo.setAlignment(Align.center);
        titulo.setBounds(30f, 230f, 540f, 70f);

        Label texto = new Label(mensagem, skin);
        texto.setWrap(true);
        texto.setAlignment(Align.center);
        texto.setBounds(55f, 145f, 490f, 80f);

        TextButton btnTentar = new TextButton("Tentar novamente", skin);
        btnTentar.setBounds(40f, 58f, 250f, 56f);
        btnTentar.getLabel().setFontScale(0.85f);
        btnTentar.getLabel().setAlignment(Align.center);
        btnTentar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                tentarNovamente.run();
            }
        });

        TextButton btnMenu = new TextButton("Menu principal", skin);
        btnMenu.setBounds(310f, 58f, 250f, 56f);
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audio.tocarSelecionarOpcao();
                voltarMenu.run();
            }
        });

        painel.addActor(titulo);
        painel.addActor(texto);
        painel.addActor(btnTentar);
        painel.addActor(btnMenu);
        painel.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    audio.tocarSelecionarOpcao();
                    tentarNovamente.run();
                    return true;
                }
                if (keycode == Input.Keys.ESCAPE) {
                    audio.tocarSelecionarOpcao();
                    voltarMenu.run();
                    return true;
                }
                return false;
            }
        });
        stageCartaZoom.addActor(painel);
        stageCartaZoom.setKeyboardFocus(painel);
        VisualMenu.animarEntrada(painel, 0.08f);
    }

}
