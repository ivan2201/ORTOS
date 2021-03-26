package Tests;

import OS.OrtOS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MyLogger {
    private final Logger log;
    public MyLogger(Class<?> clazz)
    {
         log = LoggerFactory.getLogger(clazz);
    }

    public void STask(int extra){
        log.debug('s' + Integer.toString(extra));
    }

    public void ETask(int extra)
    {
        log.debug('e' + Integer.toString(extra));
    }

    public void ATask(int extra) {
        log.debug('a' + Integer.toString(extra));
    }

    public void GRes(int extra) {
        log.debug('g' + Integer.toString(extra));
    }

    public void RRes(int extra) {
        log.debug('r' + Integer.toString(extra));
    }
}
