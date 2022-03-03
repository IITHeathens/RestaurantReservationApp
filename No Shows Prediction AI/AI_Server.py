import main
import re
import time
import random
import sys
from firebase import firebase

# Set our Google Firebase Database
firebase = firebase.FirebaseApplication("https://resresapp-default-rtdb.asia-southeast1.firebasedatabase.app/", None)

first_name = ""
first_factor_name = ""

if firebase.get("/Python AI Server/Server Status", "") is None:
    data = {
        "Status" : "Operational"
    }

    output = firebase.post("/Python AI Server/Server Status", data)

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


def create_input_factors():
    global first_factor_name

    if firebase.get("/Python AI Server/Input Factors", "") is None:
        # POST empty Prediction to Real-Time Database
        factor_data = {
            "Parking" : "",
            "Percentage Distance" : "",
            "Refundable Deposit" : ""
        }

        data = {
            "Factors" : factor_data,
            "Done" : "No"
        }

        output = firebase.post("/Python AI Server/Input Factors", data)
        print(output)
        first_factor_name = output['name']

        print()

        result = firebase.get("/Python AI Server/Input Factors", "")
        print(result)
    else:
        pass


print("Again")
create_input_factors()

entry_names = [first_name]
factor_names = [first_factor_name]


def mainloop():
    # Server Start
    status = firebase.get("/Python AI Server/Server Status", "")
    operation_name = list(status)[0]
    status_dict = list(status.values())[0]
    operation_output = firebase.put("/Python AI Server/Server Status/" + operation_name, 'Status', 'Operational')
    print("Operation output is ", str(operation_output))

    count = 1
    global first_name, entry_names, factor_names

    parking = 0
    distance = 100.0
    refundable_deposit = 0

    # Heroku Mainloop
    while True:
        # Server Status
        status = firebase.get("/Python AI Server/Server Status", "")
        status_dict = list(status.values())[0]
        if status_dict['Status'] == "Closed":
            break

        factors = firebase.get("/Python AI Server/Input Factors", "")
        name = ""

        counter = 1

        while True:
            # Server Status
            status = firebase.get("/Python AI Server/Server Status", "")
            status_dict = list(status.values())[0]
            if status_dict['Status'] == "Closed":
                break

            factors = firebase.get("/Python AI Server/Input Factors", "")
            done = False

            for entry in factors:
                """print("Entered")
                print(factors)"""

                for inner_entry in factors[entry]:
                    #print(inner_entry)
                    if inner_entry == 'Done' and factors[entry][inner_entry] == "Yes":
                        """print("Entry: " + entry)
                        print("Inner Entry: " + inner_entry)
                        print("Factor: " + factors[entry][inner_entry])"""
                        factor_name = entry
                        name = factor_name

                        current_factor_list = firebase.get("/Python AI Server/Input Factors/" + factor_name, "")

                        parking = int(current_factor_list['Factors']['Parking'])
                        distance = float(current_factor_list['Factors']['Percentage Distance'])
                        refundable_deposit = int(current_factor_list['Factors']['Refundable Deposit'])

                        done = True
                        break

            """counter += 1

            if counter == 5:
                factor_data = {
                    "Parking": "1",
                    "Percentage Distance": "70.0",
                    "Refundable Deposit": "1"
                }

                data = {
                    "Factors": factor_data,
                    "Done": "Yes"
                }

                for field in data:
                    test = firebase.put("/Python AI Server/Input Factors/" + first_factor_name, field, data[field])
                #print(test)"""

            if done:
                break


        print("Found factor inputs at: " + name)

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

        db_output = firebase.post("/Python AI Server/Predictions", db_data)
        print("[POST] - " + str(db_output))
        entry_names.append(db_output['name'])

        print()

        firebase.delete("/Python AI Server/Input Factors", name)

        # Server Status
        status = firebase.get("/Python AI Server/Server Status", "")
        status_dict = list(status.values())[0]
        if status_dict['Status'] == "Closed":
            break

        create_input_factors()

        count += 1
        time.sleep(0.2)


    """while count <= 10:
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
        time.sleep(0.2)"""


mainloop()


"""dictn = firebase.get("/Python AI Server/Predictions", "")
print(dictn)
print()

for entry in dictn:
    entry_names.append(entry)

for name in entry_names:
    print(name)

print()"""

#result = firebase.get("/Python AI Server/Predictions/" + entry_names[4], "")
#print(result)
