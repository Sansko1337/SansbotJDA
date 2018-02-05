package ooo.sansk.sansbot.voice;

import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.dv8tion.jda.core.entities.Member;
import ooo.sansk.sansbot.voice.command.VoiceCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecognizerHandler {

    private static final Logger logger = LoggerFactory.getLogger(RecognizerHandler.class);
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private final Member member;
    private final StreamSpeechRecognizer streamSpeechRecognizer;
    private final VoiceCommandHandler voiceCommandHandler;

    private RecognitionRunnable recognitionRunnable;

    public RecognizerHandler(Member member, StreamSpeechRecognizer streamSpeechRecognizer, VoiceCommandHandler voiceCommandHandler) {
        this.member = member;
        this.streamSpeechRecognizer = streamSpeechRecognizer;
        this.voiceCommandHandler = voiceCommandHandler;
    }

    public void startListening() {
        try {
            recognitionRunnable = new RecognitionRunnable(streamSpeechRecognizer);
            recognitionRunnable.start();
//            threadPool.submit(recognitionRunnable);
        } catch (IOException e) {
            logger.error("Exception initializing recognizer thread ({}: {})", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    public void stopListening() {
        recognitionRunnable.terminate();
    }

    public void handleAudio(byte[] audio) {
        recognitionRunnable.sendAudio(audio);
    }

    private class RecognitionRunnable extends Thread {

        private final StreamSpeechRecognizer streamSpeechRecognizer;
        PipedInputStream pipedInputStream = null;
        PipedOutputStream pipedOutputStream = null;

        private boolean active = true;

        public RecognitionRunnable(StreamSpeechRecognizer streamSpeechRecognizer) throws IOException {
            this.streamSpeechRecognizer = streamSpeechRecognizer;
        }

        @Override
        public void run() {
            try {
                pipedInputStream = new PipedInputStream();
                pipedOutputStream = new PipedOutputStream(pipedInputStream);
            } catch (IOException e) {
                logger.error("Exception while creating input pipes ({}: {})", e.getClass().getSimpleName(), e.getMessage());
            }
            logger.info("Loading recognition for {}", member.getNickname());
            streamSpeechRecognizer.startRecognition(pipedInputStream);
            logger.info("Started recognition for {}", member.getNickname());
            SpeechResult result;
            while ((result = streamSpeechRecognizer.getResult()) != null && active) {
                try {
                    if (!result.getHypothesis().isEmpty()) {
                        String hypothesis = result.getHypothesis();
                        logger.info("{} says: {}", member.getNickname(), hypothesis);
                        voiceCommandHandler.onVoiceCommand(member, hypothesis);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            logger.info("Stoping recognition for {}", member.getNickname());
            streamSpeechRecognizer.stopRecognition();
        }

        public void sendAudio(final byte[] audio) {
            try {
                pipedOutputStream.write(audio);
            } catch (IOException e) {
                logger.error("Exception while writing to recognizer input ({}: {})", e.getClass().getSimpleName(), e.getMessage());
            }
        }

        public void terminate() {
            this.active = false;
        }
    }

}
