package src;

import java.util.*;

public class AEstrela {
		public static final String ANSI_GREEN = "\u001B[32m";
		public static final String ANSI_BLUE = "\u001B[34m";
		public static final String ANSI_RED = "\u001b[31m";
		public static final String ANSI_YELLOW = "\u001b[33;1m";
		public static final String ANSI_WHITE = "\u001b[37m";
		public static final String ANSI_RESET = "\u001b[0m";

		private final int[][] maze;
		private final int size;

		private final Nodo start;
		private final Nodo end;
		private List<Nodo> path;

		private final Set<Nodo> closedSet;
		private final Set<Nodo> openSet;
		private final HashMap<Nodo, Nodo> cameFrom;
		private final HashMap<Nodo, Integer> gScore;
		private final HashMap<Nodo, Integer> fScore;

		public AEstrela(int[][] maze, int posX, int posY, int posXf, int posYf, int size) throws Exception {
				this.maze = maze;
				this.size = size;
				this.start = new Nodo(posX, posY);
				this.end = new Nodo(posXf, posYf);
				this.path = new LinkedList<>();

				this.closedSet = new HashSet<>();
				this.openSet = new HashSet<>();
				openSet.add(start);
				this.cameFrom = new HashMap<>();
				this.gScore = new HashMap<>();
				this.gScore.put(start, 0);
				this.fScore = new HashMap<>();
				this.fScore.put(start, heuristica(start));
		}

		public void executa() throws Exception {
				Optional<Nodo> _atual = Optional.empty();
				while (!openSet.isEmpty()) {
						_atual = openSet.stream().min(Comparator.comparingInt(fScore::get)); //pega o nodo aberto com o menor score da heuristica -> mais promissor
						Nodo atual = _atual.get();

						Thread.sleep(250);
						System.out.println("---------------------------------" + ANSI_RESET);
						try {
								printMazeWithAStarPath(atual);
						} catch (Exception e) {
								e.printStackTrace();
						}

						if (atual.equals(end))
								break;

						openSet.remove(atual); //como vai explorar todos os filhos, tira dos nodos abertos e coloca nos fechados
						closedSet.add(atual);

						getVizinhos(atual).stream()
								.filter(v -> closedSet.stream().noneMatch(c -> c.equals(v)))
								.forEach(v -> { //visita todos os vizinhos que ainda nao foram abertos
										int tentativeScore = gScore.get(atual) + 1; //score de tentativas

										if (openSet.stream().noneMatch(o -> o.equals(v))) {  //se o nodo ainda nao foi aberto -> abre ele
												openSet.add(v);

												if (gScore.keySet().stream().noneMatch(k -> k.equals(v))) //todo ver se isso precisa
														fScore.put(v, Integer.MAX_VALUE);

												if (gScore.keySet().stream().noneMatch(k -> k.equals(v))) //usado depois pra evitar loop -> talvez possa ser tirado por causa do orElse(Integer.max)
														gScore.put(v, Integer.MAX_VALUE);

										} else if (tentativeScore >= gScore.keySet().stream()
												.filter(k -> k.equals(v))
												.map(k -> gScore.get(k))
												.findFirst().orElse(Integer.MAX_VALUE)) { //vem aqui se o nodo esta aberto -> pode ser pra evitar loop entre irmaos
												return;
										}
										cameFrom.put(v, atual); //atribui relacao pai e filho -> v veio do atual
										gScore.put(v, tentativeScore); //atribui score da tentativa para evitar loop com o else if de cime
										fScore.put(v, tentativeScore + heuristica(v)); //calcula a heuristica do nodo
								});
				}
				_atual.ifPresent(this::reconstroePassos);
		}

		public void reconstroePassos(Nodo nodo) {
				path.add(nodo);
				while (cameFrom.containsKey(nodo)) {
						nodo = cameFrom.get(nodo);
						path.add(0, nodo);
				}
		}

		private List<Nodo> getVizinhos(Nodo atual) throws Exception {
				List<Nodo> vizinhos = new ArrayList<>();

				if (atual.x - 1 >= 0 && maze[atual.x - 1][atual.y] != 1) {// esquerda
						vizinhos.add(new Nodo(atual.x - 1, atual.y));
				}
				if (atual.y + 1 < size && maze[atual.x][atual.y + 1] != 1) {// baixo
						vizinhos.add(new Nodo(atual.x, atual.y + 1));
				}
				if (atual.x + 1 < size && maze[atual.x + 1][atual.y] != 1) {// direita
						vizinhos.add(new Nodo(atual.x + 1, atual.y));
				}
				if (atual.y - 1 >= 0 && maze[atual.y - 1][atual.x] != 1) {// cima
						vizinhos.add(new Nodo(atual.x, atual.y - 1));
				}

				return vizinhos;
		}

		//distancia
		public int heuristica(Nodo nodo) {
				return Math.abs(nodo.y - end.y) + Math.abs(nodo.x - end.x);
		}

		public void printMazeWithShortestPath() throws Exception {
				for (int i = 0; i < maze.length; i++) {
						for (int j = 0; j < maze[i].length; j++) {
								if (path.contains(new Nodo(j, i))) {
										System.out.print(ANSI_BLUE + "X");
								} else if (maze[j][i] == 0) {
										System.out.print(ANSI_WHITE + "0");
								} else if (maze[j][i] == 2 || maze[j][i] == 3) {
										System.out.print(ANSI_YELLOW + maze[j][i]);
								} else {
										System.out.print(ANSI_RED + maze[j][i]);
								}
								System.out.print(" " + ANSI_RESET);
						}
						System.out.println(" " + ANSI_RESET);
				}
		}

		public void printMazeWithAStarPath(Nodo n) throws Exception {
				reconstroePassos(n);
				for (int i = 0; i < maze.length; i++) {
						for (int j = 0; j < maze[i].length; j++) {
								if (path.contains(new Nodo(j, i))) {
										System.out.print(ANSI_BLUE + "X");
								} else if (maze[j][i] == 0) {
										System.out.print(ANSI_WHITE + "0");
								} else if (maze[j][i] == 2 || maze[j][i] == 3) {
										System.out.print(ANSI_YELLOW + maze[j][i]);
								} else {
										System.out.print(ANSI_RED + maze[j][i]);
								}
								System.out.print(" " + ANSI_RESET);
						}
						System.out.println(" " + ANSI_RESET);
				}
				path = new LinkedList<>();
		}

		private class Nodo {
				public int x, y;

				Nodo(int x, int y) throws Exception {
						if (x < 0 || x >= size || y < 0 || y >= size)
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
