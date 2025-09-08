package com.qiuqiuqiu.weatherPredicate.data

data class Warning(
    val id: String,
    val sender: String,
    val pubTime: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val status: String,
    val level: String,
    val severity: String,
    val severityColor: String,
    val type: String,
    val typeName: String,
    val urgency: String,
    val certainty: String,
    val text: String,
    val related: String
)