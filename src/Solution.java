public class Solution {

    private FieldPanel fieldPanel;
    public int[][] mosse;
    public int numeroMosse = 0;

    public Solution(FieldPanel fieldPanel) {
        fieldPanel.cancellaEvidenziati();
        fieldPanel.repaint();
        this.fieldPanel = (FieldPanel)(fieldPanel.clone());
    }

    public boolean solveGameProcedure() {
        mosse = new int[32][3];
        if(solveGame()) {
            mosseReverse();
            return true;
        } else {
            return false;
        }

    }

    private void mangia(int x,int y,int mossa,boolean flag) {
        if(flag)
            switch(mossa) {

                case 0:
                    fieldPanel.fieldTable[x][y][2]=0;
                    fieldPanel.fieldTable[x-1][y][2]=0;
                    fieldPanel.fieldTable[x-2][y][2]=1;
                    break;

                case 1:
                    fieldPanel.fieldTable[x][y][2]=0;
                    fieldPanel.fieldTable[x][y+1][2]=0;
                    fieldPanel.fieldTable[x][y+2][2]=1;
                    break;

                case 2:
                    fieldPanel.fieldTable[x][y][2]=0;
                    fieldPanel.fieldTable[x+1][y][2]=0;
                    fieldPanel.fieldTable[x+2][y][2]=1;
                    break;

                case 3:
                    fieldPanel.fieldTable[x][y][2]=0;
                    fieldPanel.fieldTable[x][y-1][2]=0;
                    fieldPanel.fieldTable[x][y-2][2]=1;
                    break;

                default:
                    break;
            }
        else
            switch(mossa) {

                case 0:
                    fieldPanel.fieldTable[x][y][2]=1;
                    fieldPanel.fieldTable[x-1][y][2]=1;
                    fieldPanel.fieldTable[x-2][y][2]=0;
                    break;

                case 1:
                    fieldPanel.fieldTable[x][y][2]=1;
                    fieldPanel.fieldTable[x][y+1][2]=1;
                    fieldPanel.fieldTable[x][y+2][2]=0;
                    break;

                case 2:
                    fieldPanel.fieldTable[x][y][2]=1;
                    fieldPanel.fieldTable[x+1][y][2]=1;
                    fieldPanel.fieldTable[x+2][y][2]=0;
                    break;

                case 3:
                    fieldPanel.fieldTable[x][y][2]=1;
                    fieldPanel.fieldTable[x][y-1][2]=1;
                    fieldPanel.fieldTable[x][y-2][2]=0;
                    break;

                default:
                    break;
            }

        
    }

    private boolean solveGame() {

        if(fieldPanel.isOnlyOneBall()) return true;

        for(int i=0;i<7;i++)
            for(int j=0;j<7;j++)
                if(fieldPanel.fieldTable[i][j][2]==1) {
                    if(fieldPanel.evidenziaPossibilita(i,j,false)) {
                        boolean[] possibilities = fieldPanel.possibilita.clone();

                        for(int k=0;k<4;k++)
                            if(possibilities[k]) {

                                mangia(i,j,k,true);

                                if(solveGame()) {
                                    mosse[numeroMosse][0] = i;
                                    mosse[numeroMosse][1] = j;
                                    mosse[numeroMosse][2] = k;
                                    numeroMosse++;

                                    return true;
                                }

                                mangia(i,j,k,false);

                            }
                    }

                }
        
        
        return false;

    }

    private void mosseReverse() {
        for(int i=0;i<numeroMosse/2;i++) {
            int a = mosse[i][0];
            int b = mosse[i][1];
            int c = mosse[i][2];

            mosse[i][0] = mosse[numeroMosse-i-1][0];
            mosse[i][1] = mosse[numeroMosse-i-1][1];
            mosse[i][2] = mosse[numeroMosse-i-1][2];

            mosse[numeroMosse-i-1][0] = a;
            mosse[numeroMosse-i-1][1] = b;
            mosse[numeroMosse-i-1][2] = c;

        }
    }  
}