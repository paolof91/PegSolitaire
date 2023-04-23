/*
  FieldPanel.java - Chinese checkers
  Created by Paolo Forni, September 7, 2010.
  Released into the public domain.
*/

import java.awt.Image;
import java.awt.Graphics;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.applet.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JOptionPane;

class FieldPanel extends JPanel implements Cloneable {

  /*
   * si tenga conto che il terzo vettore di fieldTable contiene in posizione 2 lo stato della cella
   * -1 => zona fantasma
   *  0 => blank, spazio vuoto
   *  1 => ball, c'è una pallina
   *  2 => option, c'è uno spazio vuoto evidenziato sul quale è possibile che finirà una pallina dopo aver mangiato
   *  3 => selected, c'è una pallina evidenziata che sta per fare una mossa
   */

  public int[][][] fieldTable = new int[7][7][3];
  public boolean personalizzato = false;
  private boolean enable = true;
  public boolean clicked = false;  //archivia i due stati possibili: se l'utente ha clicked sulla pallina oppure no
  private int[] previousBall;
  public boolean[] possibilita;     //

  private Image background = new ImageIcon(ClassLoader.getSystemResource("background.jpg")).getImage();
  private Image ball = new ImageIcon(ClassLoader.getSystemResource("ball.jpg")).getImage();
  private Image selected = new ImageIcon(ClassLoader.getSystemResource("selected.jpg")).getImage();
  private Image blank = new ImageIcon(ClassLoader.getSystemResource("blank.jpg")).getImage();
  private Image option = new ImageIcon(ClassLoader.getSystemResource("option.jpg")).getImage();

  public FieldPanel() {
      initFieldTable();

      addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                fieldPanelMouseClicked(e);
            }
      });
  }

  public Object clone() {
      try {
          FieldPanel copia = (FieldPanel)(super.clone());

          copia.fieldTable = fieldTable.clone();

          for(int i=0;i<fieldTable.length;i++)
              copia.fieldTable[i] = fieldTable[i].clone();
          for(int i=0;i<fieldTable.length;i++)
              for(int j = 0;j<fieldTable[0].length;j++)
                  copia.fieldTable[i][j] = fieldTable[i][j].clone();

          return copia;

      } catch(CloneNotSupportedException e) {
          return null;
      }

      
  }

  public void setEnabled(boolean f) {
      enable = f;
  }

  public int[] recognizeBalls(int x,int y) {

      //in a[0] viene archiviato la riga
      //in a[1] viene archiviata la colonna

      //se il mouse si trova al di fuori dalla tabella di blank, cioè fuori anche dalle zone fantasma
      //allora viene restituito null al chiamante
      int[] a = new int[2];

      if(y<34) return null;
      else if(y<68)     a[0]=0;
      else if(y<101)    a[0]=1;
      else if(y<135)    a[0]=2;
      else if(y<168)    a[0]=3;
      else if(y<202)    a[0]=4;
      else if(y<235)    a[0]=5;
      else if(y<268)    a[0]=6;
      else return null;

      if(x<137) return null;
      else if(x<170)    a[1]=0;
      else if(x<204)    a[1]=1;
      else if(x<237)    a[1]=2;
      else if(x<271)    a[1]=3;
      else if(x<304)    a[1]=4;
      else if(x<338)    a[1]=5;
      else if(x<371)    a[1]=6;
      else return null;

      if( ( a[0]>4 || a[0]<2 ) && ( a[1]>4 || a[1]<2 ) )        //zone fantasma
          return null;

      return a;

  }

  public void cancellaEvidenziati() {
      for(int i=0;i<7;i++)
          for(int j=0;j<7;j++) {
              if(fieldTable[i][j][2]==2)
                  fieldTable[i][j][2]=0;
              if(fieldTable[i][j][2]==3)
                  fieldTable[i][j][2]=1;
          }

      repaint();
  }

  public boolean isOnlyOneBall() { //controlla se c'è solo una pallina
      boolean flag = false;

      for(int i=0;i<7;i++)
          for(int j=0;j<7;j++)
              if(fieldTable[i][j][2]==1) {
                  if(flag)
                      return false;
                  else
                      flag=true;
              }
      return true;
  }

  public boolean evidenziaPossibilita(int x, int y,boolean evidenzia) {
      possibilita = new boolean[4];
      boolean evidenziato = false;
      //evidenziatore a sinistra
      if( (y>1&&y<4&&x>1&&x<5) || y>3 ) {       //se mi trovo in una zona dove è possibile evidenziare a sinistra
          if(fieldTable[x][y-1][2]==1 && fieldTable[x][y-2][2]==0 ) { //se a sinistra c'è una biglia e ancora più a sinistra uno spazio vuoto
              if(evidenzia) fieldTable[x][y-2][2] = 2;
              possibilita[3] = true;
              evidenziato = true;
          }
      }

      //evidenziatore a destra
      if( (y>2&&y<5&&x>1&&x<5) || y<3 ) {       //se mi trovo in una zona dove è possibile evidenziare a destra
          if(fieldTable[x][y+1][2]==1 && fieldTable[x][y+2][2]==0 ) { //se a destra c'è una biglia e ancora più a destra uno spazio vuoto
              if(evidenzia) fieldTable[x][y+2][2] = 2;
              possibilita[1] = true;
              evidenziato = true;
          }
      }

      //evidenziatore sopra
      if( (x>1&&x<4&&y>1&&y<5) || x>3 ) {       //se mi trovo in una zona dove è possibile evidenziare a sopra
          if(fieldTable[x-1][y][2]==1 && fieldTable[x-2][y][2]==0 ) { //se a sopra c'è una biglia e ancora più sopra uno spazio vuoto
              if(evidenzia) fieldTable[x-2][y][2] = 2;
              possibilita[0] = true;
              evidenziato = true;
          }
      }

      //evidenziatore sotto
      if( (x>2&&x<5&&y>1&&y<5) || x<3 ) {
          if(fieldTable[x+1][y][2]==1 && fieldTable[x+2][y][2]==0 ) {
              if(evidenzia) fieldTable[x+2][y][2] = 2;
              possibilita[2] = true;
              evidenziato = true;
          }
      }

      return evidenziato;


  }

  //controlla se è rimasta una pallina
  //oppure se non è più possibile effettuare mosse
  public void checkState() {
      if(isOnlyOneBall()) {
          JOptionPane.showMessageDialog(this,"HAI VINTO! Congratulazioni");
          return;
      }
      for(int i=0;i<7;i++)
          for(int j=0;j<7;j++)
              if(fieldTable[i][j][2]==1)
                  if(evidenziaPossibilita(i,j,false))
                      return;
      JOptionPane.showMessageDialog(this,"GAME OVER! Non è più possibile effettuare mosse");
  }

  public boolean isEmpty() {
      for(int i=0;i<7;i++)
          for(int j=0;j<7;j++)
              if(fieldTable[i][j][2]==1)
                  return false;
      return true;
  }

  public void fieldPanelMouseClicked(MouseEvent e) {

      if(!enable) return;

      //se l'utente è abilitato a personalizzare, cioè se personalizzato è true
      //allora le palline cambiano colore, cioè vengono messe tolte palline
      if(personalizzato) {
          int[] a = recognizeBalls(e.getX(),e.getY());
          if(a == null) return;
          if(fieldTable[a[0]][a[1]][2]==0)
              fieldTable[a[0]][a[1]][2] = 1;
          else fieldTable[a[0]][a[1]][2] = 0;
          repaint();
      }

      //se l'utente sta giocando
      else {
          int[] a = recognizeBalls(e.getX(),e.getY());

          //se l'utente ha cliccato su una pallina e ci sono mosse possibili da fare
          if(clicked) {


              //allora se clicca fuori cancello le evidenziazioni e ricomincio da capo
              if(a == null || fieldTable[a[0]][a[1]][2]==0 || fieldTable[a[0]][a[1]][2]==1) {
                  cancellaEvidenziati();
                  clicked=false;
                  return;
              } else {      //se clicco su uno spazio evidenziato lo fa diventare pallina e cancella la pallina di prima mangiando
                  cancellaEvidenziati();
                  clicked=false;
                  fieldTable[a[0]][a[1]][2]=1;      //mette la pallina nella nuova posizione
                  fieldTable[previousBall[0]][previousBall[1]][2]=0;        //cancella la pallina nella vecchia posizione
                  if(a[0]==previousBall[0])
                      fieldTable[a[0]][ (a[1]+previousBall[1])/2 ][2]=0;
                  else
                      fieldTable[ ( a[0] + previousBall[0] ) / 2 ][a[1]][2]=0;
                  //RIPRENDI QUA IL LAVORO
                  repaint();
                  checkState();
              }

          } else {

              //se stai cliccando su uno spazio vuoto oppure proprio fuori
              //il click non viene tenuto da conto
              if(a == null || fieldTable[a[0]][a[1]][2]==0) return;

              if(evidenziaPossibilita(a[0],a[1],true)) {     //se ci sono possibilità di mangiare memorizza la posizione della pallina
                  clicked = true;
                  repaint();
                  previousBall = a;
              }
                  
              
          }

      }

  }

  public void newGame(int choice) {         //personalizzato è un flag che abilita la personalizzazione della dama
      //costruizione dei quadri iniziali
      cleanAll();
      
      switch(choice) {
          case 0:   //croce
              for(int i=1;i<5;i++) fieldTable[i][3][2]=1;
              fieldTable[2][2][2]=1;
              fieldTable[2][4][2]=1;
              break;
          case 1:   //più
              for(int i=1;i<6;i++) {
                  fieldTable[i][3][2]=1;
                  fieldTable[3][i][2]=1;
              }
              break;
          case 2:   //fireplace
              for(int i=0;i<4;i++) {
                  fieldTable[i][2][2]=1;
                  fieldTable[i][4][2]=1;
                  if(i!=3)
                      fieldTable[i][3][2]=1;

              }
              break;
          case 3:   //freccia su
              for(int i=1;i<6;i++)
                  fieldTable[2][i][2]=1;
              for(int i=0;i<7;i++)
                  fieldTable[i][3][2]=1;
              fieldTable[1][2][2]=1;
              fieldTable[1][4][2]=1;
              fieldTable[5][2][2]=1;
              fieldTable[5][4][2]=1;
              fieldTable[6][2][2]=1;
              fieldTable[6][4][2]=1;
              break;
          case 4:   //piramide
              for(int i=0;i<4;i++)
                  for(int j=i;j<7-i;j++)
                      fieldTable[4-i][j][2]=1;
              break;
          case 5:    //diamante
              for(int i=0;i<4;i++)
                  for(int j=i;j<7-i;j++) {
                      fieldTable[3+i][j][2]=1;
                      fieldTable[3-i][j][2]=1;
                  }
              fieldTable[3][3][2]=0;
              break;
          case 6:   //solitario
              for(int i=0;i<7;i++)
                  for(int j=0;j<7;j++)
                      if(fieldTable[i][j][2]==0) fieldTable[i][j][2]=1;
              fieldTable[3][3][2]=0;
              break;
          default:
              break;
      }
      repaint();
  }

  public void cleanAll() {
      //porto ad essere blank tutta la tabella 7x7
      for(int i=0;i<7;i++)
          for(int j=0;j<7;j++)
              fieldTable[i][j][2]=0;

      //escludo totalmente dal repaint le zone fantasma
      for(int i=0;i<7;i++)
          for(int j=0;j<7;j++)
              if( ( i>4 || i<2 ) && ( j>4 || j<2 ) )
                fieldTable[i][j][2]=-1;

      repaint();
  }

  public void initFieldTable() {

      //serve a far capire all'oggetto Graphics dove deve disegnare le caselle
      for(int i=0;i<7;i++) {
          fieldTable[0][i][0]=34;
          fieldTable[1][i][0]=68;
          fieldTable[2][i][0]=101;
          fieldTable[3][i][0]=135;
          fieldTable[4][i][0]=168;
          fieldTable[5][i][0]=202;
          fieldTable[6][i][0]=235;

          fieldTable[i][0][1]=137;
          fieldTable[i][1][1]=170;
          fieldTable[i][2][1]=204;
          fieldTable[i][3][1]=237;
          fieldTable[i][4][1]=271;
          fieldTable[i][5][1]=304;
          fieldTable[i][6][1]=338;
      }
      cleanAll();
  }


  public void paint(Graphics g) {
    g.drawImage(background, 0, 0, null);
    

    for(int i=0;i<7;i++)
        for(int j=0;j<7;j++)
            switch(fieldTable[i][j][2]) {
                case 3:
                    g.drawImage(selected,fieldTable[i][j][1],fieldTable[i][j][0],null);
                    break;


                //se è 2 dipingi di rosso: è il caso della scelta
                case 2:
                    g.drawImage(option,fieldTable[i][j][1],fieldTable[i][j][0],null);
                    break;

                // se è 1 dipingi di bianco: è il caso della pallina presente
                case 1:
                    g.drawImage(ball,fieldTable[i][j][1],fieldTable[i][j][0],null);
                    break;

                // se è 0 dipingi di verde chiaro: è il caso di uno spazio vuoto
                case 0:
                    g.drawImage(blank,fieldTable[i][j][1],fieldTable[i][j][0],null);
                    break;
                default:
                    break;


            }

    
  }

}
