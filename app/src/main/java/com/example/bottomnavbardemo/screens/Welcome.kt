package com.example.bottomnavbardemo.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.bottomnavbardemo.MainActivity
import com.example.bottomnavbardemo.api.ServiceBuilder
import com.example.bottomnavbardemo.screens.ui.theme.BottomNavBarDemoTheme
import com.example.bottomnavbardemo.ui.theme.Green
import com.example.loadshedding.models.userLocationGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Welcome : ComponentActivity() {
    lateinit var context :Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            context = LocalContext.current
            if(getGroupSet(context) != ""){
                val intent = Intent(this@Welcome, MainActivity::class.java)
                startActivity(intent)
            }

            BottomNavBarDemoTheme {
                Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Stay on top of blackouts",
                        modifier =  Modifier.padding(15.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h3
                    )

                    Row(modifier = Modifier
                        .fillMaxSize()
                        .padding(100.dp),verticalAlignment = Alignment.Bottom,horizontalArrangement = Arrangement.Center) {
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
                    }

/*
                    Button(onClick = {
                        //setUserPreferences(context)
                        val intent = Intent(this@Welcome, MainActivity::class.java)
                        startActivity(intent)
                    },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Green,
                            contentColor = White
                        ),)
                    {
                        Text(text = "Skip")
                    }



 */
                }

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BottomNavBarDemoTheme {

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

