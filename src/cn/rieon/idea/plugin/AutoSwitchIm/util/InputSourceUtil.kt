package cn.rieon.idea.plugin.AutoSwitchIm.util

import com.intellij.openapi.diagnostic.Logger
import javafx.util.Pair

import java.io.*
import java.util.ArrayList

/**
 * @author Rieon Ke <rieon></rieon>@rieon.cn>
 * *
 * @version 1.0.0
 * *
 * @since 2017/5/19
 */
object InputSourceUtil {

    private var inputSources: ArrayList<Pair<String, String>>? = null

    private val LOG = Logger.getInstance(InputSourceUtil::class.java)


    val BY_ID = 1
    val BY_NAME = 2

    init {
        try {

            // if the lib already in the system lib path
            System.loadLibrary("AutoSwitchInputSource")
            LOG.info("LIB LOADED")

        } catch (e: UnsatisfiedLinkError) {

            try {

                val libPath = NativeUtil.getLibPath("/native/libAutoSwitchInputSource.jnilib")

                if (libPath == null) {
                    LOG.error("GET LIB PATH FAILED")

                } else {

                    System.load(libPath)
                    LOG.info("LOADED FORM NATIVE UTILS")
                    LOG.info("CURRENT LIB PATH " + libPath)

                }

            } catch (e1: IOException) {

                LOG.error("LOADED LIB FAILED")

                LOG.error(e1)

            }

        }


    }

    private external fun nativeGetCurrentInputSource(): String
    private external fun nativeSwitchToInputSource(inputSourceName: String, type: Int): Boolean
    private external fun nativeGetAllInputSources(): String

    val currentInputSource: String
        get() {
            LOG.info("GET CURRENT INPUT SOURCE")
            return nativeGetCurrentInputSource()
        }

    fun switchTo(source: String, type: Int): Boolean {
        LOG.info("SWITCH TO INPUT SOURCE " + source)
        return nativeSwitchToInputSource(source, type)
    }

    val allInputSources: ArrayList<Pair<String, String>>
        get() {

            if (inputSources != null) {
                return inputSources as ArrayList<Pair<String, String>>
            }

            LOG.info("GET ALL INPUT SOURCES")
            val originalStr = nativeGetAllInputSources()

            val pairStrArr = originalStr.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            inputSources = ArrayList<Pair<String, String>>()

            for (x in pairStrArr) {

                val p = x.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                if (!p[0].contains("Emoji")) {
                    inputSources!!.add(Pair(p[0], p[1]))
                }

            }

            return inputSources!!
        }

}
