package cn.rieon.idea.plugin.AutoSwitchIm.provider

import cn.rieon.idea.plugin.AutoSwitchIm.component.AutoSwitchComponent
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * @author oleg
 */
@State(name = "ConfigurationProvider", storages = arrayOf(Storage("selected_input_source_setting.xml")))
class ConfigurationProvider : PersistentStateComponent<ConfigurationProvider.State> {

    class State {

        internal var selectedInputSource: String? = null

    }

    private var myState: State? = State()

    override fun getState(): State? {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    var selectedInputSource: String?
        get() {

            if (myState != null) {

                return myState!!.selectedInputSource
            }

            return null
        }
        set(selected) {

            myState!!.selectedInputSource = selected
            AutoSwitchComponent.switchToInputSource = selected as String

        }

    companion object {

        val instance: ConfigurationProvider
            get() = ServiceManager.getService(ConfigurationProvider::class.java)
    }


}

