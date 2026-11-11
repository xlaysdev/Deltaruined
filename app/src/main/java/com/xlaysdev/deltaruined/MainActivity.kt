package com.xlaysdev.deltaruined

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.random.Random

private val Ink = Color(0xFF07070D)
private val Panel = Color(0xFF11111D)
private val Gold = Color(0xFFFFD84A)
private val SoulRed = Color(0xFFFF334F)
private val White = Color(0xFFF5F2FF)
private val KnightSilver = Color(0xFFDCE4F5)

private data class Bullet(val x: Float, val y: Float, val vx: Float, val vy: Float, val radius: Float = 8f)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = 5894 or 1024 or 512
        setContent { MaterialTheme { RoaringKnightFight() } }
    }
}

@Composable
private fun RoaringKnightFight() {
    val knightFrames = listOf(
        ImageBitmap.imageResource(R.drawable.knight_idle_1),
        ImageBitmap.imageResource(R.drawable.knight_idle_2),
        ImageBitmap.imageResource(R.drawable.knight_idle_3),
        ImageBitmap.imageResource(R.drawable.knight_idle_4),
        ImageBitmap.imageResource(R.drawable.knight_idle_5)
    )
    var playerHp by remember { mutableIntStateOf(20) }
    var bossHp by remember { mutableIntStateOf(420) }
    var turn by remember { mutableIntStateOf(0) }
    var frame by remember { mutableIntStateOf(0) }
    var attackTicks by remember { mutableIntStateOf(0) }
    var invulnerable by remember { mutableIntStateOf(0) }
    var soul by remember { mutableStateOf(Offset(.5f, .76f)) }
    var bullets by remember { mutableStateOf(listOf<Bullet>()) }
    var mode by remember { mutableStateOf("battle") }
    var message by remember { mutableStateOf("The Roaring Knight blocks your path.") }
    val moveSoul: (Float, Float) -> Unit = { dx, dy ->
        if (mode == "battle" && turn % 2 == 1) {
            soul = Offset((soul.x + dx).coerceIn(.13f, .87f), (soul.y + dy).coerceIn(.53f, .91f))
        }
    }

    LaunchedEffect(mode, turn) {
        if (mode == "battle" && turn % 2 == 1) {
            attackTicks = 0
            while (mode == "battle" && turn % 2 == 1 && attackTicks < 180) {
                delay(32)
                frame++
                attackTicks++
                bullets = bullets.map { it.copy(x = it.x + it.vx, y = it.y + it.vy) }
                    .filter { it.x in -.08f..1.08f && it.y in .40f..1.02f }
                if (attackTicks % 17 == 0) {
                    val x = Random.nextFloat().coerceIn(.16f, .84f)
                    val targetX = soul.x
                    val targetY = soul.y
                    val dx = (targetX - x) * .018f
                    val dy = ((targetY - .48f) * .018f).coerceIn(.004f, .014f)
                    bullets = bullets + Bullet(x, .49f, dx, dy)
                }
                if (invulnerable > 0) invulnerable--
                else if (bullets.any { hypot((it.x - soul.x) * 1.55f, (it.y - soul.y) * 1.1f) < .035f }) {
                    playerHp = (playerHp - 1).coerceAtLeast(0)
                    invulnerable = 30
                    message = "The Knight's attack hit you!"
                    if (playerHp == 0) mode = "defeat"
                }
            }
            if (mode == "battle") {
                bullets = emptyList()
                turn = 0
                message = "The Knight pauses. Your turn!"
            }
        }
    }

    LaunchedEffect(mode, turn) {
        while (mode == "battle" && turn % 2 == 0) {
            delay(100)
            frame++
        }
    }

    Column(
        Modifier.fillMaxSize().background(Ink).padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("THE ROARING KNIGHT", color = KnightSilver, fontSize = 19.sp, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp)
                Text("THE END OF THE WORLD DRAWS NEAR", color = Color.Gray, fontSize = 9.sp, letterSpacing = 1.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("HP  $playerHp / 20", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("KNIGHT  $bossHp / 420", color = Gold, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Box(Modifier.weight(1f).fillMaxWidth().background(Panel, RoundedCornerShape(4.dp)).padding(6.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                detectDragGestures { change, drag ->
                    change.consume()
                    if (mode == "battle" && turn % 2 == 1) {
                        soul = Offset(
                            (soul.x + drag.x / size.width).coerceIn(.13f, .87f),
                            (soul.y + drag.y / size.height).coerceIn(.53f, .91f)
                        )
                    }
                }
            }) {
                val w = size.width
                val h = size.height
                drawRect(Color(0xFF080811), size = size)
                for (i in 1..11) drawLine(Color(0xFF202034), Offset(w * i / 12f, 0f), Offset(w * i / 12f, h), 1f)
                val knightSize = (w * .28f).coerceAtMost(h * .34f).toInt()
                val knight = knightFrames[(frame / 9) % knightFrames.size]
                drawImage(
                    image = knight,
                    dstOffset = IntOffset((w / 2f - knightSize / 2f).toInt(), (h * .02f).toInt()),
                    dstSize = IntSize(knightSize, knightSize)
                )
                drawRect(Color.White, topLeft = Offset(w * .12f, h * .43f), size = Size(w * .76f, h * .51f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                bullets.forEach { bullet ->
                    drawCircle(KnightSilver, bullet.radius, Offset(bullet.x * w, bullet.y * h))
                    drawCircle(Color.White, bullet.radius * .38f, Offset(bullet.x * w, bullet.y * h))
                }
                if (turn % 2 == 1 && mode == "battle") {
                    val p = Offset(soul.x * w, soul.y * h)
                    if (invulnerable % 4 < 2) {
                        drawCircle(SoulRed, 9f, p)
                        drawCircle(Color.White, 2.2f, Offset(p.x - 2f, p.y - 2f))
                    }
                }
            }
            Text(if (turn % 2 == 0) "YOUR TURN" else "DODGE THE KNIGHT", color = if (turn % 2 == 0) Gold else SoulRed, modifier = Modifier.align(Alignment.TopCenter).padding(7.dp), fontSize = 11.sp, letterSpacing = 2.sp)
        }
        if (mode == "battle" && turn % 2 == 1) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 3.dp, bottom = 2.dp)) {
                Button(onClick = { moveSoul(0f, -.055f) }, modifier = Modifier.size(42.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332744)), shape = RoundedCornerShape(8.dp)) {
                    Text("▲", color = Gold, fontSize = 17.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { moveSoul(-.055f, 0f) }, modifier = Modifier.size(42.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332744)), shape = RoundedCornerShape(8.dp)) {
                        Text("◀", color = Gold, fontSize = 17.sp)
                    }
                    Button(onClick = { moveSoul(0f, .055f) }, modifier = Modifier.size(42.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332744)), shape = RoundedCornerShape(8.dp)) {
                        Text("▼", color = Gold, fontSize = 17.sp)
                    }
                    Button(onClick = { moveSoul(.055f, 0f) }, modifier = Modifier.size(42.dp), contentPadding = PaddingValues(0.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332744)), shape = RoundedCornerShape(8.dp)) {
                        Text("▶", color = Gold, fontSize = 17.sp)
                    }
                }
            }
        }
        Text(message, color = White, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
        if (mode == "battle") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("FIGHT", "ACT", "ITEM", "SPARE").forEach { action ->
                    Button(
                        onClick = {
                            if (turn % 2 == 0) {
                                when (action) {
                                    "FIGHT" -> {
                                        bossHp = (bossHp - 35).coerceAtLeast(0)
                                        message = "You strike the Roaring Knight!"
                                    }
                                    "ACT" -> message = "You call out to the Knight. The air trembles..."
                                    "ITEM" -> {
                                        playerHp = (playerHp + 6).coerceAtMost(20)
                                        message = "You restored 6 HP."
                                    }
                                    "SPARE" -> message = "The Knight cannot be spared yet."
                                }
                                if (bossHp <= 0) mode = "victory"
                                else {
                                    turn = 1
                                    message = "The Roaring Knight attacks! Use the arrow controls to dodge!"
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF251D35)),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 2.dp)
                    ) { Text(action, color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                }
            }
        } else {
            Spacer(Modifier.height(6.dp))
            Text(if (mode == "victory") "THE KNIGHT IS DEFEATED" else "YOU FELL INTO DARKNESS", color = Gold, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Button(onClick = {
                playerHp = 20
                bossHp = 420
                turn = 0
                attackTicks = 0
                invulnerable = 0
                bullets = emptyList()
                soul = Offset(.5f, .76f)
                message = "The Roaring Knight blocks your path."
                mode = "battle"
            }) { Text("FIGHT AGAIN") }
        }
    }
}
