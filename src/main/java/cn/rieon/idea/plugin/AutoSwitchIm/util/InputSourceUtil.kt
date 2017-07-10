package cn.rieon.idea.plugin.AutoSwitchIm.util

import com.intellij.openapi.diagnostic.Logger
import java.awt.SystemColor.text

import java.io.*
import java.util.*

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

    private var EXEC_PATH: String? = null

    private val EXCLUDE_IME = Arrays.asList(
            "com.apple.inputmethod.EmojiFunctionRowItem",
            "com.baidu.inputmethod.BaiduIM"
    )

    init {

        try {

            val execPath = NativeUtil.getLibPath("/native/ImSelect")

            if (execPath == null) {
                LOG.error("GET EXEC PATH FAILED")

            } else {

                EXEC_PATH = execPath

                LOG.info("LOADED FORM NATIVE UTILS")
                LOG.info("CURRENT EXEC PATH " + execPath)

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun nativeGetCurrentInputSource(): String {


        return execImSelect("c")


    }

    private fun nativeSwitchToInputSource(inputSourceName: String): Boolean {

        execImSelect("s " + inputSourceName)
        return true

    }

    private fun nativeGetAllInputSources(): String {

        return execImSelect("l")

    }

    internal fun execImSelect(command: String): String {

        var c: String? = EXEC_PATH;
        if (command.isNotEmpty()) {
            c = EXEC_PATH + " -" + command
        }

        LOG.info("EXEC COMMAND " + c)

        var result: Process? = null
        try {
            result = Runtime.getRuntime().exec(c)
        } catch (e: IOException) {

            LOG.error("EXEC FAILED!")

            e.printStackTrace()
        }

        if (result != null) {
            val text = result.inputStream.bufferedReader().use(BufferedReader::readText)
            LOG.info("GET EXEC RESULT " + text)
            return text

        }
        return ""

    }


    val currentInputSource: String
        get() {
            LOG.info("GET CURRENT INPUT SOURCE")
            return nativeGetCurrentInputSource()
        }

    fun switchTo(source: String): Boolean {
        LOG.info("SWITCH TO INPUT SOURCE " + source)
        return nativeSwitchToInputSource(source)
    }

    internal fun filterInputSource(source: String): Boolean {

        return !EXCLUDE_IME.contains(source)

    }

    val allInputSources: ArrayList<Pair<String, String>>
        get() {

            LOG.info("GET ALL INPUT SOURCES")
            val originalStr = nativeGetAllInputSources()

            val pairStrArr = originalStr.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            inputSources = ArrayList<Pair<String, String>>()

            pairStrArr
                    .map { x -> x.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
                    .filter { filterInputSource(it[0]) }
                    .forEach { inputSources!!.add(Pair(it[0], it[1])) }

            return inputSources as ArrayList<Pair<String, String>>
        }

}
