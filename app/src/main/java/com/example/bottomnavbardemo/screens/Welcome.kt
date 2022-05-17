package com.example.bottomnavbardemo.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.bottomnavbardemo.MainActivity
import com.example.bottomnavbardemo.api.ServiceBuilder
import com.example.bottomnavbardemo.screens.ui.theme.BottomNavBarDemoTheme
import com.example.bottomnavbardemo.ui.theme.Green
import com.example.loadshedding.models.userLocationGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.common.ConnectionResult

import com.google.android.gms.common.GoogleApiAvailability

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import com.example.bottomnavbardemo.models.menuOption


class Welcome : ComponentActivity() {
    lateinit var context :Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this@Welcome,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        val options = listOf<menuOption>(menuOption("Group A","A"),menuOption("Group B","B"),
            menuOption("Group C","C") )

        setContent {
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by remember { mutableStateOf(menuOption("","")) }
            context = LocalContext.current
            BottomNavBarDemoTheme {
                Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Some Random Title",
                        modifier =  Modifier.padding(15.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h3
                    )

                    Row(modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp),verticalAlignment = Alignment.Bottom,horizontalArrangement = Arrangement.Center) {
                        if(isGooglePlayServicesAvailable(this@Welcome)){
                            Button(onClick = {
                                setUserPreferences(context)
                                if(getGroupSet(context) != ""){
                                    val intent = Intent(this@Welcome, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Green,
                                    contentColor = White
                                ),)
                            {
                                Text(text = "Set Location")
                            }
                        }else{

                            AlertDialog(
                                onDismissRequest = { },
                                confirmButton = {
                                    TextButton(onClick = {
                                        Toast.makeText(context, "Selected ${selectedOption.value}", Toast.LENGTH_SHORT).show()
                                        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)
                                        val editor = sharedPreferences.edit()
                                        editor.putString("GroupSet", "true")
                                        editor.putString("group", selectedOption.value)
                                        editor.commit()
                                        val intent = Intent(this@Welcome, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    })
                                    { Text(text = "OK") }
                                },
                                dismissButton = {
                                    TextButton(onClick = {})
                                    { Text(text = "Cancel") }
                                },
                                title = { Text(text = "Select Group") },
                                text = {
                                    Column(){
                                        OutlinedTextField(
                                            value = selectedOption.label,
                                            onValueChange = {  },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .onGloballyPositioned { coordinates ->
                                                    // This value is used to assign to
                                                    // the DropDown the same width
                                                    //mTextFieldSize = coordinates.size.toSize()
                                                },
                                            label = {Text("---")},
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Filled.ArrowDropDown,"contentDescription",
                                                    Modifier.clickable { expanded = !expanded })
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier.width(150.dp),

                                            //modifier = Modifier.width(with(LocalDensity.current){textfieldSize.width.toDp()})
                                        ) {
                                            options.forEach { option ->
                                                DropdownMenuItem(onClick = {
                                                    selectedOption = option
                                                    expanded = false
                                                }) {
                                                    Text(text = option.label)
                                                }
                                            }
                                        }
                                    }



                                }
                            )


                        }
                    }



                }

            }
        }
    }
}



fun setUserPreferences(context:Context){
    //Toast.makeText(context, "Button clicked", Toast.LENGTH_SHORT).show()
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)
    val editor = sharedPreferences.edit()
    var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    editor.putString("GroupSet", "true")
    //val spinner: ProgressBar = findViewById(R.id.requestProgress);
    //spinner.visibility= View.VISIBLE
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.

        //Toast.makeText(this@Welcome, "location not allowed", Toast.LENGTH_SHORT).show()
        Toast.makeText(context, "location not allowed", Toast.LENGTH_SHORT).show()
        return
    }
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location : Location? ->
            if (location != null) {
                Toast.makeText(context, "location not null", Toast.LENGTH_SHORT).show()

                val lat:Float = location.latitude.toFloat()
                val lng:Float = location.longitude.toFloat()
                editor.putFloat("lat", lat)
                editor.putFloat("lng",lng )

                val requestCall : Call<userLocationGroup> = ServiceBuilder.api.getUserLocationGroup(lat,lng)
                requestCall.enqueue(object: Callback<userLocationGroup> {
                    override fun onResponse(
                        call: Call<userLocationGroup>,
                        response: Response<userLocationGroup>
                    ) {

                        if(response.isSuccessful){

                            editor.putString("group", response.body()?.group)
                            editor.putString("area",response.body()?.area)
                            Firebase.messaging.subscribeToTopic("group${response.body()?.group?.uppercase()}")
                                .addOnCompleteListener { task ->
                                    var msg = "subscribed to group"
                                    if (!task.isSuccessful) {
                                        msg = "didnt subscribe"
                                    }
                                    Log.d(ContentValues.TAG, msg)
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }

                            //spinner.visibility= View.GONE

                            Toast.makeText(context, response.body()?.group, Toast.LENGTH_SHORT).show()
                            editor.commit()

                        }

                    }

                    override fun onFailure(call: Call<userLocationGroup>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
            }
            else{
                Toast.makeText(context, "location null", Toast.LENGTH_SHORT).show()

            }

        }


}
fun getGroupSet(context: Context): String? {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context /* Activity context */)
    return sharedPreferences.getString("GroupSet", "")
}




fun isGooglePlayServicesAvailable(activity: Activity?): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val status = googleApiAvailability.isGooglePlayServicesAvailable(activity!!)
    if (status != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(status)) {
            //googleApiAvailability.getErrorDialog(activity, status, 2404)!!.show()
        }
        return false
    }
    return true
}

