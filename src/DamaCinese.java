/*
  DamaCinese.java - Chinese checkers
  Created by Paolo Forni, September 7, 2010.
  Released into the public domain.
*/

import java.awt.Cursor;
import javax.swing.*;

public class DamaCinese extends JFrame {

    String[] fieldComboBoxChoices = new String[] { "Croce", "Più", "Fireplace", "Freccia su", "Piramide", "Diamante", "Solitario", "PERSONALIZZATO" };

    //identificativo della scelta nel combo box
    int newGameChoice = 0;
    boolean solving = false;
    public Animation animation;
    
    public DamaCinese() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        fieldPanel = new FieldPanel();
        newGameButton = new JButton();
        solveButton = new JButton();
        fieldComboBox = new JComboBox();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dama Cinese");
        setResizable(false);

        fieldPanel.setPreferredSize(new java.awt.Dimension(430, 300));

        GroupLayout fieldPanelLayout = new GroupLayout(fieldPanel);
        fieldPanel.setLayout(fieldPanelLayout);
        fieldPanelLayout.setHorizontalGroup(
            fieldPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 430, Short.MAX_VALUE)
        );
        fieldPanelLayout.setVerticalGroup(
            fieldPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );

        newGameButton.setText("Nuovo Gioco");
        newGameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameButtonActionPerformed(evt);
            }
        });

        solveButton.setText("Soluzione");
        solveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solveButtonActionPerformed(evt);
            }
        });

        fieldComboBox.setModel(new DefaultComboBoxModel(fieldComboBoxChoices));
        fieldComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldComboBoxActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(newGameButton)
                .addGap(18, 18, 18)
                .addComponent(solveButton)
                .addGap(18, 18, 18)
                .addComponent(fieldComboBox,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fieldPanel, GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(fieldComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.PREFERRED_SIZE)
                    .addComponent(solveButton)
                    .addComponent(newGameButton))
                .addContainerGap())
        );

        pack();
    }

    private void solveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(!solving) {

            if(fieldPanel.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Attenzione! Non hai posizionato alcuna pallina sul tavolo di gioco");
                return;
            }
            if(fieldPanel.isOnlyOneBall()) {
                JOptionPane.showMessageDialog(this,"Sei già pervenuto alla soluzione");
                return;
            }

            //risolve e parte l'animazione
            Solution solution = new Solution(fieldPanel);
            if(!solution.solveGameProcedure()) {
                JOptionPane.showMessageDialog(this,"Mi dispiace, ma non esiste soluzione per questo gioco");
                return;

            } else {

                fieldPanel.setEnabled(false);     //disabilita il campo di gioco
                newGameButton.setEnabled(false);  //disabilita il newGameButton
                fieldComboBox.setEnabled(false);  //disabilita il fieldComboBox
                solveButton.setText("Stop");      //il solveButton diventa "Stop"
                solving = true;

                animation = new Animation(fieldPanel,solution.mosse,solution.numeroMosse);
                animation.start();
            }
            
        } else {
            animation.stop();
            fieldPanel.cancellaEvidenziati();
            fieldPanel.setEnabled(true);
            newGameButton.setEnabled(true);
            fieldComboBox.setEnabled(true);
            solveButton.setText("Soluzione");
            solving = false;

            
        }


    }

    private void newGameButtonActionPerformed(java.awt.event.ActionEvent evt) {

        //se sta risolvendo il bottone newGame deve essere disabilitato
        

        //personalizzato è un flag a cui corrisponde l'abilitazione dell'utente ad effettuare la personalizzazione della dama
        //se l'utente è abilitato ad effettuare la personalizzazione della dama
        //c'è scritto GIOCA ORA! che appena viene cliccato torna come prima
        //Anche il cursore torna come prima, e l'utente non è più abilitato
        if(fieldPanel.personalizzato) {
            newGameButton.setText("Nuovo Gioco");
            fieldPanel.personalizzato = false;
            fieldPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            if(fieldPanel.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Attenzione! Non hai posizionato alcuna pallina sul tavolo di gioco");
                return;
            }
            if(fieldPanel.isOnlyOneBall()) {
                JOptionPane.showMessageDialog(this,"Ti piace vincere FACILE?!?!");
                return;
            }
            fieldPanel.checkState();
                
            return;
        }

        //se viene premuto Nuovo Gioco quando il menu presenta "PERSONALIZZATO"
        //viene cancellato tutto, l'utente viene abilitato a personalizzare il quadro
        //il mouse diventa manina, il bottone diventa GIOCA ORA!
        if(newGameChoice==7) {
            newGameButton.setText("GIOCA ORA!");
            fieldPanel.personalizzato = true;
            fieldPanel.cleanAll();
            fieldPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            fieldPanel.repaint();
            return;
        }

        //nei casi diversi dal PERSONALIZZATO viene passata la scelta al fieldPanel che disegna il quadro corrispondente
        fieldPanel.newGame(newGameChoice);
    }

    private void fieldComboBoxActionPerformed(java.awt.event.ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        String name = (String)cb.getSelectedItem();

        for(int i=0;i<fieldComboBoxChoices.length;i++)
            if(name.equals(fieldComboBoxChoices[i])) {
                newGameChoice=i;

                //se la scelta è PERSONALIZZATO l'utente viene abilitato a personalizzare
                //il cursore diventa manina, il bottone diventa GIOCA ORA!
                if(newGameChoice==7) {
                    newGameButton.setText("GIOCA ORA!");
                    fieldPanel.personalizzato = true;
                    fieldPanel.cleanAll();
                    fieldPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    fieldPanel.repaint();
                    return;
                }

                //se l'utente stava personalizzando la dama ma mentre lo faceva ha scelto
                //dal combobox un altro quadro allora non è più abilitato a personalizzare
                //il cursore torna normale e così anche il bottone
                if(fieldPanel.personalizzato) {
                    fieldPanel.personalizzato = false;
                    fieldPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    newGameButton.setText("Nuovo Gioco");
                }

                //nei casi diversi dal PERSONALIZZATO viene passata la scelta al fieldPanel che disegna il quadro corrispondente
                fieldPanel.newGame(newGameChoice);
                break;
            }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DamaCinese().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox fieldComboBox;
    private FieldPanel fieldPanel;
    private JButton newGameButton;
    private JButton solveButton;
    // End of variables declaration//GEN-END:variables

}
