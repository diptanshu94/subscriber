import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import './globals.dart';
import 'dart:async';


class MapPage extends StatefulWidget {
  @override
  _MapPageState createState() => new _MapPageState();
}

class _MapPageState extends State<MapPage> {
  static const String _channel = "map_channel";
  static const BasicMessageChannel<String> platform =
  const BasicMessageChannel<String>(_channel, const StringCodec());


  @override
  void initState() {
    super.initState();
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