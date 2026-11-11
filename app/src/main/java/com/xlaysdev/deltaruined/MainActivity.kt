package com.xlaysdev.deltaruined

import android.os.Bundle
import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.random.Random

private val Void = Color(0xFF05050B)
private val Gold = Color(0xFFFFD84A)
private val Red = Color(0xFFFF334F)
private val Pale = Color(0xFFE8E7F0)
private val Muted = Color(0xFF9291A4)
private data class Shot(val x: Float, val y: Float, val vx: Float, val vy: Float)
private enum class Phase { INTRO, PLAYER, ENEMY, VICTORY, DEFEAT }

class MainActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.systemUiVisibility = 5894 or 1024 or 512
        setContent { MaterialTheme { KnightGame() } }
    }
}

@Composable
private fun KnightGame() {
    val context = LocalContext.current
    val sprites by produceState<List<androidx.compose.ui.graphics.ImageBitmap>>(emptyList()) {
        value = withContext(Dispatchers.IO) {
            val names = context.assets.list("roaring_knight")?.filter {
                it.endsWith(".png", true) && (it.startsWith("idle_", true) || it.startsWith("attack_", true) || it.startsWith("move_", true))
            }?.sorted().orEmpty()
            names.mapNotNull { name -> runCatching {
                context.assets.open("roaring_knight/$name").use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
            }.getOrNull() }
        }
    }
    var phase by remember { mutableStateOf(Phase.INTRO) }
    var hp by remember { mutableIntStateOf(20) }
    var bossHp by remember { mutableIntStateOf(600) }
    var turn by remember { mutableIntStateOf(0) }
    var tick by remember { mutableIntStateOf(0) }
    var soul by remember { mutableStateOf(Offset(.5f, .78f)) }
    var shots by remember { mutableStateOf(emptyList<Shot>()) }
    var invuln by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("A knight in black stands before the fountain.") }
    var menu by remember { mutableStateOf("") }
    var mercy by remember { mutableIntStateOf(0) }
    var items by remember { mutableIntStateOf(3) }
    var meter by remember { mutableIntStateOf(0) }
    var defending by remember { mutableStateOf(false) }

    fun startEnemyTurn() { menu = ""; phase = Phase.ENEMY; tick = 0; shots = emptyList(); soul = Offset(.5f, .78f) }
    fun hitBoss(amount: Int) {
        bossHp = (bossHp - amount).coerceAtLeast(0)
        if (bossHp == 0) { phase = Phase.VICTORY; message = "The Knight's armor shatters. The fountain is silent." }
    }
    fun reset() {
        phase = Phase.INTRO; hp = 20; bossHp = 600; turn = 0; tick = 0
        soul = Offset(.5f, .78f); shots = emptyList(); invuln = 0; menu = ""
        mercy = 0; items = 3; defending = false
        message = "A knight in black stands before the fountain."
    }
    fun move(dx: Float, dy: Float) {
        if (phase == Phase.ENEMY) soul = Offset((soul.x + dx).coerceIn(.14f, .86f), (soul.y + dy).coerceIn(.54f, .91f))
    }

    LaunchedEffect(phase, turn) {
        if (phase == Phase.INTRO) {
            delay(1300)
            if (phase == Phase.INTRO) { phase = Phase.PLAYER; message = "The Roaring Knight draws its blade." }
        } else if (phase == Phase.ENEMY) {
            tick = 0
            while (phase == Phase.ENEMY && tick < 190) {
                delay(32); tick++
                if (tick % 18 == 0) {
                    when (turn % 3) {
                        0 -> { val x = Random.nextFloat().coerceIn(.18f, .82f); shots = shots + Shot(x, .48f, ((soul.x-x)*.012f).coerceIn(-.009f,.009f), .010f) }
                        1 -> { val x = if ((tick/18)%2==0) .16f else .84f; shots = shots + Shot(x,.48f,if(x<.5f).012f else -.012f,.004f) }
                        else -> { val x=Random.nextFloat().coerceIn(.16f,.84f); shots=shots+Shot(x,.48f,0f,.015f) }
                    }
                }
                shots = shots.map { it.copy(x=it.x+it.vx,y=it.y+it.vy) }.filter { it.x in -.08f..1.08f && it.y in .40f..1.02f }
                if (invuln > 0) invuln--
                else if (shots.any { hypot((it.x-soul.x)*1.45f,(it.y-soul.y)*1.15f)<.035f }) {
                    hp=(hp-if(defending) 1 else 2).coerceAtLeast(0); invuln=32
                    message="The Knight's blade grazes your SOUL."
                    if (hp==0) phase=Phase.DEFEAT
                }
            }
            if (phase == Phase.ENEMY) { shots=emptyList(); defending=false; turn++; phase=Phase.PLAYER; message="The Knight lowers its guard. Your turn!" }
        }
    }

    Column(Modifier.fillMaxSize().background(Void).padding(horizontal=18.dp, vertical=7.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
            Column {
                Text("DELTARUINED", color=Gold, fontSize=11.sp, letterSpacing=3.sp, fontWeight=FontWeight.Black)
                Text("THE ROARING KNIGHT", color=Pale, fontSize=19.sp, fontWeight=FontWeight.Black, letterSpacing=1.sp)
            }
            Column(horizontalAlignment=Alignment.End) {
                Text("KNIGHT  $bossHp / 600", color=Gold, fontSize=12.sp, fontWeight=FontWeight.Bold)
                Text("HP  $hp / 20     LV  1", color=Pale, fontSize=13.sp, fontWeight=FontWeight.Bold)
                Box(Modifier.width(150.dp).height(6.dp).background(Color(0xFF39212B),RoundedCornerShape(4.dp))) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(bossHp/600f).background(Red,RoundedCornerShape(4.dp)))
                }
            }
        }
        Spacer(Modifier.height(5.dp))
        Box(Modifier.weight(1f).fillMaxWidth().background(Color(0xFF090912),RoundedCornerShape(5.dp)).border(1.dp,Color(0xFF39384A),RoundedCornerShape(5.dp))) {
            Canvas(Modifier.fillMaxSize()) {
                val w=size.width; val h=size.height; val cx=w*.5f
                drawRect(Color(0xFF080811),size=size)
                for(i in 0..14) drawLine(Color(0xFF151526),Offset(w*i/14f,0f),Offset(w*i/14f,h),1f)
                val bob=kotlin.math.sin(tick*.08f).toFloat()*3f
                val cy=h*.20f+bob
                if(sprites.isNotEmpty()) {
                    val img=sprites[(tick/8)%sprites.size]
                    val side=(w*.30f).coerceAtMost(h*.42f).toInt()
                    val off=IntOffset((cx-side/2).toInt(),(h*.025f).toInt())
                    val dims=IntSize(side,side)
                    drawImage(img,dstOffset=off+IntOffset(13,-3),dstSize=dims,alpha=.13f)
                    drawImage(img,dstOffset=off+IntOffset(6,-1),dstSize=dims,alpha=.25f)
                    drawImage(img,dstOffset=off,dstSize=dims)
                } else {
                    val armor=Color(0xFFBFC9D9); val shade=Color(0xFF465064)
                    drawCircle(shade,h*.095f,Offset(cx,cy+h*.055f))
                    drawCircle(armor,h*.071f,Offset(cx,cy-h*.035f))
                    drawRect(armor,Offset(cx-h*.07f,cy+h*.025f),Size(h*.14f,h*.20f))
                    drawRect(shade,Offset(cx-h*.115f,cy+h*.19f),Size(h*.23f,h*.045f))
                    drawLine(Color(0xFF242A3C),Offset(cx-h*.05f,cy-h*.04f),Offset(cx+h*.05f,cy-h*.04f),h*.025f)
                    drawLine(Color(0xFFBFC9D9),Offset(cx+h*.08f,cy+h*.08f),Offset(cx+h*.25f,cy-h*.13f),h*.025f)
                    drawLine(Color(0xFF9B243D),Offset(cx+h*.25f,cy-h*.13f),Offset(cx+h*.28f,cy-h*.20f),h*.012f)
                }
                drawRect(Color.White,Offset(w*.12f,h*.43f),Size(w*.76f,h*.51f),style=Stroke(3f))
                shots.forEach { drawCircle(Color(0xFFBBC9E8),9f,Offset(it.x*w,it.y*h)); drawCircle(Color.White,3f,Offset(it.x*w,it.y*h)) }
                if(phase==Phase.ENEMY && invuln%4<2) {
                    val p=Offset(soul.x*w,soul.y*h); drawCircle(Red,9f,p); drawCircle(Color.White,2f,Offset(p.x-2,p.y-2))
                }
                if(phase==Phase.PLAYER && menu=="FIGHT") {
                    drawRect(Gold,Offset(w*.25f,h*.955f),Size(w*.5f,3f))
                    drawCircle(Color.White,6f,Offset(w*(.25f+(meter%100)/100f*.5f),h*.965f))
                }
            }
            Text(when(phase){Phase.ENEMY->"DODGE THE ATTACK";Phase.PLAYER->"YOUR TURN";Phase.INTRO->"A DARK PRESENCE APPROACHES";else->"BATTLE OVER"},
                color=if(phase==Phase.ENEMY)Red else Gold,modifier=Modifier.align(Alignment.TopCenter).padding(6.dp),fontSize=10.sp,letterSpacing=2.sp)
        }
        if(phase==Phase.ENEMY) {
            Row(Modifier.fillMaxWidth().padding(top=3.dp),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically) {
                Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(3.dp)) {
                    Pad("▲"){move(0f,-.055f)}
                    Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){Pad("◀"){move(-.055f,0f)};Pad("▼"){move(0f,.055f)};Pad("▶"){move(.055f,0f)}}
                }
                Text("  MOVE YOUR SOUL",color=Muted,fontSize=9.sp,letterSpacing=1.sp)
            }
        }
        Box(Modifier.fillMaxWidth().padding(top=5.dp).background(Color(0xFF090910)).border(1.dp,Color(0xFF777386),RoundedCornerShape(4.dp)).padding(horizontal=12.dp,vertical=8.dp)) {
            Text(message,color=Pale,fontFamily=FontFamily.Monospace,fontSize=12.sp,minLines=2)
        }
        if(phase==Phase.PLAYER) {
            if(menu.isEmpty()) {
                Row(Modifier.fillMaxWidth().padding(top=5.dp),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                    listOf("FIGHT","ACT","ITEM","MERCY").forEach { label ->
                        Button(onClick={menu=label;meter=0},modifier=Modifier.weight(1f).height(43.dp),
                            colors=ButtonDefaults.buttonColors(containerColor=when(label){"FIGHT"->Color(0xFF4B252A);"ACT"->Color(0xFF283C32);"ITEM"->Color(0xFF28364B);else->Color(0xFF494027)}),
                            shape=RoundedCornerShape(5.dp),contentPadding=PaddingValues(2.dp)){Text(label,color=Gold,fontSize=12.sp,fontWeight=FontWeight.Black)}
                    }
                }
            } else {
                LaunchedEffect(menu) { if(menu=="FIGHT") while(menu=="FIGHT"&&phase==Phase.PLAYER){delay(16);meter=(meter+2)%100} }
                Row(Modifier.fillMaxWidth().padding(top=5.dp),horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    when(menu) {
                        "FIGHT" -> {
                            Button(onClick={val power=10+(100-abs(meter-50))*7/10;hitBoss(power);if(phase!=Phase.VICTORY){message="Your strike deals $power damage!";startEnemyTurn()}},modifier=Modifier.weight(1f)){Text("STRIKE!")}
                            Button(onClick={menu=""}){Text("BACK")}
                        }
                        "ACT" -> {
                            listOf("CHECK","CALL OUT","STUDY").forEach { action ->
                                Button(onClick={
                                    mercy=(mercy+1).coerceAtMost(2)
                                    val line=when(action){"CHECK"->"ROARING KNIGHT — ATK ??? DEF ???. A terrible force guards the fountain.";"CALL OUT"->"You call to the Knight. Its blade trembles, just for a moment.";else->"You study its stance. You feel ready for the next attack."}
                                    message=line;startEnemyTurn()
                                },modifier=Modifier.weight(1f),contentPadding=PaddingValues(2.dp)){Text(action,fontSize=10.sp)}
                            }
                            Button(onClick={menu=""}){Text("X")}
                        }
                        "ITEM" -> {
                            Button(onClick={if(items>0&&hp<20){hp=(hp+8).coerceAtMost(20);items--;message="You ate a Dark Candy. HP restored!";startEnemyTurn()}else message="No usable items remain."},modifier=Modifier.weight(1f)){Text("DARK CANDY  +8 HP  ($items)")}
                            Button(onClick={menu=""}){Text("BACK")}
                        }
                        "MERCY" -> {
                            Button(onClick={if(mercy>=2||bossHp<=150){phase=Phase.VICTORY;message="The Knight lowers its sword. The battle ends."}else{message="The Knight refuses mercy. Its resolve is unbroken.";startEnemyTurn()}},modifier=Modifier.weight(1f)){Text("SPARE")}
                            Button(onClick={menu=""}){Text("BACK")}
                        }
                    }
                }
            }
        } else if(phase==Phase.VICTORY||phase==Phase.DEFEAT) {
            Text(if(phase==Phase.VICTORY)"THE FOUNTAIN IS SAFE... FOR NOW." else "YOUR SOUL FADES INTO THE DARK.",color=Gold,fontSize=16.sp,fontWeight=FontWeight.Black,modifier=Modifier.align(Alignment.CenterHorizontally).padding(5.dp))
            Button(onClick={reset()},modifier=Modifier.align(Alignment.CenterHorizontally)){Text("TRY AGAIN")}
        }
    }
}

@Composable
private fun Pad(label:String,onClick:()->Unit) {
    Button(onClick=onClick,modifier=Modifier.size(39.dp),contentPadding=PaddingValues(0.dp),
        colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF30243D)),shape=RoundedCornerShape(7.dp)){
        Text(label,color=Gold,fontSize=17.sp,fontWeight=FontWeight.Bold)
    }
}
