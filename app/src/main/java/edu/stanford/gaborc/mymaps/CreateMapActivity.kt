package edu.stanford.gaborc.mymaps

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import edu.stanford.gaborc.mymaps.models.Place
import edu.stanford.gaborc.mymaps.models.UserMap


class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var markers: MutableList<Marker> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_map)
        supportActionBar?.title = intent.getStringExtra(EXTRA_MAP_TITLE)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapFragment.view?.let {
            Snackbar.make(it, "Long press to add a marker!", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", {})
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miSave) {
            if (markers.isEmpty()) {
                Toast.makeText(this, "There must be a least one marker on the map", Toast.LENGTH_LONG).show()
                return true
            }

            val places = markers.map { marker -> Place(marker.title, marker.snippet, marker.position.latitude, marker.position.longitude) }

            val userMap = intent.getStringExtra(EXTRA_MAP_TITLE)?.let { UserMap(it, places) }
            val data = Intent()

            data.putExtra(EXTRA_USER_MAP, userMap)
            setResult(Activity.RESULT_OK, data)
            finish()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener { marker ->
            markers.remove(marker)
            marker.remove()

        }

            mMap.setOnMapLongClickListener {latLng ->
                showAlertDiaglog(latLng)

            }
        // Add a marker in Sydney and move the camera
        val bend = LatLng(44.0, -121.3)

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bend, 10f))
    }



    private fun showAlertDiaglog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create a marker")
            .setView(placeFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
            val title = placeFormView.findViewById<EditText>(R.id.etTitle).text.toString()
            val description = placeFormView.findViewById<EditText>(R.id.etDescription).text.toString()
            if (title.trim().isEmpty() || description.trim().isEmpty()) {
                Toast.makeText(this, "Place must have non-empty title and description", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val marker = mMap.addMarker(MarkerOptions().position(latLng).title(title).snippet(description))

//            val start = SystemClock.uptimeMillis()
//            val duration = 1500
//            val interpolator = android.view.animation.BounceInterpolator()
//            val handler = android.os.Handler(Looper.getMainLooper()).post{
//                val elapsed = SystemClock.uptimeMillis() - start
//                val t =
//                    (1.toFloat() - interpolator.getInterpolation((1 * elapsed / duration).toFloat())).coerceAtLeast(
//                        0.toFloat()
//                    )
//                marker.setAnchor(0.5f, 1.0f + 14 * t)
//                if (t > 0) {
//                    Handler(Looper.getMainLooper()).postDelayed( this, 15)
//                } else {
//                    marker.showInfoWindow()
//                }
//            }

//            mMap.dropPin

            markers.add(marker)

            dialog.dismiss()
        }

    }
}