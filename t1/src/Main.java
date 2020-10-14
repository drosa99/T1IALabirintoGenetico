package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

		public static void main(String[] args) {
				try {
						File f = new File("files/lab.txt");
						FileWriter fw = new FileWriter(new File("resultado.txt"));


						Labirinto lab = new Labirinto(f);
						lab.printaLabirinto(lab.getLab());

						AG a = new AG(21, 50, lab, 85);
						Cromossomo vencedorAg = a.genetica(1000);

						int[][] labSolucaoAg = lab.getLab();
						fw.write("Score de aptidão: " + vencedorAg.getScore() + "\n");
						vencedorAg.getPosicoes().forEach(posicao -> {
								String posicaoPrintar = posicao.toString() + ", ";
								System.out.print(posicaoPrintar);
								try {
										fw.write(posicaoPrintar);
								} catch (IOException e) {
										e.printStackTrace();
								}

						});
						System.out.println("\n \n CAMINHO PERCORRIDO: \n");
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

						String labPrintado = lab.printaLabirinto(labSolucaoAg);
						fw.write(labPrintado);

						fw.close();
				} catch (IOException e) {
						e.printStackTrace();
				}
		}
}
