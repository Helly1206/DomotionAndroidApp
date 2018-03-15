package com.helly.domotion.Connection;

import android.annotation.SuppressLint;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// Created by helly on 1/13/18.
public class BWAClient {
    private final String TagParam = "tag=";
    private final String ResponseParamError = "ERROR";

    final String TagActuators = "actuators";
    final String TagSensors = "sensors";
    final String TagTimers = "timers";
    final String TagHolidays = "holidays";
    final String TagLog = "log";

    private String ServiceUrl = "http:/192.168.1.1";
    private String ServiceCredentials = "";
    private int ServiceTimeout = 3000;
    private TrustManager[] trustAllCerts = new TrustManager[] {
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
        }
    };

    static class BadRequestException extends Exception {
        // 400 Incorrect use of this URL.
    }

    static class UnauthorizedException extends Exception {
        // 401 The server could not verify that you are authorized to access the URL requested. You either supplied the wrong credentials (e.g. a bad password), or your browser doesn't understand how to supply the credentials required.
    }

    static class NotFoundException extends Exception {
        // 404 The requested URL was not found on this server.
    }

    static class InternalServerErrorException extends Exception {
        // 500 The server encountered an internal error and was unable to complete your request. Either the server is overloaded or there is an error in the application.
    }

    static class ServiceUnavailableException extends Exception {
        // 503 It looks like Domotion is not running. Try to restart Domotion and then refresh this page.
    }

    static class GateWayTimeoutException extends Exception {
        // 504 Obtaining information from Domotion timed out. Try refreshing.
    }

    static class Response_Error_Exception extends Exception {
        Response_Error_Exception(String Response_Exception) {
            super(Response_Exception);
        }
    }

    BWAClient(String Url, String Credentials, int Timeout) { // Constructor
        ServiceUrl = Url;
        ServiceCredentials = Credentials;
        ServiceTimeout = Timeout;
    }

    void SetSettings(String Url, String Credentials, int Timeout) {
        ServiceUrl = Url;
        ServiceCredentials = Credentials;
        ServiceTimeout = Timeout;
    }

    public String Get(String Tag) throws IOException, Response_Error_Exception, JSONException, BadRequestException, GateWayTimeoutException, NoSuchAlgorithmException, KeyManagementException, ServiceUnavailableException, InternalServerErrorException, NotFoundException, UnauthorizedException {
        String commandGet = "/get";
        String Response = GetRequest(BuildUrl(ServiceUrl, commandGet),BuildGetMessage(Tag));
        String responseParamGet = "VALUE";
        return DecodeResponse(responseParamGet, Tag, Response);
    }

    public String Set(String Tag, String Value) throws IOException, Response_Error_Exception, JSONException, BadRequestException, GateWayTimeoutException, NoSuchAlgorithmException, KeyManagementException, ServiceUnavailableException, InternalServerErrorException, NotFoundException, UnauthorizedException {
        String commandSet = "/set";
        String Response = GetRequest(BuildUrl(ServiceUrl, commandSet),BuildSetMessage(Tag, Value));
        String responseParamSet = "STORED";
        return DecodeResponse(responseParamSet, Tag, Response);
    }

    JSONArray GetAll(String Tag) throws JSONException, IOException, Response_Error_Exception, BadRequestException, GateWayTimeoutException, NoSuchAlgorithmException, KeyManagementException, ServiceUnavailableException, InternalServerErrorException, NotFoundException, UnauthorizedException {
        String commandGetAll = "/getall";
        String Response = GetRequest(BuildUrl(ServiceUrl, commandGetAll),BuildGetMessage(Tag));
        String responseParamGetAll = "ALL";
        return DecodeJSONResponse(responseParamGetAll, Tag, Response);
    }

    JSONArray GetInfo(String Tag) throws JSONException, IOException, Response_Error_Exception, BadRequestException, GateWayTimeoutException, NoSuchAlgorithmException, KeyManagementException, ServiceUnavailableException, InternalServerErrorException, NotFoundException, UnauthorizedException {
        String commandGetInfo = "/getinfo";
        String Response = GetRequest(BuildUrl(ServiceUrl, commandGetInfo),BuildGetMessage(Tag));
        String responseParamGetInfo = "INFO";
        return DecodeJSONResponse(responseParamGetInfo, Tag, Response);
    }

    private String BuildUrl(String Url, String Command) {
        return (Url + Command);
    }

    private String BuildGetMessage(String Tag) {
        return (TagParam + Tag);
    }

    private String BuildSetMessage(String Tag, String Value) {
        String valueParam = "value=";
        String paramSeparator = "&";
        return (TagParam + Tag + paramSeparator + valueParam + Value);
    }

    private String DecodeResponse(String ResponseParam, String Tag, String Response) throws Response_Error_Exception, JSONException {
        // [VALUE,tag,value] or [STORED,tag,value]
        // [ERROR,tag,null]
        JSONArray jResponse = new JSONArray(Response);
        String rParam = jResponse.optString(0);
        String rTag = jResponse.optString(1);
        String rValue = jResponse.optString(2);

        if (!rParam.equals(ResponseParam)) {
            throw new Response_Error_Exception("Incorrect Response");
        }

        if (rParam.equals(ResponseParamError)) {
            throw new Response_Error_Exception("Response Error");
        }

        if (!rTag.equals(Tag)) {
            throw new Response_Error_Exception("Response Tag Error");
        }

        return rValue;
    }

    private JSONArray DecodeJSONResponse(String ResponseParam, String Tag, String Response) throws Response_Error_Exception, JSONException {
        // [VALUE,tag,value] or [STORED,tag,value]
        // [ERROR,tag,null]
        JSONArray jResponse = new JSONArray(Response);
        String rParam = jResponse.optString(0);
        String rTag = jResponse.optString(1);
        jResponse.remove(0);
        jResponse.remove(0);

        if (!rParam.equals(ResponseParam)) {
            throw new Response_Error_Exception("Incorrect Response");
        }

        if (rParam.equals(ResponseParamError)) {
            throw new Response_Error_Exception("Response Error");
        }

        if (!rTag.equals(Tag)) {
            throw new Response_Error_Exception("Response Tag Error");
        }

        return jResponse;
    }

    private String GetRequest(String Url, String Message) throws IOException, KeyManagementException, NoSuchAlgorithmException, UnauthorizedException, NotFoundException, InternalServerErrorException, ServiceUnavailableException, GateWayTimeoutException, BadRequestException {
        HttpURLConnection Connection;
        String GetUrl = Url + "?" + Message;
        URL ServiceAddress = new URL(GetUrl);

        if (ServiceAddress.getProtocol().toLowerCase().equals("https")) {
            Connection = OpenHttpsConnection(ServiceAddress);
        } else if (ServiceAddress.getProtocol().toLowerCase().equals("http")) {
            Connection = OpenHttpConnection(ServiceAddress);
        } else {
            throw new NoSuchAlgorithmException("Unknown URL Protocol");
        }

        Connection.setReadTimeout(ServiceTimeout);
        Connection.setRequestMethod("GET");

        if (!ServiceCredentials.isEmpty()) {
            Connection.setRequestProperty("Authorization", "Basic " + ServiceCredentials.trim().replace("\n", ""));
        }

        Connection.connect();

        if (Connection.getResponseCode() != 200) {
            Connection.disconnect();
            switch (Connection.getResponseCode()) {
                case 401:
                    throw new UnauthorizedException();
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new InternalServerErrorException();
                case 503:
                    throw new ServiceUnavailableException();
                case 504:
                    throw new GateWayTimeoutException();
                default:
                    throw new BadRequestException();
            }
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(Connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line).append("\n");
        }

        Connection.disconnect();

        if (sb.toString().contains("<!DOCTYPE html>")) {
            sb.setLength(0);
            throw new UnauthorizedException();
        }

        return(sb.toString());
    }

    private HttpsURLConnection OpenHttpsConnection(URL url) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection Connection;
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        Connection = (HttpsURLConnection) url.openConnection();
        Connection.setSSLSocketFactory(sc.getSocketFactory());
        return Connection;
    }

    private HttpURLConnection OpenHttpConnection(URL url) throws IOException {
        HttpURLConnection Connection;
        Connection = (HttpURLConnection) url.openConnection();
        return Connection;
    }
}
