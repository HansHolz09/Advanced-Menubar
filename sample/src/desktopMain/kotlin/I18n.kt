
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.*

object I18n {
    private var systemDefault: Locale? = null
    var appLocale by mutableStateOf<Locale?>(null)
        private set

    fun switchTo(tagOrNull: String?) {
        if (systemDefault == null) systemDefault = Locale.getDefault()
        val new = when (tagOrNull) {
            null -> systemDefault!!
            else -> Locale.forLanguageTag(tagOrNull.replace('_', '-'))
        }
        Locale.setDefault(new)
        appLocale = new
    }
}