import 'package:animations/animations.dart';
import 'package:flutter/material.dart';
import './select_factor_page.dart';

class SignUpPage extends StatelessWidget {
  const SignUpPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Sign Up '),
      ),
      body: Column(children: const <Widget>[
        SignUp(),
      ]),
    );
  }
}

class SignUp extends StatefulWidget {
  const SignUp({Key? key}) : super(key: key);

  @override
  State<SignUp> createState() => _SignUpState();
}

class _SignUpState extends State<SignUp> {
  TextEditingController nameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    String _email;
    String _password;
    String _fullName;
    String _address;
    String _phoneNumber;
    return Scaffold(
      body: Padding(
          padding: const EdgeInsets.all(10),
          child: ListView(
            children: <Widget>[
              Container(
                  alignment: Alignment.center,
                  padding: const EdgeInsets.all(10),
                  child: const Text(
                    'Plebs',
                    style: TextStyle(
                        color: Colors.blue,
                        fontWeight: FontWeight.w500,
                        fontSize: 30),
                  )),
              Container(
                  alignment: Alignment.center,
                  padding: const EdgeInsets.all(10),
                  child: const Text(
                    'Sign Up ',
                    style: TextStyle(fontSize: 20),
                  )),
              TextFormField(
                decoration: InputDecoration(
                  icon: Icon(Icons.person),
                  hintText: "Alex Johnson",
                  labelText: "Full Name",
                  suffixIcon: Icon(
                    Icons.error,
                  ),
                ),
                keyboardType: TextInputType.name,
                validator: (val){
                  if (val?.length == 0)
                    return "Please enter full name";
                  else
                    return null;
                },
                onSaved: (val)=>_fullName=val!,
              ),
              TextFormField(
                decoration: InputDecoration(
                  icon: Icon(Icons.mail),
                  hintText: "aa@bb.com",
                  labelText: "Email",
                  suffixIcon: Icon(
                    Icons.error,
                  ),
                ),
                keyboardType: TextInputType.emailAddress,
                validator: (val){
                  if (val?.length == 0)
                    return "Please enter email";
                  else if (!val!.contains("@"))
                    return "Please enter valid email";
                  else
                    return null;
                },
                onSaved: (val)=>_email=val!,
              ),
              TextFormField(
                decoration: InputDecoration(
                  icon: Icon(Icons.home),
                  hintText: "37 Steet Road Flower Avenue",
                  labelText: "Address",
                  suffixIcon: Icon(
                    Icons.error,
                  ),
                ),
                keyboardType: TextInputType.streetAddress,
                validator: (val){
                  if (val?.length == 0)
                    return "Please enter address";
                  else
                    return null;
                },
                onSaved: (val)=>_address=val!,
              ),
              TextFormField(
                decoration: InputDecoration(
                  icon: Icon(Icons.phone),
                  hintText: "0786160261",
                  labelText: "Phone number",
                  suffixIcon: Icon(
                    Icons.error,
                  ),
                ),
                keyboardType: TextInputType.number,
                validator: (val){
                  if (val?.length == 0)
                    return "Please enter your correct number";
                  else if (val?.length != 10)
                    return "Please enter your correct number";
                  else
                    return null;
                },
                onSaved: (val)=>_phoneNumber=val!,
              ),
              TextFormField(
                decoration: InputDecoration(
                  icon: Icon(Icons.vpn_key),
                  hintText: "Password",
                  labelText: "Password",
                  suffixIcon: Icon(
                    Icons.error,
                  ),
                ),
                obscureText: true,
                validator: (val){
                  if (val!.length == 0)
                    return "Please enter password";
                  else if (val.length <= 5)
                    return "Your password should be more then 6 char long";
                  else
                    return null;
                },
                onSaved: (val)=>_password=val!,
              ),
              TextFormField(
                decoration: InputDecoration(
                  icon: Icon(Icons.vpn_key),
                  hintText: "Confirm Password",
                  labelText: "Confirm Password",
                  suffixIcon: Icon(
                    Icons.error,
                  ),
                ),
                obscureText: true,
                validator: (val){
                  if (val!.length == 0)
                    return "Please renter password";
                  else if (val.length <= 5)
                    return "Your password should be more then 6 char long";
                  else
                    return null;
                },
                onSaved: (val)=>_password=val!,
              ),







              ElevatedButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const SignUp()),
                  );
                },
                child: const Text('Sign Up'),
              ),
              ElevatedButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    _animationRoute(),
                  );
                },
                child: const Text('Selection Factor Page'),
              ),
              ElevatedButton(
                  onPressed: () {
                    Navigator.pop(context);
                  },
                  child: const Text("Go Back")),
            ],
          )),
    );
  }
}

Route _animationRoute() {
  return PageRouteBuilder(
      pageBuilder: (context, animation, secondaryAnimation) => const SelectFactor(),
      transitionsBuilder: (context, animation, secondaryAnimation, child) =>
          SharedAxisTransition(animation: animation, secondaryAnimation: secondaryAnimation, transitionType: SharedAxisTransitionType.horizontal, child: child,)
  );
}
