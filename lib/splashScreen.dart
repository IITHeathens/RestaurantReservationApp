import 'package:flutter/material.dart';
import 'package:restaurant_reservation_app/prototype_tester.dart';
import 'custom_color_swatch.dart';

void main() => const SplashScreen();

class SplashScreen extends StatelessWidget {
  const SplashScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Prototype Tester',
      theme: ThemeData(
        primarySwatch: CustomColorSwatch.swatch,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Splash Screen'),
        ),
        body: const splashScreen(),
      ),
    );
  }
}

class splashScreen extends StatefulWidget {
  const splashScreen({Key? key}) : super(key: key);

  @override
  State<splashScreen> createState() => _splashScreenState();
}

class _splashScreenState extends State<splashScreen> {
  @override
  void initState() {
    super.initState();
    _navigatetohome();
  }

  _navigatetohome() async {
    await Future.delayed(Duration(milliseconds: 1500), () {});
    Navigator.pushReplacement(
        context,
        MaterialPageRoute(
            builder: (context) =>
                PrototypeTester())); //uncompleted - home screen name
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
          child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            height: 100,
            width: 100,
            color: Colors.blue,
          ),
          Container(
            child: Text('Splash Screen',
                style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          ),
        ],
      )),
    );
  }
}
