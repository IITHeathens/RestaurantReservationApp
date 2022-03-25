import 'package:flutter/material.dart';

void main() => const TestPage();

class TestPage extends StatelessWidget {
  const TestPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Test',
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Test Page'),
        ),
        body: const CheckCode(),
      ),
    );
  }
}

class CheckCode extends StatelessWidget {
  const CheckCode({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget> [
              Container(
                height: 100,
                width: 100,
                color: Colors.blue,
              ),
              Container(
                child: const Text('Splash Screen', style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          ),
        ],
      )),
    );
  }
}
