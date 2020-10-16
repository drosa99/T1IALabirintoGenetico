Argumentos para execução, se não passar todos os argumentos, serão usados valores default:  tamanho da população, quantidade de movimentos do cromossomo, percentual de mutação, print a cada ... gerações, nome do arquivo de input do labirinto (deve estar dentro da pasta files)
exemplo: "21"  "45"  "85" "1000" "lab.txt".

Classe Labirinto fica estrutura do Labirinto;
Classe Cromossomo representa o cromosso;
Classe Agente usada para movimentação dentro do Labirinto recebendo uma sequência de comandos e contabilizando penalidades;
Classe AG contém lógica da execução do Algoritmo Genético
Classe AEstrela contém lógica do algoritmo A* executada após o Algoritmo Genético
Classe Main controla execução dos algoritmos e escrita dos resultados no arquivo _resultado.txt_
