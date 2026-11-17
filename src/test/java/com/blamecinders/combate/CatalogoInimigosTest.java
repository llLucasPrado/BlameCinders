package com.blamecinders.combate;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogoInimigosTest {

    @Test
    void distribuiModelosEmQuatroFaixasDeDificuldade() {
        List<Inimigo> modelos = CatalogoInimigos.listarModelos();
        Map<CategoriaInimigo, Integer> totais = new EnumMap<>(CategoriaInimigo.class);

        for (Inimigo inimigo : modelos) {
            totais.merge(inimigo.getCategoria(), 1, Integer::sum);
            assertTrue(inimigo.getCategoria().contem(inimigo.getVida()));
        }

        assertEquals(13, modelos.size());
        assertEquals(5, totais.get(CategoriaInimigo.FRACO));
        assertEquals(3, totais.get(CategoriaInimigo.MEDIO_BAIXO));
        assertEquals(3, totais.get(CategoriaInimigo.MEDIO_ALTO));
        assertEquals(2, totais.get(CategoriaInimigo.MEDIO_DIFICIL));
    }

    @Test
    void novasCriaturasPossuemIdentificadoresSemanticos() {
        List<Inimigo> modelos = CatalogoInimigos.listarModelos();

        assertTrue(modelos.stream().anyMatch(inimigo ->
            inimigo.getNome().equals("Corvo da Vigília")
                && inimigo.getIdentificadorVisual().equals("INIMIGO_CORVO_VIGIA")
        ));
        assertTrue(modelos.stream().anyMatch(inimigo ->
            inimigo.getNome().equals("Rato Couraçado")
                && inimigo.getIdentificadorVisual().equals("INIMIGO_RATO_COURACADO")
        ));
        assertTrue(modelos.stream().anyMatch(inimigo ->
            inimigo.getNome().equals("Espírito Acorrentado")
                && inimigo.getIdentificadorVisual().equals("INIMIGO_ESPIRITO_ACORRENTADO")
        ));
    }

    @Test
    void sorteioMantemPesosDasQuatroCategorias() {
        assertEquals(
            CategoriaInimigo.FRACO,
            CatalogoInimigos.gerarInimigoAleatorio(new RandomFixo(39, 0)).getCategoria()
        );
        assertEquals(
            CategoriaInimigo.MEDIO_BAIXO,
            CatalogoInimigos.gerarInimigoAleatorio(new RandomFixo(40, 0)).getCategoria()
        );
        assertEquals(
            CategoriaInimigo.MEDIO_ALTO,
            CatalogoInimigos.gerarInimigoAleatorio(new RandomFixo(70, 0)).getCategoria()
        );
        assertEquals(
            CategoriaInimigo.MEDIO_DIFICIL,
            CatalogoInimigos.gerarInimigoAleatorio(new RandomFixo(90, 0)).getCategoria()
        );
    }

    @Test
    void inimigoMaisDificilNaoMataHeroiSaudavelEmUmUnicoCombate() {
        int menorVidaHeroi = Integer.MAX_VALUE;
        for (Personagem personagem : Personagem.values()) {
            menorVidaHeroi = Math.min(menorVidaHeroi, personagem.getVidaInicial());
        }

        assertTrue(CategoriaInimigo.MEDIO_DIFICIL.getVidaMaxima() < menorVidaHeroi);
    }

    @Test
    void forneceCopiasSemExporCatalogoMutavel() {
        List<Inimigo> primeira = CatalogoInimigos.listarModelos();
        List<Inimigo> segunda = CatalogoInimigos.listarModelos();

        assertNotSame(primeira.get(0), segunda.get(0));
        assertThrows(
            UnsupportedOperationException.class,
            () -> primeira.add(new Inimigo("Teste", 5, "INIMIGO 1"))
        );
    }

    @Test
    void rejeitaInimigoSemVidaOuIdentificacao() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Inimigo("Inválido", 0, "INIMIGO 1")
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new Inimigo(" ", 5, "INIMIGO 1")
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new Inimigo("Inválido", 5, " ")
        );
    }

    private static final class RandomFixo extends java.util.Random {
        private final int[] valores;
        private int indice;

        private RandomFixo(int... valores) {
            this.valores = valores;
        }

        @Override
        public int nextInt(int limite) {
            int valor = valores[Math.min(indice++, valores.length - 1)];
            return Math.min(valor, limite - 1);
        }
    }
}
