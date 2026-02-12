package com.carlosmedina.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class MainActivity : AppCompatActivity() {
    val dataset = mutableListOf<BluetoothDevice>()
    lateinit var customAdapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        customAdapter = CustomAdapter(dataset)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val lm: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(lm)
        recyclerView.setAdapter(customAdapter)

        requestBluetoothPermissionAndUpdate()
    }

    @SuppressLint("MissingPermission")
    fun updatePairedDevices() {
        // empty list
        dataset.clear()

        // update list
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter



        for( elem in bluetoothAdapter.bondedDevices.filter { device ->
            // Filtrar per dispositius BLE
            device.type == BluetoothDevice.DEVICE_TYPE_LE ||
                    device.type == BluetoothDevice.DEVICE_TYPE_DUAL ||
                    device.type == BluetoothDevice.DEVICE_TYPE_UNKNOWN ||
                    device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC
        } ) {
            // afegim element al dataset
            dataset.add( elem )
        }
        customAdapter.notifyDataSetChanged()

        if (dataset.isEmpty()) {
            Toast.makeText(this, "No compatible paired devices found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Found ${dataset.size} paired device(s)", Toast.LENGTH_SHORT).show()
        }
    }

    private val REQUEST_CODE_BLUETOOTH = 100 // es pot posar un nombre aleatori no emprat en cap altre lloc

    private fun requestBluetoothPermissionAndUpdate() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ requereix BLUETOOTH_CONNECT
            Manifest.permission.BLUETOOTH_CONNECT
        } else {
            // Versions anteriors
            Manifest.permission.BLUETOOTH
        }

        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {

            // Demanar el permís
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                REQUEST_CODE_BLUETOOTH
            )
        } else {
            // Permís ja concedit - llegir dispositius
            updatePairedDevices()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                // Permís concedit - llegir dispositius
                updatePairedDevices()
            } else {
                // Permís denegat
                Toast.makeText(this, "Permís necessari per a llegir Bluetooth",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}