package hk.reality.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;

import com.nullwire.trace.DefaultExceptionHandler;

public class ExceptionHelper {
    public static void report(Exception e) {
        DefaultExceptionHandler handler = new DefaultExceptionHandler(new DoNothingHandler());
        handler.uncaughtException(Thread.currentThread(), e);
    }

    static class DoNothingHandler implements UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread arg0, Throwable arg1) {
        }        
    }
}
