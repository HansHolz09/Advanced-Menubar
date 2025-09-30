
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.hansholz.advancedmenubar.MenubarLanguage

@Composable
fun App(
    language: MutableState<MenubarLanguage?>,
    clickedItems: List<String>,
    customMenus: SnapshotStateList<Int>,
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
                        }
                    ) {
                        Text("Add new Custom Menu")
                    }
                    Spacer(Modifier.width(15.dp))
                    Button(
                        onClick = {
                            customMenus.removeLast()
                        },
                        enabled = customMenus.isNotEmpty()
                    ) {
                        Text("Remove last Custom Menu")
                    }
                }
            }
            item {
                OutlinedTextField(
                    state = textFieldState,
                    modifier = Modifier.padding(top = 15.dp),
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
                        value = language.value?.displayName ?: "Default",
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
                                text = { Text(entry.displayName) },
                                onClick = {
                                    language.value = entry
                                    I18n.switchTo(entry.code)
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