package iit.heathens.reservationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import com.google.android.material.slider.Slider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class PrototypeTester : AppCompatActivity() {

    private val completionChecks = object {
        var completedPrediction = true
        var clearedPrediction = true
        var check = ""
        var inputCount = 0
        var childrenIsOne = true
        var buttonCount = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prototype_tester)

        // Write a message to the database
        val database = Firebase.database
        val dbRef = database.getReference("/Python AI Server/Predictions/")

        //dbRef.setValue("Hello, World!")

        val parkingYes = findViewById<RadioButton>(R.id.parkingY)
        val distanceSlider = findViewById<Slider>(R.id.distanceSlider)
        val depositYes = findViewById<RadioButton>(R.id.depositY)

        val confirmBttn = findViewById<Button>(R.id.button1)

        completionChecks.completedPrediction = true
        completionChecks.clearedPrediction = true
        completionChecks.check = ""


        var parking = ""
        var distance = ""
        var refundableDeposit = ""

        val dbRefInputs = database.getReference("/Python AI Server/Input Factors/")

        var nameID = ""

        dbRefInputs.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val originalInputs = snapshot.getValue<Map<String, Any>>()

                if (originalInputs != null) {
                    Log.d("[CLIENT]", "dbRef Inputs: dbRef = ${completionChecks.check}")
                    Log.d("[CLIENT]", "dbRef Inputs: newsnapshot children = ${snapshot.children}")
                    Log.d("[CLIENT]", "dbRef Inputs: newsnapshot children count = ${snapshot.childrenCount}")
                    Log.d("[CLIENT]", "dbRef Inputs: nameID was = $nameID")

                    if (snapshot.childrenCount == 1L) {
                        val inputs = originalInputs as Map<*, *>

                        //nameID = inputs.keys.first().toString()
                        Log.d("[CLIENT]", "dbRef Inputs: nameID is = $nameID")

                        completionChecks.childrenIsOne = true
                    } else if (snapshot.childrenCount >= 1L){
                        completionChecks.childrenIsOne = false
                    }
                }

                if (originalInputs != null && completionChecks.clearedPrediction) {
                    Log.d("[CLIENT]", "dbRef Inputs: Entered Inputs Handler")

                    completionChecks.completedPrediction = false
                    completionChecks.clearedPrediction = false

                    val inputs = originalInputs as Map<*, *>

                    Log.d("[CLIENT]", "dbRef Inputs: Inputs are: $inputs")

                    if (nameID == "")
                        nameID = completionChecks.buttonCount.toString()
                    Log.d("[CLIENT]", "dbRef Inputs: NameID is $nameID")

                    val factors = mapOf<String, String>(
                        "Parking" to parking,
                        "Percentage Distance" to distance,
                        "Refundable Deposit" to refundableDeposit
                    )

                    val done = if (((parking == "") && (distance == "") && (refundableDeposit == ""))) {"No"} else {"Yes"}

                    val name = mapOf<String, Any>(
                        "Done" to done,
                        "Factors" to factors
                    )

                    dbRefInputs.child(nameID).updateChildren(name)

                    //TimeUnit.SECONDS.sleep(3L)

                    //val dbRefOutputs = database.getReference("/Python AI Server/")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("[CLIENT]", "dbRef Inputs: Failed to read value.", error.toException())
            }
        })

        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(newsnapshot: DataSnapshot) {

                val outputsOriginal = newsnapshot.getValue<Map<String, Any>>()

                if (outputsOriginal == null) {
                    completionChecks.check = if (!newsnapshot.hasChildren()) {"No children"} else {"Has children"}
                }

                if (outputsOriginal != null) {

                    val outputs = outputsOriginal as Map<*, *>

                    Log.d("[CLIENT]", "dbRef Predictions: Outputs are: $outputs")

                    val outputNameID = outputs.keys.first().toString()

                    val predictionOutput = findViewById<TextView>(R.id.prediction)
                    val probabilityOutput = findViewById<TextView>(R.id.probability)

                    val inside = outputs[outputNameID] as Map<*, *>

                    val prediction = inside["Prediction"].toString()
                    val probability = inside["Probability"].toString() + "%"

                    predictionOutput.setText(prediction)
                    probabilityOutput.setText(probability)

                    Log.d("[CLIENT]", "dbRef Predictions: Children count is: ${newsnapshot.childrenCount}")

                    dbRef.child(outputNameID).removeValue()     //Removed the Prediction from the Database after retrieving it to our app.
                    if (completionChecks.buttonCount != 0) completionChecks.buttonCount -= 1

                    Log.d("[CLIENT]", "dbRef Predictions: Children count is: ${newsnapshot.childrenCount}")

                    completionChecks.check = if (!newsnapshot.hasChildren()) {"No children"} else {"Has children"}
                    Log.d("[CLIENT]", "dbRef Predictions: dbRef = ${completionChecks.check}")
                    Log.d("[CLIENT]", "dbRef Predictions: newsnapshot children = ${newsnapshot.children}")
                    Log.d("[CLIENT]", "dbRef Predictions: newsnapshot children count = ${newsnapshot.childrenCount}")


                    if (completionChecks.check == "No children" && completionChecks.completedPrediction) {
                        completionChecks.clearedPrediction = true
                        Log.d("[CLIENT]", "dbRef Predictions: Cleared Prediction entry.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("[CLIENT]", "dbRef Predictions: Failed to read value.", error.toException())
            }
        })

        confirmBttn.setOnClickListener {

            completionChecks.buttonCount += 1

            if (!completionChecks.childrenIsOne) {
                completionChecks.completedPrediction = false
            } else {
                Log.d("[CLIENT]", "confirmBttn - NameID is $nameID")
                Log.d("[CLIENT]", "confirmBttn - completedPrediction is ${completionChecks.completedPrediction}")
                Log.d("[CLIENT]", "confirmBttn - inputCount is ${completionChecks.inputCount}")
            }

            if (!completionChecks.childrenIsOne && !completionChecks.completedPrediction) {
                completionChecks.inputCount += 1
            }

            if (completionChecks.buttonCount > 1 && !completionChecks.childrenIsOne) {
                Log.d("[CLIENT]", "confirmBttn - buttonCount is ${completionChecks.buttonCount}")
                completionChecks.completedPrediction = false
            }

            parking = if (parkingYes.isChecked) {
                "1"
            } else {
                "0"
            }

            distance = distanceSlider.value.toString()

            refundableDeposit = if (depositYes.isChecked) {
                "1"
            } else {
                "0"
            }

            Log.d("[CLIENT]", "Inputs are: $parking, $distance, and $refundableDeposit")

            if (completionChecks.check == "Has children" && !completionChecks.clearedPrediction) {
                checkCompletion(dbRef)

                if (completionChecks.check == "Has children" && !completionChecks.clearedPrediction) {
                    //TimeUnit.MILLISECONDS.sleep(500L)

                    checkCompletion(dbRef)
                }
            }
            else {
                Log.d("[CLIENT]", "IMPORTANT!!! - dbRefInputs = ${dbRefInputs.get()}")

                //nameID = dbRefInputs.key?.first().toString()

                val factors = mapOf<String, String>(
                    "Parking" to parking,
                    "Percentage Distance" to distance,
                    "Refundable Deposit" to refundableDeposit
                )

                val done = if (((parking == "") && (distance == "") && (refundableDeposit == ""))) {"No"} else {"Yes"}

                val name = mapOf<String, Any>(
                    "Done" to done,
                    "Factors" to factors
                )

                if (completionChecks.buttonCount > 1) {
                    dbRefInputs.child(completionChecks.inputCount.toString()).setValue(name)
                    Log.d("[CLIENT]", "IMPORTANT!!! - MORE PREDICTIONS AVAILABLE")
                    nameID = completionChecks.inputCount.toString()
                    Log.d("[CLIENT]", "IMPORTANT!!! - NAMEID = $nameID")
                } else {
                    dbRefInputs.child(nameID).updateChildren(name)
                }
            }

            //TimeUnit.SECONDS.sleep(2L)

        }
    }

    private fun checkCompletion(dbRef: DatabaseReference) {

        Log.d("[CLIENT]", "checkCompletion: Entered.")

        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("[CLIENT]", "checkCompletion: Detected Data Change.")

                val outputsOriginal = snapshot.getValue<Map<String, Any>>()

                if (outputsOriginal != null) {
                    return
                }
                else {
                    completionChecks.check = if (!snapshot.hasChildren()) {"No children"} else {"Has children"}

                    if (completionChecks.check == "No children" && completionChecks.completedPrediction) {
                        completionChecks.clearedPrediction = true
                        Log.d("[CLIENT]", "checkCompletion: Cleared Prediction entry.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("[CLIENT]", "checkCompletion: Failed to read value.", error.toException())
            }
        })

    }
}