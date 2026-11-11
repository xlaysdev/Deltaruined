package com.xlaysdev.deltaruined

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Hand-authored 16x16 pixel-art sprites stored as compact character maps.
 * '.' is transparent; all other characters use the selected palette.
 */
object PixelSprites {
    val bosses = listOf(
        listOf(
            "......PPPP......",
            "....PPPPPPPP....",
            "...PPPPPPPPPP...",
            "..PPPPPPPPPPPP..",
            ".PPPPPPPPPPPPPP.",
            "PPPPPPPPPPPPPPPP",
            "PPPP..PPPP..PPPP",
            "PPP...PPPP...PPP",
            "PP....SSSS....PP",
            "P....SSSSSS....P",
            ".....SSSSSS.....",
            "....SSSSSSSS....",
            "...SS..SS..SS...",
            "..SS...SS...SS..",
            ".RR...RRRR...RR.",
            "..R...R..R...R.."
        ),
        listOf(
            ".......KK.......",
            "......KKKK......",
            ".....KKKKKK.....",
            "....KKWWKKKK....",
            "...KKKKKKKKKK...",
            "..KKKKKKKKKKKK..",
            ".KKKKKKKKKKKKKK.",
            "KKKKKKKKKKKKKKKK",
            "KKK..KKKKKK..KKK",
            "KK...KKKKKK...KK",
            ".....KKKKKK.....",
            ".....KKKKKK.....",
            "....KKKKKKKK....",
            "...KK..KK..KK...",
            "..KK...KK...KK..",
            ".KK....KK....KK."
        ),
        listOf(
            "......RRRR......",
            ".....RRRRRR.....",
            "....RGRGRGRR....",
            "...RRRRRRRRRR...",
            "..RRRRRRRRRRRR..",
            ".RRRRRRRRRRRRRR.",
            "RRRRRRRRRRRRRRRR",
            "RRR..RRRRRR..RRR",
            "RR...SSSSSS...RR",
            "R....SSSSSS....R",
            ".....SSSSSS.....",
            "....SSSSSSSS....",
            "...SS..SS..SS...",
            "..SS...SS...SS..",
            ".RR...RRRR...RR.",
            "..R...R..R...R.."
        ),
        listOf(
            "......GGGG......",
            "....GGGGGGGG....",
            "...GGGGGGGGGG...",
            "..GGGGGGGGGGGG..",
            ".GGGGGGGGGGGGGG.",
            "GGGGGGGGGGGGGGGG",
            "GGG..GGGGGG..GGG",
            "GG...GGGGGG...GG",
            ".....WW..WW.....",
            ".....WW..WW.....",
            "....WWWWWWWW....",
            "...WW..WW..WW...",
            "..WW...WW...WW..",
            ".GG....GG....GG.",
            "..G....GG....G..",
            ".......GG......."
        ),
    )

    val soul = listOf(
        ".......RR..RR...",
        ".....RRRRRRRR...",
        "....RRRRRRRRRR..",
        "..RRRRRRRRRRRR..",
        ".RRRRRRRRRRRRRR.",
        ".RRRRRRRRRRRRRR.",
        ".RRRRRRRRRRRRRR.",
        "..RRRRRRRRRRRR..",
        "....RRRRRRRRRR..",
        ".....RRRRRRRR...",
        "........RRRR....",
        "........RRRR....",
        "..........RR....",
        "...........R....",
        "................",
        "................"
    )

    val palettes = listOf(
        mapOf('P' to Color(0xFF9B55D4), 'S' to Color(0xFFFFD6A0), 'R' to Color(0xFFE84A62)),
        mapOf('K' to Color(0xFF8D9BB8), 'W' to Color(0xFFE8EEFF)),
        mapOf('R' to Color(0xFF9E2638), 'G' to Color(0xFFE5B83D), 'S' to Color(0xFFE7B98D)),
        mapOf('G' to Color(0xFFFFD34E), 'W' to Color(0xFFFFFFFF))
    )

    fun DrawScope.drawSprite(rows: List<String>, center: Offset, pixel: Float, palette: Map<Char, Color>) {
        val left = center.x - rows.first().length * pixel / 2f
        val top = center.y - rows.size * pixel / 2f
        rows.forEachIndexed { y, row ->
            row.forEachIndexed { x, symbol ->
                val color = palette[symbol] ?: return@forEachIndexed
                drawRect(color, Offset(left + x * pixel, top + y * pixel), Size(pixel, pixel))
            }
        }
    }
}
