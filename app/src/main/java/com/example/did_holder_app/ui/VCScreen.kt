package com.example.did_holder_app.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.did_holder_app.data.model.DIDDocument.DidDocument
import com.example.did_holder_app.data.model.VC.SignInRequest
import com.example.did_holder_app.data.model.VC.VCRequest
import com.example.did_holder_app.data.model.VC.VcCredentialSubject
import com.example.did_holder_app.data.model.VC.VcResponseData
import com.example.did_holder_app.ui.viewmodel.DIDViewModel
import com.example.did_holder_app.util.Constants
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun VCScreen(navController: NavController, viewModel: DIDViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var cardFace by remember {
        mutableStateOf(CardFace.Front)
    }
    val savedVC = viewModel.vc.collectAsState(initial = VcResponseData())
    val savedDidDocument = viewModel.didDocument.collectAsState(initial = DidDocument())
//    val savedUserSeq = viewModel.userSeq.collectAsState(initial = 0)


    var isLoading by remember { mutableStateOf(false) }
    var showIssuerList by remember { mutableStateOf(false) }

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jsonAdapter: JsonAdapter<VcResponseData> = moshi.adapter(VcResponseData::class.java)
    val vcResponseData = jsonAdapter.toJson(savedVC.value)

    val issuerList: List<Issuer> = listOf(
        Issuer(
            institutionName = "세종텔레콤",
            logoImageUrl = "https://www.medigatenews.com/file/news/268421",
            type = "출입증",
            available = true,
        ),
        Issuer(
            logoImageUrl = "https://www.molit.go.kr/images/www2019/contents/img_05010401.jpg",
            institutionName = "국토교통부",
            available = false,
            type = "운전면허증"
        ),
        Issuer(
            logoImageUrl = "https://seeklogo.com/images/S/Samsung_Electronics-logo-4470505FDD-seeklogo.com.png",
            institutionName = "삼성전자",
            available = false,
            type = "사원증"
        ),
        Issuer(
            logoImageUrl = "https://seeklogo.com/images/L/LG_Electronics-logo-DDDB1A917D-seeklogo.com.png",
            institutionName = "LG전자",
            available = false,
            type = "사원증"
        ),
        Issuer(
            logoImageUrl = "https://seeklogo.com/images/S/SK_Telecom-logo-4DB6A97650-seeklogo.com.png",
            institutionName = "SK텔레콤",
            available = false,
            type = "사원증"
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            savedVC.value == null -> {
                if (isLoading) {
                    CircularProgressIndicator()
                    Box(modifier = Modifier.padding(8.dp))
                    Text(text = "생성중...", style = MaterialTheme.typography.labelSmall)
                } else {
                    savedDidDocument.value?.let {

                        Text (
                            text = "아직 보유한 VC가 없습니다.",
                            style = TextStyle(fontSize = 18.sp),
                            modifier = Modifier.padding(top = 20.dp, bottom = 5.dp))
                        IssuerCardListView(
                            issuerList = issuerList,
                            viewModel = viewModel,
                            didDocument = it,
                        )
                    }
                }
            }

            (savedVC.value != null && !showIssuerList) -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                        IconButton(
                            onClick = {
                                showIssuerList = true
                            },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp),
                                imageVector = Icons.Default.Menu,
                                contentDescription = "open issuer list",
                                tint = Color.Black
                            )
                        }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 26.dp, vertical = 0.dp),
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
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(bottom = 10.dp)
                                            .fillMaxHeight(1f)
                                            .fillMaxWidth(1f),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        AsyncImage(
                                            model = "https://www.medigatenews.com/file/news/268421",
                                            modifier = Modifier
                                                .fillMaxWidth(0.8f)
                                                .fillMaxHeight(0.6f),
                                            contentDescription = "세종텔레콤 사원증"
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color.Red)
                                        .fillMaxWidth(1f)
                                        .fillMaxHeight(1f),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                            .fillMaxWidth(1f)
                                            .fillMaxHeight(1f),
                                        verticalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Column() {
                                            Text(
                                                text = "세종텔레콤",
                                                style = TextStyle(
                                                    color = Color.White,
                                                    fontSize = 22.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )

                                            Text(
                                                text = "사원증",
                                                style = TextStyle(
                                                    color = Color.White,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Normal
                                                )
                                            )
                                        }
                                        if (savedVC.value!!.issuanceDate != "") {
                                            val issuanceDate =
                                                savedVC.value?.issuanceDate.toString()
                                            val expirationDate =
                                                savedVC.value?.validUntil.toString()
                                            val inputFormat = DateTimeFormatter.ISO_DATE_TIME
                                            val outputFormat =
                                                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                                            Text(
                                                modifier = Modifier.padding(
                                                    top = 5.dp,
                                                    bottom = 5.dp
                                                ),
                                                text = "발급일시: ${
                                                    outputFormat.format(
                                                        LocalDateTime.parse(
                                                            issuanceDate,
                                                            inputFormat
                                                        )
                                                    )
                                                }",
                                                style = TextStyle(
                                                    color = Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Light
                                                )
                                            )
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
                                if(savedVC.value!!.vcCredentialSubject.isNotEmpty()){
                                    val credentialSubject: VcCredentialSubject = savedVC.value!!.vcCredentialSubject[0]
                                    Column {
                                        Text(
                                            "사원정보", style = TextStyle(
                                                color = Color.Black,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 0.sp
                                            )
                                        )
                                        Column ( modifier = Modifier.fillMaxHeight(0.3f).padding(top = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                                            Text(
                                                "이름",
                                                style = TextStyle(
                                                    color = Color.Black,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    letterSpacing = 0.sp
                                                )
                                            )
                                            Text(
                                                text = credentialSubject.name,
                                                style = TextStyle(
                                                    color = Color.Black,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                            )
                                            Text(
                                                "직급",
                                                style = TextStyle(
                                                    color = Color.Black,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    letterSpacing = 0.sp
                                                )
                                            )
                                            Text(
                                                text = credentialSubject.position,
                                                style = TextStyle(
                                                    color = Color.Black,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                            )
                                            Text(
                                                "부서",
                                                style = TextStyle(
                                                    color = Color.Black,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    letterSpacing = 0.sp
                                                )
                                            )
                                            Text(
                                                text = credentialSubject.type,
                                                style = TextStyle(
                                                    color = Color.Black,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                )
                                            )
                                        }
                                    }
                                }

//                                Button(
//                                    modifier = Modifier
//                                        .fillMaxWidth(1f)
//                                        .padding(vertical = 5.dp),
//                                    colors = ButtonDefaults.buttonColors(
//                                        containerColor = Color.White,
//                                        contentColor = Color.Black
//                                    ),
//                                    shape = RoundedCornerShape(10.dp),
//                                    onClick = {
//                                        isLoading = true
//                                        viewModel.requestVC(
//                                            VCRequest(
//                                                39,
//                                                savedDidDocument.value?.id
//                                            )
//                                        ) {
//                                            isLoading = false
//                                            if (it.isSuccessful) {
//                                                if (it.body()?.code == 0) {
//                                                    Toast.makeText(
//                                                        context,
//                                                        "VC 발급성공",
//                                                        Toast.LENGTH_SHORT
//                                                    )
//                                                        .show()
//                                                } else {
//                                                    Toast.makeText(
//                                                        context,
//                                                        "실패 : ${it.body()?.msg}",
//                                                        Toast.LENGTH_SHORT
//                                                    ).show()
//                                                }
//                                            } else {
//                                                Toast.makeText(
//                                                    context,
//                                                    "실패 : ${it.message()}",
//                                                    Toast.LENGTH_SHORT
//                                                )
//                                                    .show()
//                                            }
//                                        }
//                                    }) {
//                                    Text(text = "인증서 재발급")
//                                }
                                Button(
                                    modifier = Modifier.fillMaxWidth(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    onClick = {
                                        scope.launch {
                                            viewModel.clearVc()
                                        }
                                        cardFace = cardFace.next
                                    },
                                ) {
                                    Text(text = "VC 삭제")
                                }

                            }
                        },
                    )
                }
            }

            savedVC.value != null && showIssuerList -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                        IconButton(
                            onClick = {
                                showIssuerList = false
                            },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp),
                                imageVector = Icons.Default.Close,
                                contentDescription = "close issuer list",
                                tint = Color.Black
                            )
                        }
                }
                IssuerCardListView(issuerList = issuerList, didDocument = savedDidDocument.value!!, viewModel = viewModel)

            }

            else -> {
                var userId by remember { mutableStateOf("androidTest") }
                var userPassword by remember { mutableStateOf("androidTest1") }
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = userPassword,
                    onValueChange = { userPassword = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(onClick = {
                    val signInRequest = SignInRequest(userId, userPassword)
                    viewModel.signInUser(signInRequest) {
                        if (it.isSuccessful) {
                            if (it.body()?.code == 0) {
                                Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "실패 : ${it.body()?.msg}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "실패 : ${it.message()}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }) {
                    Text(text = "로그인", style = MaterialTheme.typography.labelSmall)
                }
                Text(text = "또는", style = MaterialTheme.typography.labelSmall)
                Button(onClick = {
                    navController.navigate(Constants.SIGN_UP_SCREEN_NAME)
                }) {
                    Text(text = "회원가입", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}


data class Issuer(
    val logoImageUrl: String,
    val institutionName: String,
    val type: String,
    val available : Boolean
)


@Composable
fun IssuerCardListView(issuerList: List<Issuer>, viewModel: DIDViewModel, didDocument: DidDocument) {
    Column(modifier = Modifier.fillMaxHeight(0.9f)) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 0.dp),
            text = "발급기관목록",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
        )
        Box(
            modifier = Modifier
                .fillMaxHeight(1f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LazyColumn(modifier = Modifier.fillMaxHeight(1f)) {
                items(issuerList) { issuer ->
                    IssuerCard(issuer = issuer, viewModel, didDocument)
                }
            }
        }

    }
}

@Composable
fun IssuerCard(issuer: Issuer, viewModel: DIDViewModel, savedDidDocument: DidDocument) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .height(85.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable(onClick = {
                    if (issuer.available) {
                        isLoading = true
                        viewModel.requestVC(
                            VCRequest(
                                39,
                                savedDidDocument.id,
                            )
                        ) {
                            isLoading = false
                            if (it.isSuccessful) {
                                if (it.body()?.code == 0) {
                                    Toast
                                        .makeText(
                                            context,
                                            "VC 발급 성공",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "실패 : ${it.body()?.msg}",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            } else {
                                Toast
                                    .makeText(
                                        context,
                                        "실패 : ${it.message()}",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                        }
                    } else {
                        Toast
                            .makeText(context, "사용불가능한 발급기관입니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.25f),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    modifier =
                    Modifier.fillMaxWidth(1f),
                    model = issuer.logoImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center,
                )
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .fillMaxWidth(0.55f)
            ) {
                Text(
                    text = issuer.institutionName,
                    fontWeight = FontWeight.SemiBold,
                    style = TextStyle(fontSize = 18.sp)
                )
                Text(
                    text = issuer.type,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(horizontal = 0.dp),
                text = if (issuer.available) "발급가능" else "예시",
                color = if (issuer.available) Color.Green else Color.Gray,
                style = TextStyle(fontSize = 12.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}
