package cn.rieon.idea.vim;

import com.intellij.ide.FrameStateListener;
import com.intellij.ide.FrameStateManager;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.command.CommandAdapter;
import com.intellij.openapi.command.CommandEvent;
import com.intellij.openapi.command.CommandListener;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Rieon Ke <rieon@rieon.cn>
 * @version 1.0.0
 * @since 2017/5/20
 */
public class AutoSwitchComponent implements ApplicationComponent {

    private static final Logger LOG = Logger.getInstance(AutoSwitchComponent.class);
    /**
     * last input source
     */
    private String lastInputSource = null;

    private Boolean inNormal = true;

    public static final String defaultEnglishInputSource = "com.apple.keylayout.ABC";

    /**
     * switch input source in these vim states
     */
    private static final List<String> VimStateSwitchToABCOnly = new ArrayList<String>(){{
        // to visual mode
        add("Vim Exit Visual Mode");
        add("Vim Toggle Line Selection");
        add("Vim Toggle Block Selection");
        add("Vim Toggle Character Selection");
        // to normal mode
        add("Vim Exit Insert Mode");
        //EditorChange

    }};

    /**
     * switch to last input source in these vim states
     */
    private static final List<String> VimStateSwitchToLastIm = new ArrayList<String>(){{

        add("Vim Enter");
        add("Vim Insert at Line Start");
        add("Vim Insert New Line Above");
        add("Vim Insert New Line Below");
        add("Vim Insert Previous Text");
        add("Vim Insert Previous Text");
        add("Vim Insert Register");
        add("Vim Toggle Insert/Replace");
        add("Vim Change Line");
        add("Vim Change Character");
        add("Vim Change Characters");
        add("Vim Replace");
        add("Vim Insert After Cursor");
        add("Vim Insert After Line End");
        add("Vim Insert Before Cursor");
        add("Vim Insert Before First non-Blank");
        add("Vim Insert Character Above Cursor");
        add("Vim Insert Character Below Cursor");
        add("Vim Delete Inserted Text");
        add("Vim Delete Previous Word");

    }};

    public AutoSwitchComponent() {
    }

    /**
     * init component and add listener
     */
    @Override
    public void initComponent() {

        LOG.info("INIT COMPONENT");

        lastInputSource = getCurrentInputSourceId();
        CommandProcessor.getInstance().addCommandListener(getCommandListener());
        FrameStateManager.getInstance().addListener(getFrameStateListener());

    }

    /**
     * auto switch input source while vim state changed
     * @return CommandListener
     */
    private CommandListener getCommandListener(){
        return new CommandAdapter() {
            @Override
            public void beforeCommandFinished(CommandEvent event) {

                String commandName = event.getCommandName();

                if (VimStateSwitchToABCOnly.contains(commandName)){
                    lastInputSource = getCurrentInputSourceId();
                    inNormal = true;
                    if(lastInputSource == null || Objects.equals(lastInputSource, defaultEnglishInputSource))
                        return;
                    switchTo(defaultEnglishInputSource);
                }else if (VimStateSwitchToLastIm.contains(commandName)){
                    String current = getCurrentInputSourceId();
                    inNormal = false;
                    if (current == null || current.equals(lastInputSource))
                        return;
                    switchTo(lastInputSource);
                }else if ("Typing".equals(commandName)){
                    inNormal = false;
                }else if ("".equals(commandName)){
                    if (inNormal)
                        switchTo(defaultEnglishInputSource);
                }
            }
        };

    }

    /**
     * restore last input source while idea re-focused
     * @return FrameStateListener
     */
    private FrameStateListener getFrameStateListener(){

        return new FrameStateListener.Adapter() {
            @Override
            public void onFrameDeactivated() {
                lastInputSource = getCurrentInputSourceId();
            }

            @Override
            public void onFrameActivated() {
                String current = getCurrentInputSourceId();
                if (current == null || current.equals(lastInputSource))
                    return;
                switchTo(lastInputSource);
            }
        };
    }

    /**
     * get current input source
     * @return String currentInputSource
     */
    private String getCurrentInputSourceId(){

        String current = InputSource.getCurrentInputSource();

        LOG.info("CURRENT INPUT SOURCE  " + current);
        if (current == null || current.length() <= 0){
            Notification notification = new Notification("Switch IME Error","Switch IME Error","Get current input source faild",NotificationType.ERROR);
            Notifications.Bus.notify(notification);
            LOG.info("GET CURRENT INPUT SOURCE FAILED");
        }

        return current;

    }

    void switchTo(String source){

        LOG.info("SWITCH TO INPUT SOURCE  " + source);
        if (source == null)
            return;
        if (!InputSource.switchTo(source,InputSource.BY_ID)) {
            Notification notification = new Notification("Switch IME Error", "Switch IME Error", "Switch IME Failed", NotificationType.ERROR);
            Notifications.Bus.notify(notification);
            LOG.info("SWITCH TO INPUT SOURCE FAILED");
        }
    }

    @Override
    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "AutoSwitchComponent";
    }

}
