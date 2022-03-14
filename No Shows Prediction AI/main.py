import pandas as pd
import re

# Ignore sklearn module's unnecessary warnings
def warn(*args, **kwargs):
    pass
import warnings
warnings.warn = warn

from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score


# Section_1: Functions
def run(parking, distance, deposit, getScore):
    # Section_1.1: Machine Learning, No Shows Prediction, and Accuracy Score
    noShowDataset = pd.read_csv("Datasets/Output/NoShowDataset.csv")

    X = noShowDataset.drop(columns=["placeID", "No Shows Percentages", "No Shows Decisions"])
    y = noShowDataset["No Shows Decisions"]
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    model = DecisionTreeClassifier()
    model.fit(X_train.values, y_train.values)
    predictions = model.predict(X_test)

    """prediction = model.predict([[0, 100, 0]])
    prediction"""

    score = accuracy_score(y_test, predictions)

    prediction = model.predict([[parking, distance, deposit]])

    if getScore:
        return score
    else:
        return prediction


def based_run(parking, distance, deposit, noShowDataset):
    # Section_1.1: Machine Learning, No Shows Prediction, and Accuracy Score
    X = noShowDataset.drop(columns=["placeID", "No Shows Percentages", "No Shows Decisions"])
    y = noShowDataset["No Shows Decisions"]
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    model = DecisionTreeClassifier()
    model.fit(X_train.values, y_train.values)
    predictions = model.predict(X_test)

    score = accuracy_score(y_test, predictions)

    prediction = model.predict([[parking, distance, deposit]])

    return prediction


def no_show_probability(parking, distance, deposit):
    noShowDataset = pd.read_csv("Datasets/Output/NoShowDatasetPercentages.csv")

    X = noShowDataset.drop(columns=["placeID", "No Shows Percentages", "No Shows Decisions"])
    y = noShowDataset["No Shows Percentages"]
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

    model = DecisionTreeClassifier()
    model.fit(X_train.values, y_train.values)
    predictions = model.predict(X_test)

    score = accuracy_score(y_test, predictions)

    probability_prediction = model.predict([[parking, distance, deposit]])
    prediction = based_run(parking, distance, deposit, noShowDataset)

    return probability_prediction, prediction


def aggregation(parking, distance, deposit):

    probability_list = []
    prediction_list = []

    for count in range(10):
        results = no_show_probability(parking, distance, deposit)
        #print(100 - int(re.sub('\W+', '', str(results[0]))))
        #print(results[1])

        probability = re.sub('\W+', '', str(results[0]))
        probability_list.append(probability)

        prediction_list.append(str(results[1]))

    #print(probability_list)
    #print(prediction_list)

    probability_aggregation = max(set(probability_list), key=probability_list.count)
    prediction_aggregation = max(set(prediction_list), key=prediction_list.count)

    showup_reverseprobability = 100 - int(probability_aggregation)

    return showup_reverseprobability, prediction_aggregation
