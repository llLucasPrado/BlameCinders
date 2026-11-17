package com.blamecinders.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;

public final class CartaoMenu extends Group {

    private final Image imagem;
    private final Image sombra;
    private final Image[] bordas = new Image[4];
    private final Label rotulo;
    private boolean disponivel = true;

    public CartaoMenu(
        Skin skin,
        String texto,
        String caminhoImagem,
        float largura,
        float altura,
        Runnable aoFocar,
        Runnable aoClicar
    ) {
        this(
            skin,
            texto,
            GerenciadorTexturas.getArquivo(caminhoImagem),
            largura,
            altura,
            aoFocar,
            aoClicar
        );
    }

    public CartaoMenu(
        Skin skin,
        String texto,
        Texture textura,
        float largura,
        float altura,
        Runnable aoFocar,
        Runnable aoClicar
    ) {
        Objects.requireNonNull(skin, "skin");
        Objects.requireNonNull(textura, "textura");
        Objects.requireNonNull(aoFocar, "aoFocar");
        Objects.requireNonNull(aoClicar, "aoClicar");

        setSize(largura, altura);
        setOrigin(Align.center);

        imagem = new Image(new TextureRegionDrawable(new TextureRegion(textura)));
        imagem.setScaling(Scaling.fill);
        Container<Image> recorteImagem = new Container<>(imagem);
        recorteImagem.setBounds(0f, 0f, largura, altura);
        recorteImagem.fill();
        recorteImagem.setClip(true);
        addActor(recorteImagem);

        sombra = VisualMenu.criarRetangulo(
            0f,
            0f,
            largura,
            altura,
            new Color(0.02f, 0.025f, 0.035f, 0.72f)
        );
        addActor(sombra);

        criarBordas(largura, altura);

        rotulo = new Label(texto, skin);
        rotulo.setAlignment(Align.center);
        rotulo.setFontScale(1.25f);
        rotulo.setBounds(10f, 0f, largura - 20f, altura);
        rotulo.setColor(Color.LIGHT_GRAY);
        addActor(rotulo);

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (disponivel) aoFocar.run();
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (disponivel) aoClicar.run();
            }
        });
    }

    public void setSelecionado(boolean selecionado, boolean animar) {
        float escala = selecionado ? 1.025f : 1f;
        if (animar) {
            addAction(Actions.scaleTo(escala, escala, 0.2f, Interpolation.smooth));
        } else {
            setScale(escala);
        }

        sombra.setColor(0.02f, 0.025f, 0.035f, selecionado ? 0.34f : 0.72f);
        rotulo.setColor(selecionado ? Color.WHITE : Color.LIGHT_GRAY);
        Color corBorda = selecionado
            ? new Color(0.92f, 0.94f, 0.98f, 0.58f)
            : new Color(0.82f, 0.86f, 0.92f, 0.20f);
        for (Image borda : bordas) borda.setColor(corBorda);
    }

    public void setRotuloInferior() {
        rotulo.setBounds(8f, 8f, getWidth() - 16f, 58f);
        rotulo.setFontScale(1.05f);
        Image faixa = VisualMenu.criarRetangulo(
            8f,
            8f,
            getWidth() - 16f,
            58f,
            new Color(0.02f, 0.025f, 0.035f, 0.78f)
        );
        addActorBefore(rotulo, faixa);
    }

    public void setImagemCompleta() {
        imagem.setScaling(Scaling.fit);
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
        setColor(disponivel ? Color.WHITE : new Color(0.42f, 0.44f, 0.48f, 1f));
    }

    private void criarBordas(float largura, float altura) {
        Color cor = new Color(0.82f, 0.86f, 0.92f, 0.20f);
        bordas[0] = VisualMenu.criarRetangulo(0f, 0f, largura, 1f, cor);
        bordas[1] = VisualMenu.criarRetangulo(0f, altura - 1f, largura, 1f, cor);
        bordas[2] = VisualMenu.criarRetangulo(0f, 0f, 1f, altura, cor);
        bordas[3] = VisualMenu.criarRetangulo(largura - 1f, 0f, 1f, altura, cor);
        for (Image borda : bordas) addActor(borda);
    }
}
