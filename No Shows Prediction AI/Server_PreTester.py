# Updated as at 14th March 2022

import time
import main
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db


"""result = main.aggregation(1, 70.0, 1)
print(result[0])
print(result[1])"""

# Set our Google Firebase Database
cred = credentials.Certificate("resresapp-firebase-adminsdk-f9ios-dfb77556fd.json")
firebase_admin.initialize_app(cred, {
    "databaseURL" : "https://resresapp-default-rtdb.asia-southeast1.firebasedatabase.app/"
})

firebase_app_status = db.reference("/Python AI Server/Server Status")
inputs_path = "/Python AI Server/Inputs/"

# firebase_app_inputs = db.reference(inputs_path + str(0))
#
# number = 25.0 * 1
# distance = str(number)
#
# factors = firebase_app_inputs.get()
#
# factor_data = {
#     "Parking": "0",
#     "Percentage Distance": distance,
#     "Refundable Deposit": "0"
# }
#
# data = {
#     "Factors": factor_data,
#     "Done": "Yes"
# }
#
# firebase_app_inputs.update(data)

# count = 0
# firebase_app_inputs = db.reference(inputs_path + str(count))
#
# number = 25.0 * count
# distance = str(number)
#
# factors = firebase_app_inputs.get()
#
# factor_data = {
#     "Parking": "0",
#     "Percentage Distance": distance,
#     "Refundable Deposit": "0"
# }
#
# data = {
#     "Factors": factor_data,
#     "Done": "Yes"
# }
#
# firebase_app_inputs.update(data)

db_id = 0

for count in range(5):
    print("Count is at %s" %count)
    print("ID is at %s" %db_id)
    firebase_app_inputs = db.reference(inputs_path + str(db_id))
    factors = firebase_app_inputs.get()
    print(factors)

    while factors["Done"] == "Yes" and db_id >= 0:
        firebase_app_inputs = db.reference(inputs_path + str(db_id))
        factors = firebase_app_inputs.get()

        while factors is None:
            firebase_app_inputs = db.reference(inputs_path + str(db_id))
            factors = firebase_app_inputs.get()

        db_id -= 1

    if db_id == -1:
        db_id = 0

    firebase_app_inputs = db.reference(inputs_path + str(db_id))

    number = 25.0 * count
    distance = str(number)

    # factors = firebase_app_inputs.get()

    factor_data = {
        "Parking": "0",
        "Percentage Distance": distance,
        "Refundable Deposit": "0"
    }

    data = {
        "Factors": factor_data,
        "Done": "Yes"
    }

    firebase_app_inputs.update(data)
    time.sleep(0.5)

# predictions_path = "/Python AI Server/Predictions/"
#
# for count in range(5):
#     firebase_app_predictions = db.reference(predictions_path + str(count))
#     factors = firebase_app_predictions.get()
#     print("Deleting: %s" %factors)
#     firebase_app_predictions.delete()
#     time.sleep(0.25)

# status = firebase.get("/Python AI Server/Server Status", "")
# entry = list(status.values())[0]
# key = list(entry)[0]

#status_output = firebase.put("/Python AI Server/Server Status/" + list(status)[0], key, 'Closed')
