import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_sound/flutter_sound.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<String> ringtones = [];
  final player = FlutterSoundPlayer();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Device Ringtones'),
        ),
        body: Column(
          children: [
            ElevatedButton(
              onPressed: () async {
                const channel = MethodChannel('flutter_channel');
                var result = await channel.invokeMethod('getRingtones');

                if (result is List) {
                  ringtones = result.whereType<String>().toList();
                }

                setState(() {});
              },
              child: const Text('Get Ringtones'),
            ),
            Expanded(
              child: ListView.builder(
                itemCount: ringtones.length,
                itemBuilder: (context, index) {
                  final ringtone = ringtones[index];
                  return Card(
                    child: ListTile(
                      onTap: () {},
                      title: Text(ringtone),
                    ),
                  );
                },
              ),
            )
          ],
        ),
      ),
    );
  }
}
