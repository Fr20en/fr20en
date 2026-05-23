package com.fr20en.activation.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.fr20en.activation.data.CodeGenerator
import com.fr20en.activation.model.AppState
import com.fr20en.activation.util.HistoryManager
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.preference.RadioButtonPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun GeneratePage(
    appState: AppState,
    onAppStateChange: (AppState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 修复：去掉 remember 的 key，避免每次文本变化重建 TextFieldValue 导致光标跳动
    var machineCode by remember {
        mutableStateOf(TextFieldValue(appState.machineCode))
    }
    var customDuration by remember {
        mutableStateOf(TextFieldValue(appState.customDuration))
    }

    val durationOptions = listOf(
        "10分钟" to 600000L,
        "1天" to 86400000L,
        "7天" to 604800000L,
        "30天" to 2592000000L,
        "1年" to 31536000000L,
        "自定义" to -1L,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 机器码输入卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "机器码",
                    style = MiuixTheme.textStyles.subtitle,
                )
                TextField(
                    value = machineCode,
                    onValueChange = {
                        machineCode = it
                        onAppStateChange(appState.copy(machineCode = it.text))
                    },
                    label = "请输入纯数字机器码",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // 激活时长选择 - 使用 RadioButtonPreference
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            durationOptions.forEachIndexed { index, (label, _) ->
                RadioButtonPreference(
                    title = label,
                    selected = appState.selectedDuration == index,
                    onClick = {
                        onAppStateChange(appState.copy(selectedDuration = index))
                    },
                )
            }
        }

        // 自定义时长输入框
        if (appState.selectedDuration == 5) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextField(
                        value = customDuration,
                        onValueChange = {
                            customDuration = it
                            onAppStateChange(appState.copy(customDuration = it.text))
                        },
                        label = "自定义时长（毫秒）",
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(
                        text = "例如：1分钟 = 60000毫秒",
                        style = MiuixTheme.textStyles.footnote1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }
        }

        // 生成按钮
        Button(
            onClick = {
                onAppStateChange(appState.copy(errorLog = "", generatedCode = null))
                try {
                    val mcText = machineCode.text
                    if (mcText.isBlank()) {
                        onAppStateChange(appState.copy(errorLog = "错误：请输入机器码"))
                        return@Button
                    }
                    if (mcText.toLongOrNull() == null) {
                        onAppStateChange(appState.copy(errorLog = "错误：机器码必须是纯数字"))
                        return@Button
                    }

                    val durationMs = if (appState.selectedDuration == 5) {
                        val custom = appState.customDuration.toLongOrNull()
                        if (custom == null) {
                            onAppStateChange(appState.copy(errorLog = "错误：请输入有效的自定义时长（数字）"))
                            return@Button
                        }
                        custom
                    } else {
                        durationOptions[appState.selectedDuration].second
                    }

                    val result = CodeGenerator.generate(mcText, durationMs)

                    if (result.error != null) {
                        onAppStateChange(appState.copy(errorLog = "错误：${result.error}"))
                    } else {
                        HistoryManager.add(context, result)
                        onAppStateChange(appState.copy(generatedCode = result, errorLog = ""))
                    }
                } catch (e: Exception) {
                    onAppStateChange(appState.copy(errorLog = "错误：${e.message}"))
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("生成激活码")
        }

        // 生成结果卡片
        appState.generatedCode?.let { code ->
            if (code.error == null && code.code.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "激活码",
                            style = MiuixTheme.textStyles.subtitle,
                        )

                        Text(
                            text = code.code,
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.primary,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "机器码: ${code.machineCode}",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )

                        Text(
                            text = "时长: ${HistoryManager.formatDuration(code.durationMs)}",
                            style = MiuixTheme.textStyles.footnote1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(
                                text = "复制激活码",
                                onClick = {
                                    val clipboard =
                                        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("激活码", code.code)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT)
                                        .show()
                                },
                            )
                        }

                        val dateFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        Text(
                            text = "生成时间: ${dateFormat.format(Date(code.createdAt))}",
                            style = MiuixTheme.textStyles.footnote2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        )
                    }
                }
            }
        }

        // 错误日志
        if (appState.errorLog.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "日志",
                        style = MiuixTheme.textStyles.subtitle,
                        color = Color(0xFFD32F2F),
                    )
                    Text(
                        text = appState.errorLog,
                        style = MiuixTheme.textStyles.body2,
                        color = Color(0xFFD32F2F),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
