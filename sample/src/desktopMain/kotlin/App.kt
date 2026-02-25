
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.hansholz.advancedmenubar.MenubarLanguage
import org.jetbrains.skiko.hostOs
import java.util.*

@Composable
fun App(
    language: MutableState<MenubarLanguage?>,
    clickedItems: List<String>,
    customMenus: SnapshotStateList<Int>,
    showDefaultMenu: MutableState<Boolean>,
    checkboxItem1: MutableState<Boolean>,
    checkboxItem2: MutableState<Boolean>,
    checkboxItem3: MutableState<Boolean>,
    textFieldState: TextFieldState,
    onNewWindow: () -> Unit,
) {
    MaterialTheme {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(30.dp)
        ) {
            item {
                Button(
                    onClick = { onNewWindow() }
                ) {
                    Text("New Window")
                }
            }
            item {
                Row(Modifier.padding(top = 15.dp)) {
                    Button(
                        onClick = {
                            customMenus += customMenus.size + 1
                        },
                        enabled = !showDefaultMenu.value
                    ) {
                        Text("Add new Custom Menu")
                    }
                    Spacer(Modifier.width(15.dp))
                    Button(
                        onClick = {
                            customMenus.removeLast()
                        },
                        enabled = customMenus.isNotEmpty() && !showDefaultMenu.value
                    ) {
                        Text("Remove last Custom Menu")
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.padding(top = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = showDefaultMenu.value,
                        onCheckedChange = { showDefaultMenu.value = it },
                        enabled = hostOs.isMacOS
                    )
                    Spacer(Modifier.width(10.dp))
                    Text("Show default Menubar")
                }
            }
            item {
                OutlinedTextField(
                    state = textFieldState,
                    modifier = Modifier.padding(top = 15.dp),
                    enabled = !showDefaultMenu.value,
                    label = { Text("Test the Edit-Menu here") }
                )
            }
            item {
                var expanded by remember { mutableStateOf(false) }
                @OptIn(ExperimentalMaterial3Api::class)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.padding(top = 15.dp)
                ) {
                    OutlinedTextField(
                        value = language.value?.tag?.let { Locale(it).displayName } ?: "Default",
                        onValueChange = {},
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        readOnly = true,
                        label = { Text("Menubar Language") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        MenubarLanguage.entries.forEach { entry ->
                            DropdownMenuItem(
                                text = { Text("${Locale(entry.tag).displayName} (${entry.tag})") },
                                onClick = {
                                    language.value = entry
                                    I18n.switchTo(entry.tag)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
            stickyHeader {
                Box(Modifier.background(Color.White)) {
                    Text(
                        text = "Options",
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        textAlign = TextAlign.Center,
                        style = typography.titleLarge
                    )
                }
            }
            item {
                Text("Checkbox Item 1: ${checkboxItem1.value}")
                Text("Checkbox Item 2: ${checkboxItem2.value}")
                Text("Checkbox Item 3: ${checkboxItem3.value}")
            }
            if (clickedItems.isNotEmpty()) {
                stickyHeader {
                    Box(Modifier.background(Color.White)) {
                        Text(
                            text = "Clicked Items",
                            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                            textAlign = TextAlign.Center,
                            style = typography.titleLarge
                        )
                    }
                }
            }
            items(clickedItems) {
                Text("Clicked on $it")
            }
        }
    }
}