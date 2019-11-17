package com.service;

import com.abbyy.ocrsdk.AbbyyClient;
import com.abbyy.ocrsdk.ProcessingSettings;
import com.abbyy.ocrsdk.ReceiptSettings;
import com.abbyy.ocrsdk.Task;
import com.config.AbbyyProperties;
import com.config.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

@Service
public class AbbyyServiceImpl implements AbbyyService {
    private final Logger logger = LoggerFactory.getLogger(AbbyyServiceImpl.class);

    private final AbbyyProperties abbyyProperties;
    private AbbyyClient restAbbyyClient;
    private String outputDir;

    public AbbyyServiceImpl(AbbyyProperties abbyyProperties) {
        this.abbyyProperties = abbyyProperties;
    }

    @Override
    public boolean connect() {
        ClientConfiguration.setupProxy(null, null, null, null);

        restAbbyyClient = new AbbyyClient();
        // replace with 'https://cloud-eu.ocrsdk.com' to enable secure connection
        // replace with 'https://cloud-westus.ocrsdk.com' if your application is created in US location
        restAbbyyClient.serverUrl = abbyyProperties.getAbbyyServerUrl();
        restAbbyyClient.applicationId = abbyyProperties.getAbbyyApplicationId();
        restAbbyyClient.password = abbyyProperties.getAbbyyPassword();

        return true;
    }

    @Override
    public void process(processMode mode, String language, ProcessingSettings.OutputFormat outputFormat, String receiptCountry, String sourceDirPath, String targetDirPath) throws Exception {

        logger.info("Process multiple documents using ABBYY Cloud OCR SDK.\n");

        if (!checkAppId()) {
            return;
        }

        switch(mode){
            case RECOGNIZE:
                performRecognition(language, sourceDirPath, targetDirPath, outputFormat);
                break;
            case REMOTE:
                performRemoteFileRecognition(language, outputFormat, sourceDirPath, targetDirPath);
                break;
            case RECEIPT:
                performReceiptRecognition(receiptCountry, sourceDirPath, targetDirPath);
                break;
        }

    }

    /**
     * Check that user specified application id and password.
     *
     * @return false if no application id or password
     */
    private boolean checkAppId() {
        if (restAbbyyClient.applicationId.isEmpty() || restAbbyyClient.password.isEmpty()) {
            logger.error("Error: No application id and password are specified.");
            return false;
        }
        return true;
    }


    /**
     * @param language
     * @param sourceDirPath
     * @param targetDirPath
     * @param outputFormat
     * @throws Exception
     */
    private void performRecognition(String language, String sourceDirPath, String targetDirPath, ProcessingSettings.OutputFormat outputFormat) throws Exception {

        ProcessingSettings settings = new ProcessingSettings();
        settings.setLanguage(language);
        settings.setOutputFormat(outputFormat);

        setOutputPath(targetDirPath);

        File sourceDir = new File(sourceDirPath);

        File[] listOfFiles = sourceDir.listFiles();

        Vector<String> filesToProcess = new Vector<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile()) {
                String fullPath = file.getAbsolutePath();
                filesToProcess.add(fullPath);
            }
        }

        Map<String, String> taskIds = submitAllFiles(filesToProcess, settings);

        waitAndDownloadResults(taskIds);
    }


    private void performRemoteFileRecognition(String language, ProcessingSettings.OutputFormat outputFormat, String remoteFile, String targetDirPath) throws Exception {

        ProcessingSettings settings = new ProcessingSettings();
        settings.setLanguage(language);
        settings.setOutputFormat(outputFormat);

        setOutputPath(targetDirPath);

        Vector<String> urlsToProcess = new Vector<String>();
        if (remoteFile.startsWith("http://") || remoteFile.startsWith("https://")) {
            urlsToProcess.add(remoteFile);
        } else {
            // Get url list from remoteFile
            BufferedReader br = new BufferedReader(new FileReader(remoteFile));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    urlsToProcess.add(line);
                }
            } finally {
                br.close();
            }
        }

        Map<String, String> taskIds = submitRemoteUrls(urlsToProcess, settings);
        waitAndDownloadResults(taskIds);
    }

    /**
     *
     * @param receiptCountry
     * @param sourceDirPath
     * @param targetDirPath
     * @throws Exception
     */
    private void performReceiptRecognition(String receiptCountry, String sourceDirPath, String targetDirPath) throws Exception {

        ReceiptSettings settings = new ReceiptSettings();
        settings.setReceiptCountry(receiptCountry);

        setOutputPath(targetDirPath);

        File sourceDir = new File(sourceDirPath);

        File[] listOfFiles = sourceDir.listFiles();

        Vector<String> filesToProcess = new Vector<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile()) {
                String fullPath = file.getAbsolutePath();
                filesToProcess.add(fullPath);
            }
        }

        Map<String, String> taskIds = submitAllReceipts(filesToProcess, settings);

        waitAndDownloadResults(taskIds);
    }

    /**
     * Submit all files for recognition
     *
     * @return map task id, file name for submitted tasks
     */
    private Map<String, String> submitAllFiles(Vector<String> fileList, ProcessingSettings settings) throws Exception {
        logger.info(String.format("Uploading %d files..", fileList.size()));

        Map<String, String> taskIds = new HashMap<String, String>();

        for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
            String filePath = fileList.get(fileIndex);

            File file = new File(filePath);
            String fileBase = file.getName();
            if (fileBase.indexOf(".") > 0) {
                fileBase = fileBase.substring(0, fileBase.lastIndexOf("."));
            }

            logger.info(filePath);
            Task task = restAbbyyClient.processImage(filePath, settings);
            taskIds.put(task.Id, fileBase + settings.getOutputFileExt());
        }
        return taskIds;
    }

    /**
     *
     * @param fileList
     * @param settings
     * @return
     * @throws Exception
     */
    private Map<String, String> submitAllReceipts(Vector<String> fileList, ReceiptSettings settings) throws Exception {
        logger.info(String.format("Uploading %d receipts..", fileList.size()));

        Map<String, String> taskIds = new HashMap<String, String>();

        for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
            String filePath = fileList.get(fileIndex);

            File file = new File(filePath);
            String fileBase = file.getName();
            if (fileBase.indexOf(".") > 0) {
                fileBase = fileBase.substring(0, fileBase.lastIndexOf("."));
            }

            logger.info(filePath);
            Task task = restAbbyyClient.processReceipt(filePath, settings);
            taskIds.put(task.Id, fileBase + ".xml");
        }
        return taskIds;
    }

    /**
     *
     * @param urlList
     * @param settings
     * @return
     * @throws Exception
     */
    private Map<String, String> submitRemoteUrls(Vector<String> urlList, ProcessingSettings settings) throws Exception {
        logger.info(String.format("Processing %d urls...", urlList.size()));
        Map<String, String> taskIds = new HashMap<String, String>();

        for (int i = 0; i < urlList.size(); i++) {
            String url = urlList.get(i);

            String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
            String fileBase = fileName.substring(0, fileName.lastIndexOf('.'));

            logger.info(url);
            Task task = restAbbyyClient.processRemoteImage(url, settings);
            taskIds.put(task.Id, fileBase + settings.getOutputFileExt());
        }
        return taskIds;
    }

    /**
     * Wait until tasks are finished and download recognition results
     */
    private void waitAndDownloadResults(Map<String, String> taskIds) throws Exception {
        // Call listFinishedTasks while there are any not completed tasks from taskIds

        // Please note: API call 'listFinishedTasks' returns maximum 100 tasks
        // So, to get all our tasks we need to delete tasks on server. Avoid running
        // parallel programs that are performing recognition with the same Application ID

        logger.info("Waiting..");

        while (taskIds.size() > 0) {
            Task[] finishedTasks = restAbbyyClient.listFinishedTasks();

            for (int i = 0; i < finishedTasks.length; i++) {
                Task task = finishedTasks[i];
                if (taskIds.containsKey(task.Id)) {
                    // Download task
                    String fileName = taskIds.remove(task.Id);

                    if (task.Status == Task.TaskStatus.Completed) {
                        String outputPath = outputDir + "/" + fileName;
                        restAbbyyClient.downloadResult(task, outputPath);
                        logger.info(String.format("Ready %s, %d remains", fileName, taskIds.size()));
                    } else {
                        logger.info(String.format("Failed %s, %d remains", fileName, taskIds.size()));
                    }

                } else {
                    logger.info(String.format("Deleting task %s from server", task.Id));
                }
                restAbbyyClient.deleteTask(task.Id);
            }
            Thread.sleep(2000);
        }
    }


    /**
     * Set output directory and create it if necessary
     */
    private void setOutputPath(String value) {
        outputDir = value;
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


}
