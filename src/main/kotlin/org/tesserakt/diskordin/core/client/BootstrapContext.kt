package org.tesserakt.diskordin.core.client

@Suppress("UNCHECKED_CAST")
class BootstrapContext(extra: Map<Extension<*>, ExtensionContext>) {
    private val extensions: MutableMap<Extension<*>, ExtensionContext> = extra.toMutableMap()

    operator fun <E : Extension<C>, C : ExtensionContext> get(ext: E) = extensions[ext] as? C
    operator fun <P : PersistentExtension<C>, C : ExtensionContext> get(ext: P) = extensions[ext] as C

    operator fun <C : ExtensionContext, E : Extension<C>> set(ext: E, ctx: C) {
        extensions[ext] = ctx
    }

    companion object

    interface ExtensionContext
    interface Extension<C : ExtensionContext>
    interface PersistentExtension<C : ExtensionContext> : Extension<C>
}