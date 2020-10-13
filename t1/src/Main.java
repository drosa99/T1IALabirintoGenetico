package src;

import java.io.File;

public class Main {

		public static void main(String[] args) {

				File f = new File("files/lab.txt");
				Labirinto lab = new Labirinto(f);
				lab.printaLabirinto(lab.getLab());

				AG a = new AG(21, 50, lab, 85);
				Cromossomo vencedorAg = a.genetica();

				int[][] labSolucaoAg = lab.getLab();
				vencedorAg.getPosicoes().forEach(posicao -> System.out.println(posicao.toString() + ", "));
				vencedorAg.getPosicoes().forEach(posicao -> {
						labSolucaoAg[posicao.getPosX()][posicao.getPosY()] = 9;
						lab.printaLabirinto(labSolucaoAg);
						labSolucaoAg[posicao.getPosX()][posicao.getPosY()] = 4;
						try {
								Thread.sleep(750);
						} catch (InterruptedException e) {
								e.printStackTrace();
						}
				});


		}
}
