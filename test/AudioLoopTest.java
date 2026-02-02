package xmod.audio;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import xmod.utils.Utils;
import xmod.constants.Locations;

class AudioLoopTest {
    /**AudioPlayer object. */
    private AudioLoopPlayer player;

    @DisplayName("Check audio file can be found")
    @Test
    public void checkAudioFile(){
        player = new AudioLoopPlayer();
        String file = player.getAudioFile();
        String expected = Locations.TEST_AUDIO_LOCATION;
        String expected2 = "/assets/Arpeggio.wav";
        Assertions.assertEquals(expected, file, "Test audio file not same as in Locations");
        Assertions.assertEquals(expected, file, "Test audio file not correct");
    }

    @DisplayName("Check audio playing")
    @Test
    public void checkPlaying() {
        player = new AudioLoopPlayer();
        try {
            player.loopAudio(); //toggles on

            Assertions.assertEquals(true, player.isRunning(),
                "The audio should be running");
            player.loopAudio(); //toggles off
            Assertions.assertEquals(false, player.isRunning(),
                "The audio should have stopped running");
        } catch (Exception e) {
            Assertions.assertEquals(false, true, "Error in playing the audio");
        }
    }
}
