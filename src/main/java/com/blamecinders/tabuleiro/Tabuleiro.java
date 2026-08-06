package com.blamecinders.tabuleiro;

import com.blamecinders.combate.CatalogoInimigos;
import com.blamecinders.combate.Inimigo;
import com.blamecinders.item.CatalogoItens;
import com.blamecinders.item.ItemBau;
import com.blamecinders.modelo.TipoCarta;
import com.blamecinders.modelo.CartaInfo;
import com.blamecinders.modelo.EstadoCarta;

import java.util.Objects;
import java.util.Random;

//Classe responsável pela lógica do tabuleiro.

//RESPONSABILIDADES:
//manter o grid de CartaInfo, manter posição do jogador, gerar cartas aleatórias, garantir existência de apenas uma CHAMA;
//aplicar a mecânica de esteira, controlar revelação/consumo das cartas.

//IMPORTANTE PARA ESTE PROJETO:
//o grid pode conter null temporariamente;
//consultas de tipo devem ser feitas com getTipoSeguro(), a esteira deve preservar os mesmos objetos CartaInfo sempre que possível;
//idealmente, apenas uma nova carta deve entrar por movimento.

public class Tabuleiro {

    public static final int LINHAS = 4; //Quantidade de linhas do grid lógico.
    public static final int COLUNAS = 5; //Quantidade de colunas do grid lógico.
    public static final int OBJETIVO_CHAMAS = 3;

    //Grid principal do jogo.
    //Cada posição pode conter uma CartaInfo válida, null temporariamente, durante transições ou após consumo.
    private final CartaInfo[][] grid;
    private final Random random;

    private int jogadorLinha = 0; //Linha atual do jogador no grid
    private int jogadorColuna = 0; //Coluna atual do jogador no grid
    private int chamasColetadas = 0; //Quantidade de chamas coletadas na run atual.

    //Construtor do tabuleiro, cria o grid e inicializa o conteúdo inicial
    public Tabuleiro() {
        this(new Random());
    }

    Tabuleiro(Random random) {
        this.random = Objects.requireNonNull(random, "random");
        grid = new CartaInfo[LINHAS][COLUNAS];
        inicializar();
    }

    Tabuleiro(CartaInfo[][] grid, int jogadorLinha, int jogadorColuna, Random random) {
        if (grid == null || grid.length != LINHAS) {
            throw new IllegalArgumentException("Grid deve possuir " + LINHAS + " linhas.");
        }
        for (CartaInfo[] linha : grid) {
            if (linha == null || linha.length != COLUNAS) {
                throw new IllegalArgumentException("Grid deve possuir " + COLUNAS + " colunas.");
            }
        }
        if (!estaDentro(jogadorLinha, jogadorColuna)) {
            throw new IllegalArgumentException("Posição inicial do jogador fora do tabuleiro.");
        }

        this.grid = grid;
        this.jogadorLinha = jogadorLinha;
        this.jogadorColuna = jogadorColuna;
        this.random = Objects.requireNonNull(random, "random");
        this.grid[jogadorLinha][jogadorColuna] = null;
    }

    //Preenche o tabuleiro inicial.

    //Regras atuais:
    //o jogador começa em (0,0);
    //as posições (0,1) e (1,0) são forçadas como INIMIGO, o restante é aleatório;
    //ao final, garante ao menos uma CHAMA no tabuleiro.
    private void inicializar() {
        // A célula ocupada pelo herói é sempre o único espaço vazio do grid.
        grid[0][0] = null;

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {

                if (i == 0 && j == 0) continue;

                if (i == 0 && j == 1) {
                    grid[i][j] = criarCarta(TipoCarta.INIMIGO);
                    continue;
                }

                if (i == 1 && j == 0) {
                    grid[i][j] = criarCarta(TipoCarta.INIMIGO);
                    continue;
                }

                grid[i][j] = criarCarta(gerarTipoCartaAleatoria());
            }
        }

        garantirUmaChama();
    }

    //Cria uma CartaInfo conforme o tipo.
    //Regras:
    //INIMIGO recebe um inimigo aleatório, BAU recebe uma arma aleatória, outros tipos ficam apenas com o seu tipo base.
    private CartaInfo criarCarta(TipoCarta tipo) {
        CartaInfo carta = new CartaInfo(tipo);

        if (tipo == TipoCarta.INIMIGO) {
            carta.setInimigo(gerarInimigoAleatorio());
        }

        if (tipo == TipoCarta.BAU) {
            carta.setItemDentro(gerarItemAleatorio());
        }

        return carta;
    }

    //Puxa um inimigo aleatório ao catálogo.
    private Inimigo gerarInimigoAleatorio() {
        return CatalogoInimigos.gerarInimigoAleatorio(random);
    }

    //Puxa uma arma aleatória ao catálogo
    private ItemBau gerarItemAleatorio() {
        return CatalogoItens.gerarItemAleatorio(random);
    }

    //Chances/porcentagem de geração de um tipo aleatório para cartas normais do tabuleiro
    //Distribuição atual:
    //10..29 → BAU
    //30..39 → PAREDE
    //restante → INIMIGO
    //CHAMA não sai daqui, ela é controlada separadamente.
    private TipoCarta gerarTipoCartaAleatoria() {

        int r = random.nextInt(100);

        // Faixa de chance de baú: 20%
        // Porém só gera se ainda houver menos de 3 no tabuleiro.
        if (r >= 10 && r < 30) {
            if (contarBausNoTabuleiro() < 3) {
                return TipoCarta.BAU;
            }
        }

        // Faixa de chance de parede: 10%
        if (r >= 30 && r < 40) {
            return TipoCarta.PAREDE;
        }

        return TipoCarta.INIMIGO;
    }

    //Verifica se o jogador pode se mover para a posição informada.
    //Regras:
    //deve estar dentro do tabuleiro, deve ser adjacente ortogonalmente, não pode ser PAREDE.
    public boolean podeMover(int novaLinha, int novaColuna) {
        if (!estaDentro(novaLinha, novaColuna)) {
            return false;
        }

        int dx = Math.abs(novaColuna - jogadorColuna);
        int dy = Math.abs(novaLinha - jogadorLinha);

        if ((dx + dy) != 1) {
            return false;
        }

        if (getTipoSeguro(novaLinha, novaColuna) == TipoCarta.PAREDE) {
            return false;
        }

        return true;
    }

    //Consulta pública do tipo de carta numa posição, usa a versão segura para proteger contra null.
    public TipoCarta getCarta(int linha, int coluna) {
        return getTipoSeguro(linha, coluna);
    }

    //Consulta segura do tipo da posição.
    //REGRA FUNDAMENTAL DO PROJETO:
    //se a posição estiver null, ela é tratada como VAZIO.
    public TipoCarta getTipoSeguro(int linha, int coluna) {
        if (grid[linha][coluna] == null) {
            return TipoCarta.VAZIO;
        }
        return grid[linha][coluna].getTipo();
    }

    //Retorna a referência bruta de CartaInfo na posição. Pode ser null
    public CartaInfo getCartaInfo(int linha, int coluna) {
        return grid[linha][coluna];
    }

    public int getJogadorLinha() {
        return jogadorLinha;
    }

    public int getJogadorColuna() {
        return jogadorColuna;
    }

    public int getChamasColetadas() {
        return chamasColetadas;
    }

    //Atualiza a posição do jogador.
    //IMPORTANTE:
    //Apenas move o jogador lógicamente
    //Ele não consome carta, não revela carta e não aplica a esteira.
    public void moverJogador(int novaLinha, int novaColuna) {
        if (!podeMover(novaLinha, novaColuna))
            return;

        jogadorLinha = novaLinha;
        jogadorColuna = novaColuna;
        grid[jogadorLinha][jogadorColuna] = null;
    }

    //Gera uma única chama em posição aleatória válida.
    //Restrições:
    //não pode nascer na posição do jogador, não pode nascer em PAREDE, não pode sobrepor outra CHAMA, não pode nascer em carta já REVELADA.
    public void gerarNovaChamaUnica() {

        for (int tentativas = 0; tentativas < 300; tentativas++) {

            int l = random.nextInt(LINHAS);
            int c = random.nextInt(COLUNAS);

            if (l == jogadorLinha && c == jogadorColuna) continue;
            if (getTipoSeguro(l, c) == TipoCarta.PAREDE) continue;
            if (getTipoSeguro(l, c) == TipoCarta.CHAMA) continue;
            if (cartaEstaRevelada(l, c)) continue;

            grid[l][c] = criarCarta(TipoCarta.CHAMA);
            return;
        }

    }

    //Registra a coleta e deixa a célula livre para o movimento do herói.
    //A nova chama é garantida após a esteira, exceto quando o objetivo foi alcançado.
    public boolean coletarChama(int linha, int coluna) {
        if (!estaDentro(linha, coluna) || getTipoSeguro(linha, coluna) != TipoCarta.CHAMA) {
            return false;
        }

        chamasColetadas++;
        grid[linha][coluna] = null;
        return true;
    }

    //Aplica o efeito de esteira após o movimento do jogador.
    //Regras atuais:
    //se o jogador foi para a direita, a linha anda para a esquerda;
    //se foi para a esquerda, a linha anda para a direita;
    //se foi para baixo, a coluna sobe;
    //se foi para cima, a coluna desce.
    public void aplicarEsteira(int antigaLinha, int antigaColuna, int novaLinha, int novaColuna) {
        int dx = novaColuna - antigaColuna;
        int dy = novaLinha - antigaLinha;

        if (Math.abs(dx) + Math.abs(dy) != 1) {
            throw new IllegalArgumentException("A esteira exige um movimento ortogonal de uma célula.");
        }

        if (dx == 1) { // jogador foi para a direita; preenche o vazio pela borda esquerda
            preencherVazioPelaEsquerda(antigaLinha, antigaColuna);
        }
        else if (dx == -1) { // jogador foi para a esquerda; preenche pela borda direita
            preencherVazioPelaDireita(antigaLinha, antigaColuna);
        }
        else if (dy == 1) { // jogador foi para baixo; preenche pela borda superior
            preencherVazioPorCima(antigaColuna, antigaLinha);
        }
        else { // jogador foi para cima; preenche pela borda inferior
            preencherVazioPorBaixo(antigaColuna, antigaLinha);
        }

        preencherNulosExcetoJogador();

        if (!existeMovimentoValido()) {
            gerarSaidaEmergencial();
        }

        if (chamasColetadas < OBJETIVO_CHAMAS && !existeChama()) {
            garantirUmaChama();
        }
    }

    //Verifica se existe alguma CHAMA no tabuleiro
    private boolean existeChama() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.CHAMA) {
                    return true;
                }
            }
        }

        return false;
    }

    private void preencherVazioPelaEsquerda(int linha, int colunaVazia) {
        for (int c = colunaVazia; c > 0; c--) {
            grid[linha][c] = grid[linha][c - 1];
        }
        grid[linha][0] = gerarCartaEsteiraSegura();
    }

    private void preencherVazioPelaDireita(int linha, int colunaVazia) {
        for (int c = colunaVazia; c < COLUNAS - 1; c++) {
            grid[linha][c] = grid[linha][c + 1];
        }
        grid[linha][COLUNAS - 1] = gerarCartaEsteiraSegura();
    }

    private void preencherVazioPorCima(int coluna, int linhaVazia) {
        for (int l = linhaVazia; l > 0; l--) {
            grid[l][coluna] = grid[l - 1][coluna];
        }
        grid[0][coluna] = gerarCartaEsteiraSegura();
    }

    private void preencherVazioPorBaixo(int coluna, int linhaVazia) {
        for (int l = linhaVazia; l < LINHAS - 1; l++) {
            grid[l][coluna] = grid[l + 1][coluna];
        }
        grid[LINHAS - 1][coluna] = gerarCartaEsteiraSegura();
    }

    //Gera uma carta válida para entrar pela esteira.
    //CHAMA é proibida aqui, porque a presença de CHAMA é controlada separadamente por garantirUmaChama().
    private CartaInfo gerarCartaEsteiraSegura() {
        TipoCarta tipo;

        do {
            tipo = gerarTipoCartaAleatoria();
        }
        while (tipo == TipoCarta.CHAMA);

        return criarCarta(tipo);
    }

    //Verifica se o jogador possui ao menos um movimento ortogonal disponível.
    public boolean existeMovimentoValido() {
        int l = jogadorLinha;
        int c = jogadorColuna;

        if (l > 0 && getTipoSeguro(l - 1, c) != TipoCarta.PAREDE) return true;
        if (l < LINHAS - 1 && getTipoSeguro(l + 1, c) != TipoCarta.PAREDE) return true;
        if (c > 0 && getTipoSeguro(l, c - 1) != TipoCarta.PAREDE) return true;
        if (c < COLUNAS - 1 && getTipoSeguro(l, c + 1) != TipoCarta.PAREDE) return true;

        return false;
    }

    //Se o jogador ficar sem saída válida força a criação de uma carta acessível nesse caso um INIMIGO.
    private void gerarSaidaEmergencial() {
        int l = jogadorLinha;
        int c = jogadorColuna;

        if (l > 0) grid[l - 1][c] = criarCarta(TipoCarta.INIMIGO);
        else grid[l + 1][c] = criarCarta(TipoCarta.INIMIGO);
    }

    //Garante a regra de exatamente uma CHAMA ativa, se não houver nenhuma, cria uma, se houver mais de uma, mantém apenas a primeira encontrada.
    private void garantirUmaChama() {
        int quantidade = 0;

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.CHAMA) {
                    quantidade++;
                }
            }
        }

        if (quantidade == 0) {
            gerarNovaChamaUnica();
            return;
        }

        if (quantidade > 1) {
            boolean manteveUma = false;

            for (int i = 0; i < LINHAS; i++) {
                for (int j = 0; j < COLUNAS; j++) {
                    if (getTipoSeguro(i, j) == TipoCarta.CHAMA) {
                        if (!manteveUma) {
                            manteveUma = true;
                        } else {
                            grid[i][j] = gerarCartaEsteiraSegura();
                        }
                    }
                }
            }
        }
    }

    //Marca a carta como revelada no modelo lógico.
    public void revelarCarta(int linha, int coluna) {
        CartaInfo carta = grid[linha][coluna];
        if (carta != null) {
            carta.setEstado(EstadoCarta.REVELADA);
        }
    }

    //Consulta se a carta está revelada no modelo lógico.
    public boolean cartaEstaRevelada(int linha, int coluna) {
        CartaInfo carta = grid[linha][coluna];
        return carta != null && carta.getEstado() == EstadoCarta.REVELADA;
    }

    //Remove a carta da posição.
    public void consumirCarta(int linha, int coluna) {
        grid[linha][coluna] = null;
    }

    //PREENCHIMENTO GLOBAL DE NULOS
    //Recompõe qualquer posição null do grid com uma nova carta fechada.
    private void preencherNulosExcetoJogador() {
        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (i == jogadorLinha && j == jogadorColuna) {
                    grid[i][j] = null;
                    continue;
                }
                if (grid[i][j] == null) {
                    grid[i][j] = gerarCartaEsteiraSegura();
                    grid[i][j].setEstado(EstadoCarta.FECHADA);
                }
            }
        }
    }

    //Conta quantos baús existem atualmente no tabuleiro
    private int contarBausNoTabuleiro() {
        int quantidade = 0;

        for (int i = 0; i < LINHAS; i++) {
            for (int j = 0; j < COLUNAS; j++) {
                if (getTipoSeguro(i, j) == TipoCarta.BAU) {
                    quantidade++;
                }
            }
        }

        return quantidade;
    }

    private boolean estaDentro(int linha, int coluna) {
        return linha >= 0 && linha < LINHAS && coluna >= 0 && coluna < COLUNAS;
    }
}
