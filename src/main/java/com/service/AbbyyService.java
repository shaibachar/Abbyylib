package com.service;

import com.abbyy.ocrsdk.ProcessingSettings;

public interface AbbyyService {
    enum processMode {RECOGNIZE, REMOTE, RECEIPT};

    boolean connect();

    void process(AbbyyService.processMode mode, String language, ProcessingSettings.OutputFormat outputFormat, String receiptCountry, String sourceDirPath, String targetDirPath) throws Exception;
}
