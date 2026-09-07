package com.example.evolutionrules

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.evolutionrules.data.PropertyEntry

@Composable
fun DetailScreen(
    entry: PropertyEntry,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        }

        Text(
            text = entry.name,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        val (body, inlineContent) = buildDescriptionAnnotatedString(entry.description)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            inlineContent = inlineContent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

private class Segment(
    val text: String? = null,
    val token: String? = null,
)

// Токены фишек — меняются всегда, независимо от контекста.
private val ALWAYS_TOKENS = listOf("КК", "СК", "ЖК")

// Токен животного — заменяется только если после него идёт разделитель или конец строки.
private const val ANIMAL_TOKEN = "Ж"

private val ANIMAL_BOUNDARY = charArrayOf(' ', '—', '-', '.', ',', ';', ':', '«', '»', '(', ')', '/')

private fun spaceOrEnd(s: String, index: Int): Boolean {
    if (index >= s.length) return true
    return ANIMAL_BOUNDARY.contains(s[index])
}


@Composable
fun buildDescriptionAnnotatedString(
    raw: String,
): Pair<AnnotatedString, Map<String, androidx.compose.foundation.text.InlineTextContent>> {
    val placeholderSize = 16.sp
    val imageSize = 16.dp

    fun inline(resId: Int, desc: String) = androidx.compose.foundation.text.InlineTextContent(
        Placeholder(
            width = placeholderSize,
            height = placeholderSize,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Image(
            painterResource(resId),
            contentDescription = desc,
            modifier = Modifier.size(imageSize)
        )
    }

    val inlineMap = mapOf(
        "Ж" to inline(R.drawable.ic_animal, "животное"),
        "КК" to inline(R.drawable.ic_food_red, "красная фишка"),
        "СК" to inline(R.drawable.ic_food_blue, "синяя фишка"),
        "ЖК" to inline(R.drawable.ic_food_yellow, "жёлтая фишка"),
    )

    val segments = parseSegments(raw)

    val annotated = buildAnnotatedString {
        segments.forEach { seg ->
            if (seg.token != null) {
                appendInlineContent(seg.token, seg.token)
            } else if (seg.text != null) {
                append(seg.text)
            }
        }
    }
    return annotated to inlineMap
}

private fun parseSegments(text: String): List<Segment> {
    val result = mutableListOf<Segment>()
    var i = 0
    while (i < text.length) {
        var bestStart = -1
        var bestLen = 0
        var bestToken: String? = null

        // Безусловные токены (фишки) — меняются всегда.
        for (token in ALWAYS_TOKENS) {
            val at = text.indexOf(token, i)
            if (at != -1 && (bestStart == -1 || at < bestStart)) {
                bestStart = at
                bestLen = token.length
                bestToken = token
            }
        }

        // Животное: «ЖЖ» или «Ж» — только если после них пробел/разделитель/конец.
        val pairStart = text.indexOf("ЖЖ", i)
        val singleStart = text.indexOf(ANIMAL_TOKEN, i)
        if (pairStart != -1 && (bestStart == -1 || pairStart <= bestStart) && spaceOrEnd(text, pairStart + 2)) {
            bestStart = pairStart
            bestLen = 2
            bestToken = ANIMAL_TOKEN
        } else if (singleStart != -1 && (bestStart == -1 || singleStart < bestStart) && spaceOrEnd(text, singleStart + 1)) {
            bestStart = singleStart
            bestLen = 1
            bestToken = ANIMAL_TOKEN
        }

        if (bestToken == null) {
            val rest = text.substring(i)
            if (rest.isNotEmpty()) result += Segment(text = rest)
            break
        }
        if (bestStart > i) result += Segment(text = text.substring(i, bestStart))
        result += Segment(token = bestToken)
        i = bestStart + bestLen
    }
    return result
}
