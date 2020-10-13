package src;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AG {
		private int tamanhoPopulacao;
		private int qtdMovimentos;
		private Labirinto labirinto;
		private int chanceMutacao;
		private final Random r = new Random();

		public AG(int tamanhoPopulacao, int qtdMovimentos, Labirinto labirinto, int chanceMutacao) {
				this.tamanhoPopulacao = tamanhoPopulacao;
				this.qtdMovimentos = qtdMovimentos;
				this.labirinto = labirinto;
				this.chanceMutacao = chanceMutacao;
		}

		public Cromossomo genetica() {
				System.out.println(LocalDateTime.now());
				boolean achouSaida = false;
				List<Cromossomo> populacao = iniciaPopulacao();
				Cromossomo vencedor = null;
				int i = 0;

				while (!achouSaida) {

						populacao.forEach(cromossomo -> aptidao(cromossomo));

						List<Cromossomo> ordenadaPorAptidao = populacao.stream().sorted(Comparator.comparing(Cromossomo::getScore)).collect(Collectors.toList());
						vencedor = populacao.stream().filter(Cromossomo::isChegou).findFirst().orElse(null);
						if (vencedor != null) {
								achouSaida = true;
								System.out.println("VENCEDOR!!!! SCORE " + vencedor.getScore() + " GERACAO: " + i);
								System.out.println(LocalDateTime.now());
								return vencedor;
						}

						if (i % 2000 == 0) {
								System.out.println("i" + i + " score: " + ordenadaPorAptidao.get(0).getScore());
						}


						List<Cromossomo> metadeMaisApta = ordenadaPorAptidao.subList(0, ordenadaPorAptidao.size() / 2);
						populacao.clear();
						populacao = crossOver(duplicaCromossomo(metadeMaisApta));
						i++;
				}
				return vencedor;

		}

		private List<Cromossomo> duplicaCromossomo(List<Cromossomo> c) {
				List<Cromossomo> novaLista = new ArrayList<>();
				c.forEach(it -> {
						novaLista.add(new Cromossomo(it.getTrajeto()));
						novaLista.add(new Cromossomo(it.getTrajeto()));
				});
				return novaLista;
		}


		//21 agentes, com trajeto de tam 102 cada
		public List<Cromossomo> iniciaPopulacao() {
				List<Cromossomo> populacao = new ArrayList<>();
				for (int i = 0; i < tamanhoPopulacao; i++) {
						int[] trajeto = r.ints(qtdMovimentos, 0, 4).toArray();
						populacao.add(new Cromossomo(trajeto));
				}
				return populacao;
		}

		//seta no cromossomo seu valor de aptidao
		public int aptidao(Cromossomo cromossomo) {
				Agente agente = new Agente(this.labirinto);
				agente.percorrerTrajetoPeloLabirinto(cromossomo);
				// fitness = | (x2-x1) | + | (y2-y1) | + accumulated penalties
				Posicao saida = labirinto.getFim();
				Posicao posAtualAgente = agente.getPosicaoAtual();
				int score = (saida.getPosX() - posAtualAgente.getPosX()) + (saida.getPosY() - posAtualAgente.getPosX()) + agente.getPenalidades();
				cromossomo.setScore(score);
				cromossomo.setChegou(agente.getAchouSaida());
				cromossomo.setPosicoes(agente.getCaminhoPercorrido());
				return score;
		}

		public int melhorScoreAptidao(List<Cromossomo> populacao) {
				List<Integer> scores = populacao.stream().map(this::aptidao).collect(Collectors.toList());
				return Collections.min(scores); //pega o maior numero do array
		}

		public Cromossomo mutacao(int[] trajeto) {
				if (r.nextInt(101) < chanceMutacao) {
						trajeto[r.nextInt(trajeto.length)] = r.nextInt(4);
				}
				return new Cromossomo(trajeto);
		}

		//compara 2 agentes e retorna o com melhor score
		public Cromossomo torneio(List<Cromossomo> populacao) {
				int l1 = r.nextInt(populacao.size());
				int l2 = r.nextInt(populacao.size());
				return populacao.get(l1).getScore() < populacao.get(l2).getScore() ? populacao.get(l1) : populacao.get(l2);
		}

		public List<Cromossomo> crossOver(List<Cromossomo> populacao) {
				List<Cromossomo> novaPopulacao = new ArrayList<>();

				for (int i = 0; i < tamanhoPopulacao; i++) {
						int[] pai;
						int[] mae;
						//do{
						pai = torneio(populacao).getTrajeto();
						mae = torneio(populacao).getTrajeto();
						//}while(Arrays.equals(pai,mae));
						int pontoCorte = r.nextInt(qtdMovimentos);

						int[] novoTrajeto = new int[qtdMovimentos];

						//de 0 ao ponto de corte -> movimento do pai; ponte de corte ao final -> movimento da mae
						for (int j = 0; j < qtdMovimentos; j++) {
								novoTrajeto[j] = j < pontoCorte ? pai[j] : mae[j];
						}

						Cromossomo novoCromossomo = mutacao(novoTrajeto);
						novaPopulacao.add(novoCromossomo);
				}
				return novaPopulacao;
		}
}
