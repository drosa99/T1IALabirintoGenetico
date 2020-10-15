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

		private final Nodo inicio;
		private final Nodo fim;
		private List<Nodo> caminho;

		private final Set<Nodo> nodosFechados;
		private final Set<Nodo> nodosAbertos;
		private final HashMap<Nodo, Nodo> veioDe;
		private final HashMap<Nodo, Integer> gScore;
		private final HashMap<Nodo, Integer> fScore;

		public AEstrela(int[][] labirinto, int posX, int posY, int posXf, int posYf, int tamanho) throws Exception {
				this.labirinto = labirinto;
				this.tamanho = tamanho;
				this.inicio = new Nodo(posX, posY);
				this.fim = new Nodo(posXf, posYf);
				this.caminho = new LinkedList<>();

				this.nodosFechados = new HashSet<>();
				this.nodosAbertos = new HashSet<>();
				nodosAbertos.add(inicio);
				this.veioDe = new HashMap<>();
				this.gScore = new HashMap<>();
				this.gScore.put(inicio, 0);
				this.fScore = new HashMap<>();
				this.fScore.put(inicio, heuristica(inicio));
		}

		public void executa() throws Exception {
				Optional<Nodo> _atual = Optional.empty();
				int i = 1;
				while (!nodosAbertos.isEmpty()) {
						_atual = nodosAbertos.stream().min(Comparator.comparingInt(fScore::get)); //pega o nodo aberto com o menor score da heuristica -> mais promissor
						Nodo atual = _atual.get();

						Thread.sleep(250);
						System.out.println("---------------------------------" + ANSI_RESET);
						System.out.println("Passo: " + i);
						try {
								printMazeWithAStarPath(atual);
								i++;
						} catch (Exception e) {
								e.printStackTrace();
						}

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
		}

		//monta a linked list do caminho percorrido, partindo do nodo final ate o nodo de inicio, utilizando o dicionario
		public void reconstroePassos(Nodo nodo) {
				caminho.add(nodo);
				while (veioDe.containsKey(nodo)) {
						nodo = veioDe.get(nodo);
						//adiciona sempre no index 0 pra ir "colocando na frente"
						caminho.add(0, nodo);
				}
		}

		private List<Nodo> getVizinhos(Nodo atual) throws Exception {
				List<Nodo> vizinhos = new ArrayList<>();

				if (atual.x - 1 >= 0 && labirinto[atual.x - 1][atual.y] != 1) {// esquerda
						vizinhos.add(new Nodo(atual.x - 1, atual.y));
				}
				if (atual.y + 1 < tamanho && labirinto[atual.x][atual.y + 1] != 1) {// baixo
						vizinhos.add(new Nodo(atual.x, atual.y + 1));
				}
				if (atual.x + 1 < tamanho && labirinto[atual.x + 1][atual.y] != 1) {// direita
						vizinhos.add(new Nodo(atual.x + 1, atual.y));
				}
				if (atual.y - 1 >= 0 && labirinto[atual.y - 1][atual.x] != 1) {// cima
						vizinhos.add(new Nodo(atual.x, atual.y - 1));
				}

				return vizinhos;
		}

		//distancia euclidiana para a funcao heuristica
		public int heuristica(Nodo nodo) {
				return Math.abs(nodo.y - fim.y) + Math.abs(nodo.x - fim.x);
		}

		public void printMazeWithShortestPath() throws Exception {
				for (int i = 0; i < labirinto.length; i++) {
						for (int j = 0; j < labirinto[i].length; j++) {
								if (caminho.contains(new Nodo(j, i))) {
										System.out.print(ANSI_BLUE + "X");
								} else if (labirinto[j][i] == 0) {
										System.out.print(ANSI_WHITE + "0");
								} else if (labirinto[j][i] == 2 || labirinto[j][i] == 3) {
										System.out.print(ANSI_YELLOW + labirinto[j][i]);
								} else {
										System.out.print(ANSI_RED + labirinto[j][i]);
								}
								System.out.print(" " + ANSI_RESET);
						}
						System.out.println(" " + ANSI_RESET);
				}
		}

		public void printMazeWithAStarPath(Nodo n) throws Exception {
				reconstroePassos(n);
				for (int i = 0; i < labirinto.length; i++) {
						for (int j = 0; j < labirinto[i].length; j++) {
								if (caminho.contains(new Nodo(j, i))) {
										System.out.print(ANSI_BLUE + "X");
								} else if (labirinto[j][i] == 0) {
										System.out.print(ANSI_WHITE + "0");
								} else if (labirinto[j][i] == 2 || labirinto[j][i] == 3) {
										System.out.print(ANSI_YELLOW + labirinto[j][i]);
								} else {
										System.out.print(ANSI_RED + labirinto[j][i]);
								}
								System.out.print(" " + ANSI_RESET);
						}
						System.out.println(" " + ANSI_RESET);
				}
				caminho = new LinkedList<>();
		}

		private class Nodo {
				public int x, y;

				Nodo(int x, int y) throws Exception {
						if (x < 0 || x >= tamanho || y < 0 || y >= tamanho)
								throw new Exception("Nodo invalido: x = " + x + "\n y = " + y);
						this.x = x;
						this.y = y;
				}

				@Override
				public boolean equals(Object obj) {
						return this.x == ((Nodo) obj).x && this.y == ((Nodo) obj).y;
				}
		}
}
