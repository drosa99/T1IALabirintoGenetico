package src;

import java.util.ArrayList;
import java.util.Arrays;

public class Agente {
		private final int PENALIDADE_PAREDE = 20;
		private final int PENALIDADE_SAIU = 50;
		private final int PENALIDADE_REPETINDO = 20;

		private ArrayList<Posicao> caminhoPercorrido = new ArrayList<>();
		private Posicao posicaoAtual;
		private int penalidades = 0;
		private boolean achouSaida = false;
		private Labirinto labirinto;

		public Agente(Labirinto labirinto) {
				this.labirinto = labirinto;
				this.posicaoAtual = labirinto.getInicio();
		}

		public void andarPraCima() {
				Posicao novaPosicao = new Posicao(posicaoAtual.getPosX(), posicaoAtual.getPosY() - 1);
				movimentaAgente(novaPosicao);
		}

		public void andarPraBaixo() {
				Posicao novaPosicao = new Posicao(posicaoAtual.getPosX(), posicaoAtual.getPosY() + 1);
				movimentaAgente(novaPosicao);
		}

		public void andarPraEsquerda() {
				Posicao novaPosicao = new Posicao(posicaoAtual.getPosX() - 1, posicaoAtual.getPosY());
				movimentaAgente(novaPosicao);
		}

		public void andarPraDireita() {
				Posicao novaPosicao = new Posicao(posicaoAtual.getPosX() + 1, posicaoAtual.getPosY());
				movimentaAgente(novaPosicao);
		}


		private void movimentaAgente(Posicao novaPosicao) {
				if (labirinto.isSaida(novaPosicao)) {
						this.posicaoAtual = novaPosicao;
						this.achouSaida = true;
				}

				if (labirinto.isSaida(posicaoAtual)) {
						this.caminhoPercorrido.add(this.posicaoAtual);
						return;
				}

				if (labirinto.getParedes().stream().anyMatch(it -> novaPosicao.equals(it)) || labirinto.isParede(novaPosicao)) {
						this.penalidades += PENALIDADE_PAREDE;
//						if(caminhoPercorrido.stream().anyMatch(it -> it.equals(posicaoAtual))){
//								//nao saiu do lugar e bateu na parede
//								long qtdVezesNoMesmoLugar = caminhoPercorrido.stream().filter(it -> it.equals(posicaoAtual)).count();
//								this.penalidades += PENALIDADE_REPETINDO + (qtdVezesNoMesmoLugar * 5);
//						}
				}

				if (labirinto.saiuLabirinto(novaPosicao)) {
						this.penalidades += PENALIDADE_SAIU;
				}

				if (caminhoPercorrido.stream().anyMatch(it -> it.equals(novaPosicao))) {
						this.penalidades += PENALIDADE_REPETINDO;
				}

				if (labirinto.isChaoValido(novaPosicao)) {
						this.posicaoAtual = novaPosicao;
				}
				this.caminhoPercorrido.add(this.posicaoAtual);
		}

		public Posicao getPosicaoAtual() {
				return posicaoAtual;
		}

		public int getPenalidades() {
				return penalidades;
		}

		public boolean getAchouSaida() {
				return achouSaida;
		}

		public ArrayList<Posicao> getCaminhoPercorrido() {
				return caminhoPercorrido;
		}

		public void percorrerTrajetoPeloLabirinto(Cromossomo cromossomo) {
				Arrays.stream(cromossomo.getTrajeto()).forEach(comando -> {
						switch (comando) {
								case 0:
										andarPraEsquerda();
										break;
								case 1:
										andarPraCima();
										break;
								case 2:
										andarPraDireita();
										break;
								case 3:
										andarPraBaixo();
										break;
						}
				});
		}
}
