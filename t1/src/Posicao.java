package src;

public class Posicao {
   private int posX;
   private int posY;

   public Posicao(int posX, int posY) {
      this.posX = posX;
      this.posY = posY;
   }

   public int getPosX() {
      return posX;
   }

   public int getPosY() {
      return posY;
   }

   @Override
   public boolean equals(Object o) {
      Posicao posicao = (Posicao) o;
      return this.posX == posicao.getPosX() && this.posY == posicao.getPosY();
   }

   @Override
   public String toString() {
      return "(" + posX + ", " + posY + ')';
   }
}
