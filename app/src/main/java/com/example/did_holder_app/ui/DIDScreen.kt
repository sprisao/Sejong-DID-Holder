package com.example.did_holder_app.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.did_holder_app.R
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DIDScreen(viewModel: DIDViewModel) {
    val scope = rememberCoroutineScope()
    val didDocumentState = viewModel.didDocument.collectAsState(initial = DidDocument())
    val state = if (didDocumentState.value?.id != null) {
        DIDState.Existing(didDocument = didDocumentState.value!!)
    } else {
        DIDState.None
    }

    DIDScreenState(viewModel, scope, state)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DIDScreenState(
    viewModel: DIDViewModel,
    scope: CoroutineScope,
    state: DIDState
) {
    when (state) {
        is DIDState.None -> EmptyDidScreen(viewModel)
        is DIDState.Existing -> WithDidScreen(viewModel, scope, state.didDocument)
    }
}

sealed class DIDState {
    object None : DIDState()
    data class Existing(val didDocument: DidDocument) : DIDState()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EmptyDidScreen(viewModel: DIDViewModel) {
    var isLoading by remember {
        mutableStateOf(false)
    }

    var scope = rememberCoroutineScope()

    if (isLoading) {
        LoadingScreen(message = "DID 생성 중입니다.")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.generateDidDocument{
                        isLoading = false
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "DID 생성")
            }
        }
    }
}

@Composable
fun LoadingScreen(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Text(text = message)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WithDidScreen(
    viewModel: DIDViewModel,
    scope: CoroutineScope,
    didDocument: DidDocument
) {
    val context = LocalContext.current
    var cardFace by remember {
        mutableStateOf(CardFace.Front)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        FlipCard(
            cardFace = cardFace,
            onClick = { cardFace = cardFace.next },
            modifier = Modifier
                .fillMaxWidth(1f)
                .aspectRatio(0.6f),
            front = {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,

                    ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(0.7f)
                            .background(Color.Red),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .fillMaxHeight(1f)
                                .fillMaxWidth(1f),
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .fillMaxHeight(0.7f),
                                contentAlignment = Alignment.Center

                            ) {
                                Image(
                                    modifier = Modifier
                                        .fillMaxWidth(0.4f)
                                        .fillMaxHeight(0.4f),
                                    painter = painterResource(id = R.drawable.sejong_ci),
                                    contentDescription = "did holder app logo"
                                )
                            }
                            Text(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                text = "SEJONG DID",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                text = "자기주권신원인증 시스템",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(1f)
                            .fillMaxHeight(1f),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column() {
                            Text(
                                "나의 DID", style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.sp
                                )
                            )
                            Text(
                                text = didDocument.id,
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            )
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(1f),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                viewModel.saveDidDocumentToBlockchain(didDocument) {
                                    if (it.isSuccessful) {
                                        if (it.body()?.code == 0) {
                                            Toast.makeText(
                                                context,
                                                "블록체인에 저장되었습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "실패 : ${it.body()?.msg}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "실패 : ${it.body()?.msg}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }) {
                            Text(text = "블록체인에 저장")
                        }

                    }
                }
            },
            back = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "back",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.clearDidDocument()
                            }
                        },
                    ) {
                        Text(text = "DID 삭제")
                    }
                }
            },
        )
    }
}

enum class CardFace(val angle: Float) {
    Front(180f) {
        override val next: CardFace
            get() = Back
    },
    Back(0f) {
        override val next: CardFace
            get() = Front
    };

    abstract val next: CardFace
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlipCard(
    cardFace: CardFace,
    onClick: (CardFace) -> Unit,
    modifier: Modifier = Modifier,
    front: @Composable () -> Unit = {},
    back: @Composable () -> Unit = {},
) {
    val rotation = animateFloatAsState(
        targetValue = cardFace.angle,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    ).value
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = { onClick(cardFace) },
        modifier = modifier.graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * density
        },
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        if (rotation <= 90f) {
            Box(
                Modifier.fillMaxSize()
            ) {
                back()
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f
                    }
            ) {
                front()
            }
        }
    }
}