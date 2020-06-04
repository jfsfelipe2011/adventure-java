package com.zegames.main;

// import java.applet.Applet;
// import java.applet.AudioClip;
import java.io.*;
import javax.sound.sampled.*;

public class Sound {
    // Old Method
    /*private AudioClip clip;
    public static final Sound musicBackgroup = new Sound("/music.wav");
    public static final Sound hurtEffect = new Sound("/hurt.wav");

    private Sound (String name) {
        this.clip = null;

        try {
            this.clip = Applet.newAudioClip(Sound.class.getResource(name));
        } catch (Throwable exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void play() {
        try {
            new Thread() {
                public void run() {
                    clip.play();
                }
            }.start();
        } catch (Throwable exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void loop(){
        try {
            new Thread() {
                public void run() {
                    clip.loop();
                }
            }.start();
        } catch (Throwable exception) {
            System.out.println(exception.getMessage());
        }
    }*/

    public static class Clips {
        public Clip[] clips;
        private int p;
        private int count;

        public Clips(byte[] buffer, int count) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
            if (buffer == null) {
                return;
            }
            
            clips = new Clip[count];
            this.count = count;

            for (int i = 0; i < count; i++) {
                clips[i] = AudioSystem.getClip();
                clips[i].open(AudioSystem.getAudioInputStream(new ByteArrayInputStream(buffer)));
            }
        }

        public void play() {
            if (clips == null) {
                return;
            }

            clips[this.p].stop();
            clips[this.p].setFramePosition(0);
            clips[this.p].start();

            this.p++;

            if (this.p >= this.count) {
                this.p = 0;
            }
        }

        public void loop() {
            if (clips == null) {
                return;
            }

            clips[this.p].loop(300);
        }
    }

    public static Clips music = load("/music.wav", 1);
    public static Clips hurt = load("/hurt.wav", 1);

    private static Clips load(String name, int count) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataInputStream dataInputStream = new DataInputStream(Sound.class.getResourceAsStream(name));

            byte[] buffer = new byte[1024];
            int read = 0;

            while ((read = dataInputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, read);
            }

            dataInputStream.close();
            byte[] data = byteArrayOutputStream.toByteArray();

            return new Clips(data, count);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
