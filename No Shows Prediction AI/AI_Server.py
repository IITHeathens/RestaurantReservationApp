# Updated as at 14th March 2022

import main
import re
import time
import random
import sys
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db


# Set our Google Firebase Database
cred = credentials.Certificate("resresapp-firebase-adminsdk-f9ios-dfb77556fd.json")
firebase_admin.initialize_app(cred, {
    "databaseURL" : "https://resresapp-default-rtdb.asia-southeast1.firebasedatabase.app/"
})

firebase_app_status = db.reference("/Python AI Server/Server Status")

# firebase_app = firebase.FirebaseApplication("https://resresapp-default-rtdb.asia-southeast1.firebasedatabase.app/", None)

data = {
    "Status" : "Operational"
}

firebase_app_status.set(data)


predictions_count = 0
predictions_path = "/Python AI Server/Predictions/"
firebase_app_predictions = db.reference("/Python AI Server/Predictions/0")


def create_prediction_results():
    # POST empty Prediction to Real-Time Database
    global data
    data = {
        "Factors" : "",
        "Prediction" : "None",
        "Probability" : "None"
    }

    firebase_app_predictions.set(data)


create_prediction_results()

inputs_count = 0
inputs_path = "/Python AI Server/Inputs/"
firebase_app_inputs = db.reference("/Python AI Server/Inputs/0")
check = firebase_app_inputs.get()
print(type(check))

def create_input_factors():
    #global first_factor_name

    # POST empty Prediction to Real-Time Database
    factor_data = {
        "Parking" : "",
        "Percentage Distance" : "",
        "Refundable Deposit" : ""
    }

    data_data = {
        "Factors" : factor_data,
        "Done" : "No"
    }

    firebase_app_inputs.set(data_data)
    print()

print("Again")
create_input_factors()


def mainloop():
    # Server Start
    # status = firebase_app_status.get()
    # operation_name = list(status)[0]
    # status_dict = list(status.values())[0]
    # operation_output = firebase_app.put("/Python AI Server/Server Status/" + operation_name, 'Status', 'Operational')
    # print("Operation output is ", str(operation_output))

    global predictions_count, predictions_path, inputs_count, inputs_path
    global firebase_app_predictions, firebase_app_inputs

    firebase_app_predictions = db.reference(predictions_path + str(predictions_count))
    firebase_app_inputs = db.reference(inputs_path + str(inputs_count))

    # inputs_count = 0
    # inputs_path = "/Python AI Server/Inputs/"
    # firebase_app_inputs = db

    parking = 0
    distance = 100.0
    refundable_deposit = 0

    # Heroku Mainloop
    while True:
        # Server Status
        # status = firebase_app.get("/Python AI Server/Server Status", "")
        # status_dict = list(status.values())[0]
        # if status_dict['Status'] == "Closed":
        #     break

        firebase_app_predictions = db.reference(predictions_path + str(predictions_count))
        firebase_app_inputs = db.reference(inputs_path + str(inputs_count))

        factors = firebase_app_inputs.get()
        print(factors)
        print(type(factors))


        while True:
            # Server Status
            # status = firebase_app.get("/Python AI Server/Server Status", "")
            # status_dict = list(status.values())[0]
            # if status_dict['Status'] == "Closed":
            #     break

            factors = firebase_app_inputs.get()
            done = False

            for entry in factors:
                #print("Outer Key: %s", entry)
                if entry == 'Done' and factors[entry] == "Yes":
                    inputs_count += 1

                    parking = int(factors['Factors']['Parking'])
                    distance = float(factors['Factors']['Percentage Distance'])
                    refundable_deposit = int(factors['Factors']['Refundable Deposit'])

                    done = True
                    break

            if done:
                break

        if True:
            firebase_predictions_check = db.reference("/Python AI Server/Predictions")
            predictions_check = firebase_predictions_check.get()
            if predictions_check is None:
                firebase_app_predictions = db.reference("/Python AI Server/Predictions/0")
                create_prediction_results()

                predictions_count = 0
                print("Predictions count is at %s" % predictions_count)
                firebase_app_predictions = db.reference(predictions_path + str(predictions_count))

        print("Predictions count is at %s" % predictions_count)

        print()
        print(parking)
        print(distance)
        print(refundable_deposit)

        print("Found factor inputs at: 0")

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

        # global firebase_app_predictions
        # firebase_app_predictions_child = db.reference(predictions_path + str(predictions_count))
        print("Predictions path is at %s" %predictions_path)
        print("Predictions count is at %s" %predictions_count)
        firebase_app_predictions.update(db_data)
        predictions_count += 1
        print()
        firebase_app_predictions = db.reference("/Python AI Server/Predictions")
        predictions_dict = firebase_app_predictions.get()
        print(predictions_dict)

        # predictions_count = 0
        # for entry in predictions_dict:
        #     print(entry)
        #     predictions_count += 1
        # print("Number of Predictions Available: %s" %predictions_count)

        firebase_app_inputs.delete()

        inputs_count -= 1
        create_input_factors()
        # time.sleep(0.05)


mainloop()
