import pandas as pd

# Ignore sklearn module's unnecessary warnings
def warn(*args, **kwargs):
    pass
import warnings
warnings.warn = warn

from sklearn.tree import DecisionTreeClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score


# Section_1: Functions
def run(prking, distance, deposit, getScore):
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

    prediction = model.predict([[prking, distance, deposit]])

    if getScore:
        return score
    else:
        return prediction
