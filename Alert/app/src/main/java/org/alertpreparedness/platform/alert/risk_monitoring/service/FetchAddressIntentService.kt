package org.alertpreparedness.platform.alert.risk_monitoring.service

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import org.alertpreparedness.platform.alert.risk_monitoring.model.ModelGps
import timber.log.Timber
import java.io.IOException
import java.util.*


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 *
 *
 * TODO: Customize class - update intent actions and extra parameters.
 */
class FetchAddressIntentService : IntentService("FetchAddressIntentService") {

    private var addresses: List<Address>? = null
    private lateinit var mReceiver: ResultReceiver

    override fun onHandleIntent(intent: Intent?) {
        var errorMessage = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        val location = intent?.getParcelableExtra<ModelGps>(LOCATION_DATA_EXTRA)


        try {
            location?.let {
                addresses = geocoder.getFromLocation(
                        it.latitude.toDouble(),
                        it.longitude.toDouble(),
                        // In this sample, get just a single address.
                        1)
            }

        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = "io error"
            Timber.e("io error")
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "illegal error"
            Timber.e("illegal error")
        }

        // Handle case where no address was found.
        if (addresses == null || addresses!!.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "address error"
            }
            deliveryResultToReceiver(FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses!![0]
            val addressFragments = (0..address.maxAddressLineIndex).mapTo(ArrayList<String>()) { address.getAddressLine(it) }

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            deliveryResultToReceiver(SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments))

        }


    }

    private fun deliveryResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle()
        bundle.putString(RESULT_DATA_KEY, message)
        mReceiver.send(resultCode, bundle)
    }


    companion object {

        val SUCCESS_RESULT = 0
        val FAILURE_RESULT = 1
        private val PACKAGE_NAME = "org.alertpreparedness.platform.alert.risk_monitoring.service.locationaddress"
        val RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY"
        val LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA"
    }
}
