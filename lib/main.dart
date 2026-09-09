import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(const KeyboardSetupApp());

class KeyboardSetupApp extends StatelessWidget {
  const KeyboardSetupApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Professional Keyboard',
      theme: ThemeData(
        colorSchemeSeed: Colors.indigo,
        useMaterial3: true,
      ),
      home: const SetupScreen(),
    );
  }
}

class SetupScreen extends StatefulWidget {
  const SetupScreen({super.key});

  @override
  State<SetupScreen> createState() => _SetupScreenState();
}

class _SetupScreenState extends State<SetupScreen> with WidgetsBindingObserver {
  static const _channel = MethodChannel('keyboard_app/channel');

  bool _enabled = false;
  bool _selected = false;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _refreshStatus();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    // Re-check status when user comes back from Settings.
    if (state == AppLifecycleState.resumed) {
      _refreshStatus();
    }
  }

  Future<void> _refreshStatus() async {
    setState(() => _loading = true);
    try {
      final enabled = await _channel.invokeMethod<bool>('isKeyboardEnabled') ?? false;
      final selected = await _channel.invokeMethod<bool>('isKeyboardSelected') ?? false;
      setState(() {
        _enabled = enabled;
        _selected = selected;
        _loading = false;
      });
    } on PlatformException {
      setState(() => _loading = false);
    }
  }

  Future<void> _openEnableSettings() async {
    // Opens Settings > System > Languages & input > On-screen keyboard
    // where the user can toggle "Professional Keyboard" ON.
    await _channel.invokeMethod('openLanguageSettings');
  }

  Future<void> _openPicker() async {
    // Opens the system input-method picker so the user can switch
    // the active keyboard to this one.
    await _channel.invokeMethod('showInputMethodPicker');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Professional Keyboard')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _refreshStatus,
              child: ListView(
                padding: const EdgeInsets.all(20),
                children: [
                  const Icon(Icons.keyboard, size: 90, color: Colors.indigo),
                  const SizedBox(height: 16),
                  Text(
                    'Setup',
                    style: Theme.of(context).textTheme.headlineSmall,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 24),
                  _StepCard(
                    stepNumber: 1,
                    title: 'Enable the keyboard',
                    subtitle: 'Turn on "Professional Keyboard" in system settings.',
                    done: _enabled,
                    buttonLabel: 'Open Settings',
                    onPressed: _openEnableSettings,
                  ),
                  const SizedBox(height: 16),
                  _StepCard(
                    stepNumber: 2,
                    title: 'Switch to it',
                    subtitle: 'Pick "Professional Keyboard" as your active keyboard.',
                    done: _selected,
                    buttonLabel: 'Choose Keyboard',
                    onPressed: _enabled ? _openPicker : null,
                  ),
                  const SizedBox(height: 24),
                  if (_enabled && _selected)
                    const Card(
                      color: Color(0xFFE8F5E9),
                      child: Padding(
                        padding: EdgeInsets.all(16),
                        child: Row(
                          children: [
                            Icon(Icons.check_circle, color: Colors.green),
                            SizedBox(width: 12),
                            Expanded(
                              child: Text('All set! Open any text field to try it out.'),
                            ),
                          ],
                        ),
                      ),
                    ),
                ],
              ),
            ),
    );
  }
}

class _StepCard extends StatelessWidget {
  final int stepNumber;
  final String title;
  final String subtitle;
  final bool done;
  final String buttonLabel;
  final VoidCallback? onPressed;

  const _StepCard({
    required this.stepNumber,
    required this.title,
    required this.subtitle,
    required this.done,
    required this.buttonLabel,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 1,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            CircleAvatar(
              backgroundColor: done ? Colors.green : Colors.indigo,
              child: done
                  ? const Icon(Icons.check, color: Colors.white)
                  : Text('$stepNumber', style: const TextStyle(color: Colors.white)),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(title, style: Theme.of(context).textTheme.titleMedium),
                  const SizedBox(height: 4),
                  Text(subtitle, style: Theme.of(context).textTheme.bodySmall),
                  const SizedBox(height: 12),
                  ElevatedButton(
                    onPressed: onPressed,
                    child: Text(buttonLabel),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
