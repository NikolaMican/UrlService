package com.spt.urls.webService.protocolVersion1

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageConfig
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.spt.urls.CONF_DOMAIN
import com.spt.urls.di.di
import com.spt.urls.extensions.getUrlWithDomain
import com.spt.urls.logs.TicketLoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.OK
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.BufferedImageHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.print.attribute.standard.Media


data class QrCodeRequest(val apiKey: String?,
                         val urlId: String?,
                         val width: Int? = null, val height: Int? = null)

@RestController
class QrCodeWebServiceRestController {
    private val LOG = TicketLoggerFactory.getTicketLogger(QrCodeWebServiceRestController::class.java)

    private val dynamicUrlDbController = di().getDynamicUrlDbController()
    private val userDbController = di().getUserDbController()

    @PostMapping("/v1/createQrCode", produces = [MediaType.IMAGE_PNG_VALUE])
    @ResponseBody
    fun createQrCodePost(
        @RequestBody request: QrCodeRequest,
    ): ResponseEntity<BufferedImage> {
        LOG.info("Processing createQrCode request. Received urlId: ${request.urlId}")
        if (request.apiKey.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received apiKey is null.")
        if (request.urlId.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received urlId is null.")

        if (request.width != null && (request.width < 10 || request.width > 10000)) {
            throw ResponseStatusException(BAD_REQUEST, "Received width must be in range [10, 10000].")
        }
        if (request.height != null && (request.height < 10 || request.height > 10000)) {
            throw ResponseStatusException(BAD_REQUEST, "Received height must be in range [10, 10000].")
        }
        val width = request.width ?: 200
        val height = request.height ?: 200

        val user = userDbController.getByApiKey(request.apiKey) ?: throw ResponseStatusException(BAD_REQUEST, "Api key doesn't exist in database.")
        val dUBean = dynamicUrlDbController.get(user.idUser, request.urlId) ?: throw ResponseStatusException(BAD_REQUEST, "Required urlId: '${request.urlId}' doesn't exits for user: ${user.username}")

        return ResponseEntity(generateQRCodeImage(dUBean.dynamicUrlTemplate.getUrlWithDomain(), width, height), OK)
    }


    private fun generateQRCodeImage(barcodeText: String, width: Int, height: Int): BufferedImage? {
        val barcodeWriter = QRCodeWriter()
        val bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, width, height)
        return MatrixToImageWriter.toBufferedImage(bitMatrix)
    }

    // NOTE: must be here to, somehow, perform internal conversion bytes to image
    @Bean
    fun createImageHttpMessageConverter(): HttpMessageConverter<BufferedImage> {
        return BufferedImageHttpMessageConverter()
    }
}



@Controller
class QrCodeWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(QrCodeWebServiceController::class.java)

    private val restController = QrCodeWebServiceRestController()

    @GetMapping("/v1/createQrCodeGet", produces = [MediaType.IMAGE_PNG_VALUE])
    @ResponseBody
    fun editDynamicUrl(
        @RequestParam(name = "apiKey") apiKey: String?,
        @RequestParam(name = "urlId") urlId: String,
        @RequestParam(required = false, name = "width") width: Int?,
        @RequestParam(required = false, name = "height") height: Int?,
    ): ResponseEntity<BufferedImage>  {
        return restController.createQrCodePost(QrCodeRequest(apiKey, urlId, width, height))
    }
}