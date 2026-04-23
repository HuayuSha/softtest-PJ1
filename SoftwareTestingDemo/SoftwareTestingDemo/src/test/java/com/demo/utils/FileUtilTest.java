package com.demo.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FileUtilTest {
    @TempDir
    Path tempDir;

    @Test
    void shouldInstantiateUtilityClass() {
        assertThat(new FileUtil()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenVenuePictureIsEmpty() throws Exception {
        MockMultipartFile picture = new MockMultipartFile("picture", "", "image/png", new byte[0]);

        assertThat(FileUtil.saveVenueFile(picture)).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenUserPictureIsEmpty() throws Exception {
        MockMultipartFile picture = new MockMultipartFile("picture", "", "image/png", new byte[0]);

        assertThat(FileUtil.saveUserFile(picture)).isEmpty();
    }

    @Test
    void shouldSavePictureToGivenDirectory() throws Exception {
        MockMultipartFile picture = new MockMultipartFile("picture", "avatar.png", "image/png", "avatar".getBytes());

        String savedName = FileUtil.savePicture(tempDir.toString(), picture);

        assertThat(savedName).endsWith(".png");
        assertThat(Files.readAllBytes(tempDir.resolve(savedName))).isEqualTo("avatar".getBytes());
    }

    @Test
    void shouldSaveVenuePictureUnderStaticDirectory() throws Exception {
        MockMultipartFile picture = new MockMultipartFile("picture", "venue.txt", "text/plain", "venue".getBytes());

        String savedPath = FileUtil.saveVenueFile(picture);

        assertThat(savedPath).startsWith("file/venue/").endsWith(".txt");
        assertSavedClasspathFile(savedPath, "venue");
    }

    @Test
    void shouldSaveUserPictureUnderStaticDirectory() throws Exception {
        MockMultipartFile picture = new MockMultipartFile("picture", "user.txt", "text/plain", "user".getBytes());

        String savedPath = FileUtil.saveUserFile(picture);

        assertThat(savedPath).startsWith("file/user/").endsWith(".txt");
        assertSavedClasspathFile(savedPath, "user");
    }

    private void assertSavedClasspathFile(String savedPath, String expectedContent) throws Exception {
        File savedFile = new File(ClassUtils.getDefaultClassLoader().getResource("static").getPath(), savedPath);
        assertThat(savedFile).exists();
        assertThat(Files.readAllBytes(savedFile.toPath())).isEqualTo(expectedContent.getBytes());
        assertThat(savedFile.delete()).isTrue();
    }
}
