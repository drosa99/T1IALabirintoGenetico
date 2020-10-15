package src;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

		public static void main(String[] args) {
				int tamanhoPopulacao = args.length == 4 ? Integer.parseInt(args[0]) : 21;
				int qtdMovimentos = args.length == 4 ? Integer.parseInt(args[1]) : 50;
				int chanceMutacao = args.length == 4 ? Integer.parseInt(args[2]) : 85;
				int qtdGeracoesPrint = args.length == 4 ? Integer.parseInt(args[3]) : 1000;

				try {
						File f = new File("files/lab.txt");
						FileWriter fw = new FileWriter(new File("resultado.txt"));


						Labirinto lab = new Labirinto(f);
						lab.printaLabirinto(lab.getLab());

						AG a = new AG(tamanhoPopulacao, qtdMovimentos, lab, chanceMutacao);
						Cromossomo vencedorAg = a.genetica(qtdGeracoesPrint);

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
								lab.getLab()[posicao.getPosX()][posicao.getPosY()] = 9;
								lab.printaLabirinto(lab.getLab());
								lab.getLab()[posicao.getPosX()][posicao.getPosY()] = 4;
								try {
										Thread.sleep(750);
								} catch (InterruptedException e) {
										e.printStackTrace();
								}
						});

						String labPrintado = lab.printaLabirinto(lab.getLab());
						fw.write(labPrintado);

						fw.close();

						Labirinto labAEstrela = new Labirinto(f);
						AEstrela aEstrela = null;
						try {
								aEstrela = new AEstrela(labAEstrela.getLab(), labAEstrela.getInicio().getPosX(), labAEstrela.getInicio().getPosY(), labAEstrela.getFim().getPosX(), labAEstrela.getFim().getPosY(), labAEstrela.getLab().length);
								aEstrela.executa();
								System.out.println("----------------------------------");
								aEstrela.printMazeWithShortestPath();
						} catch (Exception e) {
								e.printStackTrace();
						}

				} catch (IOException e) {
						e.printStackTrace();
				}
		}
}
