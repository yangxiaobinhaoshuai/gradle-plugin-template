import me.yangxiaobin.base_lib.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class BaseAppPlugin : Plugin<Project> {

    override fun apply(p: Project) {

        Logger.i("Base-android-lib")
    }

}