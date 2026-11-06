package com.js.smartvolt.asiste.model.data.local

import java.security.MessageDigest

fun sha256(texto : String): String = MessageDigest.getInstance("SHA-256").digest(texto.toByteArray(
    Charsets.UTF_8)).joinToString (""){"%02x".format(it)}
