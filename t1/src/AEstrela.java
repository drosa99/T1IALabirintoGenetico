package src;

import java.util.*;

public class AEstrela {
		public static final String ANSI_GREEN = "\u001B[32m";
		public static final String ANSI_BLUE = "\u001B[34m";
		public static final String ANSI_RED = "\u001b[31m";
		public static final String ANSI_YELLOW = "\u001b[33;1m";
		public static final String ANSI_WHITE = "\u001b[37m";
		public static final String ANSI_RESET = "\u001b[0m";

		private final int[][] labirinto;
		private final int tamanho;

		private final Posicao inicio;
		private final Posicao fim;
		private List<Posicao> caminho;

		private final Set<Posicao> nodosFechados;
		private final Set<Posicao> nodosAbertos;
		private final HashMap<Posicao, Posicao> veioDe;
		private final HashMap<Posicao, Integer> gScore;
		private final HashMap<Posicao, Integer> fScore;

		public AEstrela(int[][] labirinto, Posicao inicio, Posicao fim, int tamanho) {
				this.labirinto = labirinto;
				this.tamanho = tamanho;
				this.inicio = new Posicao(inicio.getPosX(), inicio.getPosY());
				this.fim = new Posicao(fim.getPosX(), fim.getPosY());
				this.caminho = new LinkedList<Posicao>();

				this.nodosFechados = new HashSet<Posicao>();
				this.nodosAbertos = new HashSet<Posicao>();
				nodosAbertos.add(inicio);
				this.veioDe = new HashMap<Posicao, Posicao>();
				this.gScore = new HashMap<Posicao, Integer>();
				this.gScore.put(inicio, 0);
				this.fScore = new HashMap<Posicao, Integer>();
				this.fScore.put(inicio, heuristica(inicio));
		}

		public List<Posicao> executa() {
				Optional<Posicao> _atual = Optional.empty();
				int i = 1;
				while (!nodosAbertos.isEmpty()) {
						_atual = nodosAbertos.stream().min(Comparator.comparingInt(fScore::get)); //pega o nodo aberto com o menor score da heuristica -> mais promissor
						Posicao atual = _atual.get();

						try {
								Thread.sleep(250);
						} catch (InterruptedException e) {
								e.printStackTrace();
						}
						System.out.println("---------------------------------" + ANSI_RESET);
						System.out.println("Passo: " + i);
						printLabirintoAPartirPosicao(atual);
						i++;

						if (atual.equals(fim))
								break;

						nodosAbertos.remove(atual); //como vai explorar todos os filhos, tira dos nodos abertos e coloca nos fechados
						nodosFechados.add(atual);

						getVizinhos(atual).stream()
								.filter(vizinho -> nodosFechados.stream().noneMatch(nodoFechado -> nodoFechado.equals(vizinho)))
								.forEach(vizinho -> { //visita todos os vizinhos que ainda nao foram abertos
										int tentativaScore = gScore.get(atual) + 1; //score de tentativas

										if (nodosAbertos.stream().noneMatch(nodoAberto -> nodoAberto.equals(vizinho))) {  //se o nodo ainda nao foi aberto -> abre ele
												nodosAbertos.add(vizinho);
										}

										//se o nodo ja esta aberto, e ja foi avaliado como vizinho de alguem,
										//sai da execucao e nao avalia ele, para evitar que o nodo esteja duplicado no dicionaraio veioDe
										else if (tentativaScore >= gScore.keySet().stream()
												.filter(k -> k.equals(vizinho))
												.map(k -> gScore.get(k))
												.findFirst().orElse(Integer.MAX_VALUE)) {
												return;
										}
										veioDe.put(vizinho, atual); //atribui relacao pai e filho -> vizinho veio do atual
										gScore.put(vizinho, tentativaScore); //atribui score da tentativa para evitar loop com o else if de cime
										fScore.put(vizinho, tentativaScore + heuristica(vizinho)); //calcula a heuristica do nodo
								});
				}
				_atual.ifPresent(this::reconstroePassos);
				return caminho;
		}

		//monta a lista do caminho percorrido, partindo do nodo final ate o nodo de inicio, utilizando o dicionario
		public void reconstroePassos(Posicao nodo) {
				caminho.add(nodo);
				while (veioDe.containsKey(nodo)) {
						nodo = veioDe.get(nodo);
						//adiciona sempre no index 0 pra ir "colocando na frente"
						caminho.add(0, nodo);
				}
		}

		private List<Posicao> getVizinhos(Posicao atual) {
				List<Posicao> vizinhos = new ArrayList<>();

				if (atual.getPosX() - 1 >= 0 && labirinto[atual.getPosX() - 1][atual.getPosY()] != 1) {// esquerda
						vizinhos.add(new Posicao(atual.getPosX() - 1, atual.getPosY()));
				}
				if (atual.getPosY() + 1 < tamanho && labirinto[atual.getPosX()][atual.getPosY() + 1] != 1) {// baixo
						vizinhos.add(new Posicao(atual.getPosX(), atual.getPosY() + 1));
				}
				if (atual.getPosX() + 1 < tamanho && labirinto[atual.getPosX() + 1][atual.getPosY()] != 1) {// direita
						vizinhos.add(new Posicao(atual.getPosX() + 1, atual.getPosY()));
				}
				if (atual.getPosY() - 1 >= 0 && labirinto[atual.getPosX()][atual.getPosY() - 1] != 1) {// cima
						vizinhos.add(new Posicao(atual.getPosX(), atual.getPosY() - 1));
				}

				return vizinhos;
		}

		//distancia euclidiana para a funcao heuristica
		public int heuristica(Posicao nodo) {
				return Math.abs(nodo.getPosY() - fim.getPosY()) + Math.abs(nodo.getPosX() - fim.getPosX());
		}

		public String printaLabirintoComCaminho() {
				String retorno = "\n \n ";
				for (int i = 0; i < labirinto.length; i++) {
						for (int j = 0; j < labirinto[i].length; j++) {
								if (caminho.contains(new Posicao(j, i))) {
										System.out.print(ANSI_BLUE + "X");
										retorno = retorno.concat("X" + " ");
								} else if (labirinto[j][i] == 0) {
										System.out.print(ANSI_WHITE + "0");
										retorno = retorno.concat("0" + " ");
								} else if (labirinto[j][i] == 2 || labirinto[j][i] == 3) {
										System.out.print(ANSI_YELLOW + labirinto[j][i]);
										retorno = retorno.concat(labirinto[j][i] + " ");
								} else {
										System.out.print(ANSI_RED + labirinto[j][i]);
										retorno = retorno.concat(labirinto[j][i] + " ");
								}
								System.out.print(" " + ANSI_RESET);
						}
						System.out.println(" " + ANSI_RESET);
						retorno = retorno.concat("\n ");
				}
				return retorno.concat("___________________________________________ \n");
		}

		public void printLabirintoAPartirPosicao(Posicao n) {
				reconstroePassos(n);
				printaLabirintoComCaminho();
				caminho = new LinkedList<Posicao>();
		}
}
