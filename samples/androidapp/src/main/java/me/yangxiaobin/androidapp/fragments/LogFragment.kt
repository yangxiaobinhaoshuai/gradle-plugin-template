package me.yangxiaobin.androidapp.fragments

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.yangxiaobin.androidapp.LOG_TAG
import me.yangxiaobin.androidapp.R
import me.yangxiaobin.androidapp.aLogD
import me.yangxiaobin.logger.RawLogger
import me.yangxiaobin.logger.disk_writer.DiskWriter
import me.yangxiaobin.logger.disk_writer.OnLogging
import me.yangxiaobin.logger.disk_writer.OnPostLoggingInterceptor
import me.yangxiaobin.logger.elements.InterceptorLogElement
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class LogFragment : Fragment() {

    private val onLogger: OnLogging = {
        aLogD("onPostLogging :${it.message}}")
    }

    private val logger = RawLogger.clone(newLogContext = InterceptorLogElement(OnPostLoggingInterceptor(onLogger)))

    private fun logD(m: String) = logger.d(LOG_TAG, "$m.")
    private fun logE(m: String) = logger.e(LOG_TAG, "$m.")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
    }

    private fun initialize() {
        logD("usr dir :${System.getProperty("user.home")}")
        DiskWriter.setConfig {
            // TODO 应该是手机设备上的路径
            //this.logFileName = "/Users/yangxiaobin/DevelopSpace/IDEA/gradle-plugin-template/samples/androidapp/temp/build.log"
            this.logFileName = "Users/yangxiaobin/Downloads/abc"
            this.pid = android.os.Process.myPid().toLong()
            this.processName = getProcessName().toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_log, container, false)
        root.setBackgroundColor(Color.WHITE)
        root.isClickable = true
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bt1 = view.findViewById<View>(R.id.bt1)?.setOnClickListener {
            DiskWriter.startSession()
        }

        val bt2 = view.findViewById<View>(R.id.bt2)?.setOnClickListener {

        }

        val bt3 = view.findViewById<View>(R.id.bt3)?.setOnClickListener {

        }
    }


    /**
     * @see https://stackoverflow.com/a/55842542/10247834
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun getProcessName(): Any? {
        return if (Build.VERSION.SDK_INT >= 28) Application.getProcessName() else try {
            @SuppressLint("PrivateApi") val activityThread = Class.forName("android.app.ActivityThread")

            // Before API 18, the method was incorrectly named "currentPackageName", but it still returned the process name
            // See https://github.com/aosp-mirror/platform_frameworks_base/commit/b57a50bd16ce25db441da5c1b63d48721bb90687
            val methodName = if (Build.VERSION.SDK_INT >= 18) "currentProcessName" else "currentPackageName"
            val getProcessName: Method = activityThread.getDeclaredMethod(methodName)
            getProcessName.invoke(null)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        }

        // Using the same technique as Application.getProcessName() for older devices
        // Using reflection since ActivityThread is an internal API
    }
}
