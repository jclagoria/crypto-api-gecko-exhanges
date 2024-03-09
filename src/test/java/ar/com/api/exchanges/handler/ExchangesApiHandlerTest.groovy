package ar.com.api.exchanges.handler

import ar.com.api.exchanges.dto.ExchangeDTO
import ar.com.api.exchanges.enums.ErrorTypeEnum
import ar.com.api.exchanges.exception.ApiClientErrorException
import ar.com.api.exchanges.model.Exchange
import ar.com.api.exchanges.services.ExchangeApiService
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

class ExchangesApiHandlerTest extends Specification {

    ExchangeApiService exchangeApiServiceMock
    ServerRequest serverRequestMock

    ExchangesApiHandler exchangesApiHandler

    def setup() {
        exchangeApiServiceMock = Mock(ExchangeApiService)
        serverRequestMock = Mock(ServerRequest)

        exchangesApiHandler = new ExchangesApiHandler(exchangeApiServiceMock)
    }

    def "GetAllExchangesCoinGecko return successfully a ServerResponse with HttpStatus Ok"() {
        given: "Mocked service with a list of Exchanges"
        def expectedListOfExchanges = Instancio.ofList(Exchange.class).
                size(3).create()
        serverRequestMock.queryParam(_) >> Optional.of("150")
        serverRequestMock.queryParam(_) >> Optional.of("1")
        exchangeApiServiceMock.getAllExchanges(_ as ExchangeDTO)
                >> Flux.just(expectedListOfExchanges)

        when: "GetAllExchangesCoinGecko is called and return successfully ServerResponse"
        def actualObject = exchangesApiHandler.getAllExchangesCoinGecko(serverRequestMock)

        then: "It returns a ServerResponse with the list of Exchanges"
        StepVerifier.create(actualObject)
                .expectNextMatches { response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "getAllExchangeCOinGecko returns not found when no exchanges return form the API Service"() {
        given: "A mock server request and an empty list of exchanges"
        serverRequestMock.queryParam(_) >> Optional.of("100")
        serverRequestMock.queryParam(_) >> Optional.of("2")
        exchangeApiServiceMock.getAllExchanges(_) >> Flux.empty()

        when: "getAllExchangesCoinGecko is called"
        def responseActualResponse = exchangesApiHandler.getAllExchangesCoinGecko(serverRequestMock)

        then: "It return a not found response"
        StepVerifier.create(responseActualResponse)
                .expectNextMatches { responseMono ->
                    responseMono.statusCode() == HttpStatus.OK
                }
                .verifyComplete()
    }

    def "getAllExchangeCoinGecko handles error gracefully"() {
        given: "A mock server request and an error"
        serverRequestMock.queryParam(_) >> Optional.of("100")
        serverRequestMock.queryParam(_) >> Optional.of("2")
        exchangeApiServiceMock.getAllExchanges(_) >> Flux.error(new RuntimeException("An error occurred"))

        when: "getAllExchangesCoinGecko called"
        def responseActualResponse = exchangesApiHandler.getAllExchangesCoinGecko(serverRequestMock)

        then: "It handles the error and returns an internal server error"
        StepVerifier.create(responseActualResponse)
                .expectErrorMatches { actualError ->
                    actualError instanceof ApiClientErrorException &&
                            ErrorTypeEnum.API_SERVER_ERROR == actualError.getErrorTypeEnum() &&
                            "An unexpected error occurred in getAllExchangesCoinGecko" == actualError.getMessage()
                }
                .verify()
    }


}
