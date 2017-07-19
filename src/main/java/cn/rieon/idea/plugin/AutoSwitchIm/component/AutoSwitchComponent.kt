package cn.rieon.idea.plugin.AutoSwitchIm.component

import cn.rieon.idea.plugin.AutoSwitchIm.Constraints
import cn.rieon.idea.plugin.AutoSwitchIm.provider.ConfigurationProvider
import cn.rieon.idea.plugin.AutoSwitchIm.util.InputSourceUtil
import com.intellij.ide.FrameStateListener
import com.intellij.ide.FrameStateManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.command.CommandAdapter
import com.intellij.openapi.command.CommandEvent
import com.intellij.openapi.command.CommandListener
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.diagnostic.Logger
import java.util.*

/**
 * @author Rieon Ke <rieon></rieon>@rieon.cn>
 * *
 * @version 1.0.0
 * *
 * @since 2017/5/20
 */
class AutoSwitchComponent : ApplicationComponent {

    private var currentInputSource: String? = null

    /**
     * last input source
     */
    private var lastOutVimInsert: String? = null
    private var lastInVimInsert: String? = null
    private var lastOutIdea: String? = null
    private var lastInIdea: String? = null

    private var inNormal: Boolean? = true

    private var configurationProvider: ConfigurationProvider? = null



    /**
     * init component and add listener
     */
    override fun initComponent() {

        LOG.info("INIT COMPONENT")

        currentInputSource = currentInputSourceId
        CommandProcessor.getInstance().addCommandListener(commandListener)
        FrameStateManager.getInstance().addListener(frameStateListener)

        /**
         * Reload configuration from configuration provider
         */

        configurationProvider = ConfigurationProvider.instance

        if (configurationProvider!!.outVimInsertConfig != null) {

            outVimInsertConfig = configurationProvider!!.outVimInsertConfig!!

            LOG.info("USE CONFIG INPUT SOURCE FOR OUT VIM INSERT MODE" + outVimInsertConfig)
        }

        if (configurationProvider!!.InVimInsertConfig != null) {

            inVimInsertConfig = configurationProvider!!.InVimInsertConfig!!

            LOG.info("USE CONFIG INPUT SOURCE FOR In VIM INSERT MODE" + inVimInsertConfig)
        }
        if (configurationProvider!!.outOfIdeaConfig != null) {

            outOfIdeaConfig = configurationProvider!!.outOfIdeaConfig!!

            LOG.info("USE CONFIG INPUT SOURCE FOR OUT OF IDEA" + outOfIdeaConfig)
        }
        if (configurationProvider!!.inIdeaConfig != null) {

            inIdeaConfig = configurationProvider!!.inIdeaConfig!!

            LOG.info("USE CONFIG INPUT SOURCE FOR IDEA FOCUSED" + inIdeaConfig)
        } //load complete

        LOG.info("INIT SUCCESSFUL")
    }

    /**
     * auto switch input source while plugin state changed
     * @return CommandListener
     */
    private val commandListener: CommandListener
        get() = object : CommandAdapter() {
            override fun beforeCommandFinished(event: CommandEvent?) {

                val commandName = event!!.commandName

                LOG.info("Current Command Name is " + commandName)

                if (commandName == null || commandName == "null"){
                    return;
                }

                /**
                 * out of vim insert mode
                 */
                if (OUT_VIM_INSERT_MODE.contains(commandName)) {
                    handleOutVimInsertSwitch()
                    /**
                     * into vim insert mode
                     */
                } else if (IN_VIM_INSERT_MODE.contains(commandName)) {
                    handleInVimInsertSwitch()

                    /**
                     * start typing => not in vim normal mode
                     */
                } else if ("Typing" == commandName) {


                    // set current vim mode to normal
                    inNormal = false

                } else if ("" == commandName) {

                    // if current in normal mode
                    if (inNormal!!)
                        handleOutVimInsertSwitch()

                }
            }
        }

    /**
     * restore last input source while idea re-focused
     * @return FrameStateListener
     */
    private val frameStateListener: FrameStateListener
        get() = object : FrameStateListener.Adapter() {
            override fun onFrameDeactivated() {
                handleOutIdeaSwitch()
            }

            override fun onFrameActivated() {
                handInIdeaSwitch()
            }
        }

    /**
     * get current input source
     * @return String currentInputSource
     */
    private val currentInputSourceId: String?
        get() {

            val current = InputSourceUtil.currentInputSource

            LOG.info("CURRENT INPUT SOURCE  " + current)
            if (current.isEmpty()) {
                val notification = Notification("Switch IME Error", "Switch IME Error", "Get current input source faild", NotificationType.ERROR)
                Notifications.Bus.notify(notification)
                LOG.info("GET CURRENT INPUT SOURCE FAILED")
            }

            return current

        }

    internal fun switchTo(source: String?) {

        LOG.info("SWITCH TO INPUT SOURCE  " + source!!)
        if (!InputSourceUtil.switchTo(source)) {
            val notification = Notification("Switch IME Error", "Switch IME Error", "Switch IME Failed", NotificationType.ERROR)
            Notifications.Bus.notify(notification)
            LOG.info("SWITCH TO INPUT SOURCE FAILED")
        }
    }

    override fun disposeComponent() {
        // TODO: insert component disposal logic here
    }

    override fun getComponentName(): String {
        return "AutoSwitchComponent"
    }

    companion object {

        private val LOG = Logger.getInstance(AutoSwitchComponent::class.java)

        var outVimInsertConfig = Constraints.CONFIG_INPUT_DEFAULT
        var inVimInsertConfig = Constraints.CONFIG_INPUT_DEFAULT
        var inIdeaConfig = Constraints.CONFIG_INPUT_DEFAULT
        var outOfIdeaConfig = Constraints.CONFIG_INPUT_DEFAULT


        /**
         * switch input source in these plugin states
         */
        private val OUT_VIM_INSERT_MODE = Arrays.asList(
                "Vim Exit Visual Mode",
                "Vim Toggle Line Selection",
                "Vim Toggle Block Selection",
                "Vim Toggle Character Selection",
                "Vim Exit Insert Mode")

        /**
         * switch to last input source in these plugin states
         */
        private val IN_VIM_INSERT_MODE = Arrays.asList(
                "Vim Enter",
                "Vim Insert at Line Start",
                "Vim Insert New Line Above",
                "Vim Insert New Line Below",
                "Vim Insert Previous Text",
                "Vim Insert Previous Text",
                "Vim Insert Register",
                "Vim Toggle Insert/Replace",
                "Vim Change Line",
                "Vim Change Character",
                "Vim Change Characters",
                "Vim Replace",
                "Vim Insert After Cursor",
                "Vim Insert After Line End",
                "Vim Insert Before Cursor",
                "Vim Insert Before First non-Blank",
                "Vim Insert Character Above Cursor",
                "Vim Insert Character Below Cursor",
                "Vim Delete Inserted Text",
                "Vim Delete Previous Word")
    }

    internal fun handleInVimInsertSwitch(){

        inNormal = false
        if (inVimInsertConfig == Constraints.CONFIG_INPUT_NONE) return;

        //get current input source
        currentInputSource = currentInputSourceId
        //record last input source state for last-in-vim-insert-mode
        lastOutVimInsert = currentInputSource

        handle(currentInputSource,lastInVimInsert, inVimInsertConfig)

    }

    internal fun handleOutVimInsertSwitch(){

        inNormal = true
        if (outVimInsertConfig == Constraints.CONFIG_INPUT_NONE) return;
        //get current input source
        currentInputSource = currentInputSourceId
        //record last input source state for last-in-vim-insert-mode
        lastInVimInsert = currentInputSource


        handle(currentInputSource,lastOutVimInsert, outVimInsertConfig)

    }

    internal fun handleOutIdeaSwitch(){

        if (outOfIdeaConfig == Constraints.CONFIG_INPUT_NONE) return;
        //get current input source
        currentInputSource = currentInputSourceId
        //record last input source state for last-in-vim-insert-mode
        lastInIdea = currentInputSource

        handle(currentInputSource,lastOutIdea, outOfIdeaConfig)

    }

    internal fun handInIdeaSwitch(){

        if (inIdeaConfig == Constraints.CONFIG_INPUT_NONE) return;
        //get current input source
        currentInputSource = currentInputSourceId
        //record last input source state for last-in-vim-insert-mode
        lastOutIdea = currentInputSource

        handle(currentInputSourceId,lastInIdea, inIdeaConfig)

    }

    internal fun handle(current:String?,last:String?,config:String) {

        if (current == null || current == config) return

        when (config) {

            Constraints.CONFIG_INPUT_LAST -> {

                if (last == null || current == last) {
                    return
                } else {
                    switchTo(last)
                }

            }

            Constraints.CONFIG_INPUT_NONE -> return

        // use input source in configuration
            else -> {
                switchTo(config)
            }
        }
    }
}
