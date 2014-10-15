package dv360updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * @version 1.0.00
 * @author Arturo Martínez <arturo.martinez@gdsmodellica.com>
 */
public class UpdaterEngine extends SwingWorker{
    private static Object _settings = null;
    private List<String> _extensions;
    private JProgressBar _status;
    private JProgressBar _global;
    private File _dest;
    private String _version;
    
    public UpdaterEngine(){}
    public UpdaterEngine(List<String> aExt){
        this();
        _extensions = aExt;
    }
    public UpdaterEngine(List<String> aExt, JProgressBar globalBar){
        this(aExt);
        _global = globalBar;
    }
    public UpdaterEngine(List<String> aExt, JProgressBar globalBar, JProgressBar fileBar){
        this(aExt, globalBar);
        _status = fileBar;
    }
    public UpdaterEngine(List<String> aExt, JProgressBar globalBar, JProgressBar fileBar, boolean defSettings, String exePath) throws FileNotFoundException, IOException{
        this(aExt, globalBar, fileBar);
        setSettingsBundle(defSettings, exePath);
    }
    
    public UpdaterEngine(List<String> aExt, JProgressBar globalBar, JProgressBar fileBar, boolean defSettings, String exePath, UpdatesVersions version, File destination) throws FileNotFoundException, IOException{
        this(aExt, globalBar, fileBar, defSettings, exePath);
        _version = version.getSelected();
        _dest = destination;
    }
    
    private void setSettingsBundle(boolean byDefault, String exePath) throws FileNotFoundException, IOException{
        if (byDefault){
            _settings = ResourceBundle.getBundle("resources.conf.settings");
        }else{
            Properties settings = new Properties();
            FileInputStream fis = new FileInputStream(new File((new File(exePath).getParentFile().getPath())+File.separator+"resources"+File.separator+"conf"+File.separator+"settings.properties"));
            settings.load(fis);
            _settings = settings;
        }
    }
    
    private boolean copyAllFilesByExtensions() throws FileNotFoundException, IOException{
        File[] filesInDir = null;
        
        if (_settings instanceof ResourceBundle){
            ResourceBundle settings = (ResourceBundle) _settings;
            filesInDir = new File(settings.getString("UPD_PATH")+settings.getString("UPD_FOLD")+"-"+_version).listFiles();
        }
        
        if (_settings instanceof Properties){
            Properties settings = (Properties) _settings;
            filesInDir = new File(settings.getProperty("UPD_PATH")+settings.getProperty("UPD_FOLD")+"-"+_version).listFiles();
        }
                    
        Iterator<String> extensions = _extensions.iterator();
        _global.setMaximum(_extensions.size());
        _global.setValue(0);
        while (extensions.hasNext()){
            _global.setValue(_global.getValue()+1);
            _global.repaint();
            String extension = extensions.next();
            copyFilesByExtension(filesInDir, extension);
        }//while
        
        return true;
    }
    
    private void copyFilesByExtension(File[] files, String ext) throws FileNotFoundException, IOException{
        for (File pf : files){
            
            if (extensionMatch(pf, ext) || isMatchName(pf, ext)){
                _status.setString("Coping: "+pf.getName());
                _status.setMaximum(Integer.parseInt(String.valueOf(pf.length())));
                _status.setValue(0);
                copyFile(pf);
            }
        }//for:pf
    }
    
    private boolean extensionMatch(File file, String ext){
        return (file.getName().indexOf("."+ext) != -1);
    }
    
    private boolean isMatchName(File file, String name){
        return (file.getName().toLowerCase().equals(name.toLowerCase()));
    }
    
    private void copyFile(File file) throws FileNotFoundException, IOException{
        
        OutputStream os = new FileOutputStream(_dest+File.separator+file.getName());
        
        InputStream is = new FileInputStream(file);
        try{
            byte[] buffer = new byte[1024];
            int length;
            while((length = is.read(buffer)) > 0){
                os.write(buffer, 0, length);
                _status.setValue(_status.getValue()+length);
                _status.repaint();
            }//while
        }finally{
            is.close();
            os.close();
        }//try-finally
    }

    @Override
    protected Object doInBackground() throws Exception {
        return copyAllFilesByExtensions();
    }
}
