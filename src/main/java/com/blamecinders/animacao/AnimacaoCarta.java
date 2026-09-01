package com.blamecinders.animacao;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.blamecinders.util.GerenciadorTexturas;

/** Animações Scene2D usadas por cartas, armas e transições de combate. */
public class AnimacaoCarta {

    public void aplicarFlip(Actor carta, Runnable aposFlip) {
        carta.setOrigin(Align.center);

        carta.addAction(
            Actions.sequence(
                Actions.scaleTo(0.05f, 1f, 0.12f, Interpolation.fade),
                Actions.run(aposFlip),
                Actions.scaleTo(1f, 1f, 0.12f, Interpolation.fade)
            )
        );
    }

    public void aplicarIdleFlutuacao(Actor carta) {
        carta.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.moveBy(0, 8, 1.2f, Interpolation.sine),
                    Actions.moveBy(0, -8, 1.2f, Interpolation.sine)
                )
            )
        );
    }

    public void dissolverCartaZoom(Actor cartaZoom, Runnable aoFinalizar) {
        cartaZoom.clearActions();

        cartaZoom.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.fadeOut(0.13f),
                    Actions.scaleTo(0.80f, 0.80f, 0.22f, Interpolation.fade),
                    Actions.moveBy(0f, 15f, 0.22f, Interpolation.fade)
                ),
                Actions.run(() -> {
                    if (aoFinalizar != null) {
                        aoFinalizar.run();
                    }
                })
            )
        );
    }

    public void animarImpactoJogador(Actor cartaJogador) {
        cartaJogador.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(-20f, 0f, 0.08f, Interpolation.fade),
                    Actions.scaleTo(1.05f, 1.05f, 0.08f, Interpolation.fade)
                ),
                Actions.parallel(
                    Actions.moveBy(20f, 0f, 0.08f, Interpolation.fade),
                    Actions.scaleTo(1f, 1f, 0.08f, Interpolation.fade)
                )
            )
        );
    }

    public void animarQuebraArma(Image miniArma, Stage stage, Runnable aoFinalizar) {
        if (miniArma == null) {
            if (aoFinalizar != null) aoFinalizar.run();
            return;
        }

        miniArma.clearActions();
        miniArma.setOrigin(Align.center);
        miniArma.toFront();

        criarRachadurasSobreCarta(stage, miniArma);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                criarFragmentosQuebra(stage, miniArma, 8);
            }
        }, 0.18f);

        miniArma.addAction(
            Actions.sequence(
                Actions.delay(0.12f),

                Actions.parallel(
                    Actions.fadeOut(0.48f, Interpolation.fade),
                    Actions.scaleTo(0.45f, 0.45f, 0.48f, Interpolation.pow2In),
                    Actions.moveBy(0f, -22f, 0.48f, Interpolation.pow2In)
                ),

                Actions.run(() -> {
                    miniArma.remove();

                    if (aoFinalizar != null) {
                        aoFinalizar.run();
                    }
                })
            )
        );
    }

    public void animarDerrotaInimigo(Actor cartaInimigo, Stage stage, Runnable aoFinalizar) {
        if (cartaInimigo == null) {
            if (aoFinalizar != null) aoFinalizar.run();
            return;
        }

        cartaInimigo.clearActions();
        cartaInimigo.setOrigin(Align.center);
        cartaInimigo.toFront();
        criarRachadurasSobreCarta(stage, cartaInimigo);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                criarFragmentosQuebra(stage, cartaInimigo, 16);
            }
        }, 0.24f);

        cartaInimigo.addAction(
            Actions.sequence(

                Actions.parallel(
                    Actions.sequence(
                        Actions.moveBy(-7f, 0f, 0.04f, Interpolation.fade),
                        Actions.moveBy(14f, 0f, 0.05f, Interpolation.fade),
                        Actions.moveBy(-7f, 0f, 0.04f, Interpolation.fade)
                    ),
                    Actions.sequence(
                        Actions.scaleTo(1.04f, 0.98f, 0.08f, Interpolation.fade),
                        Actions.scaleTo(1f, 1f, 0.08f, Interpolation.fade)
                    )
                ),

                Actions.delay(0.10f),

                Actions.parallel(
                    Actions.fadeOut(0.62f, Interpolation.fade),
                    Actions.scaleTo(0.58f, 0.58f, 0.62f, Interpolation.pow2In),
                    Actions.moveBy(0f, -46f, 0.62f, Interpolation.pow2In)
                ),

                Actions.run(() -> {
                    if (aoFinalizar != null) {
                        aoFinalizar.run();
                    }
                })
            )
        );
    }

    public void resetarTransformacoes(Actor imagem) {
        if (imagem == null) return;

        imagem.clearActions();
        imagem.setScale(1f, 1f);
        imagem.setRotation(0f);
        imagem.getColor().a = 1f;
    }

    private void criarFragmentosQuebra(Stage stage, Actor origem, int quantidade) {
        if (stage == null || origem == null) return;

        for (int i = 0; i < quantidade; i++) {
            Image fragmento = criarFragmentoVisual();

            float centroX = origem.getX() + origem.getWidth() / 2f;
            float centroY = origem.getY() + origem.getHeight() / 2f;

            float offsetX = (float) (Math.random() * origem.getWidth() - origem.getWidth() / 2f);
            float offsetY = (float) (Math.random() * origem.getHeight() - origem.getHeight() / 2f);

            fragmento.setPosition(centroX + offsetX, centroY + offsetY);

            float tamanho = 5f + (float) Math.random() * 10f;
            fragmento.setSize(tamanho, tamanho * (0.6f + (float) Math.random()));

            fragmento.setOrigin(Align.center);
            fragmento.setRotation((float) Math.random() * 180f);
            fragmento.getColor().a = 0.85f;

            stage.addActor(fragmento);
            fragmento.toFront();

            float direcaoX = -50f + (float) Math.random() * 100f;
            float direcaoY = -35f + (float) Math.random() * 90f;

            fragmento.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveBy(direcaoX, direcaoY, 0.45f, Interpolation.pow2Out),
                        Actions.rotateBy(180f + (float) Math.random() * 180f, 0.45f, Interpolation.fade),
                        Actions.fadeOut(0.45f, Interpolation.fade),
                        Actions.scaleTo(0.2f, 0.2f, 0.45f, Interpolation.fade)
                    ),
                    Actions.removeActor()
                )
            );
        }
    }

    private Image criarFragmentoVisual() {
        return new Image(new TextureRegionDrawable(new TextureRegion(
            GerenciadorTexturas.getSolid(new Color(0.08f, 0.08f, 0.08f, 1f))
        )));
    }

    private void criarRachadurasSobreCarta(Stage stage, Actor carta) {
        if (stage == null || carta == null) return;

        float x = carta.getX();
        float y = carta.getY();
        float w = carta.getWidth();
        float h = carta.getHeight();

        criarLinhaRachadura(stage, x + w * 0.50f, y + h * 0.06f, 3.4f, h * 0.88f, -5f);
        criarLinhaRachadura(stage, x + w * 0.37f, y + h * 0.14f, 2.2f, h * 0.64f, 12f);
        criarLinhaRachadura(stage, x + w * 0.63f, y + h * 0.20f, 2.2f, h * 0.58f, -14f);

        criarLinhaRachadura(stage, x + w * 0.50f, y + h * 0.74f, 2.3f, h * 0.24f, 46f);
        criarLinhaRachadura(stage, x + w * 0.45f, y + h * 0.78f, 2.1f, h * 0.20f, -52f);
        criarLinhaRachadura(stage, x + w * 0.61f, y + h * 0.70f, 2.0f, h * 0.18f, 34f);

        criarLinhaRachadura(stage, x + w * 0.52f, y + h * 0.54f, 2.3f, h * 0.28f, 58f);
        criarLinhaRachadura(stage, x + w * 0.46f, y + h * 0.48f, 2.2f, h * 0.26f, -55f);
        criarLinhaRachadura(stage, x + w * 0.36f, y + h * 0.45f, 2.0f, h * 0.22f, -34f);
        criarLinhaRachadura(stage, x + w * 0.65f, y + h * 0.42f, 2.0f, h * 0.22f, 38f);

        criarLinhaRachadura(stage, x + w * 0.50f, y + h * 0.30f, 2.2f, h * 0.28f, 36f);
        criarLinhaRachadura(stage, x + w * 0.44f, y + h * 0.24f, 2.0f, h * 0.24f, -42f);
        criarLinhaRachadura(stage, x + w * 0.60f, y + h * 0.20f, 2.0f, h * 0.20f, 50f);
    }

    private void criarLinhaRachadura(Stage stage, float x, float y, float largura, float altura, float rotacao) {
        Image linha = new Image(new TextureRegionDrawable(new TextureRegion(
            GerenciadorTexturas.getSolid(new Color(0f, 0f, 0f, 0.95f))
        )));
        linha.setSize(largura, altura);
        linha.setOrigin(Align.center);
        linha.setPosition(x, y);
        linha.setRotation(rotacao);
        linha.getColor().a = 0f;

        stage.addActor(linha);
        linha.toFront();

        linha.addAction(
            Actions.sequence(
                Actions.fadeIn(0.08f, Interpolation.fade),
                Actions.delay(0.38f),
                Actions.fadeOut(0.20f, Interpolation.fade),
                Actions.removeActor()
            )
        );
    }

    public void animarFurtividade(Actor cartaJogador, Actor cartaInimigo, boolean sucesso, Runnable aoFinalizar) {
        cartaJogador.clearActions();
        cartaJogador.setOrigin(Align.center);

        Action faseComum = Actions.parallel(
            Actions.moveBy(-25f, 0f, 1.8f, Interpolation.fade),
            Actions.scaleTo(0.9f, 0.9f, 1.8f, Interpolation.fade),
            Actions.alpha(0.7f, 1.8f, Interpolation.fade)
        );

        Action faseSucesso = Actions.sequence(
            Actions.parallel(
                Actions.moveBy(-35f, 0f, 2.0f, Interpolation.fade),
                Actions.alpha(1f, 2.0f, Interpolation.fade),
                Actions.scaleTo(1f, 1f, 2.0f, Interpolation.swingOut)
            ),
            Actions.moveBy(60f, 0f, 1.2f, Interpolation.fade)
        );

        Action faseFalha = Actions.sequence(
            Actions.parallel(
                Actions.moveBy(20f, 0f, 1.0f, Interpolation.fade),
                Actions.scaleTo(1.05f, 1.05f, 1.0f, Interpolation.fade),
                Actions.alpha(1f, 1.0f, Interpolation.fade)
            ),
            Actions.parallel(
                Actions.moveBy(-20f, 0f, 1.0f, Interpolation.fade),
                Actions.scaleTo(1f, 1f, 1.0f, Interpolation.fade)
            ),
            Actions.moveBy(25f, 0f, 1.2f, Interpolation.fade)
        );

        if (cartaInimigo != null) {
            cartaInimigo.clearActions();
            cartaInimigo.setOrigin(Align.center);
            cartaInimigo.addAction(
                Actions.delay(1.8f,
                    sucesso
                        ? Actions.sequence(
                            Actions.moveBy(15f, 0f, 1.0f, Interpolation.fade),
                            Actions.moveBy(-15f, 0f, 1.0f, Interpolation.fade)
                        )
                        : Actions.sequence(
                            Actions.scaleTo(1.12f, 1.12f, 0.5f, Interpolation.fade),
                            Actions.scaleTo(1f, 1f, 0.5f, Interpolation.fade)
                        )
                )
            );
        }

        cartaJogador.addAction(
            Actions.sequence(
                faseComum,
                sucesso ? faseSucesso : faseFalha,
                Actions.run(() -> {
                    if (aoFinalizar != null) {
                        aoFinalizar.run();
                    }
                })
            )
        );
    }

}
