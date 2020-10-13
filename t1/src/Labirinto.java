package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Labirinto {
		public static final String ANSI_GREEN = "\u001B[32m";
		public static final String ANSI_BLUE = "\u001B[34m";
		public static final String ANSI_RED = "\u001b[31m";
		public static final String ANSI_YELLOW = "\u001b[33;1m";
		public static final String ANSI_WHITE = "\u001b[37m";
		public static final String ANSI_RESET = "\u001b[0m";

		private int[][] lab = new int[12][12];
		private Posicao inicio;
		private Posicao fim;
		private List<Posicao> paredes = new ArrayList<>();
		private int qtdZeros = 0;

		public Labirinto(File file) {
				try {
						this.lab = parseFile(file);
				} catch (IOException e) {
						e.printStackTrace();
						System.exit(0);
				}

		}

		public boolean isParede(Posicao p) {
				if (saiuLabirinto(p)) {
						return false;
				}
				int celula = lab[p.getPosX()][p.getPosY()];
				return celula == 1;
		}

		public boolean isChaoValido(Posicao p) {
				if (saiuLabirinto(p)) {
						return false;
				}
				int celula = lab[p.getPosX()][p.getPosY()];
				return celula == 0;
		}

		public boolean saiuLabirinto(Posicao p) {
				boolean saiuX = p.getPosX() < inicio.getPosX() || p.getPosX() > fim.getPosX();
				boolean saiuY = p.getPosY() < inicio.getPosY() || p.getPosY() > fim.getPosY();
				return saiuX || saiuY;
		}

		public boolean isSaida(Posicao p) {
				return fim.equals(p);
		}

		public int[][] parseFile(File file) throws IOException {
				BufferedReader in = new BufferedReader(new FileReader(file));

				String st;
				int j = 0;
				int tam = Integer.parseInt(in.readLine());
				int[][] maze = new int[tam][tam];
				while ((st = in.readLine()) != null) {
						int l = 0;
						for (int i = 0; i < st.length(); i++) {
								Character aux = st.charAt(i);
								if (aux == 'E') {
										maze[l][j] = 2;
										this.inicio = new Posicao(l, j);
										l++;
								} else if (aux == 'S') {
										maze[l][j] = 3;
										this.fim = new Posicao(l, j);
										l++;
								} else if (aux == '0') {
										maze[l][j] = 0;
										l++;
										this.qtdZeros++;
								} else if (aux == '1') {
										maze[l][j] = 1;
										this.addParede(new Posicao(l, j));
										l++;
								}
						}
						j++;
				}
				return maze;
		}


		public List<Posicao> getParedes() {
				return paredes;
		}

		public void addParede(Posicao p) {
				this.paredes.add(p);
		}

		public void printaLabirinto(int[][] labirinto) {
				for (int y = 0; y < labirinto.length; y++) {
						for (int x = 0; x < labirinto[y].length; x++) {
								if (labirinto[x][y] == 2 || labirinto[x][y] == 3) {
										System.out.print(ANSI_YELLOW + labirinto[x][y]);
								} else if (labirinto[x][y] == 0) {
										System.out.print(ANSI_WHITE + "0");
								} else if (labirinto[x][y] == 4) {
										System.out.print(ANSI_GREEN + labirinto[x][y]);
								} else if (labirinto[x][y] == 9) {
										System.out.print(ANSI_BLUE + labirinto[x][y]);
								} else {
										System.out.print(ANSI_RED + labirinto[x][y]);
								}
								System.out.print(" " + ANSI_RESET);
						}
						System.out.println(" \t" + ANSI_RESET);
				}
				System.out.println("____________________________________________" + ANSI_RESET);
		}


		public Posicao getInicio() {
				return inicio;
		}

		public Posicao getFim() {
				return fim;
		}

		public int[][] getLab() {
				return lab;
		}

		public int getQtdZeros() {
				return qtdZeros;
		}
}
