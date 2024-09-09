package com.spt.urls.logs

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.StreamUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.*
import java.util.stream.Collectors
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse


/**
 * NOTE: to enable request logging (before processing by spring controllers and handlers) uncomment class 'MyFilterConfig'
 */
//@Configuration
//open class MyFilterConfig {
//    @Bean
//    open fun loggingFilter() = FilterRegistrationBean<MyRequestLoggingFilter>().apply {
//            filter = MyRequestLoggingFilter()
//            addUrlPatterns("/*")
//            println("****************  initiated MyFilterConfig")
//        }
//}


@WebFilter("/*")
class MyRequestLoggingFilter : OncePerRequestFilter() {
    private val LOG = TicketLoggerFactory.getTicketLogger(MyRequestLoggingFilter::class.java)

    override fun destroy() {
        // Cleanup logic, if any
    }

    override fun doFilterInternal(request1: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val request: HttpServletRequest = CachedBodyHttpServletRequest(request1)

        if ("POST" == request.method) {
            val body = request.reader.lines().collect(Collectors.joining(System.lineSeparator()))
            LOG.debug("[request interceptor], method: post, request: ${request.requestURI}, body: $body")
        } else {
            LOG.debug("[request interceptor], method: get, request: ${request.requestURI}")
        }

        // Log request information
        // For example: request.getMethod(), request.getRequestURL(), request.getHeader("...")

        // Proceed with the request
        filterChain.doFilter(request, response)

        // Log response information
        // For example: response.getStatus(), response.getHeader("...")
    }
}

class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private val cachedBody: ByteArray

    init {
        val requestInputStream: InputStream = request.inputStream
        cachedBody = StreamUtils.copyToByteArray(requestInputStream)
    }

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        return CachedBodyServletInputStream(cachedBody)
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        val byteArrayInputStream = ByteArrayInputStream(cachedBody)
        return BufferedReader(InputStreamReader(byteArrayInputStream))
    }


}

class CachedBodyServletInputStream(cachedBody: ByteArray) : ServletInputStream() {
    private val cachedBodyInputStream: InputStream

    init {
        cachedBodyInputStream = ByteArrayInputStream(cachedBody)
    }

    @Throws(IOException::class)
    override fun read(): Int {
        return cachedBodyInputStream.read()
    }

    override fun isFinished(): Boolean = cachedBodyInputStream.available() == 0


    override fun isReady(): Boolean = true


    override fun setReadListener(p0: ReadListener?) {
        TODO("Not yet implemented")
    }
}