package dv360updater;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * @version 1.0.00
 * @author Arturo Martínez <arturo.martinez@gdsmodellica.com>
 */
public class DV360Updater extends JFrame {
    
    private static boolean _innerResources = false;
    private static UpdaterEngine _engine = null;
    private static String _exePath = null;
    private static File _toolsFolder = null;
    private static UpdatesVersions _versions = null;
    
    private JComboBox _cbVersions;
    private JTextField toolsPath;
    private JProgressBar _partial;
    private JProgressBar _global;
    private JButton _start;
    
    
    public void loadEngine() throws FileNotFoundException, IOException{
        
        List<String> extensions = new ArrayList();
        extensions.add("xml");
        extensions.add("dll");
        extensions.add("xslt");
        extensions.add("DataView360Workbench.exe");
        extensions.add("DataView360Workbench.exe.config");
        _engine = new UpdaterEngine(extensions, _global, _partial, _innerResources, _exePath, _versions, _toolsFolder);
        _engine.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
               
               if (pce.getNewValue() == SwingWorker.StateValue.DONE){
                   JOptionPane.showMessageDialog(null, "DV360 Workbench has been updated!", "Successful!", JOptionPane.INFORMATION_MESSAGE);
               }//fi*/
               
            }
        });
        _engine.execute();
    }
    
    private void centerWindow() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }
    
    private void setBackground(){
        
        ImageIcon logo = null;
        Dimension wDim = new Dimension(this.getWidth(), this.getHeight());
        
        if (_innerResources){
            logo = new ImageIcon(getClass().getClassLoader().getResource("./resources/images/gdsLogo.jpg"));            
        }else{
            File image = new File((new File(_exePath).getParentFile().getPath())+File.separator+"resources"+File.separator+"images"+File.separator+"gdsLogo.jpg");
            logo = new ImageIcon(image.getAbsolutePath());
        }
        
        this.setContentPane(new DV360Logo(logo.getImage(), wDim));
    }
    
    private void appendItemsToFrame(){
        
        /* PROGRESS BARS */
        _global = new JProgressBar();
        _global.setBounds(10, this.getHeight()-100, this.getWidth()-30, 25);
        _global.setString("Total Progress");
        _global.setMaximum(100);
        _global.setValue(0);
        _global.setStringPainted(true);
        this.getContentPane().add(_global);
        
        _partial = new JProgressBar();
        _partial.setBounds(10, this.getHeight()-50, this.getWidth()-30, 25);
        _partial.setStringPainted(true);
        this.getContentPane().add(_partial);
        
        /* BUTTON: Upate */
        _start = new JButton("Update");
        _start.setEnabled(false);
        _start.setBounds(5, _global.getY()-30, 75, 25);
        _start.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                try{
                    _start.setEnabled(false);
                    loadEngine();
                }catch(Exception ex){
                    ex.printStackTrace(System.out);
                }
            }
        });
        this.getContentPane().add(_start);
        
        /* BUTTON: Close */
        JButton close = new JButton("Close");
        close.setBounds(this.getContentPane().getWidth()-80, 5, 75, 25);
        close.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                System.gc();
                System.exit(0);
            }
        });
        this.getContentPane().add(close);    
        
        /* LABEL AND COMBO: Versions */
        JLabel lVersion = new JLabel("Version to update:");
        lVersion.setBounds(5, 5, 150, 25);
        this.getContentPane().add(lVersion);
        
        _cbVersions = new JComboBox();
        _cbVersions.setBounds(lVersion.getWidth()+5, 5, 100, 25);
        _cbVersions.addItem("Choose one");
        for (Object version : _versions.getAvailables()){
            _cbVersions.addItem(String.valueOf(version));
        }
        _cbVersions.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                if (_cbVersions.getSelectedIndex() > 0)
                    _versions.setSelected(_cbVersions.getSelectedItem().toString());
                if (_toolsFolder != null)
                    _start.setEnabled(true);
            }
        });
        this.getContentPane().add(_cbVersions);
        
        /* LABEL AND FOLDER SELECT: DV360 Tools */
        JLabel lTools = new JLabel("DV360 Workbench Tools Path:");
        lTools.setBounds(5, lVersion.getY()+lVersion.getHeight()+5, 300, 25);
        this.getContentPane().add(lTools);
        
        toolsPath = new JTextField();
        toolsPath.setBounds(5, lTools.getY()+lTools.getHeight()+5, 300, 25);
        this.getContentPane().add(toolsPath);
        
        JButton fSearch = new JButton("Explore");
        fSearch.setBounds(toolsPath.getWidth()+5, lTools.getY()+lTools.getHeight()+5, 100, 25);
        fSearch.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                JFileChooser fc = new JFileChooser();
                fc.setDialogType(JFileChooser.OPEN_DIALOG);
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fc.showDialog(null, "Select") == JFileChooser.APPROVE_OPTION){
                    _toolsFolder = fc.getSelectedFile();
                    toolsPath.setText(_toolsFolder.getAbsolutePath());
                    if (_versions.getSelected() != null)
                        _start.setEnabled(true);
                }
            }
        });
        this.getContentPane().add(fSearch);
        
    }
    
    private void setOSEnviromentVars(){
        _exePath = DV360Updater.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public DV360Updater() throws FileNotFoundException, IOException {
        
        this.setUndecorated(true);
        this.setBounds(0, 0, 640, 338);
        this.setTitle("DV360 Workbench Updater");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        
        setOSEnviromentVars();        
        _versions = new UpdatesVersions(_innerResources, _exePath);
        
        setBackground();
        appendItemsToFrame();        
        centerWindow();
        
        this.pack();
        setVisible(true);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new DV360Updater();
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                }
            }
        });
    }
}
