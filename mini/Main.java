package org.MKVS.mini;

import java.io.IOException;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;
import javax.sound.sampled.AudioInputStream;

import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioFileFormat;

//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;



public class New {

    public static void main(String[] st) {
        //System.setProperties("webdriver.chrome.driver",);

        // Set audio properties to prevent crackling
        System.setProperty("mary.audio.sampleRate", "22050"); // Try different sample rate
        System.setProperty("mary.audio.bits", "16");
        System.setProperty("java.sound.useDefaultMixers", "false");

        Configuration config = new Configuration();

        config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        config.setDictionaryPath("src\\main\\resources\\project.dic");
        config.setLanguageModelPath("src\\main\\resources\\project.lm");

        try {
            // Create MaryTTS interface
            MaryInterface marytts = new LocalMaryInterface();

            // Set the voice and audio effects to prevent crackling
            marytts.setVoice("cmu-slt-hsmm"); // Keep your original voice
            marytts.setAudioEffects("Volume(amount:0.6)"); // Further reduce volume

            speak(marytts,"Hello! This is Mary speaking. And I am the voice of this project");

            LiveSpeechRecognizer mySpeech = new LiveSpeechRecognizer(config);
            mySpeech.startRecognition(true);

            SpeechResult newS = null;

            boolean exit=false;

            while (!exit && (newS = mySpeech.getResult()) != null) {
                String myVoice = newS.getHypothesis();
                System.out.println("given voice : " + myVoice);
                switch (myVoice.toLowerCase()) {

                    case "open settings":
                        speak(marytts,"opening settings");
                        Runtime.getRuntime().exec("cmd.exe /c start ms-settings:");
                        break;

                    case "close settings":
                        speak(marytts,"closing settings");
                        Runtime.getRuntime().exec("cmd.exe /c TASKKILL /IM SystemSettings.exe /F");
                        break;

                    case "open chrome":
                        speak(marytts,"opening chrome");
                        Runtime.getRuntime().exec("cmd.exe /c start chrome.exe");
                        break;

                    case "close chrome":
                        speak(marytts,"closing settings");
                        Runtime.getRuntime().exec("cmd.exe /c TASKKILL /IM chrome.exe /F");
                        break;

                    case "open youtube":
                        speak(marytts,"opening youtube");
                        speak(marytts,"what should i search for");
                        SpeechResult ytQuery = mySpeech.getResult();
                        if(ytQuery!=null){
                            String ytSearch = ytQuery.getHypothesis();
                            speak(marytts,"serching on youtube "+ytSearch);
                            String urL="https://www.youtube.com/results?search_query=" + ytSearch.replace(" ","+");
                            Runtime.getRuntime().exec("cmd.exe /c start "+ urL);
                        }else{
                            speak(marytts,"sorry i dont get it");
                        }

                        break;

                    case "close youtube":
                        speak(marytts,"closing settings");
                        Runtime.getRuntime().exec("cmd.exe /c TASKKILL /IM youtube.com /F");
                        break;

                    case "bye":
                        speak(marytts,"thank you");
                        exit = true;
                        break;

                    default:
                        speak(marytts,"Invalid Command");
                        break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error during recognition:");
            e.printStackTrace();
        } catch (MaryConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    // Fixed speak method with format conversion to handle unsupported format error
    private static void speak(MaryInterface marytts, String text) {
        try {
            // Generate audio
            AudioInputStream audio = marytts.generateAudio(text);

            // Get the original format
            javax.sound.sampled.AudioFormat originalFormat = audio.getFormat();

            // Create a compatible PCM format
            javax.sound.sampled.AudioFormat pcmFormat = new javax.sound.sampled.AudioFormat(
                    javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                    originalFormat.getSampleRate(),
                    16, // 16-bit
                    originalFormat.getChannels(),
                    originalFormat.getChannels() * 2, // frame size
                    originalFormat.getSampleRate(),
                    false // little endian
            );

            // Convert to PCM if needed
            AudioInputStream pcmAudio = audio;
            if (!AudioSystem.isConversionSupported(pcmFormat, originalFormat)) {
                // Try to get a converted stream
                if (AudioSystem.isConversionSupported(pcmFormat, originalFormat)) {
                    pcmAudio = AudioSystem.getAudioInputStream(pcmFormat, audio);
                }
            } else {
                pcmAudio = AudioSystem.getAudioInputStream(pcmFormat, audio);
            }

            // Use SourceDataLine for playback
            javax.sound.sampled.DataLine.Info info = new javax.sound.sampled.DataLine.Info(
                    javax.sound.sampled.SourceDataLine.class, pcmFormat);

            javax.sound.sampled.SourceDataLine line = (javax.sound.sampled.SourceDataLine) AudioSystem.getLine(info);
            line.open(pcmFormat);
            line.start();

            // Play the audio
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = pcmAudio.read(buffer)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            // Clean up
            line.drain();
            line.stop();
            line.close();
            pcmAudio.close();
            if (pcmAudio != audio) {
                audio.close();
            }

        } catch (Exception e) {
            System.err.println("Error in speech synthesis: " + e.getMessage());
            e.printStackTrace();

            // Fallback to simple AudioPlayer method
            try {
                AudioInputStream audio = marytts.generateAudio(text);
                AudioPlayer player = new AudioPlayer(audio);
                player.start();
                player.join();
            } catch (Exception fallbackError) {
                System.err.println("Fallback method also failed: " + fallbackError.getMessage());
            }
        }
    }
}
