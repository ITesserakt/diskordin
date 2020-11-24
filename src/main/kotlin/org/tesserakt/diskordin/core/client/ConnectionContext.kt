package org.tesserakt.diskordin.core.client

data class ConnectionContext(
    val url: String,
    val compression: String
) : BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<ConnectionContext>
}