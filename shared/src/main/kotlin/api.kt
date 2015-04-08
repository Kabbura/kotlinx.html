package html4k

import java.util.LinkedHashSet

public trait TagConsumer<out R> {
    fun onTagStart(tag : Tag)
    fun onTagAttributeChange(tag : Tag, attribute : String, value : String)
    fun onTagEnd(tag : Tag)
    fun onTagContent(content : CharSequence)
    fun onTagContentEntity(entity : Entities)
    fun onCDATA(content : CharSequence)
    fun finalize() : R
}

public trait Tag {
    val tagName: String
    val consumer: TagConsumer<*>

    val attributes : MutableMap<String, String>
}

trait AttributeEnum {
    val realValue : String
}

public fun <T> Set<T>.plus(value : T) : Set<T> = with(LinkedHashSet(this)) { add(value); this }

inline fun <T : Tag> T.visit(block : T.() -> Unit) {
    consumer.onTagStart(this)
    this.block()
    consumer.onTagEnd(this)
}

fun Iterable<Pair<String, String?>>.toAttributesMap() : Map<String, String> = filter{it.second != null}.map { it.first to it.second!! }.toMap()

fun <T, C : TagConsumer<T>, TAG : Tag> C.build(attributes : Map<String, String>, builder : (Map<String, String>, TagConsumer<T>, TAG.() -> Unit) -> Unit, block : TAG.() -> Unit) : C {
    builder(attributes, this, block)
    return this
}

fun Map<*, *>.isNotEmpty() : Boolean = !isEmpty()