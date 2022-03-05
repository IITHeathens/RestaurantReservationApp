package iit.heathens.reservationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class IntroPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // Write a message to the database
        val database = Firebase.database
        val dbRef = database.getReference("/Python AI Server/Predictions/")

        //dbRef.setValue("Hello, World!")

        // Read from the database
        dbRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val keys = snapshot.getValue<Map<String, Any>>()
                Log.d("[CLIENT]", "Value is: $keys")

                val valuesKeys = mutableListOf<String>()
                val valuesData = mutableListOf<Any>()

                if (keys != null) {
                    for ((k, v) in keys) {
                        valuesKeys.add(k)
                        valuesData.add(v)
                    }
                }

                val values = keys?.get(valuesKeys[0]) as Map<*, *>
                Log.d("[CLIENT]", "Keys are: $values")

                val prediction = values["Prediction"]
                val probability = values["Probability"]

                Log.d("[CLIENT]", "Prediction is: $prediction")
                Log.d("[CLIENT]", "Probability is: $probability")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("[CLIENT]", "Failed to read value.", error.toException())
            }

        })
    }
}