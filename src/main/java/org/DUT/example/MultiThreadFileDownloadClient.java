package org.DUT.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class MultiThreadFileDownloadClient {

    private static final String DOWNLOAD_URL = "http://10.7.8.7:1025/asyncDownload";
    private static final String FILE_NAME = "DataGrip.zip";
    private static final String SAVE_PATH = "C:\\Users\\young\\Downloads\\test\\";
    private static final int THREAD_COUNT = 10; // Number of download threads

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        long fileSize = getFileSize();
        long partSize = fileSize / THREAD_COUNT;
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CompletionService<Void> completionService = new ExecutorCompletionService<>(executorService);

        for (int i = 0; i < THREAD_COUNT; i++) {
            long start = i * partSize;
            long end = (i == THREAD_COUNT - 1) ? fileSize - 1 : start + partSize - 1;
            completionService.submit(new DownloadTask(DOWNLOAD_URL, FILE_NAME, SAVE_PATH, i, start, end));
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            completionService.take().get();
        }

        executorService.shutdown();
        mergeFiles(SAVE_PATH, FILE_NAME, THREAD_COUNT);
        long endTime = System.currentTimeMillis();
        System.out.println(String.format("文件下载时间:%.2f s",(endTime-startTime)/1000.0));
    }

    private static long getFileSize() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(DOWNLOAD_URL + "?filename=" + FILE_NAME).openConnection();
        connection.setRequestMethod("HEAD");
        return connection.getContentLengthLong();
    }

    private static void mergeFiles(String savePath, String fileName, int partCount) throws IOException {
        try (RandomAccessFile mergedFile = new RandomAccessFile(savePath + fileName, "rw")) {
            for (int i = 0; i < partCount; i++) {
                File partFile = new File(savePath + fileName + ".part" + i);
                try (FileInputStream fis = new FileInputStream(partFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        mergedFile.write(buffer, 0, bytesRead);
                    }
                }
                partFile.delete();
            }
        }
    }

    static class DownloadTask implements Callable<Void> {
        private final String downloadUrl;
        private final String fileName;
        private final String savePath;
        private final int partIndex;
        private final long start;
        private final long end;

        public DownloadTask(String downloadUrl, String fileName, String savePath, int partIndex, long start, long end) {
            this.downloadUrl = downloadUrl;
            this.fileName = fileName;
            this.savePath = savePath;
            this.partIndex = partIndex;
            this.start = start;
            this.end = end;
        }

        @Override
        public Void call() throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(downloadUrl + "?filename=" + fileName).openConnection();
            connection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                try (InputStream inputStream = connection.getInputStream();
                     RandomAccessFile partFile = new RandomAccessFile(savePath + fileName + ".part" + partIndex, "rw")) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalBytesRead = 0;
                    long startTime = System.currentTimeMillis();

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        partFile.write(buffer, 0, bytesRead);
                        totalBytesRead += bytesRead;
                        long currentTime = System.currentTimeMillis();
                        long elapsedTime = currentTime - startTime;
                        if (elapsedTime > 1000) { // Update speed every second
                            double speed = (totalBytesRead / 1024.0/1024) / (elapsedTime / 1000.0); // KB/s
                            //System.out.printf("Thread %d: Download speed: %.2f MB/s\n", partIndex, speed);
                            startTime = currentTime;
                            totalBytesRead = 0;
                        }
                    }
                }
            } else {
                throw new IOException("Server did not support partial content. Response code: " + responseCode);
            }

            return null;
        }
    }
}
