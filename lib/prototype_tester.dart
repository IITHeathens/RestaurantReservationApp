import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:restaurant_reservation_app/custom_color_swatch.dart';

import 'login_page.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  // UserManagement userManagement = UserManagement();
  // userManagement.register();
  // userManagement.verify();
  // userManagement.signIn();
  AIServerIntegration();
  runApp(const PrototypeTester());
}

class UserManagement {
  var acs = ActionCodeSettings(
      // URL you want to redirect back to. The domain (www.example.com) for this
      // URL must be whitelisted in the Firebase Console.
      url: 'https://www.example.com/finishSignUp?cartId=1234',
      // This must be true
      handleCodeInApp: true,
      iOSBundleId: 'iit.heathens.reservationapp',
      androidPackageName:
          'iit.heathens.reservationapp.restaurant_reservation_app',
      // installIfNotAvailable
      androidInstallApp: true,
      // minimumVersion
      androidMinimumVersion: '11');

  verify() {
    var emailAuth = 'alainjiehfeng@gmail.com';
    FirebaseAuth.instance
        .sendSignInLinkToEmail(email: emailAuth, actionCodeSettings: acs)
        .catchError(
            (onError) => print('Error sending email verification $onError'))
        .then((value) => print('Successfully sent email verification'));
  }

  register() async {
    try {
      UserCredential userCredential = await FirebaseAuth.instance
          .createUserWithEmailAndPassword(
              email: "alainjiehfeng@gmail.com", password: "Heathens123567");
    } on FirebaseAuthException catch (e) {
      if (e.code == 'weak-password') {
        print('The password provided is too weak.');
      } else if (e.code == 'email-already-in-use') {
        print('The account already exists for that email.');
      }
    } catch (e) {
      print(e);
    }
  }

  signIn() async {
    try {
      UserCredential userCredential = await FirebaseAuth.instance
          .signInWithEmailAndPassword(
              email: "alainjiehfeng@gmail.com", password: "Heathens123567");
    } on FirebaseAuthException catch (e) {
      if (e.code == 'user-not-found') {
        print('No user found for that email.');
      } else if (e.code == 'wrong-password') {
        print('Wrong password provided for that user.');
      }
    }
  }
}

class AIServerIntegration {
  static FirebaseDatabase database = database = FirebaseDatabase.instance;
  static DatabaseReference reference =
      FirebaseDatabase.instance.ref("Python AI Server/Inputs/0");

  AIServerIntegration() {
    database = FirebaseDatabase.instance;
    reference = FirebaseDatabase.instance.ref("Python AI Server/Inputs/0");
  }

  static DatabaseReference getDBReference() {
    return reference;
  }

  static DatabaseReference getOutputsDBReference() {
    DatabaseReference outputsReference =
        FirebaseDatabase.instance.ref("Python AI Server/Predictions/0");
    return outputsReference;
  }
}

class InputVariables {
  static ValueNotifier<bool> parkingListener = ValueNotifier<bool>(false);
  static ValueNotifier<double> distanceListener = ValueNotifier<double>(50.0);
  static ValueNotifier<bool> depositListener = ValueNotifier<bool>(false);
}

class OutputVariables {
  static ValueNotifier<String> predictionListener =
      ValueNotifier<String>('No Prediction');
  static ValueNotifier<String> percentageListener =
      ValueNotifier<String>('No Percentage');
}

class PrototypeTester extends StatelessWidget {
  const PrototypeTester({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Prototype Tester',
      theme: ThemeData(
        primarySwatch: CustomColorSwatch.swatch,
      ),
      home: const HomeApp(),
    );
  }
}

class HomeApp extends StatelessWidget {
  const HomeApp({Key? key}) : super(key: key);

  setResults(var data) {
    OutputVariables.predictionListener.value = data['Prediction'];
    OutputVariables.percentageListener.value = data['Probability'] + "%";
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Prototype Tester'),
      ),
      body: Column(
        children: <Widget>[
          const InputsContainer(),
          Container(
            margin: const EdgeInsets.only(bottom: 40),
            child: ElevatedButton(
              onPressed: () async {
                DatabaseReference reference =
                    AIServerIntegration.getDBReference();
                //reference.child("Inputs/0");

                String parking = "0";
                String deposit = "0";

                if (InputVariables.parkingListener.value) {
                  parking = "1";
                } else {
                  parking = "0";
                }

                if (InputVariables.depositListener.value) {
                  deposit = "1";
                } else {
                  deposit = "0";
                }

                var factors = {
                  "Parking": parking,
                  "Percentage Distance":
                      InputVariables.distanceListener.value.toString(),
                  "Refundable Deposit": deposit,
                };

                await reference.update({
                  "Done": "Yes",
                  "Factors": factors,
                });

                DatabaseReference outputsReference =
                    AIServerIntegration.getOutputsDBReference();

                var data;

                outputsReference.onValue.listen((DatabaseEvent event) {
                  final dataDB = event.snapshot.value;
                  data = dataDB;
                  print(data);

                  if (data != null) {
                    if (data['Prediction'] != "None") {
                      setResults(data);
                      outputsReference.remove();
                    }
                  }
                });
              },
              child: const Text('AI Recommendation'),
            ),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const LoginPage()),
              );
            },
            child: const Text("Login"),
          ),
          const InputResults(),
          const PredictionResults(),
        ],
      ),
    );
  }
}

class InputResults extends StatelessWidget {
  const InputResults({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 170,
      child: Column(
        children: <Widget>[
          ValueListenableBuilder(
            valueListenable: InputVariables.parkingListener,
            builder: (context, value, widget) {
              return Text('Parking: $value');
            },
          ),
          ValueListenableBuilder(
            valueListenable: InputVariables.distanceListener,
            builder: (context, value, widget) {
              return Text(
                  'Distance: ${double.parse(value.toString()).toStringAsFixed(2)}');
            },
          ),
          ValueListenableBuilder(
            valueListenable: InputVariables.depositListener,
            builder: (context, value, widget) {
              return Text('Deposit: $value');
            },
          ),
        ],
      ),
    );
  }
}

class PredictionResults extends StatelessWidget {
  const PredictionResults({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Column(
        children: <Widget>[
          ValueListenableBuilder(
            valueListenable: OutputVariables.predictionListener,
            builder: (context, value, widget) {
              return Text('Prediction: $value');
            },
          ),
          ValueListenableBuilder(
            valueListenable: OutputVariables.percentageListener,
            builder: (context, value, widget) {
              return Text('Probability to Show Up: $value');
            },
          ),
        ],
      ),
    );
  }
}

class InputsContainer extends StatelessWidget {
  const InputsContainer({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 300,
      child: Row(
        children: const <Widget>[
          Expanded(
            flex: 1,
            child: InputLabels(),
          ),
          Expanded(
            flex: 2,
            child: Inputs(),
          ),
        ],
      ),
    );
  }
}

class Inputs extends StatefulWidget {
  const Inputs({Key? key}) : super(key: key);

  @override
  State<Inputs> createState() => _InputsState();
}

class _InputsState extends State<Inputs> {
  bool parkingIsSwitched = false;
  double sliderValue = 50.0;
  bool depositIsSwitched = false;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 200,
      child: Column(
        children: <Widget>[
          Expanded(
            child: Switch(
                value: parkingIsSwitched,
                //activeColor: color,
                onChanged: (value) {
                  setState(() {
                    parkingIsSwitched = value;
                    InputVariables.parkingListener.value = parkingIsSwitched;
                  });
                }),
          ),
          Expanded(
            child: Slider(
                value: sliderValue,
                /*activeColor: active,
                inactiveColor: inactive,
                thumbColor: thumb,*/
                min: 0.0,
                max: 100.0,
                divisions: 10000,
                label: sliderValue.toStringAsFixed(2),
                onChanged: (value) {
                  setState(() {
                    sliderValue = value;
                    InputVariables.distanceListener.value = sliderValue;
                  });
                }),
          ),
          Expanded(
            child: Switch(
                value: depositIsSwitched,
                //activeColor: color,
                onChanged: (value) {
                  setState(() {
                    depositIsSwitched = value;
                    InputVariables.depositListener.value = depositIsSwitched;
                  });
                }),
          ),
        ],
      ),
    );
  }
}

class InputLabels extends StatelessWidget {
  const InputLabels({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 200,
      child: Column(
        children: const <Widget>[
          Expanded(
            //flex: 3,
            child: Center(child: Text('Parking:')),
          ),
          Expanded(
            child: Center(child: Text('Distance:')),
          ),
          Expanded(
            child: Center(child: Text('Deposit:')),
          ),
        ],
      ),
    );
  }
}
