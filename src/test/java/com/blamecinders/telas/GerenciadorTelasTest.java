package com.blamecinders.telas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GerenciadorTelasTest {

    @Test
    void trocaEncerraTelaAnteriorUmaUnicaVez() {
        GerenciadorTelas gerenciador = new GerenciadorTelas();
        TelaFalsa primeira = new TelaFalsa();
        TelaFalsa segunda = new TelaFalsa();

        gerenciador.trocarTela(primeira);
        gerenciador.trocarTela(segunda);

        assertEquals(1, primeira.mostrou);
        assertEquals(1, primeira.escondeu);
        assertEquals(1, primeira.destruiu);
        assertEquals(1, segunda.mostrou);
    }

    @Test
    void encerrarTelaAtualPodeSerChamadoMaisDeUmaVez() {
        GerenciadorTelas gerenciador = new GerenciadorTelas();
        TelaFalsa tela = new TelaFalsa();
        gerenciador.trocarTela(tela);

        gerenciador.encerrarTelaAtual();
        gerenciador.encerrarTelaAtual();

        assertEquals(1, tela.escondeu);
        assertEquals(1, tela.destruiu);
    }

    @Test
    void telaNulaNaoDestroiTelaAtual() {
        GerenciadorTelas gerenciador = new GerenciadorTelas();
        TelaFalsa tela = new TelaFalsa();
        gerenciador.trocarTela(tela);

        assertThrows(NullPointerException.class, () -> gerenciador.trocarTela(null));

        assertEquals(0, tela.escondeu);
        assertEquals(0, tela.destruiu);
    }

    private static final class TelaFalsa implements Tela {
        private int mostrou;
        private int escondeu;
        private int destruiu;

        @Override
        public void mostrar() {
            mostrou++;
        }

        @Override
        public void render(float delta) {
        }

        @Override
        public void redimensionar(int largura, int altura) {
        }

        @Override
        public void esconder() {
            escondeu++;
        }

        @Override
        public void destruir() {
            destruiu++;
        }
    }
}
