package in.koreatech.payment.unit.support;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class MockHttpServer {

    private final MockWebServer mockWebServer;

    public MockHttpServer() {
        try {
            this.mockWebServer = new MockWebServer();
            this.mockWebServer.start();
        } catch (IOException e) {
            throw new IllegalStateException("mock web server failed", e);
        }
    }

    public String baseUrl() {
        return mockWebServer.url("/").toString();
    }

    public void enqueueJson(String json, int statusCode) {
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(statusCode)
            .setHeader("Content-Type", "application/json")
            .setBody(json));
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return mockWebServer.takeRequest();
    }

    public void shutdown() {
        try {
            mockWebServer.shutdown();
        } catch (IOException e) {
            throw new IllegalStateException("mock web server failed", e);
        }
    }
}
