package me.camilanino.unabstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onClickLogout: () -> Unit = {}) {
    val auth = Firebase.auth
    val user = auth.currentUser

    // 🔸 Variables locales para Firestore
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var productos by remember { mutableStateOf(listOf<Producto>()) }
    var mensaje by remember { mutableStateOf("") }

    // 🔸 Cargar productos al entrar
    LaunchedEffect(Unit) {
        FirebaseService.obtenerProductos(
            onSuccess = { productos = it },
            onFailure = { e -> mensaje = "Error al obtener productos: ${e.message}" }
        )
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Unab Shop",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.Notifications, "Notificaciones")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.ShoppingCart, "Carrito")
                    }
                    IconButton(onClick = {
                        auth.signOut()
                        onClickLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFFF9900),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Bloque de sesión (sin tocar)
            Text("HOME SCREEN", fontSize = 30.sp)
            if (user != null) {
                Text(user.email.toString())
            } else {
                Text("No hay usuario")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Formulario para agregar productos
            TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
            TextField(value = precio, onValueChange = { precio = it }, label = { Text("Precio") })

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    val producto = Producto(
                        nombre = nombre,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0
                    )
                    FirebaseService.agregarProducto(
                        producto,
                        onSuccess = {
                            mensaje = "Producto agregado correctamente"
                            FirebaseService.obtenerProductos(
                                onSuccess = { productos = it },
                                onFailure = { e -> mensaje = "Error: ${e.message}" }
                            )
                        },
                        onFailure = { e -> mensaje = "Error: ${e.message}" }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
            ) {
                Text("Guardar producto")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(mensaje)

            // 🔹 Lista de productos
            Spacer(modifier = Modifier.height(16.dp))
            productos.forEach { producto ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    Text("Nombre: ${producto.nombre}", fontWeight = FontWeight.Bold)
                    Text("Descripción: ${producto.descripcion}")
                    Text("Precio: $${producto.precio}")

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            producto.id?.let {
                                FirebaseService.eliminarProducto(
                                    it,
                                    onSuccess = {
                                        mensaje = "Producto eliminado"
                                        FirebaseService.obtenerProductos(
                                            onSuccess = { productos = it },
                                            onFailure = { e -> mensaje = "Error: ${e.message}" }
                                        )
                                    },
                                    onFailure = { e -> mensaje = "Error: ${e.message}" }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9900))
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}
