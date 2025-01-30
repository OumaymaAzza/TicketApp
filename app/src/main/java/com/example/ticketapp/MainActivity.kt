package com.example.ticketapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderItemList: MutableList<OrderItem>

    private lateinit var printButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.order_items_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        orderItemList = mutableListOf(
            OrderItem("Mexican Healthy", 1, 25.00, R.drawable.bowl),
            OrderItem("Pizza Margherita", 2, 30.00, R.drawable.pizza),
            OrderItem("Beef burger", 2, 30.00, R.drawable.burger)
        )

        orderAdapter = OrderAdapter(this, orderItemList)
        recyclerView.adapter = orderAdapter

        printButton = findViewById(R.id.printButton)
        printButton.setOnClickListener {
            generateTicketWithEscPos(orderItemList, "12345678") // Use the dynamic order number
        }
    }

    private fun generateTicketWithEscPos(cartItems: List<OrderItem>, commandNumber: String) {
        // Launch a background task using Kotlin coroutines
        GlobalScope.launch(Dispatchers.IO) {
            val printerIp = "192.168.10.200" // Replace with your printer's IP
            val printerPort = 9100 // Default port for ESC/POS printers

            // Generate ticket text using ESC/POS commands
            val ticketText = buildString {
                append("\u001B\u0040") // Initialize the printer
                append("\u001B\u0061\u0001") // Align center
                append("Snack Store\n")
                append("===================================\n\n")
                append(String.format("%-20s %5s %10s\n", "Item", "Qty", "Price"))
                append("-----------------------------------\n\n")

                cartItems.forEach {
                    append(String.format("%-20s %5d %10.2f\n", it.name, it.quantity, it.price * it.quantity.toDouble()))
                }

                val subTotal = cartItems.sumOf { it.price * it.quantity }
                val deliveryCharge = 20
                val total = subTotal + deliveryCharge

                append("------------------------------------\n\n")
                append("Sub Total:        $subTotal DH\n")
                append("Delivery Charge:   $deliveryCharge DH\n")
                append("Total:            $total DH\n")
                append("====================================\n\n")
                append("\u001B\u0061\u0001") // Align center again
                append("\u001D\u0068\u0032") // Cut the paper
                append("\u001D\u0077\u0002") // Barcode width
                append("\u001D\u006B\u0004") // Barcode command
                append("$commandNumber\u0000") // Add order number

                append("N.$commandNumber\n\n")
                append("Thank you for your order!\n\n")
                append("karima maryam oumayma hiba\n\n")
                append("\n\n\n")

                append("\n\n\n\u001D\u0056\u0000") // Command to cut the paper
            }

            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(printerIp, printerPort), 5000)  // Timeout set to 5 seconds

                // Check if the socket is connected
                if (socket.isConnected) {
                    val outputStream: OutputStream = socket.getOutputStream()

                    // Send ESC/POS commands
                    val escPosBytes = ticketText.toByteArray(Charset.forName("UTF-8"))
                    outputStream.write(escPosBytes)
                    outputStream.flush()

                    // Close output stream and socket
                    outputStream.close()
                    socket.close()

                    // Run on the main thread to show success message
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ticket imprimé avec succès!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Erreur: Impossible de se connecter à l'imprimante.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle errors and show a message
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Erreur d'impression: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
