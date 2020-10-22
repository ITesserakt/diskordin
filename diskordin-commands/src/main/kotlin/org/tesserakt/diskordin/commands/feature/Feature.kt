package org.tesserakt.diskordin.commands.feature

import arrow.Kind
import arrow.core.Nel
import arrow.typeclasses.ApplicativeError
import io.github.classgraph.ClassRefTypeSignature
import org.tesserakt.diskordin.commands.ValidationError

interface Feature<F : Feature<F>> {
    fun <G> validate(AE: ApplicativeError<G, Nel<ValidationError>>): Kind<G, F>

    fun ClassRefTypeSignature.subtypeOf(className: String) =
        if (this.classInfo == null) this.loadClass().interfaces.contains(Class.forName(className))
        else classInfo.implementsInterface(className) or classInfo.extendsSuperclass(className)
}

interface PersistentFeature<F : PersistentFeature<F>> : Feature<F>

interface ModuleFeature<F : ModuleFeature<F>> : PersistentFeature<F>