package com.logger.xlog.sample.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.logger.xlog.LogLevel
import com.logger.xlog.Logger
import com.logger.xlog.XLog
import com.logger.xlog.flattener.Flattener
import com.logger.xlog.flattener.PatternFlattener
import com.logger.xlog.formatter.message.json.DefaultJsonFormatter
import com.logger.xlog.formatter.message.xml.DefaultXmlFormatter
import com.logger.xlog.printer.AndroidPrinter
import com.logger.xlog.printer.Printer
import com.logger.xlog.sample.App
import com.logger.xlog.sample.theme.XLogTheme
import com.logger.xlog.sample.ui.MainActivity.Companion.LEVELS
import com.logger.xlog.sample.ui.MainActivity.Companion.STACK_TRACE_DEPTHS


class MainActivity : ComponentActivity() {
    companion object {
        private const val MESSAGE = "Simple message"
        val LEVELS =
            listOf(
                LogLevel.VERBOSE,
                LogLevel.DEBUG,
                LogLevel.INFO,
                LogLevel.WARN,
                LogLevel.ERROR,
                LogLevel.ASSERT
            )
        val STACK_TRACE_DEPTHS = listOf(0, 1, 2, 3, 4, 5)
    }

    // 权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            if (shouldShowRequestPermissionRationale()) {
                showPermissionDialog.value = true
            } else {
                showSettingsDialog.value = true
            }
        }
    }

    // 状态管理
    private val showPermissionDialog = mutableStateOf(false)
    private val showSettingsDialog = mutableStateOf(false)
    private val showTagDialog = mutableStateOf(false)
    private var hasPermission by mutableStateOf(false)

    // Compose 打印机
    private lateinit var composePrinter: ComposePrinter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化打印机
        composePrinter = ComposePrinter()

        // 检查权限
        hasPermission = hasPermission()
        if (!hasPermission && !shouldShowRequestPermissionRationale()) {
            requestPermission()
        }

        setContent {
            XLogTheme {
                MainScreen(
                    hasPermission = hasPermission,
                    onPrintClick = { config -> printLog(config) },
                    onTagChangeClick = { showTagDialog.value = true },
                    logs = composePrinter.logs
                )

                // 权限请求对话框
                if (showPermissionDialog.value) {
                    PermissionDialog(onDismiss = { showPermissionDialog.value = false },
                        onAllow = { requestPermission() })
                }

                // 设置跳转对话框
                if (showSettingsDialog.value) {
                    SettingsDialog(onDismiss = { showSettingsDialog.value = false },
                        onSettings = { openAppSettings() })
                }

                // 标签编辑对话框
                if (showTagDialog.value) {
                    TagDialog(currentTag = tagState.value,
                        onDismiss = { showTagDialog.value = false },
                        onConfirm = { newTag -> tagState.value = newTag })
                }
            }
        }

        // 打印欢迎消息
        XLog.printers(composePrinter).i("XLog is ready.\nPrint your log now!")

        printAndroidLog()
    }

    val jsonStr = """
        {"query":"Pizza","locations":[94043,90210]}
    """.trimIndent()

    val xmlStr = """
        <?xml version="1.0" encoding="UTF-8"?> <note><to>George</to><from>John</from><heading>Reminder</heading><body>Don't forget the meeting!</body></note>
    """.trimIndent()

    private fun printAndroidLog() {

        val logger = Logger.Builder().logLevel(LogLevel.ALL)
            .enableBorder().enableThreadInfo()
            .enableStackTrace(5).jsonFormatter(DefaultJsonFormatter())
            .xmlFormatter(DefaultXmlFormatter()).build()

        logger.apply {
            v(message)
            XLog.tag("test").v(message)
            d(message)
            XLog.tag("test").d(message)
            i(message)
            XLog.tag("test").i(message)
            w(message)
            XLog.tag("test").w(message)
            e(message)
            XLog.tag("test").e(message)
            wtf(message)
            XLog.tag("test").wtf(message)
            json(jsonStr, LogLevel.ERROR)
            XLog.tag("test").json(jsonStr, LogLevel.ERROR)
            xml(xmlStr, LogLevel.ERROR)
            XLog.tag("test").xml(xmlStr, LogLevel.ERROR)
        }

    }

    private val message: String
        get() = "This is a simple message!  The time is ${SystemClock.elapsedRealtime()}"

    override fun onResume() {
        super.onResume()
        val message = if (hasPermission) {
            "Permission granted.\nLog to file."
        } else {
            "Permission not granted.\nCan not log to file."
        }
        XLog.printers(composePrinter).i(message)
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    private fun shouldShowRequestPermissionRationale(): Boolean {
        return shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun openAppSettings() {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
            startActivity(this)
        }
    }

    private fun printLog(config: LogConfig) {
        Logger.Builder().apply {
            if (config.tag.isNotBlank()) {
                tag(config.tag)
            }

            if (config.showThreadInfo) {
                enableThreadInfo()
            } else {
                disableThreadInfo()
            }

            if (config.showStackTrace) {
                enableStackTrace(config.stackTraceDepth)
            } else {
                disableStackTrace()
            }

            if (config.showBorder) {
                enableBorder()
            } else {
                disableBorder()
            }

            // 添加打印机
            printers(
                composePrinter,
                AndroidPrinter(),
                *if (hasPermission) arrayOf(App.globalFilePrinter) else emptyArray()
            )
        }.build().let { logger ->
            when (config.logLevel) {
                LogLevel.VERBOSE -> logger.v(message)
                LogLevel.DEBUG -> logger.d(message)
                LogLevel.INFO -> logger.i(message)
                LogLevel.WARN -> logger.w(message)
                LogLevel.ERROR -> logger.e(message)
                LogLevel.ASSERT -> logger.wtf(message)
            }

            XLog.log(config.logLevel, message)
            XLog.tag("test").log(config.logLevel, message)
        }
    }
}

// 数据类保存日志配置
data class LogConfig(
    val tag: String,
    val logLevel: Int,
    val showThreadInfo: Boolean,
    val showStackTrace: Boolean,
    val stackTraceDepth: Int,
    val showBorder: Boolean
)

// Compose 打印机实现
class ComposePrinter : Printer {
    val logs = mutableStateListOf<LogItem>()

    override fun println(logLevel: Int, tag: String, msg: String) {
        // "${System.currentTimeMillis()} $logLevel [${tag}] $msg"
        logs.add(LogItem(System.currentTimeMillis(), logLevel, tag, msg))
        // 限制日志数量
        if (logs.size > 100) logs.removeAt(logs.size - 1)
    }
}

class LogItem(
    private var timeMillis: Long,
    var logLevel: Int,
    private var tag: String,
    private var msg: String
) {
    var label: String = ""
        get() {
            return flattener.flatten(timeMillis, logLevel, tag, msg).toString()
        }
        private set

    companion object {
        var flattener: Flattener = PatternFlattener("{d HH:mm:ss.SSS} {l}/{t}: {m}")
    }
}


// 状态管理
val tagState = mutableStateOf("XLog")

// 主界面
@SuppressLint("AutoboxingStateCreation")
@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    hasPermission: Boolean = false,
    onPrintClick: (LogConfig) -> Unit = {},
    onTagChangeClick: () -> Unit = {},
    logs: List<LogItem> = listOf()
) {
    // 状态管理
    var logLevel by remember { mutableIntStateOf(LogLevel.INFO) }
    var showThreadInfo by remember { mutableStateOf(true) }
    var showStackTrace by remember { mutableStateOf(true) }
    var stackTraceDepth by remember { mutableIntStateOf(2) }
    var showBorder by remember { mutableStateOf(true) }

    // 权限状态文本
    val permissionText = if (hasPermission) {
        "权限已授予 ✓ 可记录到文件"
    } else {
        "权限未授予 ✗ 无法记录到文件"
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("XLog 演示") }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            )
        )
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = {
                onPrintClick(
                    LogConfig(
                        tag = tagState.value,
                        logLevel = logLevel,
                        showThreadInfo = showThreadInfo,
                        showStackTrace = showStackTrace,
                        stackTraceDepth = stackTraceDepth,
                        showBorder = showBorder
                    )
                )
            },
            modifier = Modifier.padding(20.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Create, "打印日志")
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 权限状态
            Text(
                text = permissionText,
                color = if (hasPermission) Color.Green else Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 配置区域
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 标签部分
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("标签", fontWeight = FontWeight.Bold)
                        Text(text = tagState.value,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .clickable { onTagChangeClick() }
                                .padding(8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 日志级别选择
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("日志级别", fontWeight = FontWeight.Bold)
                        LevelDropdown(selectedLevel = logLevel, onLevelSelected = { logLevel = it })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 选项开关
                    OptionItem(text = "显示线程信息",
                        checked = showThreadInfo,
                        onCheckedChange = { showThreadInfo = it })

                    OptionItem(text = "显示堆栈跟踪",
                        checked = showStackTrace,
                        onCheckedChange = { showStackTrace = it })

                    // 堆栈跟踪深度
                    if (showStackTrace) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("堆栈跟踪深度", fontWeight = FontWeight.Bold)
                            DepthDropdown(selectedDepth = stackTraceDepth,
                                onDepthSelected = { stackTraceDepth = it })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OptionItem(text = "显示边框",
                        checked = showBorder,
                        onCheckedChange = { showBorder = it })
                }
            }

            // 日志列表
            Text(
                text = "日志内容",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LogList(logs = logs, showBorder = showBorder)
        }
    }
}

// 日志级别下拉菜单
@Composable
fun LevelDropdown(
    selectedLevel: Int, onLevelSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.width(150.dp)) {
        OutlinedButton(
            onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = when (selectedLevel) {
                    LogLevel.VERBOSE -> "VERBOSE"
                    LogLevel.DEBUG -> "DEBUG"
                    LogLevel.INFO -> "INFO"
                    LogLevel.WARN -> "WARN"
                    LogLevel.ERROR -> "ERROR"
                    LogLevel.ASSERT -> "ASSERT"
                    else -> "DEBUG"
                }
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            LEVELS.forEach { level ->
                DropdownMenuItem(text = {
                    Text(
                        when (level) {
                            LogLevel.VERBOSE -> "VERBOSE"
                            LogLevel.DEBUG -> "DEBUG"
                            LogLevel.INFO -> "INFO"
                            LogLevel.WARN -> "WARN"
                            LogLevel.ERROR -> "ERROR"
                            LogLevel.ASSERT -> "ASSERT"
                            else -> "DEBUG"
                        }
                    )
                }, onClick = {
                    onLevelSelected(level)
                    expanded = false
                })
            }
        }
    }
}

// 堆栈深度下拉菜单
@Composable
fun DepthDropdown(
    selectedDepth: Int, onDepthSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.width(100.dp)) {
        OutlinedButton(
            onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedDepth.toString())
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            STACK_TRACE_DEPTHS.forEach { depth ->
                DropdownMenuItem(text = { Text(depth.toString()) }, onClick = {
                    onDepthSelected(depth)
                    expanded = false
                })
            }
        }
    }
}

// 日志列表组件
@Composable
fun LogList(logs: List<LogItem>, showBorder: Boolean) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(900.dp)
            .background(Color.Black)
            .border(
                if (showBorder) 1.dp else 0.dp, MaterialTheme.colorScheme.outline
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            items(logs) { log ->
                LogEntryItem(log = log)
            }
        }

        // 自动滚动到底部
        LaunchedEffect(logs.size) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
}

// 日志条目组件
@Composable
fun LogEntryItem(log: LogItem) {
    val color = when (log.logLevel) {
        LogLevel.ERROR, LogLevel.ASSERT -> Color(0xFFFF5252)
        LogLevel.WARN -> Color(0xFFFFB74D)
        LogLevel.INFO -> Color(0xFF4FC3F7)
        LogLevel.DEBUG -> Color(0xFF81C784)
        else -> Color.White
    }

    Text(
        text = log.label ?: "",
        color = color,
        modifier = Modifier.padding(vertical = 4.dp),
        fontSize = 14.sp
    )
}

// 选项开关组件
@Composable
fun OptionItem(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onCheckedChange(!checked) }
        .padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked, onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Medium)
    }
}

// 权限请求对话框
@Composable
fun PermissionDialog(onDismiss: () -> Unit, onAllow: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("存储权限请求") },
        text = { Text("XLog 需要存储权限来记录日志到文件。请允许此权限以使用完整功能。") },
        confirmButton = {
            Button(onClick = onAllow) {
                Text("允许")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        })
}

// 设置跳转对话框
@Composable
fun SettingsDialog(onDismiss: () -> Unit, onSettings: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("需要存储权限") },
        text = { Text("您已永久拒绝存储权限。请前往应用设置手动启用权限。") },
        confirmButton = {
            Button(onClick = onSettings) {
                Text("前往设置")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        })
}

// 标签编辑对话框
@Composable
fun TagDialog(currentTag: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newTag by remember { mutableStateOf(currentTag) }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("更改标签") }, text = {
        Column {
            Text("输入新的日志标签:")
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newTag, onValueChange = { newTag = it }, singleLine = true
            )
        }
    }, confirmButton = {
        Button(
            onClick = {
                if (newTag.isNotBlank()) {
                    onConfirm(newTag)
                }
                onDismiss()
            }, enabled = newTag.isNotBlank()
        ) {
            Text("确定")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("取消")
        }
    })
}