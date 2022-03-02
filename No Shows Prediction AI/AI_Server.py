import main
import re
from firebase import firebase


# Set our Google Firebase Database
firebase = firebase.FirebaseApplication("https://resresapp-default-rtdb.asia-southeast1.firebasedatabase.app/", None)

# Get the AI Predictions
prediction = str(main.run(1, 0.0, 1, False))
prediction_result = re.sub('\W+',' ', prediction)
prediction_result = prediction_result[1 : (len(prediction_result) - 1)]
print(prediction_result)

# POST Prediction to Real-Time Database
data = {
    "Prediction" : prediction_result
}

output = firebase.post("/Python AI Server/Predictions", data)
print(output)

print()

result = firebase.get("/Python AI Server/Predictions", "")
print(result)
