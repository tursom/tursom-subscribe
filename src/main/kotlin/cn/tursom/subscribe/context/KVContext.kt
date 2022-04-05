package cn.tursom.subscribe.context

interface KVContext {
  companion object {
    fun key(vararg keys: String) = buildString {
      keys.forEach { key ->
        if (this.isNotEmpty()) {
          append('-')
        }
        append(key)
      }
    }
  }

  operator fun get(vararg keys: String): String? {
    return get(key(keys = keys))
  }

  operator fun set(vararg keys: String, value: String?) {
    this[key(keys = keys)] = value
  }

  operator fun get(key: String): String?
  operator fun set(key: String, value: String?)
}