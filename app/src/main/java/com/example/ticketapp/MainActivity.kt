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
            generateTicketWithEscPos(orderItemList, "12345678") 
        }
    }

    private fun generateTicketWithEscPos(cartItems: List<OrderItem>, commandNumber: String) {
       
        GlobalScope.launch(Dispatchers.IO) {
            val printerIp = "192.168.10.200" 
            val printerPort = 9100 

            val ticketText = buildString {
                append("\u001B\u0040")
                append("\u001B\u0061\u0001") 
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
                append("\u001B\u0061\u0001") 
                append("\u001D\u0068\u0032") 
                append("\u001D\u0077\u0002") 
                append("\u001D\u006B\u0004") 
                append("$commandNumber\u0000") 

                append("N.$commandNumber\n\n")
                append("Thank you for your order!\n\n")
                append("karima maryam oumayma hiba\n\n")
                append("\n\n\n")

                append("\n\n\n\u001D\u0056\u0000") 
            }

            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(printerIp, printerPort), 5000) 

          
                if (socket.isConnected) {
                    val outputStream: OutputStream = socket.getOutputStream()

             
                    val escPosBytes = ticketText.toByteArray(Charset.forName("UTF-8"))
                    outputStream.write(escPosBytes)
                    outputStream.flush()

                 
                    outputStream.close()
                    socket.close()

                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Ticket imprimé avec succès!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Erreur: Impossible de se connecter à l'imprimante.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
          
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Erreur d'impression: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
