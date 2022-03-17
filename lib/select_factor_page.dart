import 'package:flutter/material.dart';
import './selection.dart';

class SelectFactorPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      debugShowCheckedModeBanner: false,
      home: SelectFactor(),
    );
  }
}

class SelectFactor extends StatefulWidget {
  const SelectFactor({Key? key}) : super(key: key);

  @override
  State<SelectFactor> createState() => _SelectFactorState();
}

class _SelectFactorState extends State<SelectFactor> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
                alignment: Alignment.center,
                padding: const EdgeInsets.all(10),
                child: const Text(
                  'this is what we are going to do here',
                  style: TextStyle(
                      color: Colors.blue,
                      fontWeight: FontWeight.w400,
                      fontSize: 20),
                )),
            SizedBox(height: 8.0),
            ElevatedButton(
              onPressed: () {
              },
              child: Text("    FACTORS   "),
            ),
            SizedBox(height: 8.0),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const Selection()),
                );
              },
              child: Text(" SELECTIONS"),
            ),

            SizedBox(height: 40.0),
            ElevatedButton(
              //Temporary
                onPressed: () {
                  Navigator.pop(context);
                },
                child: const Text("Go Back")),
          ],
        ),
      ),
    );
  }
}

