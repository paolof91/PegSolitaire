public class Animation implements Runnable {

    FieldPanel fieldPanel;
    int[][] mosse;
    int numeroMosse;

    public Thread thread;

    public Animation(FieldPanel fieldPanel,int[][] mosse,int numeroMosse) {
        this.fieldPanel = fieldPanel;
        this.mosse = mosse;
        this.numeroMosse = numeroMosse;

    }

    public void start() {
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if(thread != null) {
            thread.stop();
            thread = null;
        }
    }


    public void pause(int time) {
        try {
            Thread.sleep(time);
        } catch(InterruptedException e) {
            
        }
    }

    public void run() {
        for(int i=0;i<numeroMosse;i++) {
            fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]][2]=3;

            switch(mosse[i][2]) {
                case 0:
                    fieldPanel.fieldTable[mosse[i][0]-2][mosse[i][1]][2]=2;
                    break;
                case 1:
                    fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]+2][2]=2;
                    break;
                case 2:
                    fieldPanel.fieldTable[mosse[i][0]+2][mosse[i][1]][2]=2;
                    break;
                case 3:
                    fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]-2][2]=2;
                    break;
                default:
                    break;
            }

            fieldPanel.repaint();

            pause(2000);

            fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]][2]=0;

            switch(mosse[i][2]) {
                case 0:
                    fieldPanel.fieldTable[mosse[i][0]-1][mosse[i][1]][2]=0;
                    fieldPanel.fieldTable[mosse[i][0]-2][mosse[i][1]][2]=1;
                    break;
                case 1:
                    fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]+1][2]=0;
                    fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]+2][2]=1;
                    break;
                case 2:
                    fieldPanel.fieldTable[mosse[i][0]+1][mosse[i][1]][2]=0;
                    fieldPanel.fieldTable[mosse[i][0]+2][mosse[i][1]][2]=1;
                    break;
                case 3:
                    fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]-1][2]=0;
                    fieldPanel.fieldTable[mosse[i][0]][mosse[i][1]-2][2]=1;
                    break;
                default:
                    break;
            }

            fieldPanel.repaint();

            pause(2000);

        }
        
    }
}