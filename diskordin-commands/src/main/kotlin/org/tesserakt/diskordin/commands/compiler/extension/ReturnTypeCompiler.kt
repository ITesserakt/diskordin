package org.tesserakt.diskordin.commands.compiler.extension

import io.github.classgraph.MethodInfo
import org.tesserakt.diskordin.commands.compiler.PersistentCompilerExtension
import org.tesserakt.diskordin.commands.feature.ReturnType

class ReturnTypeCompiler : PersistentCompilerExtension<ReturnType>() {
    override fun compileFeature(function: MethodInfo, name: String): ReturnType {
        val returnType = function.typeSignatureOrTypeDescriptor.resultType
        val module = function.classInfo.typeSignature.superclassSignature
        function.typeSignatureOrTypeDescriptorStr

        return ReturnType(name, returnType, module)
    }
}