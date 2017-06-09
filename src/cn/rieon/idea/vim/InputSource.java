package cn.rieon.idea.vim;

import com.intellij.openapi.diagnostic.Logger;

import java.io.*;
import java.util.Map;

/**
 * @author Rieon Ke <rieon@rieon.cn>
 * @version 1.0.0
 * @since 2017/5/19
 */
class InputSource {

    private static final Logger LOG = Logger.getInstance(InputSource.class);

    static final int BY_ID = 1;
    static final int BY_NAME = 2;

    static {
        try {

            // if the lib already in the system lib path
            System.loadLibrary("AutoSwitchInputSource");
            LOG.info("LIB LOADED");

        } catch (UnsatisfiedLinkError e) {

            try {

                String libPath = NativeUtils.getLibPath("/native/libAutoSwitchInputSource.jnilib");

                if (libPath == null){
                    LOG.error("GET LIB PATH FAILED");

                }else{

                    System.load(libPath);
                    LOG.info("LOADED FORM NATIVE UTILS");
                    LOG.info("CURRENT LIB PATH " + libPath);

                }

            } catch (IOException e1) {

                LOG.error("LOADED LIB FAILED");

                LOG.error(e1);

            }

        }
    }

    private static native String nativeGetCurrentInputSource();
    private static native boolean nativeSwitchToInputSource(String inputSourceName,int type);
    private static native Map<String,String> nativeGetAllInputSources();

    static String getCurrentInputSource(){
        LOG.info("GET CURRENT INPUT SOURCE");
        return nativeGetCurrentInputSource();
    }

    static boolean switchTo(String source,int type){
        LOG.info("SWITCH TO INPUT SOURCE "+ source);
        return nativeSwitchToInputSource(source,type);
    }

    static Map<String,String> getAllInputSources(){
        LOG.info("GET ALL INPUT SOURCES");
        return nativeGetAllInputSources();
    }

}
