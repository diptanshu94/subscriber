import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import './globals.dart';
import 'dart:async';


class MapPage extends StatefulWidget {

  static BasicMessageChannel<String> platform;
  @override
  _MapPageState createState() => new _MapPageState();
}

class _MapPageState extends State<MapPage> {
  static const String _channel = "map_channel";
  static BasicMessageChannel<String> platform;
  @override
  void initState() {
    super.initState();
    const MethodChannel methodChannel = const MethodChannel('com.locationapp/maps');
    methodChannel.invokeMethod('launchMaps',{"lat": 37.4219999, "long": -122.0840575});

    platform = new BasicMessageChannel<String>(_channel, const StringCodec());

    platform.setMessageHandler(_handleMapMessages);
    platform.send("abcd");
    print("calledmapactivity");
  }

  Future<String> _handleMapMessages(String message) async {
    return "";
  }

  @override
  Widget build(BuildContext context) {
    return new AppBar(
        title: const Text('Map page')
    );
  }
}