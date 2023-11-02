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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.AppTheme
import com.example.ppbapp.Data.LoginData
import com.example.ppbapp.Data.RegisterData
import com.example.ppbapp.Data.UpdateData
import com.example.ppbapp.Respond.LoginRespond
import com.example.ppbapp.Respond.UserRespond
import com.example.ppbapp.Service.LoginService
import com.example.ppbapp.Service.RegisterService
import com.example.ppbapp.Service.UserService
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

            val startDestination: String
            val jwt = sharedPreferences.getString("jwt", "")

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
                        composable(route = "register") {
                            RegisterPage(navController = navController)
                        }
                        composable(route = "edituser/{userid}/{username}") { backStackEntry ->
                            EditUserPage(
                                navController = navController,
                                userid = backStackEntry.arguments?.getString("userid"),
                                usernameParameter = backStackEntry.arguments?.getString("username")
                            )
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
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val rememberMe = remember { mutableStateOf(false) }
    val passwordVisible = remember { mutableStateOf(false) }
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
                                Toast.makeText(
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
                        onClick = {
                            navController.navigate("register")
                        },
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
    val listUser = remember { mutableStateListOf<UserRespond>() }
    val baseUrl = "http://10.0.2.2:1337/api/"
    val retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserService::class.java)
    val call = retrofit.getData()
    call.enqueue(object : Callback<List<UserRespond>> {
        override fun onResponse(
            call: Call<List<UserRespond>>,
            response: Response<List<UserRespond>>
        ) {
            if (response.code() == 200) {
                listUser.clear()
                response.body()?.forEach { userRespond ->
                    listUser.add(userRespond)
                }
            } else if (response.code() == 400) {
                print("error login")
                Toast.makeText(
                    context,
                    "Koneksi Gagal",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onFailure(call: Call<List<UserRespond>>, t: Throwable) {
            print(t.message)
        }
    })

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("register")
                }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn() {
                listUser.forEach { user ->
                    item {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = user.username)
                            Row {
                                Button(
                                    onClick = {
                                        navController.navigate("edituser/" + user.id + "/" + user.username)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        Color.Yellow
                                    ),
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit User"
                                    )
                                }
                                Button(
                                    onClick = {
                                        val retrofit = Retrofit
                                            .Builder()
                                            .baseUrl(baseUrl)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build()
                                            .create(UserService::class.java)
                                        val call = retrofit.delete(user.id)
                                        call.enqueue(object : Callback<UserRespond> {
                                            override fun onResponse(
                                                call: Call<UserRespond>,
                                                response: Response<UserRespond>
                                            ) {
                                                if (response.code() == 200) {
                                                    listUser.remove(user)
                                                } else if (response.code() == 400) {
                                                    print("error login")
                                                    Toast.makeText(
                                                        context,
                                                        "Koneksi Gagal",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<UserRespond>,
                                                t: Throwable
                                            ) {
                                                print(t.message)
                                            }
                                        })
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavController, context: Context = LocalContext.current) {
    val username = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordConfirm = remember { mutableStateOf("") }
    val rememberMe = remember { mutableStateOf(false) }
    val passwordVisible = remember { mutableStateOf(false) }
    val passwordVisibleConfirm = remember { mutableStateOf(false) }
    val baseUrl = "http://10.0.2.2:1337/api/"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp, 75.dp, 20.dp, 0.dp),
    ) {
        Text(
            text = "Sign Up",
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
                        Text(text = "Username")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            )
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                singleLine = true,
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(text = "Email")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
            OutlinedTextField(
                value = passwordConfirm.value,
                onValueChange = { passwordConfirm.value = it },
                singleLine = true,
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Password Confirmation")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                visualTransformation =
                if (passwordVisibleConfirm.value)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisibleConfirm.value = !passwordVisibleConfirm.value },
                        modifier = Modifier.padding(end = 10.dp)
                    ) {
                        Icon(
                            painter =
                            if (passwordVisibleConfirm.value)
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
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = rememberMe.value,
                        onCheckedChange = { rememberMe.value = it }
                    )
                    Text(
                        text = "I agree to the",
                    )
                    TextButton(
                        onClick = { /*TODO*/ },
                        content = {
                            Text(
                                text = "Terms",
                            )
                        }
                    )
                    Text(text = "And")
                    TextButton(
                        onClick = { /*TODO*/ },
                        content = {
                            Text(
                                text = "Conditions",
                            )
                        }
                    )
                }
            }
            Button(
                onClick = {
                    if (password.value != passwordConfirm.value) {
                        Toast.makeText(
                            context,
                            "Password and Password Confirmation must be same",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val retrofit = Retrofit
                            .Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(RegisterService::class.java)
                        val call = retrofit.saveData(
                            RegisterData(
                                email.value,
                                username.value,
                                password.value
                            )
                        )
                        call.enqueue(object : Callback<LoginRespond> {
                            override fun onResponse(
                                call: Call<LoginRespond>,
                                response: Response<LoginRespond>
                            ) {
                                if (response.code() == 200) {
                                    navController.popBackStack()
                                } else if (response.code() == 400) {
                                    Toast.makeText(
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
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign Up")
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Already have an account?")
                    TextButton(
                        onClick = {
                            navController.navigate("login")
                        },
                        content = {
                            Text(
                                text = "Sign In",
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
fun EditUserPage(
    navController: NavController,
    context: Context = LocalContext.current,
    userid: String?,
    usernameParameter: String?
) {
    val preferencesManager = remember { PreferencesManager(context = context) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    if (usernameParameter != null) {
        username = usernameParameter
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit User") },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(value = username, onValueChange = { newText ->
                username = newText
            }, label = { Text("Username") }, modifier = Modifier.padding(top = 15.dp))
            ElevatedButton(modifier = Modifier.padding(top = 15.dp),
                onClick = {
                var baseUrl = "http://10.0.2.2:1337/api/"
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(UserService::class.java)
                val call = retrofit.save(userid, UpdateData(username))
                call.enqueue(object : Callback<LoginRespond> {
                    override fun onResponse(
                        call: Call<LoginRespond>,
                        response: Response<LoginRespond>
                    ) {
                        print(response.code())
                        if (response.code() == 200) {
                            navController.navigate("home")
                        } else if (response.code() == 400) {
                            print("error login")
                            var toast = Toast.makeText(
                                context,
                                "Username atau password salah",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginRespond>, t: Throwable) {
                        print(t.message)
                    }

                })
            }) {
                Text("Simpan")
            }
        }
    }
}
