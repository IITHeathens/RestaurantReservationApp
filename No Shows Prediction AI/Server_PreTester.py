import main
from firebase import firebase


"""result = main.aggregation(1, 70.0, 1)
print(result[0])
print(result[1])"""

firebase = firebase.FirebaseApplication("https://resresapp-default-rtdb.asia-southeast1.firebasedatabase.app/", None)

factors = firebase.get("/Python AI Server/Input Factors", "")

factor_data = {
    "Parking": "1",
    "Percentage Distance": "0.0",
    "Refundable Deposit": "1"
}

data = {
    "Factors": factor_data,
    "Done": "Yes"
}

for field in data:
    test = firebase.put("/Python AI Server/Input Factors/" + list(factors)[0], field, data[field])

"""print(factors)
print()"""

status = firebase.get("/Python AI Server/Server Status", "")
entry = list(status.values())[0]
key = list(entry)[0]

status_output = firebase.put("/Python AI Server/Server Status/" + list(status)[0], key, 'Closed')
