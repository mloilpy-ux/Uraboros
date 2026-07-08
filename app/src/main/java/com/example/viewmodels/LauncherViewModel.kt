package com.example.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication()

    // Screen Lock State
    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    // Wallpaper theme State
    private val _wallpaperTheme = MutableStateFlow("Sophisticated")
    val wallpaperTheme: StateFlow<String> = _wallpaperTheme.asStateFlow()

    // System Control States
    val isWifiEnabled = MutableStateFlow(true)
    val isBluetoothEnabled = MutableStateFlow(true)
    val isDoNotDisturb = MutableStateFlow(false)
    val volume = MutableStateFlow(0.75f)
    val brightness = MutableStateFlow(0.80f)

    // Opened Dialogs / Top Menus States
    val isLaunchpadOpen = MutableStateFlow(false)
    val isControlCenterOpen = MutableStateFlow(false)
    val isCalendarOpen = MutableStateFlow(false)
    val isAppleMenuOpen = MutableStateFlow(false)

    // Window Management
    val activeWindows = mutableStateListOf<WindowItem>()
    private val _focusedWindowType = MutableStateFlow<WindowType?>(null)
    val focusedWindowType: StateFlow<WindowType?> = _focusedWindowType.asStateFlow()

    private var maxZIndex = 1f

    // Apps list
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Notes App State
    private val _notes = MutableStateFlow<List<String>>(listOf(
        "Welcome to macOS Notes!\n\nThis is a fully-functional note-taking application designed for macOS on Android.\n\n- Click '+' to write a new note.\n- Keep track of ideas instantly.\n- Enjoy the retro paper theme!",
        "Android Launcher Ideas\n\n- Enable drag-and-drop windows.\n- Query real packages via intent.\n- Add a Terminal Shell command emulator.\n- Make Safari render real URLs in a custom webview."
    ))
    val notes: StateFlow<List<String>> = _notes.asStateFlow()

    private val _activeNoteIndex = MutableStateFlow(0)
    val activeNoteIndex: StateFlow<Int> = _activeNoteIndex.asStateFlow()

    // Terminal State
    private val _terminalHistory = MutableStateFlow<List<TerminalLine>>(listOf(
        TerminalLine("Last login: " + SimpleDateFormat("EEE MMM d HH:mm:ss", Locale.getDefault()).format(Date()) + " on ttys001"),
        TerminalLine("macOS Sequoia Shell (zsh) initialized successfully."),
        TerminalLine("Type 'neofetch' to see system specs or 'help' for commands."),
        TerminalLine("")
    ))
    val terminalHistory: StateFlow<List<TerminalLine>> = _terminalHistory.asStateFlow()

    // Simulated File System (Finder / Terminal)
    private val _currentPath = MutableStateFlow("/User/visitor")
    val currentPath: StateFlow<String> = _currentPath.asStateFlow()

    val simulatedFiles = mapOf(
        "/User/visitor" to listOf(
            FinderFile("Documents", isDirectory = true, iconRes = "folder"),
            FinderFile("Downloads", isDirectory = true, iconRes = "folder"),
            FinderFile("Desktop", isDirectory = true, iconRes = "folder"),
            FinderFile("Welcome.txt", isDirectory = false, iconRes = "text", content = "Welcome to macOS Launcher on Android!\n\nThis is an incredible launcher app mirroring a real MacBook experience. Tap items on the bottom dock, explore your real Android app list inside the Launchpad, or browse the web in real-time inside the Safari browser window!")
        ),
        "/User/visitor/Documents" to listOf(
            FinderFile("Notes_backup.txt", isDirectory = false, iconRes = "text", content = "Backup: Remember to review the Jetpack Compose guidelines for pixel-perfect rendering of macOS elements."),
            FinderFile("Design_Concept.txt", isDirectory = false, iconRes = "text", content = "macOS Sequoia theme on Android:\n1. Top translucent menu bar.\n2. Bottom curved glass dock.\n3. Floating window manager.\n4. Real web-rendering browser.")
        ),
        "/User/visitor/Downloads" to listOf(
            FinderFile("Xcode_16_Beta.dmg", isDirectory = false, iconRes = "dmg", content = "Binary file simulated. (14.2 GB)"),
            FinderFile("Wallpaper_Mac.jpg", isDirectory = false, iconRes = "image", content = "Vibrant colors.")
        ),
        "/User/visitor/Desktop" to listOf(
            FinderFile("FinderHD", isDirectory = false, iconRes = "mac_hd", content = "Local Drive capacity: 512 GB (Simulated)")
        )
    )

    init {
        loadInstalledApps()
        // Save initial values in Shared Preferences if desired
        val sharedPref = context.getSharedPreferences("macos_settings", Context.MODE_PRIVATE)
        val savedTheme = sharedPref.getString("wallpaper_theme", "Sophisticated") ?: "Sophisticated"
        _wallpaperTheme.value = savedTheme
    }

    fun unlock() {
        _isLocked.value = false
    }

    fun lock() {
        _isLocked.value = true
        isAppleMenuOpen.value = false
        isControlCenterOpen.value = false
        isCalendarOpen.value = false
    }

    fun setWallpaperTheme(theme: String) {
        _wallpaperTheme.value = theme
        val sharedPref = context.getSharedPreferences("macos_settings", Context.MODE_PRIVATE)
        sharedPref.edit().putString("wallpaper_theme", theme).apply()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = mutableListOf<AppInfo>()
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val myPackageName = context.packageName

            for (resolveInfo in resolveInfos) {
                val label = resolveInfo.loadLabel(pm).toString()
                val packageName = resolveInfo.activityInfo.packageName
                if (packageName == myPackageName) continue // Hide self from launcher grid

                val icon = resolveInfo.loadIcon(pm)
                val launchIntent = pm.getLaunchIntentForPackage(packageName)
                apps.add(AppInfo(label, packageName, icon, launchIntent))
            }

            // Sort alphabetically
            apps.sortBy { it.label.lowercase() }
            _installedApps.value = apps
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun launchApp(app: AppInfo) {
        app.launchIntent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
            isLaunchpadOpen.value = false
        }
    }

    // --- Window Actions ---

    fun openWindow(type: WindowType) {
        // If window already exists, bring it to focus
        val existingIndex = activeWindows.indexOfFirst { it.type == type }
        if (existingIndex != -1) {
            val window = activeWindows[existingIndex]
            if (window.isMinimized) {
                activeWindows[existingIndex] = window.copy(isMinimized = false)
            }
            focusWindow(type)
        } else {
            // Create a window with dynamic placements so they don't stack perfectly on top
            val count = activeWindows.size
            val offset = (count * 25).dp
            val defaultTitle = when (type) {
                WindowType.SAFARI -> "Safari"
                WindowType.NOTES -> "Notes"
                WindowType.TERMINAL -> "Terminal (zsh)"
                WindowType.FINDER -> "Finder"
                WindowType.SETTINGS -> "System Settings"
                WindowType.ABOUT_MAC -> "About This Mac"
            }

            val defaultWidth = when (type) {
                WindowType.ABOUT_MAC -> 280.dp
                WindowType.SETTINGS -> 340.dp
                WindowType.SAFARI -> 330.dp
                else -> 310.dp
            }

            val defaultHeight = when (type) {
                WindowType.ABOUT_MAC -> 240.dp
                WindowType.SETTINGS -> 440.dp
                WindowType.SAFARI -> 460.dp
                else -> 400.dp
            }

            maxZIndex += 1f
            val newWindow = WindowItem(
                type = type,
                title = defaultTitle,
                x = 30.dp + offset,
                y = 80.dp + offset,
                width = defaultWidth,
                height = defaultHeight,
                zIndex = maxZIndex
            )
            activeWindows.add(newWindow)
            _focusedWindowType.value = type
        }
    }

    fun closeWindow(type: WindowType) {
        activeWindows.removeIf { it.type == type }
        if (_focusedWindowType.value == type) {
            _focusedWindowType.value = activeWindows.maxByOrNull { it.zIndex }?.type
        }
    }

    fun minimizeWindow(type: WindowType) {
        val index = activeWindows.indexOfFirst { it.type == type }
        if (index != -1) {
            activeWindows[index] = activeWindows[index].copy(isMinimized = true)
            if (_focusedWindowType.value == type) {
                _focusedWindowType.value = activeWindows.filter { !it.isMinimized }.maxByOrNull { it.zIndex }?.type
            }
        }
    }

    fun toggleMaximize(type: WindowType) {
        val index = activeWindows.indexOfFirst { it.type == type }
        if (index != -1) {
            val win = activeWindows[index]
            activeWindows[index] = win.copy(isMaximized = !win.isMaximized)
            focusWindow(type)
        }
    }

    fun focusWindow(type: WindowType) {
        val index = activeWindows.indexOfFirst { it.type == type }
        if (index != -1) {
            maxZIndex += 1f
            activeWindows[index] = activeWindows[index].copy(zIndex = maxZIndex, isMinimized = false)
            _focusedWindowType.value = type
        }
    }

    fun dragWindow(type: WindowType, dx: Dp, dy: Dp) {
        val index = activeWindows.indexOfFirst { it.type == type }
        if (index != -1) {
            val win = activeWindows[index]
            if (!win.isMaximized) {
                activeWindows[index] = win.copy(
                    x = win.x + dx,
                    y = win.y + dy
                )
            }
        }
    }

    fun resizeWindow(type: WindowType, dw: Dp, dh: Dp) {
        val index = activeWindows.indexOfFirst { it.type == type }
        if (index != -1) {
            val win = activeWindows[index]
            if (!win.isMaximized) {
                val newWidth = (win.width + dw).coerceAtLeast(180.dp)
                val newHeight = (win.height + dh).coerceAtLeast(180.dp)
                activeWindows[index] = win.copy(
                    width = newWidth,
                    height = newHeight
                )
            }
        }
    }

    // --- Notes App Actions ---

    fun selectNote(index: Int) {
        _activeNoteIndex.value = index
    }

    fun updateActiveNoteContent(content: String) {
        val currentList = _notes.value.toMutableList()
        if (currentList.isNotEmpty() && _activeNoteIndex.value in currentList.indices) {
            currentList[_activeNoteIndex.value] = content
            _notes.value = currentList
        }
    }

    fun createNote() {
        val currentList = _notes.value.toMutableList()
        currentList.add(0, "New Note\n\nType something here...")
        _notes.value = currentList
        _activeNoteIndex.value = 0
    }

    fun deleteActiveNote() {
        val currentList = _notes.value.toMutableList()
        if (currentList.isNotEmpty() && _activeNoteIndex.value in currentList.indices) {
            currentList.removeAt(_activeNoteIndex.value)
            _notes.value = if (currentList.isEmpty()) listOf("No Notes\n\nPress '+' to create a note.") else currentList
            _activeNoteIndex.value = 0
        }
    }

    // --- Terminal Shell logic ---

    fun executeTerminalCommand(cmd: String) {
        val trimmed = cmd.trim()
        val history = _terminalHistory.value.toMutableList()
        history.add(TerminalLine("visitor@macbook-pro ~ % $trimmed", LineType.INPUT))

        val parts = trimmed.split(" ")
        val command = parts.getOrNull(0)?.lowercase() ?: ""
        val arg = parts.getOrNull(1) ?: ""

        when (command) {
            "" -> {}
            "clear" -> {
                history.clear()
            }
            "help" -> {
                history.add(TerminalLine("Available shell commands:", LineType.SUCCESS))
                history.add(TerminalLine("  neofetch       Show stunning emulated MacBook specs"))
                history.add(TerminalLine("  ls             List current directories and files"))
                history.add(TerminalLine("  cat <file>     Read simulated text files"))
                history.add(TerminalLine("  cd <dir>       Change directories"))
                history.add(TerminalLine("  pwd            Print current path"))
                history.add(TerminalLine("  date           Get standard UTC time/date"))
                history.add(TerminalLine("  matrix         Animate falling digital matrix code rain"))
                history.add(TerminalLine("  ping <host>    Ping mock connection delay"))
                history.add(TerminalLine("  clear          Reset terminal screen logs"))
            }
            "neofetch" -> {
                history.add(TerminalLine("                   ,xX               visitor@macbook-pro", LineType.SUCCESS))
                history.add(TerminalLine("                .dXXXX               -------------------", LineType.SUCCESS))
                history.add(TerminalLine("               dXXXXX'               OS: macOS Sequoia 15.1 (Android Launcher)", LineType.OUTPUT))
                history.add(TerminalLine("              :XXXXX'                Host: MacBook Pro Emulated", LineType.OUTPUT))
                history.add(TerminalLine("             ,XXXXX'                 Kernel: Linux (Android Framework)", LineType.OUTPUT))
                history.add(TerminalLine("            ;XXXXX'                  Uptime: 2 hours, 14 mins", LineType.OUTPUT))
                history.add(TerminalLine("           ;XXXXX(                   Shell: zsh 5.9", LineType.OUTPUT))
                history.add(TerminalLine("          ,XXXXXX                    Theme: Aqua / Graphite", LineType.OUTPUT))
                history.add(TerminalLine("        .dXXXXXXX;                   Terminal: macOS-Term v2.1", LineType.OUTPUT))
                history.add(TerminalLine("     .dXXXXXXXXXX                    CPU: Emulated Apple M3 Max (Core 16)", LineType.OUTPUT))
                history.add(TerminalLine("   .dXXXXXXXXXXXX;                   Memory: 16 GB Unified Memory", LineType.OUTPUT))
                history.add(TerminalLine("  dXXXXXXXXXXXXXX                    GPU: 40-Core Apple GPU", LineType.OUTPUT))
                history.add(TerminalLine("  XXXXXXXXXXXXXX'                    Disk: 512 GB SSD (340 GB available)", LineType.OUTPUT))
                history.add(TerminalLine("  `XXXXXXXXXXXX'                     Display: Liquid Retina XDR 120Hz", LineType.OUTPUT))
                history.add(TerminalLine("    `XXXXXXXX'                       ", LineType.OUTPUT))
                history.add(TerminalLine("       `XX'                          ", LineType.OUTPUT))
            }
            "pwd" -> {
                history.add(TerminalLine(_currentPath.value))
            }
            "ls" -> {
                val files = simulatedFiles[_currentPath.value] ?: emptyList()
                if (files.isEmpty()) {
                    history.add(TerminalLine("(directory is empty)"))
                } else {
                    val formatted = files.joinToString("   ") { 
                        if (it.isDirectory) "${it.name}/" else it.name 
                    }
                    history.add(TerminalLine(formatted, LineType.SUCCESS))
                }
            }
            "cd" -> {
                if (arg.isEmpty() || arg == "~") {
                    _currentPath.value = "/User/visitor"
                    history.add(TerminalLine("Moved to home: /User/visitor"))
                } else {
                    val fullTarget = if (arg.startsWith("/")) arg else {
                        val base = _currentPath.value
                        if (base == "/") "/$arg" else "$base/$arg"
                    }
                    val normalized = fullTarget.replace("//", "/")
                    if (simulatedFiles.containsKey(normalized)) {
                        _currentPath.value = normalized
                        history.add(TerminalLine("Directory changed: $normalized"))
                    } else {
                        history.add(TerminalLine("cd: no such file or directory: $arg", LineType.ERROR))
                    }
                }
            }
            "cat" -> {
                if (arg.isEmpty()) {
                    history.add(TerminalLine("cat: missing file name", LineType.ERROR))
                } else {
                    val files = simulatedFiles[_currentPath.value] ?: emptyList()
                    val match = files.find { it.name.lowercase() == arg.lowercase() && !it.isDirectory }
                    if (match != null) {
                        match.content.split("\n").forEach {
                            history.add(TerminalLine(it))
                        }
                    } else {
                        history.add(TerminalLine("cat: $arg: No such file or is directory", LineType.ERROR))
                    }
                }
            }
            "date" -> {
                history.add(TerminalLine(Date().toString()))
            }
            "matrix" -> {
                history.add(TerminalLine("Loading Matrix rain shell... (Press enter to exit simulation)", LineType.SUCCESS))
                history.add(TerminalLine("0 1 0 1 1 0 1 0 1 0 1 1 0 0 1 1 0 1", LineType.SUCCESS))
                history.add(TerminalLine("1 0 1 0 0 1 1 0 1 0 0 1 0 1 1 0 0 1", LineType.SUCCESS))
                history.add(TerminalLine("0 1 1 0 1 1 0 1 0 1 1 0 0 1 1 1 0 0", LineType.SUCCESS))
                history.add(TerminalLine("1 0 0 1 0 0 1 1 1 0 1 0 1 0 0 1 1 0", LineType.SUCCESS))
                history.add(TerminalLine("MATRIX SIMULATION ACTIVE.", LineType.SUCCESS))
            }
            "ping" -> {
                if (arg.isEmpty()) {
                    history.add(TerminalLine("ping: host required", LineType.ERROR))
                } else {
                    history.add(TerminalLine("PING $arg (142.250.190.46): 56 data bytes"))
                    history.add(TerminalLine("64 bytes from 142.250.190.46: icmp_seq=0 ttl=116 time=14.2 ms"))
                    history.add(TerminalLine("64 bytes from 142.250.190.46: icmp_seq=1 ttl=116 time=16.8 ms"))
                    history.add(TerminalLine("64 bytes from 142.250.190.46: icmp_seq=2 ttl=116 time=15.1 ms"))
                    history.add(TerminalLine("--- $arg ping statistics ---"))
                    history.add(TerminalLine("3 packets transmitted, 3 packets received, 0.0% packet loss"))
                    history.add(TerminalLine("round-trip min/avg/max/stddev = 14.2/15.3/16.8/1.07 ms"))
                }
            }
            else -> {
                history.add(TerminalLine("zsh: command not found: $command", LineType.ERROR))
            }
        }
        _terminalHistory.value = history
    }
}
