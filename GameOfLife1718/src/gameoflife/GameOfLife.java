package gameoflife;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author vincent
 */
public class GameOfLife  extends JComponent implements WindowListener{

    private static final int CELL_SIZE = 15;
    private static final String CELL_COLOR = "222222";
    
    private int[][] zone;
    private int nbCol = 0;
    private int nbLigne = 0;
    
    
    /**
     * @return the nbCol
     */
    public int getNbCol() {
        return nbCol;
    }

    /**
     * @param nbCol the nbCol to set
     */
    public void setNbCol(int nbCol) {
        this.nbCol = nbCol;
    }

    /**
     * @return the nbLigne
     */
    public int getNbLigne() {
        return nbLigne;
    }

    /**
     * @param nbLigne the nbLigne to set
     */
    public void setNbLigne(int nbLigne) {
        this.nbLigne = nbLigne;
    }
    
    public GameOfLife(){
    
    }
    
    public GameOfLife(int nbLigne,int nbCol){
        setNbCol(nbCol);
        setNbLigne(nbLigne);
        init();
    }
    
    public void init(){
        zone = new int[nbLigne][nbCol];
    }
     
    public void iterate(){
        //Pour chaque cellule, on calcule le nb de voisin vivant et on en déduit sont état...
        int[][] newZone = new int[nbLigne][nbCol];
        for(int i = 0; i < getNbLigne(); i++){
            for(int j = 0; j < getNbCol(); j++){
                newZone[i][j] = calculerNouvelEtat(zone[i][j],getNbVoisinVivant(i,j));
            }
        }
        zone = newZone;

    }
    
    public int getNbVoisinVivant(int ligne, int col){
        
        int nbVoisin = 0;
        nbVoisin += isCellVivant(ligne-1, col-1);
        nbVoisin += isCellVivant(ligne-1, col);
        nbVoisin += isCellVivant(ligne-1, col+1);
        
        nbVoisin += isCellVivant(ligne, col-1);
        //La cellule : nbVoisin += isCellVivant(ligne, col);
        nbVoisin += isCellVivant(ligne, col+1);
        
        nbVoisin += isCellVivant(ligne+1, col-1);
        nbVoisin += isCellVivant(ligne+1, col);
        nbVoisin += isCellVivant(ligne+1, col+1);

        return nbVoisin;
    }
    
    public int calculerNouvelEtat(int etatCourant,int nbVoisin){
        switch(nbVoisin){
            case 2 :
                return etatCourant;
            case 3 :
                return 1;
            default:
                 return 0;
        }
        
    }

    void setVivant(int ligne, int col) {
        zone[ligne][col] = 1;
    }
    
    int isCellVivant(int ligne, int col){
        
        //ligne precedente
        if(ligne >= 0 && ligne < nbLigne && col >= 0 && col < nbCol){
            return zone[ligne][col] == 1?1:0;
        }
        return 0;
    }
    
    public void paint(Graphics g) {  
        
        //La zone
        g.setColor(Color.decode(CELL_COLOR));
        g.fillRect(0, 0, getNbCol()*CELL_SIZE, getNbLigne()*CELL_SIZE);

        //Les cellules
        for(int i = 0; i < getNbLigne(); i++){
            for(int j = 0; j < getNbCol(); j++){                
                if(isCellVivant(i, j)==1){
                    g.setColor(Color.WHITE);
                    g.fillRect(j*CELL_SIZE, i*CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }

            }
        }
    }
    
    public void redraw() {
        paint(getGraphics());
    }
    
    public static GameOfLife fromFile(File f) throws FileNotFoundException, IOException{
        GameOfLife gof = new GameOfLife();
             BufferedReader buff = new BufferedReader(new FileReader(f));
             
             //Lecture premiere ligne
             String line0 = buff.readLine();
             String[] lignes; 
                gof.setNbCol(Integer.parseInt(line0.split("[*]")[0]));
                gof.setNbLigne(Integer.parseInt(line0.split("[*]")[1]));
                lignes = new String[gof.getNbLigne()];
                String tmp;
                int i = 0;
                while((tmp = buff.readLine())!=null){
                    lignes[i] = tmp;
                    i++;
                }
             
             gof.init();
             
             //init lines
             for(int ligne = 0; ligne < gof.getNbLigne();ligne++){        
                 for(int col = 0; col < gof.getNbCol(); col++){
                    if('o' == lignes[ligne].charAt(col)){
                        gof.setVivant(ligne,col);
                    }
                 }
             }
             
         
        
        return gof;
    }
    
    public static GameOfLife toFile(GameOfLife gof, File f) throws FileNotFoundException, IOException{
        
        
        
        StringBuilder sb = new StringBuilder();
        
        //Ecriture premiere ligne
        sb.append(gof.getNbCol()+"*"+gof.getNbLigne()+"\n");
        
             //init lines
        for(int ligne = 0; ligne < gof.getNbLigne();ligne++){        
            for(int col = 0; col < gof.getNbCol(); col++){
                if(gof.isCellVivant(ligne,col)==1){
                    sb.append("o");
                }else{
                    sb.append("x");
                }
            }
            sb.append("\n");
        }
        
        //Write file
        FileWriter fw = new FileWriter(f); 
        fw.write(sb.toString());
        fw.close();
        
        return gof;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int nbIter =-1;//-1 = infini
        int sleepTime = 100;
        
        try {
            GameOfLife g = fromFile(new File("test/osci.txt"));
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setBounds(30, 30, 800, 800);
            window.getContentPane().add(g);
            window.setVisible(true);
            window.addWindowListener(g);
            g.redraw();
            Thread.sleep(sleepTime);
            //g.drawZone();            
            while(nbIter !=0) {
                g.iterate();                
                g.redraw();
                Thread.sleep(sleepTime);
                nbIter--;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        //Nothing to do
    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            toFile(this, new File("test/out.txt"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        //Nothing to do
    }

    @Override
    public void windowIconified(WindowEvent e) {
        //Nothing to do
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //Nothing to do
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //Nothing to do
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //Nothing to do
    }
    
}
