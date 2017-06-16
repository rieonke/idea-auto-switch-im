package cn.rieon.idea.plugin.AutoSwitchIm.provider

import cn.rieon.idea.plugin.AutoSwitchIm.component.AutoSwitchComponent
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

/**
 * @author oleg
 */
@State(name = "ConfigurationProvider", storages = arrayOf(Storage("selected_input_source_setting_1_3.xml")))
class ConfigurationProvider : PersistentStateComponent<ConfigurationProvider.State> {

    class State {

        internal var OutOfIdeaInput: String? = null
        internal var IdeaFocusedInput: String? = null
        internal var InVimInsertInput: String? = null
        internal var OutVimInsertInput: String? = null

    }

    private var myState: State? = State()

    override fun getState(): State? {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    var inIdeaConfig: String?
        get() {

            if (myState != null) {

                return myState!!.IdeaFocusedInput
            }

            return null
        }
        set(input) {

            myState!!.IdeaFocusedInput = input
            AutoSwitchComponent.inIdeaConfig = myState!!.IdeaFocusedInput!!

        }

    var InVimInsertConfig: String?
        get() {

            if (myState != null) {

                return myState!!.InVimInsertInput
            }

            return null
        }
        set(input) {

            myState!!.InVimInsertInput = input
            AutoSwitchComponent.inVimInsertConfig = myState!!.InVimInsertInput!!

        }

    var outVimInsertConfig: String?
        get() {

            if (myState != null) {

                return myState!!.OutVimInsertInput
            }

            return null
        }
        set(input) {

            myState!!.OutVimInsertInput = input
            AutoSwitchComponent.outVimInsertConfig = myState!!.OutVimInsertInput!!

        }

    var outOfIdeaConfig: String?
        get() {

            if (myState != null) {

                return myState!!.OutOfIdeaInput
            }

            return null
        }
        set(input) {

            myState!!.OutOfIdeaInput = input
            AutoSwitchComponent.outOfIdeaConfig = myState!!.OutOfIdeaInput!!

        }

    companion object {

        val instance: ConfigurationProvider
            get() = ServiceManager.getService(ConfigurationProvider::class.java)
    }


}

