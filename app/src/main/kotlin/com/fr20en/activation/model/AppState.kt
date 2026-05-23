package com.fr20en.activation.model

data class AppState(
    val generatedCode: ActivationCode? = null,
    val errorLog: String = "",
    val machineCode: String = "",
    val selectedDuration: Int = 0,
    val customDuration: String = "",
)

data class ActivationCode(
    val id: Long,
    val appName: String,
    val code: String,
    val machineCode: String,
    val durationMs: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val error: String? = null,
)