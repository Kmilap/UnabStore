package me.camilanino.unabstore

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseService {

    private val db = FirebaseFirestore.getInstance()

    // Crear producto
    fun agregarProducto(
        producto: Producto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("productos")
            .add(producto)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Leer productos
    fun obtenerProductos(
        onSuccess: (List<Producto>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                val productos = result.map { doc ->
                    doc.toObject(Producto::class.java).copy(id = doc.id)
                }
                onSuccess(productos)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Eliminar producto
    fun eliminarProducto(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("productos")
            .document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
