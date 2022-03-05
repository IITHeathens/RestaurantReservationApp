package iit.heathens.reservationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class PrototypeTester : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prototype_tester)

        // Write a message to the database
        val database = Firebase.database
        val dbRef = database.getReference("/Python AI Server/Predictions/")

        //dbRef.setValue("Hello, World!")


        val confirmBttn = findViewById<Button>(R.id.button1)

        confirmBttn.setOnClickListener {

            val parking = findViewById<EditText>(R.id.parking)
            val distance = findViewById<EditText>(R.id.distance)
            val refundableDeposit = findViewById<EditText>(R.id.refundableDeposit)

            val dbRefInputs = database.getReference("/Python AI Server/Input Factors/")

//            dbRefInputs.get().addOnSuccessListener {
//                Log.i("firebase", "Got value ${it.value}")
//
//                Log.d("[CLIENT]", "Value is: $it")
//
//            }.addOnFailureListener{
//                Log.e("firebase", "Error getting data", it)


            var nameID = ""

            dbRefInputs.addListenerForSingleValueEvent(object: ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val inputs = snapshot.getValue<Map<String, Any>>() as Map<*, *>
                    Log.d("[CLIENT]", "Inputs are: $inputs")

                    if (nameID == "")
                        nameID = inputs.keys.first().toString()
                    Log.d("[CLIENT]", "NameID is $nameID")

                    val factors = mapOf<String, String>(
                        "Parking" to parking.text.toString(),
                        "Percentage Distance" to distance.text.toString(),
                        "Refundable Deposit" to refundableDeposit.text.toString()
                    )

                    val name = mapOf<String, Any>(
                        "Done" to "Yes",
                        "Factors" to factors
                    )

                    dbRefInputs.child(nameID).updateChildren(name)

                    TimeUnit.SECONDS.sleep(3L)

                    //val dbRefOutputs = database.getReference("/Python AI Server/")

                    dbRef.addListenerForSingleValueEvent(object: ValueEventListener{

                        override fun onDataChange(newsnapshot: DataSnapshot) {
                            val outputs = newsnapshot.getValue<Map<String, Any>>() as Map<*, *>
                            Log.d("[CLIENT]", "Outputs are: $outputs")

                            val outputNameID = outputs.keys.first().toString()

                            val predictionOutput = findViewById<TextView>(R.id.prediction)
                            val probabilityOutput = findViewById<TextView>(R.id.probability)

                            val inside = outputs[outputNameID] as Map<*, *>

                            val prediction = inside["Prediction"].toString()
                            val probability = inside["Probability"].toString() + "%"

                            predictionOutput.setText(prediction)
                            probabilityOutput.setText(probability)

                            dbRef.removeValue()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w("[CLIENT]", "Failed to read value.", error.toException())
                        }
                    })

//                    dbRef.child("Predictions").get().addOnSuccessListener {
//                        Log.d("[CLIENT]", "Got value ${it.value}")
//
//                        val predictionOutput = findViewById<TextView>(R.id.prediction)
//                        val probabilityOutput = findViewById<TextView>(R.id.probability)
//
//                        val output = it.value
//
//                        Log.d("[CLIENT]", "Output is $output")
//
//                        //val output = it.child(it.value.toString()).value as Map<*, *>
//
////                        predictionOutput.setText(output["Prediction"].toString())
////                        probabilityOutput.setText(output["Probability"].toString())
//                    }.addOnFailureListener{
//                        Log.e("[CLIENT]", "Error getting data", it)
//                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("[CLIENT]", "Failed to read value.", error.toException())
                }
            })

//            dbRefInputs.child(nameID).child("Factors").child("Parking").setValue(parking.text.toString())
//            dbRefInputs.child(nameID).child("Factors").child("Percentage Distance").setValue(distance.text.toString())
//            dbRefInputs.child(nameID).child("Factors").child("Refundable Deposit").setValue(refundableDeposit.text.toString())
//            dbRefInputs.child(nameID).child("Done").setValue("Yes")

            //TimeUnit.SECONDS.sleep(2L)

        }



//        // Read from the database
//        dbRef.addValueEventListener(object: ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                val keys = snapshot.getValue<Map<String, Any>>()
//                Log.d("[CLIENT]", "Value is: $keys")
//
//                val valuesKeys = mutableListOf<String>()
//                val valuesData = mutableListOf<Any>()
//
//                if (keys != null) {
//                    for ((k, v) in keys) {
//                        valuesKeys.add(k)
//                        valuesData.add(v)
//                    }
//                }
//
//                val values = keys?.get(valuesKeys[0]) as Map<*, *>
//                Log.d("[CLIENT]", "Keys are: $values")
//
//                val prediction = values["Prediction"]
//                val probability = values["Probability"]
//
//                Log.d("[CLIENT]", "Prediction is: $prediction")
//                Log.d("[CLIENT]", "Probability is: $probability")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.w("[CLIENT]", "Failed to read value.", error.toException())
//            }
//
//        })
    }
}