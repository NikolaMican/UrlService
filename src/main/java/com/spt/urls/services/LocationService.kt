package com.spt.urls.services

import com.spt.urls.extensions.fromJson
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets
import java.time.Duration


data class LocationApiResponse(val city: String, val country: String) {
/*
{
  "query": "24.48.0.1",
  "status": "success",
  "country": "Canada",
  "countryCode": "CA",
  "region": "QC",
  "regionName": "Quebec",
  "city": "Montreal",
  "zip": "H1K",
  "lat": 45.6085,
  "lon": -73.5493,
  "timezone": "America/Toronto",
  "isp": "Le Groupe Videotron Ltee",
  "org": "Videotron Ltee",
  "as": "AS5769 Videotron Ltee"
}
 */
}

class LocationService {
    /**
     * NOTE:  ip-api.com   limit number of requests per min to 45 for free-package
     *
     * if ip is null, return location where is a server
     */
    fun getLocation(clientIp: String? = null): LocationApiResponse {

        /*
         NOTE: with this implementation, after some time I have error. As you can see it takes 11 sec to get error

2024-03-04 09:47:11.469  INFO w.DynamicUrlWebServiceController         :    clientIp: 69.164.217.245
2024-03-04 09:47:22.387 ERROR 110230 w.DynamicUrlWebServiceController  : failed to get location

java.net.http.HttpConnectTimeoutException: HTTP connect timed out
        at java.net.http/jdk.internal.net.http.HttpClientImpl.send(HttpClientImpl.java:567) ~[java.net.http:na]
        at java.net.http/jdk.internal.net.http.HttpClientFacade.send(HttpClientFacade.java:123) ~[java.net.http:na]

         */






        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI("http://ip-api.com/json/${clientIp ?: ""}"))
            .timeout(Duration.ofMillis(1500))
            .GET()
            .build()

        val response = HttpClient.newBuilder().connectTimeout(Duration.ofMillis(1500)).build()
            .send(request, BodyHandlers.ofString())
        if (response.statusCode() == 200) {
            return response.body().fromJson()
        }
        throw IllegalStateException("failed to get location. StatusCode: ${response.statusCode()}, Error: " + response.body())

//        val responseAsync: CompletableFuture<HttpResponse<String>> = HttpClient.newBuilder()
//            .build()
//            .sendAsync(request, BodyHandlers.ofString())
    }

    fun getLocationUseApacheHttpClient(clientIp: String? = null): LocationApiResponse {
        HttpClients.createDefault().use { client ->
            val httpGetRequest = HttpGet("http://ip-api.com/json/${clientIp ?: ""}")
            val response = client.execute(httpGetRequest)
            if (response.statusLine.statusCode == 200) {
                return EntityUtils.toString(response.entity, StandardCharsets.UTF_8).fromJson()
            }
            throw IllegalStateException("failed to get location. StatusCode: ${response.statusLine.statusCode}, Error: " + EntityUtils.toString(response.entity, StandardCharsets.UTF_8))
        }
    }
}