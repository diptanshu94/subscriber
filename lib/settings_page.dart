import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import './globals.dart';

class SettingsPage extends StatefulWidget {
  const SettingsPage({ Key key }) : super(key: key);

  @override
  SettingsPageState createState() => new SettingsPageState();
}


class SettingsPageState extends State<SettingsPage> {
  final GlobalKey<ScaffoldState> _scaffoldKey = new GlobalKey<ScaffoldState>();
  final TextEditingController _hostNameController = new TextEditingController(text: settingsData.hostName);
  final TextEditingController _portController = new TextEditingController(text: settingsData.port.toString());

  bool _autovalidate = false;

  final GlobalKey<FormState> _formKey = new GlobalKey<FormState>();

  String _validateHostName(String value) {
    if (value.isEmpty)
      return 'Host Name is required.';
    final RegExp validIpAddressRegex = new RegExp(r'^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$');
    final RegExp validHostnameRegex = new RegExp(r'^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\-]*[a-zA-Z0-9])\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\-]*[A-Za-z0-9])$');
    if (!value.startsWith("http://") && !value.startsWith("https://")) {
      return 'Host Name should start with http:// or https://';
    }
    if(value.startsWith("http://")) {
      value = value.replaceFirst("http://", "");
    } else if (value.startsWith("https://")) {
      value = value.replaceFirst("https://", "");
    }
    if(!validIpAddressRegex.hasMatch(value) && !validHostnameRegex.hasMatch(value)) {
      return 'Please enter a valid host name.';
    }
    return null;
  }

  String _validatePort(String value) {
    if (value.isEmpty)
      return 'Port is required.';
    return null;
  }

  void showInSnackBar(String value) {
    _scaffoldKey.currentState.showSnackBar(new SnackBar(
        content: new Text(value)
    ));
  }

  void _handleSubmitted() {
    _autovalidate = true;
    final FormState form = _formKey.currentState;
    if(!form.validate()) {
      showInSnackBar('Please fix the errors in red before submitting.');
    } else {
      form.save();
      Navigator.of(context).pop();
    }
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      key: _scaffoldKey,
      appBar: new AppBar(
        title: const Text('Settings page'),
      ),
      body: new Form(
        key: _formKey,
        autovalidate: _autovalidate,
        child: new ListView(
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          children: <Widget>[
            new TextFormField(
              decoration: const InputDecoration(
                hintText: 'IP address or FQDN',
                labelText: 'Host Name *',
              ),
              onSaved: (String value) async {
                settingsData.hostName = value;
                SharedPreferences prefs = await SharedPreferences.getInstance();
                prefs.setString('hostname', value);
              },
              validator: _validateHostName,
              controller: _hostNameController,
            ),
            new TextFormField(
              decoration: const InputDecoration(
                labelText: 'Port *',
              ),
              keyboardType: TextInputType.number,
              onSaved: (String value) async {
                settingsData.port = int.parse(value);
                SharedPreferences prefs = await SharedPreferences.getInstance();
                prefs.setInt('port', int.parse(value));
              },
              controller: _portController,
              validator: _validatePort,
            ),
            new Container(
              padding: const EdgeInsets.all(20.0),
              alignment: const FractionalOffset(0.5, 0.5),
              child: new RaisedButton(
                child: const Text('SUBMIT'),
                onPressed: _handleSubmitted,
              ),
            )
          ],
        ),
      ),
    );
  }
}

//class SettingsPage extends StatelessWidget {
//  @override
//  Widget build(BuildContext context) {
//    return new Scaffold(
//      appBar: new AppBar(title: new Text('Settings Page')),
//      body: new Center(
//        child: new FlatButton(
//          child: new Text('POP'),
//          onPressed: () {
//            Navigator.of(context).pop();
//          },
//        ),
//      ),
//    );
//  }

