package ar.com.api.exchanges.services

import ar.com.api.exchanges.configuration.ExternalServerConfig
import ar.com.api.exchanges.configuration.HttpServiceCall
import ar.com.api.exchanges.enums.ErrorTypeEnum
import ar.com.api.exchanges.exception.ApiServeErrorException
import ar.com.api.exchanges.model.Ping
import org.instancio.Instancio
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

@SpringBootTest
class CoinGeckoServiceStatusTest extends Specification {

    HttpServiceCall httpServiceCallMock
    ExternalServerConfig externalServerConfigMock
    CoinGeckoServiceStatus coinGeckoServiceStatus

    def setup() {
        httpServiceCallMock = Mock(HttpServiceCall)
        externalServerConfigMock = Mock(ExternalServerConfig)

        externalServerConfigMock.getPing() >> "pingURLEndPointMock"

        coinGeckoServiceStatus = new CoinGeckoServiceStatus(httpServiceCallMock, externalServerConfigMock)
    }

    def "CoinGeckoServiceStatus should successfully retrieve service status"() {
        given: "A mock setup for HttpServiceCall and ExternalServerConfig"
        def expectedPinObject = Instancio.create(Ping)
        httpServiceCallMock.getMonoObject("pingURLEndPointMock", Ping.class)
                >> Mono.just(expectedPinObject)

        when: "getStatusCoinGeckoService is invoked"
        def actualObject = coinGeckoServiceStatus.getStatusCoinGeckoService()

        then: "The service returns the expected Pin object"
        StepVerifier.create(actualObject)
                .assertNext { pingObject ->
                    assert pingObject.geckoSays != null: "Ping should not be null"
                    assert pingObject.geckoSays == expectedPinObject.geckoSays: "Gecko says field does not match"
                }
                .verifyComplete()
    }

    def "CoinGeckoServiceStatus should handle 4xx client error gracefully"() {
        given: "A mock setup HttServiceCall and ExternalServerConfig with a 4xx client error"
        def expectedApiClientError = new ApiServeErrorException("Client error occurred", "Bad Request",
                ErrorTypeEnum.GECKO_CLIENT_ERROR, HttpStatus.BAD_REQUEST)
        httpServiceCallMock.getMonoObject("pingURLEndPointMock", Ping.class)
                >> Mono.error(expectedApiClientError)

        when: "getStatusCoinGeckoService is invoked with a 4xx error scenario"
        def actualErrorObject = coinGeckoServiceStatus.getStatusCoinGeckoService();

        then: "The service gracefully handle the error"
        StepVerifier.create(actualErrorObject)
                .expectErrorMatches { errorObject ->
                    errorObject instanceof ApiServeErrorException &&
                            errorObject.getHttpStatus().is4xxClientError() &&
                            errorObject.getErrorTypeEnum() == expectedApiClientError.getErrorTypeEnum() &&
                            errorObject.getOriginalMessage() == expectedApiClientError.getOriginalMessage()
                }.verify()
    }

    def "CoinGeckoServiceStatus should handle 5xx client error gracefully"() {
        given: "A mock setup HttServiceCall and ExternalServerConfig with a 4xx client error"
        def expectedApiClientError = new ApiServeErrorException("Client error occurred", "Bad Request",
                ErrorTypeEnum.GECKO_CLIENT_ERROR, HttpStatus.BAD_REQUEST)
        httpServiceCallMock.getMonoObject("pingURLEndPointMock", Ping.class)
                >> Mono.error(expectedApiClientError)

        when: "getStatusCoinGeckoService is invoked with a 4xx error scenario"
        def actualErrorObject = coinGeckoServiceStatus.getStatusCoinGeckoService();

        then: "The service gracefully handle the error"
        StepVerifier.create(actualErrorObject)
                .expectErrorMatches { errorObject ->
                    errorObject instanceof ApiServeErrorException &&
                            errorObject.getHttpStatus().is4xxClientError() &&
                            errorObject.getErrorTypeEnum() == expectedApiClientError.getErrorTypeEnum() &&
                            errorObject.getOriginalMessage() == expectedApiClientError.getOriginalMessage()
                }.verify()
    }

}
