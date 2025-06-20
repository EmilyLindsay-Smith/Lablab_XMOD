package xmod.audio;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import xmod.utils.Utils;

class AudioTest {
    /**AudioPlayer object. */
    private AudioPlayer player;
    /**Real audio file in testFiles. */
    private String realFile = "./test/testFiles/charlie_short.wav";

    @DisplayName("ValidateAudio")
    @Test
    public void validateAudio() {
        String filepath = this.realFile;
        player = new AudioPlayer();
        player.loadAudio(filepath);
        Assertions.assertEquals(true, player.isAudioLoaded(),
                                 "Audio should be loaded");
    }

    @DisplayName("InValidateNullAudio")
    @ParameterizedTest
    @ValueSource(strings = {"", "./test/charlie_short.tms",
                                "./charlie_short2.wav"})
    public void invalidateAudio(final String filepath) {
        player = new AudioPlayer();
        player.loadAudio(filepath);
        Assertions.assertEquals(false, player.isAudioLoaded(),
                                 "Audio should not be loaded");
    }

    @DisplayName("Check audio Length")
    @Test
    public void checkLength() {
        player = new AudioPlayer();
        player.loadAudio(this.realFile);
        int length = player.getAudioLengthInSeconds();
        Assertions.assertEquals(29, length, "Audio file should be 29s long");
    }


    @DisplayName("Check invalid audio Length")
    @Test
    public void checkInvalidLength() {
        player = new AudioPlayer();
        player.loadAudio("");
        int length = player.getAudioLengthInSeconds();
        Assertions.assertEquals(-1, length, "Audio file length should be -1");
    }

    @DisplayName("Check audio playing")
    @Test
    public void checkPlaying() {
        player = new AudioPlayer();
        player.loadAudio(this.realFile);
        try {
            player.playAudio();
            Utils.pause(5000);
            player.stopAudio();
            Assertions.assertEquals(true, true, "The audio should play");
        } catch (Exception e) {
            Assertions.assertEquals(false, true, "Error in playing the audio");
        }
    }
}
