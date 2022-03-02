import pandas as pd
import random
import math
from sklearn import preprocessing


# Initialize Global Variables
noShowDataset = None
model = None
prediction = ""
score = 0.0

# Section_1: Customer Geo-Location Dataset Preparation
usercuisine = pd.read_csv("Datasets/usercuisine.csv")
customerInfo = pd.read_csv("Datasets/userprofile.csv")
customerLocations = customerInfo[['userID', 'latitude', 'longitude']].copy()

restaurantInfo = pd.read_csv("Datasets/geoplaces2.csv")
restaurantInfo = restaurantInfo[['placeID', 'latitude', 'longitude']].copy()

# Section_2: Restaurant & Parking Information Dataset Addition
parkingInfo = pd.read_csv("Datasets/chefmozparking.csv")

parkingLot = parkingInfo['parking_lot']

le = preprocessing.LabelEncoder()
le.fit(parkingLot)
updated = le.transform(parkingLot)
parkingLot.update(updated)

for placeID, parking in parkingInfo.iterrows():

    if parking['parking_lot'] == 1:
        parkingInfo.at[placeID, 'parking_lot'] = 0
    else:
        parkingInfo.at[placeID, 'parking_lot'] = 1

parkingCombo = parkingInfo.merge(restaurantInfo, left_on="placeID", right_on="placeID")

# Section_3: Percentage Distance Away from Customer to Restaurant (from a maximum of the furthest customer from the
# furthest restaurant)
customerLatitude = []
customerLongitude = []
restaurantLatitude = []
restaurantLongitude = []
distances = []

for ind in customerLocations.index:
    customerLatitude.append((customerLocations['latitude'][ind]))
    customerLongitude.append((customerLocations['longitude'][ind]))

for ind in restaurantInfo.index:
    restaurantLatitude.append((restaurantInfo['latitude'][ind]))
    restaurantLongitude.append((restaurantInfo['longitude'][ind]))

for count in range(130):
    dist = math.hypot(customerLatitude[count] - customerLongitude[count],
                      restaurantLatitude[count] - restaurantLongitude[count])
    distances.append(dist)

total = 0
max = 0
min = 180

for number in distances:
    total = total + number

    if number > max:
        max = number

    if number < min:
        min = number

meander = total / 130

percentages = []

for number in distances:
    percentages.append((100 - (((number - min) * 100) / (max - min))))

parkingCombo['Percentage Distances Away'] = percentages

# Section_4: Refundable Deposits Addition (based on restaurant pricing)
restaurantPricing = pd.read_csv("Datasets/geoplaces2.csv")
restaurantPricing = restaurantPricing[['placeID', 'price']].copy()

refundableDeposits = []

for index, columns in restaurantPricing.iterrows():

    if columns['price'] != "low":
        refundableDeposits.append(1)
    else:
        refundableDeposits.append(0)

restaurantPricing['Refundable Deposits'] = refundableDeposits

restaurantDeposits = restaurantPricing[['placeID', 'Refundable Deposits']].copy()

# Section_5: Final No Shows Dataset
parkingCombo = parkingCombo.drop(columns=['latitude', 'longitude'])
noShowDataset = parkingCombo.merge(restaurantDeposits, left_on="placeID", right_on="placeID")

randParking = []
randDistances = []
randDeposits = []

for index, column in noShowDataset.iterrows():

    if column['parking_lot'] == 0:
        randParking.append(33.33)
    else:
        randParking.append(0)

    randDistances.append(((column['Percentage Distances Away'] / 100) * 33))

    if column['Refundable Deposits'] == 0:
        randDeposits.append(33.33)
    else:
        randDeposits.append(0)

noShowPercentages = []
noShowsDecison = []

for count in range(len(randParking)):

    totalPercentage = randParking[count] + randDistances[count] + randDeposits[count]

    if totalPercentage == 99.99:
        totalPercentage = 100
    elif (randDistances[count] < 10 and randDeposits[count] == 1):
        totalPercentage = 100

    if (randParking[count] == 1 and randDeposits[count] == 1):
        totalPercentage = 100

    noShowPercentages.append(totalPercentage)

    percentageCalculator = (random.random() < (totalPercentage / 100))

    yesOrNo = 0

    if percentageCalculator == True:
        yesOrNo = 1

    if yesOrNo == 0:
        noShowsDecison.append("Will show up.")
    else:
        noShowsDecison.append("Won't show up.")

noShowDataset['No Shows Percentages'] = noShowPercentages
noShowDataset['No Shows Decisions'] = noShowsDecison


# Export Dataset to .csv file
decision = input("Do you want to generate a new No Show Dataset? Y/N:")
decision_2 = input("Are you sure? Y/N:")

if (decision == "y" or decision == "Y") and (decision_2 == "y" or decision_2 == "Y"):
    noShowDataset.to_csv("Datasets/Output/NoShowDataset.csv", index=False)
else:
    print("Ok.")
