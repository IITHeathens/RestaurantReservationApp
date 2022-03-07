package iit.heathens.reservationapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis

class PrototypeResults : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val enter = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(androidx.appcompat.R.id.action_bar_container, true)
            excludeTarget(R.id.bottom_navigation, true)
        }
        window.enterTransition = enter

        // Allow Activity A’s exit transition to play at the same time as this Activity’s
        // enter transition instead of playing them sequentially.
        window.allowEnterTransitionOverlap = true

        val exit = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {

            // Only run the transition on the contents of this activity, excluding
            // system bars or app bars if provided by the app’s theme.
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(androidx.appcompat.R.id.action_bar_container, true)
            excludeTarget(R.id.bottom_navigation, true)
        }
        window.exitTransition = exit

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prototype_results)

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationBar.selectedItemId = R.id.results

        bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.inputs -> {
                    val inputsIntent = Intent(this, PrototypeTester::class.java)
                    val bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                    Log.d("[NAV BAR]", "Intent processing...")
//                    resultsIntent.putExtra("Correct", correctGuesses.toString())
//                    resultsIntent.putExtra("Incorrect", incorrectGuesses.toString())

                    val parking = intent.getStringExtra("Parking")
                    val distance = intent.getStringExtra("Distance")
                    val deposit = intent.getStringExtra("Deposit")

                    inputsIntent.putExtra("Parking", parking)
                    inputsIntent.putExtra("Distance", distance)
                    inputsIntent.putExtra("Deposit", deposit)

                    Log.d("[TRANSITION RESULTS]", "$parking, $distance, $deposit")

                    startActivity(inputsIntent, bundle)
                    true
                }
                R.id.results -> {
                    true
                }
                else -> false
            }
        }
    }
}