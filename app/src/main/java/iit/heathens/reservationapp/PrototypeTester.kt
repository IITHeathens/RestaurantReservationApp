package iit.heathens.reservationapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.Group
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.platform.MaterialSharedAxis
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class PrototypeTester : AppCompatActivity() {

    private val completionChecks = object {
        var completedPrediction = true
        var clearedPrediction = true
        var check = ""
        var inputCount = 0
        var childrenIsOne = true
        var buttonCount = 0
    }

    private var parking = ""
    private var distance = ""
    private var refundableDeposit = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        val enter = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(androidx.appcompat.R.id.action_bar_container, true)
            excludeTarget(R.id.bottom_navigation, true)
        }
        window.enterTransition = enter

        // Allow Activity A’s exit transition to play at the same time as this Activity’s
        // enter transition instead of playing them sequentially.
        window.allowEnterTransitionOverlap = true

        val exit = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {

            // Only run the transition on the contents of this activity, excluding
            // system bars or app bars if provided by the app’s theme.
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(androidx.appcompat.R.id.action_bar_container, true)
            excludeTarget(R.id.bottom_navigation, true)
        }
        window.exitTransition = exit

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prototype_tester)
        restoreInstanceState(intent)

        //restoreInstanceState(savedInstanceState)

        val resultsView = findViewById<Group>(R.id.resultsView)
        val resultsViewRoot = resultsView.rootView
        resultsView.visibility = View.INVISIBLE


        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationBar.selectedItemId = R.id.inputs

        bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.inputs -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.results -> {
                    val toResultsIntent = Intent(this, PrototypeResults::class.java)
                    val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    Log.d("[NAV BAR]", "Intent processing...")
//                    resultsIntent.putExtra("Correct", correctGuesses.toString())
//                    resultsIntent.putExtra("Incorrect", incorrectGuesses.toString())

                    saveInstanceState(toResultsIntent)

                    startActivity(toResultsIntent, bundle)
                    true
                }
                else -> false
            }
        }

        // Write a message to the database
        val database = Firebase.database
        val dbRef = database.getReference("/Python AI Server/Predictions/")

        //dbRef.setValue("Hello, World!")

        val parkingSwitch = findViewById<SwitchCompat>(R.id.parkingSwitch)
        val distanceSlider = findViewById<Slider>(R.id.distanceSlider)
        val depositSwitch = findViewById<SwitchCompat>(R.id.depositSwitch)

        val confirmBttn = findViewById<Button>(R.id.button1)

        completionChecks.completedPrediction = true
        completionChecks.clearedPrediction = true
        completionChecks.check = ""

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
                    val parkingInput = findViewById<TextView>(R.id.parkingInput)
                    val distanceInput = findViewById<TextView>(R.id.distanceInput)
                    val depositInput = findViewById<TextView>(R.id.depositInput)

                    val inside = outputs[outputNameID] as Map<*, *>

                    val prediction = inside["Prediction"].toString()
                    val probability = inside["Probability"].toString() + "%"

                    var distanceFloat = distance.toFloat()
                    distanceFloat *= 100
                    Log.d("[MATH]", "multiplied number is $distanceFloat")
                    var distanceIntermediary = distanceFloat.roundToInt()
                    Log.d("[MATH]", "rounded number is $distanceIntermediary")
                    val distanceResult = distanceIntermediary.toFloat() / 100
                    Log.d("[MATH]", "divded back number is $distanceResult")
                    val distanceString = "$distanceResult%"

                    //Print to Output Labels with Animations
                    predictionOutput.setText(if (prediction == "Won't show up") {"Won't Show Up"} else {"Will Show Up"})
                    probabilityOutput.setText(probability)

                    parkingInput.setText(if (parking == "1") {"Yes"} else {"No"})
                    distanceInput.setText(distanceString)
                    depositInput.setText(if (refundableDeposit == "1") {"Yes"} else {"No"})

                    val contextView = findViewById<View>(R.id.a_container)

                    val materialFade = MaterialFade().apply {
                        duration = 150L
                    }
                    TransitionManager.beginDelayedTransition(resultsViewRoot as ViewGroup, materialFade)
                    resultsView.visibility = View.VISIBLE

                    Snackbar.make(contextView, "Success.", Snackbar.LENGTH_SHORT)
                        .setAction("DISMISS") {
                            // Responds to click on the action
                        }
                        .show()

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

            val contextView = findViewById<View>(R.id.a_container)

            resultsView.visibility = View.INVISIBLE

            Snackbar.make(contextView, "Data sent successfully.", Snackbar.LENGTH_SHORT)
                .setAction("DISMISS") {
                    // Responds to click on the action
                }
                .show()

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

            parking = if (parkingSwitch.isChecked) {
                "1"
            } else {
                "0"
            }

            distance = distanceSlider.value.toString()

            refundableDeposit = if (depositSwitch.isChecked) {
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

    private fun saveInstanceState(toResultsIntent: Intent) {
        val parkingSwitch = findViewById<SwitchCompat>(R.id.parkingSwitch)
        val distanceSlider = findViewById<Slider>(R.id.distanceSlider)
        val depositSwitch = findViewById<SwitchCompat>(R.id.depositSwitch)

        parking = if (parkingSwitch.isChecked) {"1"} else {"0"}
        distance = distanceSlider.value.toString()
        refundableDeposit = if (depositSwitch.isChecked) {"1"} else {"0"}

        toResultsIntent.putExtra("Parking", parking)
        toResultsIntent.putExtra("Distance", distance)
        toResultsIntent.putExtra("Deposit", refundableDeposit)

        Log.d("[TRANSITION INPUTS]", "Saved: $parking, $distance, $refundableDeposit")
    }

    private fun restoreInstanceState(fromResultsIntent: Intent) {
        Log.d("[TRANSITION INPUTS]", "Entered: $parking, $distance, $refundableDeposit")

        if (fromResultsIntent.extras != null) {
            val parking = fromResultsIntent.getStringExtra("Parking").toString()
            val distance = fromResultsIntent.getStringExtra("Distance").toString()
            val refundableDeposit = fromResultsIntent.getStringExtra("Deposit").toString()

            Log.d("[TRANSITION INPUTS]", "Retrieved: $parking, $distance, $refundableDeposit")

            val parkingSwitch = findViewById<SwitchCompat>(R.id.parkingSwitch)
            val distanceSlider = findViewById<Slider>(R.id.distanceSlider)
            val depositSwitch = findViewById<SwitchCompat>(R.id.depositSwitch)

            parkingSwitch.isChecked = parking == "1"
            distanceSlider.value = distance.toFloat()
            depositSwitch.isChecked = refundableDeposit == "1"
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