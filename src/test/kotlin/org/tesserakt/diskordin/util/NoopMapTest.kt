package org.tesserakt.diskordin.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class NoopMapTest : StringSpec({
    "size should be always 0" {
        val map = NoopMap<String, String>()
        map.size shouldBe 0
        map += "test" to "value"
        map.size shouldBe 0
        map -= "test"
        map.size shouldBe 0
    }

    "containsKey and containsValue should always return false" {
        val map = NoopMap<String, String>()
        map += "hello" to "world"

        map.containsKey("hello").shouldBeFalse()
        map.containsKey("no").shouldBeFalse()
        map.containsValue("world").shouldBeFalse()
        map.containsValue("yes!").shouldBeFalse()
    }

    "get should always returns null" {
        val map = NoopMap<String, String>()
        map += "hello" to "world"

        map["hello"].shouldBeNull()
        map["Am I exist?"].shouldBeNull()
    }

    "isEmpty should always returns true" {
        val map = NoopMap<String, String>()
        map.isEmpty().shouldBeTrue()
        map += "hello" to "world"
        map.isEmpty().shouldBeTrue()
    }

    "entries should always equals to emptySet" {
        val map = NoopMap<String, String>()
        map.entries shouldBe emptySet()
        map += "hello" to "world"
        map.entries shouldBe emptySet()
    }

    "put shouldn't append any value to map, and so should always returns null" {
        val map = NoopMap<String, String>()
        map.put("hello", "world").shouldBeNull()
    }

    "remove should always returns null" {
        val map = NoopMap<String, String>()
        map += "hello" to "world"
        map.remove("hello").shouldBeNull()
        map.remove("test").shouldBeNull()
    }
})
