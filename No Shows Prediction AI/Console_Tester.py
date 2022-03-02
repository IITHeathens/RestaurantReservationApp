import main
import time


# Section_1: User Menu
print("""Enter Y to Get the Accuracy Scores,
Or Press Enter to input values to make a No Show Prediction:""")
decision = input()

print("")
print("Press Ctrl+C to stop.")


# Section_2: Accuracy Score Printing
try:
    while decision == "Y" or decision == "y":
        print("The Accuracy Score is: " + str(main.run(0, 100.0, 0, True)))
        time.sleep(0.5)
except KeyboardInterrupt:
    print("Press Ctrl-C to terminate while statement")
    pass


# Section_3: Input Based Prediction Making
if decision == "":
    try:
        while True:
            print("Enter Parking (0 ; No | 1 ; Yes): ")
            parking = int(input())
            print("Enter Distance (0.00% Distance to Travel to the Restaurant ; to 100.00% Distance to Travel: ")
            distance = float(input())
            print("Enter Refundable Deposit (0 ; No | 1 ; Yes): ")
            refundableDeposit = int(input())

            print("")
            print("Result: " + main.run(parking, distance, refundableDeposit, False))
            print("")
    except KeyboardInterrupt:
        print("Press Ctrl-C to terminate while statement")
        pass


print("Done.")
