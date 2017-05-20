package cn.rieon.idea.vim;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rieon Ke <rieon@rieon.cn>
 * @version 1.0.0
 * @since 2017/5/19
 */
public class InputSource {

    private String ImSwitchLocation = null;

    private static InputSource instance = null;

    private InputSource(String imSwitchLocation){
        ImSwitchLocation = imSwitchLocation;
    }

    public static InputSource getInstance(String imSwitchLocation){

        if (instance == null){
            instance = new InputSource(imSwitchLocation);
        }
        return instance;
    }

    public List<String> getAllInputSoure() throws IOException {

        BufferedReader reader = execImSelect("il");
        String s = null;
        List<String> allInputResource = new ArrayList<>();
        while ((s = reader.readLine()) != null) {
            allInputResource.add(s);
        }

        allInputResource.forEach(System.out::println);

        return allInputResource;
    }

    public String getCurrentInputSource() throws IOException {

        BufferedReader reader = execImSelect("i");
        String s = null;
        List<String> allInputResource = new ArrayList<>();
        while ((s = reader.readLine()) != null) {
            allInputResource.add(s);
        }

        return allInputResource.get(0);
    }

    public BufferedReader execImSelect(String command) throws IOException {

        Process result = Runtime.getRuntime().exec(ImSwitchLocation+" -"+command);
        return new BufferedReader(new InputStreamReader(result.getInputStream()));

    }

    public void switchToInputSource(String sourceId) throws IOException {

        execImSelect("is " + sourceId);

    }

}
