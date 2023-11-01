package com.example.ppbapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sharedPreferences: SharedPreferences =
                LocalContext.current.getSharedPreferences("auth", Context.MODE_PRIVATE)
            val navController = rememberNavController()

            var startDestination: String
            var jwt = sharedPreferences.getString("jwt", "")

            if (jwt.equals("")) {
                startDestination = "login"
            } else {
                startDestination = "home"
            }

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController, startDestination = startDestination) {
                        composable(route = "login") {
                            LoginPage(navController = navController)
                        }
                        composable(route = "home") {
                            HomePage(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(context: Context = LocalContext.current, navController: NavController) {
    val preferencesManager = remember { PreferencesManager(context) }
    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var rememberMe = remember { mutableStateOf(false) }
    var passwordVisible = remember { mutableStateOf(false) }
    val baseUrl = "http://10.0.2.2:1337/api/"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 75.dp, 20.dp, 0.dp),
    ) {
        Text(
            text = "Log In",
            fontWeight = FontWeight(600),
            fontSize = 43.sp
        )
        Column(
            modifier = Modifier
                .padding(top = 35.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            OutlinedTextField(
                value = username.value,
                onValueChange = { username.value = it },
                singleLine = true,
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Username"
                        )
                        Text(text = "Username")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            )
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                singleLine = true,
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                        Text(text = "Password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                visualTransformation =
                if (passwordVisible.value)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible.value = !passwordVisible.value },
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            painter =
                            if (passwordVisible.value)
                                painterResource(id = R.drawable.eye_slash_solid)
                            else
                                painterResource(id = R.drawable.eye_solid),
                            contentDescription = "Toggle Password"
                        )
                    }
                }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe.value,
                        onCheckedChange = { rememberMe.value = it }
                    )
                    Text(
                        text = "Remember Me",
                    )
                }
                TextButton(
                    onClick = { /*TODO*/ },
                    content = {
                        Text(
                            text = "Forgot Password?",
                            textAlign = TextAlign.End,
                        )
                    }
                )
            }
            Button(
                onClick = {
                    val retrofit = Retrofit
                        .Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(LoginService::class.java)
                    val call = retrofit.getData(LoginData(username.value, password.value))
                    call.enqueue(object : Callback<LoginRespond> {
                        override fun onResponse(
                            call: Call<LoginRespond>,
                            response: Response<LoginRespond>
                        ) {
                            if (response.code() == 200) {
                                preferencesManager.saveData("jwt", response.body()?.jwt!!)
                                navController.navigate("home")
                            } else if (response.code() == 400) {
                                var toast = Toast.makeText(
                                    context,
                                    "Invalid Username Or Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginRespond>, t: Throwable) {
                            print(t.message)
                        }
                    })
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = "Or Sign in With")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { /*TODO*/ },
                        shape = CircleShape,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.google_icon),
                            contentDescription = "Google"
                        )
                    }
                    Button(
                        onClick = { /*TODO*/ },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.facebook_icon),
                            contentDescription = "Facebook"
                        )
                    }
                    Button(
                        onClick = { /*TODO*/ },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.twitter_icon),
                            contentDescription = "Twitter"
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Don't have an account?")
                    TextButton(
                        onClick = { /*TODO*/ },
                        content = {
                            Text(
                                text = "Sign Up",
                                fontWeight = FontWeight(600),
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController, context: Context = LocalContext.current) {
    val preferenceManager = remember { PreferencesManager(context = context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Home Page")
                },
                actions = {
                    IconButton(onClick = {
                        preferenceManager.saveData("jwt", "")
                        navController.navigate("login")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Home Page")
        }
    }
}