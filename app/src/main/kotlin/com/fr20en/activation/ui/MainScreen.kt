package com.fr20en.activation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.fr20en.activation.model.AppState
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Notes

@Composable
fun MainScreen(
    appState: AppState,
    onAppStateChange: (AppState) -> Unit,
) {
    var currentTab by remember { mutableIntStateOf(0) }

    val navigationItems = remember {
        listOf(
            NavigationItem("生成", MiuixIcons.Add),
            NavigationItem("历史", MiuixIcons.Notes),
        )
    }

    val topBarTitles = listOf("天一科技 · 获取激活码", "历史记录")

    Scaffold(
        topBar = {
            SmallTopAppBar(title = topBarTitles[currentTab])
        },
        bottomBar = {
            NavigationBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = currentTab == index,
                        onClick = { currentTab = index },
                        icon = item.icon,
                        label = item.label,
                    )
                }
            }
        },
    ) { padding ->
        when (currentTab) {
            0 -> GeneratePage(
                appState = appState,
                onAppStateChange = onAppStateChange,
                modifier = Modifier.padding(padding),
            )
            1 -> HistoryPage(modifier = Modifier.padding(padding))
        }
    }
}