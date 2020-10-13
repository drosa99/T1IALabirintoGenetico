package src;

import java.util.ArrayList;

public class Cromossomo {

    private int[] trajeto;
    private int score;
    private boolean chegou = false;
    private ArrayList<Posicao> posicoes;

    public Cromossomo(int[] trajeto) {
        this.trajeto = trajeto;
        posicoes = new ArrayList<>();
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public boolean isChegou() {
        return chegou;
    }

    public void setChegou(boolean chegou) {
        this.chegou = chegou;
    }

    public int[] getTrajeto() {
        return trajeto;
    }

    public ArrayList<Posicao> getPosicoes() {
        return posicoes;
    }

    public void setPosicoes(ArrayList<Posicao> posicoes) {
        this.posicoes = posicoes;
    }
}
