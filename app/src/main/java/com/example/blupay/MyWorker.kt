package com.example.blupay

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.nio.charset.Charset
import java.util.UUID
import android.app.Activity


private const val SERVICE_UUID = "25AE1441-05D3-4C5B-8281-93D4E07420CF"
class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    //val bluetoothManager: BluetoothManager = (appContext.getActivity()).getSystemService(BluetoothManager::class.java)
    var advertiser = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser
    var settings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
        .setConnectable(false)
        .build()
    var pUuid = ParcelUuid(UUID.fromString(SERVICE_UUID))

    override fun doWork(): Result {
        Log.d(TAG, "Performing long running task in scheduled job")
        var data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(pUuid)
            .addServiceData(pUuid, "Data".toByteArray(Charset.forName("UTF-8")))
            .build()

        val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                Log.e("BLE", "Advertising onStartFailure: $errorCode")
                super.onStartFailure(errorCode)
            }
        }
        advertiser.startAdvertising(settings, data, advertisingCallback)

        return Result.success()
    }



    companion object {
        private val TAG = "MyWorker"
    }
}