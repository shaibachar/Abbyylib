    private void displayHelp() {
        logger.info(
                "Recognize multiple files at once.\n" +
                        "Usage:\n" +
                        "  1. Recognize all files from a directory:\n" +
                        "    java com.net.ProcessManyFiles recognize <imagesDir> <resultDir>\n" +
                        "  2. Recognize files from url (experimental):\n" +
                        "    java com.net.ProcessManyFiles remote <imageUrl> <resultDir>\n" +
                        "  3. Recognize many files from urls in a file (experimental):\n" +
                        "    java com.net.ProcessManyFiles remote <urlFilePath> <resultDir>\n" +
                        "  4. Recognize all receipts from a directory:\n" +
                        "    java com.net.ProcessManyFiles receipt <receiptsDir> <resultDir>\n" +
                        "\n" +
                        "For detailed help, call\n" +
                        "  java com.net.ProcessManyFiles help <mode>\n" +
                        "where <mode> is one of: recognize, remote, receipt"
        );
    }

    private void displayRecognizeHelp() {
        logger.info(
                "Recognize all images from a directory.\n"
                        + "\n"
                        + "Usage:\n"
                        + "  java com.net.ProcessManyFiles recognize [--lang=<languages>] [--format=<format>] <directory> <output dir>\n"
                        + "\n"
                        + "Possible output formats:\n"
                        + "  txt, rtf, docx, xlsx, pptx, pdfSearchable, pdfTextAndImages, xml\n"
                        + "  Default format is txt\n"
                        + "\n"
                        + "Examples:\n"
                        + "java com.net.ProcessManyFiles recognize ~/myImages ~/text\n"
                        + "java com.net.ProcessManyFiles recognize --lang=French,Spanish --format=pdfSearchable myImages ocrPdfImages\n");

    }

    private void displayRemoteHelp() {
        logger.info(
                "Recognize images specified by URL.\n"
                        + "\n"
                        + "Usage:\n"
                        + "  java com.net.ProcessManyFiles remote [--lang=<languages>] [--format=<format>] <url|file with urls> <output dir>\n"
                        + "\n"
                        + "If url is specified then only one image from that url is recognized.\n"
                        + "If file is specified then all urls from that file are recognized as different tasks.\n"
                        + "\n"
                        + "Possible output formats:\n"
                        + "  txt, rtf, docx, xlsx, pptx, pdfSearchable, pdfTextAndImages, xml\n"
                        + "  Default format is txt\n"
                        + "\n"
                        + "Examples:\n"
                        + "java com.net.ProcessManyFiles remote https://github.com/abbyysdk/ocrsdk.com/blob/master/SampleData/Page_08.tif?raw=true ~/text\n"
                        + "java com.net.ProcessManyFiles remote --lang=French,Spanish --format=pdfSearchable ~/myUrlList.txt ocrPdfImages\n");

    }
  private void displayReceiptHelp() {
        logger.info(
                "Recognize all receipts from a directory.\n"
                        + "\n"
                        + "Usage:\n"
                        + "  java com.net.ProcessManyFiles receipt [--country=<countryNames>] <directory> <output dir>\n"
                        + "\n"
                        + "--country \n"
                        + "  Set the country where receipt was printed. You can set any country listed at"
                        + " https://ocrsdk.com/documentation/apireference/processReceipt/ or set comma-separated combination of them.\n"
                        + "  Default country is Usa\n"
                        + "\n"
                        + "Examples:\n"
                        + "java com.net.ProcessManyFiles receipt --country=Usa,Spain ~/myReceipts ~/text\n");
    }