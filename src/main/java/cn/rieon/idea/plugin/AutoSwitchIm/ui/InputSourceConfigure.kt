package cn.rieon.idea.plugin.AutoSwitchIm.ui

import cn.rieon.idea.plugin.AutoSwitchIm.Constraints
import cn.rieon.idea.plugin.AutoSwitchIm.provider.ConfigurationProvider
import cn.rieon.idea.plugin.AutoSwitchIm.util.InputSourceUtil
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.jetbrains.annotations.Nls

import javax.swing.*
import java.util.ArrayList

/**
 * @author Rieon Ke <rieon></rieon>@rieon.cn>
 * *
 * @version 1.0.0
 * *
 * @since 2017/6/9
 */
class InputSourceConfigure : Configurable {

    private var settingPanel: InputSourceConfigureForm? = null
    private var configurationProvider: ConfigurationProvider? = null

    /**
     * Returns the visible name of the configurable component.
     * Note, that this method must return the display name
     * that is equal to the display name declared in XML
     * to avoid unexpected errors.

     * @return the visible name of the configurable component
     */
    @Nls
    override fun getDisplayName(): String {
        return "Auto Switch Input Source"
    }

    /**
     * Creates new Swing settingPanel that enables user to configure the settings.
     * Usually this method is called on the EDT, so it should not take a long time.
     *
     *
     * Also this place is designed to allocate resources (subscriptions/listeners etc.)

     * @return new Swing settingPanel to show, or `null` if it cannot be created
     * *
     * @see .disposeUIResources
     */
    override fun createComponent(): JComponent? {
        settingPanel = InputSourceConfigureForm()
        configurationProvider = ConfigurationProvider.instance
        var sources = InputSourceUtil.allInputSources
        val nonePair:Pair<String,String> = Pair(Constraints.CONFIG_INPUT_NONE,"Never Switch It!")
        val lastPair:Pair<String,String> = Pair(Constraints.CONFIG_INPUT_LAST,"Last Input Source")
        sources.add(nonePair)
        sources.add(lastPair)
        return settingPanel!!.createPanel(configurationProvider, sources)

    }

    /**
     * Indicates whether the Swing settingPanel was modified or not.
     * This method is called very often, so it should not take a long time.

     * @return `true` if the settings were modified, `false` otherwise
     */
    override fun isModified(): Boolean {
        return settingPanel!!.isModified
    }

    /**
     * Notifies the configurable component that the Swing settingPanel will be closed.
     * This method should dispose all resources associated with the component.
     */
    override fun disposeUIResources() {
        settingPanel = null
    }

    /**
     * Stores the settings from the Swing settingPanel to the configurable component.
     * This method is called on EDT upon user's request.

     * @throws ConfigurationException if values cannot be applied
     */
    @Throws(ConfigurationException::class)
    override fun apply() {

        settingPanel!!.apply()

    }

}
