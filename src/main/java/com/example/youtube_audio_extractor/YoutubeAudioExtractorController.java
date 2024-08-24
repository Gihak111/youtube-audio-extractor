package com.example.youtube_audio_extractor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Controller
public class YoutubeAudioExtractorController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/extract")
    public String extractAudio(@RequestParam("url") String url, Model model) {
        String ffmpegPath = "C:\\Program Files\\ffmpeg-2024-08-21-git-9d15fe77e3-full_build\\bin\\ffmpeg.exe";

        try {
            ProcessBuilder pb = new ProcessBuilder("yt-dlp", "-x", "--audio-format", "mp3", "--ffmpeg-location", ffmpegPath, url);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                Path dir = Paths.get(System.getProperty("user.dir"));
                File[] files = dir.toFile().listFiles((d, name) -> name.endsWith(".mp3"));

                if (files != null && files.length > 0) {
                    File latestFile = Arrays.stream(files)
                            .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                            .orElse(null);

                    if (latestFile != null) {
                        String fileName = latestFile.getName();

                        // 파일 이름을 URL에 적합한 형태로 인코딩
                        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

                        model.addAttribute("message", "Audio extracted successfully: " + fileName);
                        model.addAttribute("filePath", "/download?file=" + encodedFileName);
                    } else {
                        model.addAttribute("message", "Failed to find the extracted audio file.");
                    }
                } else {
                    model.addAttribute("message", "Failed to find the extracted audio file.");
                }
            } else {
                model.addAttribute("message", "Error during audio extraction.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "An error occurred: " + e.getMessage());
        }
        return "result";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("file") String fileName) {
        try {
            // URL에서 받은 파일 이름을 디코딩하여 사용
            String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString());
            Path filePath = FileSystems.getDefault().getPath(System.getProperty("user.dir"), decodedFileName);
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 파일의 MIME 타입을 설정 (MP3 파일은 "audio/mpeg")
            String contentType = "audio/mpeg";

            // 다운로드 응답 헤더를 설정
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
