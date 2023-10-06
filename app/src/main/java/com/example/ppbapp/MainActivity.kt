package com.example.ppbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginPage()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage() {
    var username = remember { mutableStateOf("") }
    var password = remember { mutableStateOf("") }
    var rememberMe = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                shape = RoundedCornerShape(50)
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
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }
        }
    }

    Column(verticalArrangement = Arrangement.Bottom) {
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