import main
import re
import time
import random
from firebase import firebase

# Set our Google Firebase Database
firebase = firebase.FirebaseApplication("https://resresapp-default-rtdb.asia-southeast1.firebasedatabase.app/", None)

first_name = ""

if firebase.get("/Python AI Server/Predictions", "") is None:
    # POST empty Prediction to Real-Time Database
    data = {
        "Factors" : "",
        "Prediction" : "None",
        "Probability" : "None"
    }

    output = firebase.post("/Python AI Server/Predictions", data)
    print(output)
    first_name = output['name']

    print()

    result = firebase.get("/Python AI Server/Predictions", "")
    print(result)
else:
    pass

entry_names = [first_name]


def mainloop():
    count = 1
    global first_name, entry_names

    # Heroku Mainloop
    while count <= 10:
        parking = random.randint(0, 1)
        distance = random.uniform(0.0, 100.0)
        refundable_deposit = random.randint(0, 1)

        main_prediction = main.aggregation(parking, distance, refundable_deposit)
        db_prediction = main_prediction[1]
        db_prediction_result = re.sub('\W+', ' ', db_prediction)
        db_prediction_result = db_prediction_result[1: (len(db_prediction_result) - 1)]
        if db_prediction_result == "Won t show up":
            db_prediction_result = "Won't show up"
        print(db_prediction_result)

        db_data_factors = {
            "Parking": str(parking),
            "Percentage Distance": str(distance),
            "Refundable Deposit": str(refundable_deposit)
        }

        db_data = {
            "Factors" : db_data_factors,
            "Prediction" : db_prediction_result,
            "Probability" : str(main_prediction[0])
        }

        if count == 1:
            db_output = ""

            for field in db_data:
                db_output = firebase.put("/Python AI Server/Predictions/" + first_name, field, db_data[field])

            print("[PUT] - " + str(db_output))

            entry_names.append(first_name)
        else:
            db_output = firebase.post("/Python AI Server/Predictions", db_data)
            print("[POST] - " + str(db_output))
            entry_names.append(db_output['name'])

        print()

        count += 1
        time.sleep(0.2)


mainloop()


"""dictn = firebase.get("/Python AI Server/Predictions", "")
print(dictn)
print()

for entry in dictn:
    entry_names.append(entry)

for name in entry_names:
    print(name)

print()"""

result = firebase.get("/Python AI Server/Predictions/" + entry_names[4], "")
print(result)
