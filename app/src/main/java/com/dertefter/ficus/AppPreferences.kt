import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private var sharedPreferences: SharedPreferences? = null

    fun setup(context: Context) {

        sharedPreferences = context.getSharedPreferences("ficus.sharedprefs", MODE_PRIVATE)
    }

    var login: String?
        get() = Key.LOGIN.getString()
        set(value) = Key.LOGIN.setString(value)

    var theme: String?
        get() = Key.THEME.getString()
        set(value) = Key.THEME.setString(value)

    var Review: Boolean?
        get() = Key.REV.getBoolean()
        set(value) = Key.REV.setBoolean(value)

    var password: String?
        get() = Key.PASSWORD.getString()
        set(value) = Key.PASSWORD.setString(value)

    var group: String?
        get() = Key.GROUP.getString()
        set(value) = Key.GROUP.setString(value)

    var token: String?
        get() = Key.TOKEN.getString()
        set(value) = Key.TOKEN.setString(value)

    var name: String?
        get() = Key.NAME.getString()
        set(value) = Key.NAME.setString(value)

    var fullName: String?
        get() = Key.FULLNAME.getString()
        set(value) = Key.FULLNAME.setString(value)

    private enum class Key {
        LOGIN, PASSWORD, GROUP, TOKEN, NAME, FULLNAME, REV, THEME;

        fun getBoolean(): Boolean? =
            if (sharedPreferences!!.contains(name)) sharedPreferences!!.getBoolean(
                name,
                false
            ) else null

        fun getFloat(): Float? =
            if (sharedPreferences!!.contains(name)) sharedPreferences!!.getFloat(name, 0f) else null

        fun getInt(): Int? =
            if (sharedPreferences!!.contains(name)) sharedPreferences!!.getInt(name, 0) else null

        fun getLong(): Long? =
            if (sharedPreferences!!.contains(name)) sharedPreferences!!.getLong(name, 0) else null

        fun getString(): String? =
            if (sharedPreferences!!.contains(name)) sharedPreferences!!.getString(
                name,
                ""
            ) else null

        fun setBoolean(value: Boolean?) =
            value?.let { sharedPreferences!!.edit { putBoolean(name, value) } } ?: remove()

        fun setFloat(value: Float?) =
            value?.let { sharedPreferences!!.edit { putFloat(name, value) } } ?: remove()

        fun setInt(value: Int?) =
            value?.let { sharedPreferences!!.edit { putInt(name, value) } } ?: remove()

        fun setLong(value: Long?) =
            value?.let { sharedPreferences!!.edit { putLong(name, value) } } ?: remove()

        fun setString(value: String?) =
            value?.let { sharedPreferences!!.edit { putString(name, value) } } ?: remove()

        fun remove() = sharedPreferences!!.edit { remove(name) }
    }
}