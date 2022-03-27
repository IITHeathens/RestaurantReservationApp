import 'package:flutter/material.dart';
import './selection.dart';
import 'custom_color_swatch.dart';

class SelectFactorPage extends StatelessWidget {
  const SelectFactorPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('SELECTION_FACTOR_PAGE '),
      ),
      body: const SelectFactor(),
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
      appBar: AppBar(
        title: const Text('SELECTION_FACTOR_PAGE '),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
                alignment: Alignment.center,
                padding: const EdgeInsets.all(10),
                child: const Text(
                  'You can select either Factor  tab or Selection tab',
                  style: TextStyle(
                      color: Colors.blue,
                      fontWeight: FontWeight.w400,
                      fontSize: 20
                      ),
                )),
            SizedBox(height: 20.0),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget> [
                SizedBox(
                  width: 110,
                  child: ElevatedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const Selection()),
                      );
                    },
                    child: const Text('Factors'),
                  ),
                ),
                SizedBox(
                  width: 50,
                  child: IconButton(
                    icon: const Icon(Icons.info,),
                    tooltip: 'Increase volume by 10',// no need this line
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const Selection()),
                      );
                    },
                  ),
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget> [
                SizedBox(
                  width: 110,
                  child: ElevatedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const Selection()),
                      );
                    },
                    child: const Text('Selection'),
                  ),
                ),
                SizedBox(
                  width: 50,
                  child: IconButton(
                    icon: const Icon(Icons.info,),
                    tooltip: 'Increase volume by 10',// no need this line
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => const Selection()),
                      );
                    },
                  ),
                ),
              ],
            )
          ],
        ),
      ),
    );
    /*SizedBox(height: 40.0),
      ElevatedButton(
        //Temporary
          onPressed: () {
            Navigator.pop(context);
          },
          child: const Text("Go Back")),
      ],
    ),)
    ,
    );*/
  }
}
