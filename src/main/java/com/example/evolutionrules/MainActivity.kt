package com.example.evolutionrules

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.evolutionrules.data.PropertyEntry
import com.example.evolutionrules.data.PropertyRepository
import com.example.evolutionrules.ui.theme.EvolutionRulesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EvolutionRulesTheme {
                EvolutionRulesApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun EvolutionRulesApp() {
    var selectedEntry by remember { mutableStateOf<PropertyEntry?>(null) }

    val entries = rememberEntries()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_home),
                        contentDescription = "Свойства"
                    )
                },
                label = { Text("Свойства") },
                selected = true,
                onClick = { selectedEntry = null }
            )
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val selected = selectedEntry
            if (selected != null) {
                DetailScreen(
                    entry = selected,
                    onBack = { selectedEntry = null },
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                HomeScreen(
                    entries = entries,
                    onEntryClick = { selectedEntry = it },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun rememberEntries(): List<PropertyEntry> {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember {
        PropertyRepository.loadEntries(context)
    }
}
