package dv360updater;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version 1.0.00
 * @author Arturo Martínez <arturo.martinez@gdsmodellica.com>
 */
public class EngineLogger {
    
    private static final Logger _log = Logger.getAnonymousLogger();
    
    public EngineLogger(){
        _log.setLevel(Level.ALL);
    }
    
    public EngineLogger(String exePath) throws IOException{
        this();
        _log.addHandler(new FileHandler(new File(exePath).getParentFile().getAbsolutePath()+File.separator+"logs"+File.separator+"dv360updater.log"));
    }
    
    public void info(String message){
        _log.info(message);
    }
    
    public void debug(String message){
        _log.fine(message);
    }
    
    public void error(String message){
        _log.severe(message);
    }
    
    public void closeLog(){
        for (Handler h : _log.getHandlers()){
            h.close();
        }
    }
}
