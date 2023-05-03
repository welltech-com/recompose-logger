package com.welltech.recomposition_logger_sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.welltech.recomposition_logger_annotations.DisableLogs
import com.welltech.recomposition_logger_annotations.LogArgument
import com.welltech.recomposition_logger_annotations.highlight.debugHighlightOptions
import com.welltech.recomposition_logger_sample.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        debugHighlightOptions = debugHighlightOptions.copy(enabled = true)
        binding.root.setContent { Content() }
    }

    @Composable
    @DisableLogs
    private fun Content() {
        val colors = /*if (isSystemInDarkTheme()) darkColors() else*/ lightColors()
        MaterialTheme(colors = colors) {
            Box(modifier = Modifier.padding(2.dp)) {
                var itemCount by remember {
                    mutableStateOf(5)
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    (0..itemCount).forEach {
                        // set random string for force the Item to recompose
                        Item(
                            id = it,
                            string = randomString(),
                        )
                    }
                }
                Row(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(
                        onClick = { itemCount += 1 },
                        content = { Text(text = "Add") },
                    )

                    Button(
                        onClick = { debugHighlightOptions = debugHighlightOptions.copy(enabled = !debugHighlightOptions.enabled) },
                        content = { Text(text = "Change highlighting") },
                    )
                }
            }
        }
    }

    private fun randomString(): String {
        return if (Random.Default.nextBoolean()) {
            "lorem"
        } else {
            "lorem ipsum"
        }
    }

    @Composable
    private fun Item(
        @LogArgument id: Int,
        string: String,
        modifier: Modifier = Modifier,
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text(
                modifier = Modifier
                    .padding(3.dp),
                text = string,
                color = MaterialTheme.colors.onSurface,
            )
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                modifier = Modifier
                    .padding(3.dp),
                text = "ipsum",
                color = MaterialTheme.colors.onSurface,
            )
        }
    }
}
