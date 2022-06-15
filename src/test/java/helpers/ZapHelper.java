package helpers;


import static java.lang.String.format;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.zaproxy.clientapi.core.Alert;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseList;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import com.google.common.base.Preconditions;

public class ZapHelper {
	
	private static ClientApi clientApi = new ClientApi("127.0.0.1", 8080, null);
    private static String securityTestReportPath = "target/zap-security-report.html";

    public static void spiderTarget(String targetURL) throws InterruptedException, ClientApiException {
        
    	String application_base_url = "https://<application_url>/"; // Example code to start and synchronize the spider scan.private static void startSpiderScan()
        
        System.out.print("Spider : " + application_base_url);

        // Start spider scan
        ApiResponse apiResponse = clientApi.spider.scan(targetURL, null, null, null, null);
        int progress;

        // Scan response returns scan id to support concurrent scanning.
        String scanId = ((ApiResponseElement) apiResponse).getValue();
        // Poll the status till over.
        do {
            Thread.sleep(5000);
            progress = Integer.parseInt(((ApiResponseElement) clientApi.spider.status(scanId)).getValue());
            System.out.println("Scan progress: "+ progress);
        } while (progress < 100);
        System.out.println("scan complete");
        // Process the spider results if needed
        List<ApiResponse> spiderResults = ((ApiResponseList) clientApi.spider.results(scanId)).getItems();
        System.out.println("spider results" + spiderResults);
    }


    public static void waitForPassiveScanToComplete() throws ClientApiException {
        System.out.println("--- Waiting for passive scan to finish --- ");
        try {
            // Passive scanner are run by default: https://stackoverflow.com/a/35944273/270835
            clientApi.pscan.enableAllScanners(); // enables all passive scanner.

            ApiResponse response = clientApi.pscan.recordsToScan();

            //iterating till pending scan count is 0
            while (!response.toString().equals("0")) {
                response = clientApi.pscan.recordsToScan();
            }
        } catch (ClientApiException e) {
            throw new ClientApiException("Was zap proxy started?", e);
        }
        System.out.println("--- Passive scan finished! ---");
    }

    public static void activeScan(String targetURL) throws InterruptedException, ClientApiException {
        System.out.println("Active scan: {}");

        // Start active scan
        ApiResponse resp = clientApi.ascan.scan(targetURL, "True", "False", null, null, null);
        int progress;

        // Scan response returns scan id to support concurrent scanning.
        String scanId = ((ApiResponseElement) resp).getValue();

        // Poll the status till over.
        do {
            Thread.sleep(5000);
            progress = Integer.parseInt(((ApiResponseElement) clientApi.ascan.status(scanId)).getValue());
            System.out.println("Scan progress: "  + progress);
        } while (progress < 100);
        System.out.println("Active scan complete");
    }

    public static void checkRiskCount(String filterURL) throws ClientApiException {
        System.out.println("Target URL"+ filterURL);

        int riskCountHigh = 0;
        int riskCountMedium = 0;
        int riskCountLow = 0;
        int riskCountInformational = 0;
        int totalRiskCount;

        List<Alert> alertList = clientApi.getAlerts(filterURL, 0, 9999999);
        for (Alert alert : alertList) {
            String riskName = alert.getRisk().name();
            Alert.Risk risk = alert.getRisk();
            switch (risk) {
                case High:
                    riskCountHigh = riskCountHigh + 1;
                    break;
                case Medium:
                    riskCountMedium = riskCountMedium + 1;
                    break;
                case Low:
                    riskCountLow = riskCountLow + 1;
                    break;
                case Informational:
                    riskCountInformational = riskCountInformational + 1;
                    break;
                default:
                    throw new IllegalStateException(format("Unknown risk level %s", riskName));
            }
        }
        totalRiskCount = riskCountHigh + riskCountMedium + riskCountLow + riskCountInformational;
        System.out.println("Total risk count: ");
        Preconditions.checkState(totalRiskCount == 0,
                format("Page %s" +
                                "\nHigh Risk count: %s" +
                                "\nMedium Risk count: %s" +
                                "\nLow Risk count: %s" +
                                "\nInformational Risk count: %s" +
                                "\nplease check: %s",
                        filterURL, riskCountHigh, riskCountMedium, riskCountLow, riskCountInformational, securityTestReportPath));
    }

   
    public void generateScanReport() throws ClientApiException, IOException {
        byte[] bytes = clientApi.core.htmlreport();
        String str = new String(bytes, StandardCharsets.UTF_8);
        File newTextFile = new File(securityTestReportPath);
        try (FileWriter fw = new FileWriter(newTextFile)) {
            fw.write(str);
        }
    }

}
