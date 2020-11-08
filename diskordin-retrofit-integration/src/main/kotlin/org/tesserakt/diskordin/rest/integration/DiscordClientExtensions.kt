package org.tesserakt.diskordin.rest.integration

import org.tesserakt.diskordin.impl.core.client.BackendProvider
import org.tesserakt.diskordin.impl.core.client.RetrofitScope

val Retrofit = BackendProvider(::RetrofitScope)