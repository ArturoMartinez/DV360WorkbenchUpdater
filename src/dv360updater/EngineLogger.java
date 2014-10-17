package dv360updater;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


class EngineLoggerFormatter extends Formatter{

    private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append(df.format(new Date(record.getMillis()))).append(" - ");
        //builder.append("[").append(record.getSourceClassName()).append(".");
        //builder.append(record.getSourceMethodName()).append("] - ");
        builder.append("[").append(record.getLevel()).append("] :: ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String getHead(Handler h){
        return super.getHead(h);
    }
    @Override
    public String getTail(Handler h){
        return super.getTail(h);
    }
}

/**
 * @version 1.0.00
 * @author Arturo Martínez <arturo.martinez@gdsmodellica.com>
 */
public class EngineLogger {
    
    private static final Logger _log = Logger.getLogger("DV360 Workbench Updater");
    
    public EngineLogger(){}
    
    public EngineLogger(String exePath) throws IOException{
        this();
        Handler hndlr = new FileHandler(new File(exePath).getParentFile().getAbsolutePath()+File.separator+"logs"+File.separator+"dv360updater%g.log", false);
        hndlr.setFormatter(new EngineLoggerFormatter());
        _log.addHandler(hndlr);
        _log.setLevel(Level.ALL);
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
