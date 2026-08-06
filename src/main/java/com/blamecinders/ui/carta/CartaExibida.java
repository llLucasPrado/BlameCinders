package com.blamecinders.ui.carta;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

/** Carta composta por fundo e texto, transformados como uma única unidade. */
public class CartaExibida extends Group {

    private final Image imagem;
    private final Label rotulo;

    public CartaExibida(Texture textura, String texto, BitmapFont fonte) {
        this(new TextureRegionDrawable(new TextureRegion(textura)), texto, fonte);
    }

    public CartaExibida(Drawable fundo, String texto, BitmapFont fonte) {
        imagem = new Image();
        imagem.setTouchable(Touchable.disabled);

        Label.LabelStyle estilo = new Label.LabelStyle(fonte, Color.WHITE);
        rotulo = new Label(texto, estilo);
        rotulo.setAlignment(Align.center);
        rotulo.setWrap(true);
        rotulo.setTouchable(Touchable.disabled);

        addActor(imagem);
        addActor(rotulo);
        setTransform(true);
        setOrigin(Align.center);
        setConteudo(fundo, texto);
    }

    @Override
    public void setSize(float largura, float altura) {
        super.setSize(largura, altura);
        imagem.setBounds(0f, 0f, largura, altura);
        rotulo.setBounds(12f, 0f, Math.max(0f, largura - 24f), altura);
    }

    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
        Color cor = getColor();
        imagem.setColor(cor.r, cor.g, cor.b, 1f);
        rotulo.setColor(1f, 1f, 1f, 1f);
        super.draw(batch, parentAlpha);
    }

    public void setConteudo(Texture textura, String texto) {
        setConteudo(new TextureRegionDrawable(new TextureRegion(textura)), texto);
    }

    public void setConteudo(Drawable fundo, String texto) {
        imagem.setDrawable(fundo);
        rotulo.setText(texto == null ? "" : texto);
    }

    public Image getImagem() {
        return imagem;
    }

    public BitmapFont getFonte() {
        return rotulo.getStyle().font;
    }

    public String getTexto() {
        return rotulo.getText().toString();
    }
}
