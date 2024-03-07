package ar.com.api.exchanges.handler

import ar.com.api.exchanges.enums.ErrorTypeEnum
import ar.com.api.exchanges.exception.ApiClientErrorException
import ar.com.api.exchanges.model.Ping
import ar.com.api.exchanges.services.CoinGeckoServiceStatus
import org.instancio.Instancio
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class HealthCoinGeckoApiHandlerTest extends Specification {

    CoinGeckoServiceStatus coinGeckoServiceStatusMock
    ServerRequest serverRequestMock
    HealthCoinGeckoApiHandler apiHandler

    def setup() {
        coinGeckoServiceStatusMock = Mock(CoinGeckoServiceStatus)
        serverRequestMock = Mock(ServerRequest)
        apiHandler = new HealthCoinGeckoApiHandler(coinGeckoServiceStatusMock)
    }

    def "getStatusServiceCoinGecko return 200 Ok with expected body successfully response"() {
        given: "A mock CoinGeckoServiceStatus and a successfully Ping response"
        def expectedPing = Instancio.create(Ping)
        coinGeckoServiceStatusMock.getStatusCoinGeckoService() >> Mono.just(expectedPing)

        when: "getStatusServiceCoinGecko is called"
        def actualObject = apiHandler.getStatusServiceCoinGecko(serverRequestMock)

        then: "The response is 200 Ok with the expected body"
        StepVerifier.create(actualObject)
                .assertNext { actualResponse ->
                    assert actualResponse.statusCode() == HttpStatus.OK: "StatusCode should not be different to Ok"
                    assert actualResponse.headers().getContentType()
                            == MediaType.APPLICATION_JSON: "ContentType should not be different to Application Json"
                }
                .verifyComplete()
    }

    def "getStatusServiceCoinGecko returns 404 Not Found for empty service response"() {
        given: "A mock CoinGeckoServiceStatus and an empty response"
        coinGeckoServiceStatusMock.getStatusCoinGeckoService() >> Mono.empty()

        when: "getStatusCoinGeckoService is called"
        def actualEmptyResult = apiHandler.getStatusServiceCoinGecko(serverRequestMock)

        then: "The response expected is 404 Not Found"
        StepVerifier.create(actualEmptyResult)
                .assertNext { actualResponse ->
                    assert actualResponse.statusCode() == HttpStatus.NOT_FOUND: "StatusCode should by Not Found"
                    assert actualResponse.headers().isEmpty()
                }
                .verifyComplete();
    }

    def "getStatusServiceCoinGecko handle error gracefully"() {
        given: "A mock CoinGeckoServiceStatus and an error response"
        coinGeckoServiceStatusMock.getStatusCoinGeckoService() >> Mono.error(new RuntimeException("Error occurred"))

        when: "getStatusCoinGeckoService is called"
        def actualErrorResponse = apiHandler.getStatusServiceCoinGecko(serverRequestMock)

        then: "The response indicate and internal error"
        StepVerifier.create(actualErrorResponse)
                .expectErrorMatches { actualError ->
                    actualError instanceof ApiClientErrorException &&
                            actualError.getHttpStatus() == HttpStatus.INTERNAL_SERVER_ERROR &&
                            actualError.getErrorTypeEnum() == ErrorTypeEnum.API_SERVER_ERROR &&
                            actualError.getMessage() == "An expected error occurred in getStatusServiceCoinGecko"
                }
                .verify()
    }

}
