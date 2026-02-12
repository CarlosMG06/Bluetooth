package com.carlosmedina.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val dataSet: MutableList<BluetoothDevice>) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val deviceNameTextView: TextView = view.findViewById(R.id.deviceName)
        val deviceAddressTextView: TextView = view.findViewById(R.id.deviceAddress)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val device = dataSet[position]

        // Display device name (might be null for some devices)
        viewHolder.deviceNameTextView.text = device.name ?: "Dispositiu desconegut"

        // Display device address
        viewHolder.deviceAddressTextView.text = device.address
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}