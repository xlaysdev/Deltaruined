package com.xlaysdev.deltaruined

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

private data class Boss(val name: String, val title: String, val hp: Int, val intro: String, val color: Color)
private data class Bullet(var x: Float, var y: Float, var vx: Float, var vy: Float, var radius: Float = 9f)

private val bosses = listOf(
    Boss("JEVIL", "THE CHAOS CLOWN", 180, "I CAN DO ANYTHING! CAN YOU DODGE ANYTHING?", Color(0xFFB66CFF)),
    Boss("THE KNIGHT", "THE ROAMING DARK", 260, "THE WORLD WILL BE COVERED IN DARKNESS.", Color(0xFFB7C5E8)),
    Boss("KING", "THE RULED ONE", 220, "BOW BEFORE THE POWER OF THE KING.", Color(0xFFDA354A)),
    Boss("SPAMTON NEO", "THE [BIG SHOT]", 240, "NOW'S YOUR CHANCE TO BE A [[HERO]]!", Color(0xFFFFD34E))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = 5894 or 1024 or 512
        setContent { MaterialTheme { DeltaruinedGame() } }
    }
}

@Composable
private fun DeltaruinedGame() {
    var selected by remember { mutableIntStateOf(0) }
    var mode by remember { mutableStateOf("menu") }
    var playerHp by remember { mutableIntStateOf(20) }
    var bossHp by remember { mutableIntStateOf(bosses[0].hp) }
    var turn by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("A strange shadow blocks your path.") }
    var soul by remember { mutableStateOf(Offset(0.5f, 0.72f)) }
    var bullets by remember { mutableStateOf(listOf<Bullet>()) }
    var frame by remember { mutableIntStateOf(0) }
    var invulnerable by remember { mutableIntStateOf(0) }
    val boss = bosses[selected]

    fun startFight(index: Int) {
        selected = index
        bossHp = bosses[index].hp
        playerHp = 20
        turn = 0
        bullets = emptyList()
        soul = Offset(.5f, .72f)
        message = bosses[index].intro
        mode = "battle"
    }

    LaunchedEffect(mode, selected) {
        while (mode == "battle") {
            delay(32)
            frame++
            if (mode == "battle" && turn % 2 == 1) {
                val next = bullets.map { it.copy(x = it.x + it.vx, y = it.y + it.vy) }
                    .filter { it.x in -0.1f..1.1f && it.y in -0.1f..1.1f }
                bullets = next
                if (frame % 22 == 0) {
                    val x = Random.nextFloat().coerceIn(.12f, .88f)
                    bullets = bullets + Bullet(x, .05f, (soul.x - x) * .008f, .009f + Random.nextFloat() * .004f)
                }
                if (invulnerable > 0) invulnerable--
                else if (bullets.any { hypot((it.x - soul.x) * 1.7f, it.y - soul.y) < .045f }) {
                    playerHp = (playerHp - 1).coerceAtLeast(0)
                    invulnerable = 28
                    message = "Ouch! Keep moving!"
                    if (playerHp <= 0) mode = "defeat"
                }
                if (frame % 190 == 0) {
                    turn = 0
                    bullets = emptyList()
                    message = "Your turn. What will you do?"
                }
            }
        }
    }

    Column(
        Modifier.fillMaxSize().background(Ink).padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (mode == "menu") {
            Spacer(Modifier.weight(1f))
            Text("DELTARUINED", color = White, fontSize = 38.sp, fontWeight = FontWeight.Black, letterSpacing = 5.sp)
            Text("A FAN-MADE BOSS RUSH", color = Gold, fontSize = 12.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(24.dp))
            Text("CHOOSE YOUR OPPONENT", color = White, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 14.dp)) {
                bosses.forEachIndexed { index, item ->
                    Button(onClick = { startFight(index) }, colors = ButtonDefaults.buttonColors(containerColor = Panel), shape = RoundedCornerShape(4.dp)) {
                        Text(item.name, color = item.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Text("Touch controls • Survive the chaos", color = Color.Gray, fontSize = 11.sp)
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(boss.name, color = boss.color, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    Text(boss.title, color = Color.LightGray, fontSize = 10.sp, letterSpacing = 2.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("HP  $playerHp / 20", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("BOSS  $bossHp / ${boss.hp}", color = Gold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.weight(1f).fillMaxWidth().background(Panel, RoundedCornerShape(4.dp)).padding(8.dp)) {
                Canvas(Modifier.fillMaxSize().pointerInput(mode, turn) {
                    detectDragGestures { change, drag ->
                        change.consume()
                        if (turn % 2 == 1) {
                            soul = Offset(
                                (soul.x + drag.x / size.width).coerceIn(.06f, .94f),
                                (soul.y + drag.y / size.height).coerceIn(.08f, .92f)
                            )
                        }
                    }
                }) {
                    val w = size.width
                    val h = size.height
                    drawRect(Color(0xFF080811), size = size)
                    // Pixel-grid arena and boss emblem.
                    for (i in 1..11) drawLine(Color(0xFF202034), Offset(w * i / 12f, 0f), Offset(w * i / 12f, h), 1f)
                    drawCircle(boss.color.copy(alpha = .12f), w * .12f, Offset(w * .5f, h * .25f))
                    drawCircle(boss.color, w * .045f, Offset(w * .5f, h * .25f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))
                    drawRect(Color.White, topLeft = Offset(w * .12f, h * .48f), size = Size(w * .76f, h * .44f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                    bullets.forEach { b ->
                        drawCircle(boss.color, b.radius, Offset(b.x * w, b.y * h))
                        drawCircle(Color.White.copy(alpha = .8f), b.radius * .35f, Offset(b.x * w, b.y * h))
                    }
                    if (turn % 2 == 1) {
                        val p = Offset(soul.x * w, soul.y * h)
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(p.x, p.y - 12f); lineTo(p.x + 10f, p.y); lineTo(p.x, p.y + 12f); lineTo(p.x - 10f, p.y); close()
                        }
                        if (invulnerable % 4 < 2) drawPath(path, SoulRed)
                    }
                }
                if (turn % 2 == 0) {
                    Text("YOUR TURN", color = Gold, modifier = Modifier.align(Alignment.TopCenter).padding(8.dp), fontSize = 11.sp, letterSpacing = 2.sp)
                } else {
                    Text("DODGE!", color = SoulRed, modifier = Modifier.align(Alignment.TopCenter).padding(8.dp), fontSize = 11.sp, letterSpacing = 2.sp)
                }
            }
            Text(message, color = White, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp))
            if (mode == "battle") {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("FIGHT", "ACT", "ITEM", "SPARE").forEach { action ->
                        Button(
                            onClick = {
                                if (turn % 2 == 0) {
                                    when (action) {
                                        "FIGHT" -> { bossHp = (bossHp - 30).coerceAtLeast(0); message = "You strike with determination!" }
                                        "ACT" -> message = "You studied ${boss.name}. It seems unsettled."
                                        "ITEM" -> { playerHp = (playerHp + 6).coerceAtMost(20); message = "You recovered 6 HP!" }
                                        "SPARE" -> { message = "You offered mercy."; if (bossHp < boss.hp / 3) mode = "victory" else message = "But ${boss.name} is not ready to be spared." }
                                    }
                                    if (bossHp <= 0) mode = "victory"
                                    else if (mode == "battle") { turn++; message = "${boss.name} attacks! Drag the soul to dodge!" }
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
                Spacer(Modifier.height(8.dp))
                Text(if (mode == "victory") "VICTORY!" else "YOU LOST", color = Gold, fontSize = 26.sp, fontWeight = FontWeight.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { startFight(selected) }) { Text("RETRY") }
                    Button(onClick = { mode = "menu"; bullets = emptyList() }) { Text("BOSS SELECT") }
                }
            }
        }
    }
}
