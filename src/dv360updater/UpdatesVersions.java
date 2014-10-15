package dv360updater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * @version 1.0.00
 * @author Arturo Martinez <arturo.martinez@gdsmodellica.com>
 */
public class UpdatesVersions {
    
    private static String _selected = null;
    private static List<String> _availables = null;
    
    public UpdatesVersions(boolean useInnerRscs, String exePath) throws FileNotFoundException, IOException{
        
        Object settings = getSettingsBundle(useInnerRscs, exePath);
        File[] fUpdates = null;       
        String fPatternName = null;
        
        if (settings instanceof ResourceBundle){
            fUpdates = new File(((ResourceBundle) settings).getString("UPD_PATH")).listFiles();
            fPatternName = ((ResourceBundle) settings).getString("UPD_FOLD");
        }
        if (settings instanceof Properties){
            fUpdates = new File(((Properties) settings).getProperty("UPD_PATH")).listFiles();
            fPatternName = ((Properties) settings).getProperty("UPD_FOLD");
        }
        
        Pattern patt = Pattern.compile(fPatternName+"\\-[\\.\\d]+");
        for (File pf : fUpdates){
            if (pf.isDirectory() && patt.matcher(pf.getName()).matches()){
                String version = pf.getName().substring(pf.getName().lastIndexOf("-")+1, pf.getName().length());
                if (_availables == null)
                    _availables = new ArrayList();
                _availables.add(version);
            }
        }
    }
    
    public void setSelected(String version){
        _selected = version;
    }
    
    public String getSelected(){
        return _selected;
    }
    
    public Object[] getAvailables(){
        return _availables.toArray();
    }
    
    private Object getSettingsBundle(boolean useInnerRscs, String exePath) throws FileNotFoundException, IOException{
        Object settings = null;
        
        if (useInnerRscs){
            settings = ResourceBundle.getBundle("resources.conf.settings");
        }else{
            settings = new Properties();
            FileInputStream fis = new FileInputStream(new File((new File(exePath).getParentFile().getPath())+File.separator+"resources"+File.separator+"conf"+File.separator+"settings.properties"));
            ((Properties) settings).load(fis);
        }
        
        return settings;
    }
    
}
