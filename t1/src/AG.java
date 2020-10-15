package src;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
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

		public Cromossomo genetica(int qtdGeracoesPrinte) {
				System.out.println(LocalDateTime.now());
				boolean achouSaida = false;
				List<Cromossomo> populacao = iniciaPopulacao();
				Cromossomo vencedor = null;
				int i = 0;

				//Ponto de parada: solucao otima -> achar a saida; apos um certo numero de geracoes -> recomeca
				while (!achouSaida) {

						//calcula a aptidao de todos os cromossomos da populacao
						populacao.forEach(this::aptidao);

						//ordenada os cromossomos da populacao por aptida em order ascendente -> melhor primeiro
						List<Cromossomo> ordenadaPorAptidao = populacao.stream().sorted(Comparator.comparing(Cromossomo::getScore)).collect(Collectors.toList());

						//filtra dentro da populacao se algum cromossomo encontrou a saida -> se nao encontrou -> vencedor = null
						vencedor = populacao.stream().filter(Cromossomo::isChegou).findFirst().orElse(null);
						if (vencedor != null) {
								achouSaida = true;
								System.out.println("VENCEDOR!!!! SCORE " + vencedor.getScore() + " GERACAO: " + i);
								System.out.println(LocalDateTime.now());
								return vencedor;
						}

						//printa o andamento do algoritmo a cada X geracoes que recebeu por parametro
						if (i % qtdGeracoesPrinte == 0) {
								System.out.println("Geracao: " + i);
								System.out.println("Melhor cromossomo com score: " + ordenadaPorAptidao.get(0).getScore());
								ordenadaPorAptidao.get(0).getPosicoes().forEach(posicao -> System.out.print(posicao.toString() + ", "));
								System.out.println("--------------------------------------------------------------------------- \n");
						}


						//pega a metade da populacao com melhor aptidao
						List<Cromossomo> metadeMaisApta = ordenadaPorAptidao.subList(0, ordenadaPorAptidao.size() / 2);
						populacao.clear();

						//depois de um grande numero de geracoes, sem ter encontrado a saida, reinicia a populacao, pois deve estar com a populacao toda muito similar
						if (i != 0 && i % 150000 == 0) {
								System.out.println(" \n RECOMECANDO.... \n");
								metadeMaisApta = iniciaPopulacao().subList(0, ordenadaPorAptidao.size() / 2);
						}

						//faz o crossover utilizando a nova populacao, que eh a metade mais apta da populacao anterior, duplicada
						populacao = crossOver(duplicaCromossomo(metadeMaisApta));
						i++;
				}
				return vencedor;

		}

		//duplica uma lista de cromossomos em uma nova lista
		private List<Cromossomo> duplicaCromossomo(List<Cromossomo> c) {
				List<Cromossomo> novaLista = new ArrayList<>();
				c.forEach(it -> {
						novaLista.add(new Cromossomo(it.getTrajeto()));
						novaLista.add(new Cromossomo(it.getTrajeto()));
				});
				return novaLista;
		}


		//cria uma lista de Cromossomos, com trajeto randomizado, tamanho da populacao e tamanho do vetor de movimentos parametrizado
		public List<Cromossomo> iniciaPopulacao() {
				List<Cromossomo> populacao = new ArrayList<>();
				for (int i = 0; i < tamanhoPopulacao; i++) {
						int[] trajeto = r.ints(qtdMovimentos, 0, 4).toArray();
						populacao.add(new Cromossomo(trajeto));
				}
				return populacao;
		}

		//calcula a aptidao somando a distancia euclidiana da ultima posicao do cromossomo ate a saida + penalidades que sofreu ao andar pelo labirinto
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

		//faz a mutacao de um trajeto, se cair na porcentagem de chance de mutacao, pega uma posicao aleatoria do vetor de movimentos, e altera seu valor de forma randomica
		public Cromossomo mutacao(int[] trajeto) {
				if (r.nextInt(101) < chanceMutacao) {
						trajeto[r.nextInt(trajeto.length)] = r.nextInt(4);
				}
				return new Cromossomo(trajeto);
		}

		//pega 2 cromossomos aleatorios da populacao e retorna o cromossomo com melhor score de aptidao
		public Cromossomo torneio(List<Cromossomo> populacao) {
				int l1 = r.nextInt(populacao.size());
				int l2 = r.nextInt(populacao.size());
				return populacao.get(l1).getScore() < populacao.get(l2).getScore() ? populacao.get(l1) : populacao.get(l2);
		}

		/*
		 * Metodo que recebe uma populacao e retorna uma nova populacao, fazendo crossover, torneio e mutacao
		 * Para cada novo cromossomo da nova populacao:
		 * 	Seleciona 2 cromossomos da antiga populacao por torneio
		 * 	Randomiza um indice que sera o ponto de corte para o crossover destes 2 cromossomos
		 * 	Cruzamento uniponto com ponto de corte randomico: Mergeia estes 2 cromossomos em um novo, sendo de 0 a ponto de corte, o cromossomo pai; sendo do ponto de corte ao final do trajto, o cromossomo mae;
		 * 	Faz mutacao do trajeto deste novo cromossomo
		 * 	Adiciona o novo cromossomo na nova populacao
		 * */
		public List<Cromossomo> crossOver(List<Cromossomo> populacao) {
				List<Cromossomo> novaPopulacao = new ArrayList<>();

				for (int i = 0; i < tamanhoPopulacao; i++) {
						int[] pai;
						int[] mae;

						pai = torneio(populacao).getTrajeto();
						mae = torneio(populacao).getTrajeto();

						int pontoCorte = r.nextInt(qtdMovimentos);

						int[] novoTrajeto = new int[qtdMovimentos];

						//de 0 ao ponto de corte -> movimento do pai; ponte de corte ao final -> movimento da mae
						for (int j = 0; j < qtdMovimentos; j++) {
								novoTrajeto[j] = j < pontoCorte ? pai[j] : mae[j];
						}
						//faz mutacao no novo cromossomo
						Cromossomo novoCromossomo = mutacao(novoTrajeto);
						novaPopulacao.add(novoCromossomo);
				}
				return novaPopulacao;
		}
}
