package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig

@Suppress("unused")
class ProjectConfig : AbstractProjectConfig() {
    override val parallelism: Int = 2
}