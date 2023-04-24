package com.example.did_holder_app.ui

import android.graphics.Color.rgb
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.did_holder_app.R
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

@Composable
fun EmptyDidScreen(viewModel: DIDViewModel) {
    val context = LocalContext.current
    var isLoading by remember {
        mutableStateOf(false)
    }
    var loadingMessage by remember {
        mutableStateOf("DID 생성 중입니다.")
    }

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
                    loadingMessage = "DID 생성 중입니다."
                    viewModel.generateDidDocument(context)
                    isLoading = false
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "DID 생성")
            }
        }
    }
}



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
    val isDidSaved  = viewModel.isDidSaved.collectAsState(initial = false)
    val resultIsDidSaved = isDidSaved.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                },
            ) {
            }
        }
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
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(1f),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom

                            ) {
                                Text(
                                    "나의 DID", style = TextStyle(
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.sp
                                    )
                                )
                            }
                            Text(
                                text = didDocument.id,
                                style = TextStyle(
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            )
                        }
                        if(
                            resultIsDidSaved!!
                        ){
                            Row(
                                modifier = Modifier.fillMaxWidth(1f),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier.padding(end = 10.dp).width(25.dp).height(25.dp),
                                    painter = painterResource(id = R.drawable.baseline_security_24),
                                    contentDescription = "did holder app logo",
                                    tint =Color(rgb(83, 145, 101))
                                )
                                Text(
                                    text = "블록체인에 안전하게 등록 되었습니다.",
                                    style = TextStyle(
                                        color = Color(rgb(83, 145, 101)),
                                        fontSize =14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.sp
                                    )
                                )
                            }
                        } else {
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
                                                    "DID가 블록체인에 저장되었습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                viewModel.saveIsDidSaved(true)
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
                }
            },
            back = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween,

                ) {
                    Column {
                        Text(
                            "나의 DID 정보", style = TextStyle(
                                color = Color.Black,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.sp
                            )
                        )
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .height(10.dp)
                        )
                        Text(
                            "DID",
                            style = TextStyle(
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
                        Text(
                            text = "Document",
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.sp
                            )
                        )
                        Text(
                            text = didDocument.toString(),
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
                            scope.launch {
                                viewModel.clearDidDocument()
                                viewModel.clearIsDidSaved()
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