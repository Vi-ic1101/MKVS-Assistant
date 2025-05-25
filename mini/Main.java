
package org.vosk.function;


import java.io.IOException;
import javax.sound.sampled.*;
import edu.cmu.sphinx.api.*;
import marytts.*;
import marytts.exceptions.*;
import marytts.util.data.audio.AudioPlayer;


public class MainWork{
    public static void main(String[] st){

        System.setProperty("mary.audio.sampleRate","22050");
        System.setProperty("mary.audio.bits","16");
        System.setProperty("java.sound.useDefaultMixers","false");

        Configuration config = SpeechConfig.getConfiguration();

        try{
            MaryInterface marytts= new LocalMaryInterface();
            marytts.setVoice("cmu-slt-hsmm");
            marytts.setAudioEffects("volume(amount:0.6)");

            Speaker.speak(marytts,"hello i am mary and i am listening");
            CommandProcessor.startListening(marytts,config);
        }catch (Exception e){
            System.err.println("error"+e.getMessage());
            e.printStackTrace();
        }
    }
}


class SpeechConfig{
    public static Configuration getConfiguration(){
        Configuration config = new Configuration();
        config.getAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        config.getDictionaryPath("src/main/resources/project.dic");
        config.getLanguageModelPath("src/main/resources/project.lm");
        return config;
    }
}


class Speaker {
   public static void speak(MaryInterface marytts, String text){
       try{
           AudioInputStream audio = marytts.generateAudio(text);
           AudioFormat originalFormat = audio.getFormat();

           AudioFormat pcmFormat= new AudioFormat(
                   AudioFormat.encoding.PCM_SIGNED,
                   originalFormat.getSampleRate();
                   sampleSizeInBits:16;
                   OriginalFormat.getChannels(),
                   OriginalFormat.getChannels()*2,
                   originalFormat.getSampleRate(),
                   bigEndian:false
           );

           AudioInputStream pcmAudio=AudioSystem.getAudioInputStream(pcmFormat, audio);

           DataLine.Info info = new DataLine.Info(SourceDataLine.class,pcmFormat);
           SourceDataLine line =(SourceDataLine) AudioSystem.getLine(info);
           line.open(pcmFormat);
           line.start();

           byte[] bufer = new byte[4096];
           int bytesRead;
           while((bytesRead=pcmAudio.read(buffer))!=-1){
               line.write(buffer,0,bytesRead);
           }

           line.drain();
           line.stop();
           line.close();
           pcmAudio.close();
       }catch(Exception e){
           System.err.println("Text to speech error " + e.getMessage());
           e.printStackTrace();
       }
   }
}


class CommandProcessor{
    public static void startListening(MaryInterface marytts,Configuration config) throws IOException{
        LiveSpeechRecognizer recognizer =new LiveSpeechRecognizer(config);
        recognizer.startRecognition(true);

        SpeechResult result;
        boolean exit = false;

        while(!exit &&(result = recognizer.getResult())!=null){
            String command = result.getHypothesis().toIgnoreCase();
            System.out.println("command regonized : "+ command);

            switch(command){
                case "open settings" :
                    Speaker.speak(marytts,"opening settings");
                    Runtime.getRuntime().exec("cmd.exe /c start ms-settings:");
                break;

                case "close settings" :
                    Speaker.speak(marytts,"closing settings");
                    Runtime.getRuntime().exec("cmd.exe /c TASKKILL /IM SystemSettings.exe /F");
                break;

                case "open chrome" :
                    Speaker.speak(marytts,"opening settings");
                    Runtime.getRuntime().exec("cmd.exe /c start chrome.exe");
                break;

                case "close Chrome" :
                    Speaker.speak(marytts,"closing chrome");
                    Runtime.getRuntime().exec("cmd.exe /c TASKKILL /IM chrome.exe /F");
                break;

                case "open youtube" :
                    Speaker.speak(marytts,"opening youtube");
                    Speaker.speak(marytts,"what shouldi search for");

                    Runtime.getRuntime().exec("cmd.exe /c start  https://www.youtube.com/results?search_query=" + ytQuery);
                break;
                case "bye":
                    Speaker.speak(marytts,"thank you");
                    exit=true;
                    break;
            }
        }
    }
}
