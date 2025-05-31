hello everyone<br>
this is just a sample repository<br>
-**main repository will be different and created accordingly** <br>
plz accept the collaboration request as informed <br>
_______________________________________________________________________________________________________________________
_______________________________________________________________________________________________________________________
1. main() Function (Class: MainWork)
Purpose: This is the main entry point of the program. It initializes everything.

Step-by-step Process:

Sets audio system properties for MaryTTS.

Calls SpeechConfig.getConfiguration() to get the configuration needed for speech recognition.

Initializes the text-to-speech engine marytts using LocalMaryInterface.

Sets the voice type and volume.

Calls Speaker.speak() to say: "Hello, I am Mary and I am listening."

Starts listening for voice commands using CommandProcessor.startListening().

___________________________________________________________________________________________________________________
 2. getConfiguration() Function (Class: SpeechConfig)
Purpose: Creates and returns the configuration for voice recognition.

What It Does:

Creates a new Configuration object.

Sets the paths for:

Acoustic Model → helps detect sounds.

Dictionary → defines valid words.

Language Model → helps with sentence prediction.

Returns the config object to the main() function.
_______________________________________________________________________________________________________
 3. speak() Function (Class: Speaker)
Purpose: Converts text into speech and plays it through the speakers.

What It Does:

Uses marytts.generateAudio() to convert the text into an audio stream.

Converts the audio format into PCM (playable format).

Opens a sound line using AudioSystem.

Plays the audio stream through the system speakers.

Closes the line and stream after playback.
__________________________________________________________________________________________________________
4. startListening() Function (Class: CommandProcessor)
Purpose: Listens for voice commands and performs specific system actions.

What It Does:

Creates a LiveSpeechRecognizer using the config.

Starts continuous listening (startRecognition(true)).

Loops and waits for spoken commands.

For each command it hears, it matches and executes the correct response.
_______________________________________________________________________________________________________
VOICE COMMAND PROCESSING :

User Speaks a Command;
Example: "open chrome", "close settings", etc.

Speech is Captured;
LiveSpeechRecognizer listens and converts the voice to text using the configuration from SpeechConfig.

Command Matching (Switch-Case);
The recognized text is compared against pre-defined commands inside a switch-case block in the startListening() function.

Text-to-Speech Response;
If a match is found, Speaker.speak() is called to reply using MaryTTS.
Example: "Opening settings", "Closing Chrome", etc.

System Command Execution;
Then, the matching system command is executed using:
Runtime.getRuntime().exec(...)

Loop Continues;
The system goes back to listening for the next command unless the command is "bye".

Exit Command;
If the user says "bye", the system responds "Thank you" and stops listening by setting exit = true.


