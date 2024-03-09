package ar.com.api.exchanges.services

import ar.com.api.exchanges.configuration.ExternalServerConfig
import ar.com.api.exchanges.configuration.HttpServiceCall
import ar.com.api.exchanges.dto.ExchangeDTO
import ar.com.api.exchanges.enums.ErrorTypeEnum
import ar.com.api.exchanges.exception.ApiServeErrorrException
import ar.com.api.exchanges.model.Exchange
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

class ExchangeApiServiceTest extends Specification {

    HttpServiceCall httpServiceCallMock
    ExternalServerConfig externalServerConfigMock
    ExchangeApiService exchangeApiService

    def setup() {
        httpServiceCallMock = Mock(HttpServiceCall)
        externalServerConfigMock = Mock(ExternalServerConfig)

        externalServerConfigMock.getExchangeList() >> "exchangeListEndPointMock"

        exchangeApiService = new ExchangeApiService(httpServiceCallMock, externalServerConfigMock)
    }

    def "GetAllExchanges should successfully retrieve a list of Exchanges"() {
        given: "Mocked external server config and filter DTO"
        def filterDTO = Instancio.create(ExchangeDTO)
        def expectedExchange = Instancio.ofList(Exchange.class).size(5).create()
        httpServiceCallMock.getFluxObject(externalServerConfigMock.getExchangeList()
                + filterDTO.getUrlFilterString(),  Exchange.class) >> Flux.fromIterable(expectedExchange)

        when: "getAllExchanges is called with the filter DTO"
        def returnedObject = exchangeApiService.getAllExchanges(filterDTO)

        then: "The correct number of Exchanges is returned, and content is verified"
        StepVerifier.create(returnedObject)
                .recordWith(ArrayList::new)
                .expectNextCount(expectedExchange.size())
                .consumeRecordedWith { actualExchanges ->
                    assert actualExchanges.containsAll(expectedExchange)
                }
                .verifyComplete()
    }

    def "GetAllExchanges should handle 4xx client error gracefully"() {
        given: "A mock setup for ExternalServerConfig and HttServiceCall with a 4xx client error"
        def filterDTO = Instancio.create(ExchangeDTO)
        def clientErrorExpected = new ApiServeErrorrException("An error occurred", "Bad Request",
                ErrorTypeEnum.GECKO_CLIENT_ERROR, HttpStatus.BAD_REQUEST)
        httpServiceCallMock.getFluxObject("exchangeListEndPointMock" +
                filterDTO.getUrlFilterString(), Exchange.class) >> Flux.error(clientErrorExpected)

        when: "getAllExchanges is invoked with a 4xx error scenario"
        def actualExceptionObject = exchangeApiService.getAllExchanges(filterDTO)

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches {errorActual ->
                    errorActual instanceof ApiServeErrorrException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_CLIENT_ERROR &&
                            errorActual.getHttpStatus().is4xxClientError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

    def "GetAllExchanges should handle 5xx server error gracefully"() {
        given: "A mock setup for ExternalServerConfig and HttServiceCall with a 5xx client error"
        def filterDTO = Instancio.create(ExchangeDTO)
        def clientErrorExpected = new ApiServeErrorrException("Server Error on Client", "Bad Gateway",
                ErrorTypeEnum.GECKO_SERVER_ERROR, HttpStatus.BAD_GATEWAY)
        httpServiceCallMock.getFluxObject("exchangeListEndPointMock" +
                filterDTO.getUrlFilterString(), Exchange.class) >> Flux.error(clientErrorExpected)

        when: "getAllExchanges is invoked with a 4xx error scenario"
        def actualExceptionObject = exchangeApiService.getAllExchanges(filterDTO)

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches {errorActual ->
                    errorActual instanceof ApiServeErrorrException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }
}
