package xmod.audio;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

class AudioTest{
    AudioPlayer player;

    String realFile = "./test/testFiles/charlie_short.wav";

    @DisplayName("ValidateAudio")
    @Test
    public void validateAudio() {
        String filepath = this.realFile;
        player = new AudioPlayer(filepath);
        Assertions.assertEquals(true, player.isAudioLoaded(),
                                 "Audio should be loaded");
    }

    @DisplayName("InValidateNullAudio")
    @ParameterizedTest
    @ValueSource(strings = {"", "./test/charlie_short.tms",
                                "./charlie_short2.wav"})
    public void invalidateAudio(final String filepath) {
        player = new AudioPlayer(filepath);
        Assertions.assertEquals(false, player.isAudioLoaded(),
                                 "Audio should not be loaded");
    }

    @DisplayName("Check audio Length")
    @Test
    public void checkLength() {
        player = new AudioPlayer(this.realFile);
        int length = player.getAudioLengthInSeconds();
        Assertions.assertEquals(29, length, "Audio file should be 29s long");
    }


    @DisplayName("Check invalid audio Length")
    @Test
    public void checkInvalidLength() {
        player = new AudioPlayer("");
        int length = player.getAudioLengthInSeconds();
        Assertions.assertEquals(-1, length, "Audio file length should be -1");
    }
}
