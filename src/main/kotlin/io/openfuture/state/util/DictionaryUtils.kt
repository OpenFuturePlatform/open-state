package io.openfuture.state.util

import io.openfuture.state.entity.base.Dictionary
import java.lang.IllegalArgumentException

object DictionaryUtils {

    fun <T : Enum<*>> valueOf(clazz: Class<out T>, id: Int): T =
            clazz.enumConstants.firstOrNull { (it as Dictionary).getId() == id }
                    ?: throw IllegalArgumentException("Type ID not found")

}
