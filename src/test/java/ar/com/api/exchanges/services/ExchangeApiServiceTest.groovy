package ar.com.api.exchanges.services

import ar.com.api.exchanges.configuration.ExternalServerConfig
import ar.com.api.exchanges.configuration.HttpServiceCall
import ar.com.api.exchanges.dto.ExchangeDTO
import ar.com.api.exchanges.dto.ExchangeVolumeDTO
import ar.com.api.exchanges.enums.ErrorTypeEnum
import ar.com.api.exchanges.exception.ApiServeErrorException
import ar.com.api.exchanges.model.Exchange
import ar.com.api.exchanges.model.ExchangeBase
import ar.com.api.exchanges.model.ExchangeById
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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
        externalServerConfigMock.getExchangeListMarket() >> "exchangeListMarketEndPointMock"
        externalServerConfigMock.getExchangeById() >> "/exchangeByIdEndPointMock/binance"

        exchangeApiService = new ExchangeApiService(httpServiceCallMock, externalServerConfigMock)
    }

    def "GetAllExchanges should successfully retrieve a list of Exchanges"() {
        given: "Mocked external server config and filter DTO"
        def filterDTO = Instancio.create(ExchangeDTO)
        def expectedExchange = Instancio.ofList(Exchange.class).size(5).create()
        httpServiceCallMock.getFluxObject(externalServerConfigMock.getExchangeList()
                + filterDTO.getUrlFilterString(), Exchange.class) >> Flux.fromIterable(expectedExchange)

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
        def clientErrorExpected = new ApiServeErrorException("An error occurred", "Bad Request",
                ErrorTypeEnum.GECKO_CLIENT_ERROR, HttpStatus.BAD_REQUEST)
        httpServiceCallMock.getFluxObject("exchangeListEndPointMock" +
                filterDTO.getUrlFilterString(), Exchange.class) >> Flux.error(clientErrorExpected)

        when: "getAllExchanges is invoked with a 4xx error scenario"
        def actualExceptionObject = exchangeApiService.getAllExchanges(filterDTO)

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServeErrorException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_CLIENT_ERROR &&
                            errorActual.getHttpStatus().is4xxClientError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

    def "GetAllExchanges should handle 5xx server error gracefully"() {
        given: "A mock setup for ExternalServerConfig and HttServiceCall with a 5xx client error"
        def filterDTO = Instancio.create(ExchangeDTO)
        def clientErrorExpected = new ApiServeErrorException("Server Error on Client", "Bad Gateway",
                ErrorTypeEnum.GECKO_SERVER_ERROR, HttpStatus.BAD_GATEWAY)
        httpServiceCallMock.getFluxObject("exchangeListEndPointMock" +
                filterDTO.getUrlFilterString(), Exchange.class) >> Flux.error(clientErrorExpected)

        when: "getAllExchanges is invoked with a 4xx error scenario"
        def actualExceptionObject = exchangeApiService.getAllExchanges(filterDTO)

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServeErrorException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

    def "GetAllSupportedMarkets should successfully retrieve a list of markets Supported"() {
        given: "Mocked external server config and filter DTO"
        def expectedMarketExchangeList = Instancio.ofList(ExchangeBase.class)
                .size(7).create()
        httpServiceCallMock.getFluxObject(externalServerConfigMock.getExchangeList()
                + externalServerConfigMock.getExchangeListMarket(), ExchangeBase.class) >>
                Flux.fromIterable(expectedMarketExchangeList)

        when: "getAllSupportedMarkets is called without filter"
        def returnedObject = exchangeApiService.getAllSupportedMarkets()

        then: "The correct number of Markets is returned,and content is verified"
        StepVerifier.create(returnedObject)
                .recordWith(ArrayList::new)
                .expectNextCount(expectedMarketExchangeList.size())
                .consumeRecordedWith { actualExchanges ->
                    assert actualExchanges.containsAll(expectedMarketExchangeList)
                }
                .verifyComplete()
    }

    def "GetAllSupportedMarkets should handle 4xx client error gracefully"() {
        given: "A mock setup for ExternalServerConfig and HttServiceCall with a 4xx client error"
        def clientErrorExpected = new ApiServeErrorException("An error occurred on APIClient", "Forbidden",
                ErrorTypeEnum.GECKO_CLIENT_ERROR, HttpStatus.FORBIDDEN)
        httpServiceCallMock.getFluxObject(externalServerConfigMock.getExchangeList()
                + externalServerConfigMock.getExchangeListMarket(), ExchangeBase.class)
                >> Flux.error(clientErrorExpected)

        when: "GetAllSupportedMarkets is invoked with a 4xx error scenario"
        def actualExceptionObject = exchangeApiService.getAllSupportedMarkets()

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServeErrorException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_CLIENT_ERROR &&
                            errorActual.getHttpStatus().is4xxClientError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }.verify()
    }

    def "GetAllSupportedMarkets should handle 5xx Server error gracefully"() {
        given: "A mock setup for ExternalServerConfig and HttServiceCall with a 5xx server error"
        def clientErrorExpected = new ApiServeErrorException("An error occurred on APIServer",
                "Internal Server Error",
                ErrorTypeEnum.GECKO_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
        httpServiceCallMock.getFluxObject(externalServerConfigMock.getExchangeList()
                + externalServerConfigMock.getExchangeListMarket(), ExchangeBase.class)
                >> Flux.error(clientErrorExpected)

        when: "GetAllSupportedMarkets is invoked with a 5xx error scenario"
        def actualExceptionObject = exchangeApiService.getAllSupportedMarkets()

        then: "The service return 4xx client"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches { errorActual ->
                    errorActual instanceof ApiServeErrorException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }.verify()
    }

    def "GetExchangeById should successfully retrieve a Exchange By Market ID"() {
        given: "A mocked ExchangeVolumenDTO and expected ExchangeById response"
        def filterDTO = Instancio.create(ExchangeVolumeDTO.class)
        def expectedExchangeById = Instancio.create(ExchangeById.class)
        httpServiceCallMock.getMonoObject(_ as String, ExchangeById.class) >> Mono.just(expectedExchangeById)

        when: "GetExchangeVolumesById is called with the filter DTO"
        def result = exchangeApiService.getExchangeVolumesById(filterDTO)

        then: "The correct object ExchangeById is returned, and content is verified"
        StepVerifier.create(result)
                .assertNext {exchangeObject ->
                    assert exchangeObject.getName() != null : "Name should not be null"
                    assert exchangeObject.getTrustScoreRank() > 0 : "Trust Score Rank should not be 0"
                    assert !exchangeObject.getStatusUpdates().isEmpty() : "Status Updates should not be empty"
                    assert exchangeObject.getStatusUpdates().size() ==
                            expectedExchangeById.getStatusUpdates().size() :
                            "The number of elements in returned object and expected object should not be different"
                    assert exchangeObject.getStatusUpdates()
                            .containsAll(expectedExchangeById.getStatusUpdates()) :
                            "The content of the list should not be different"
                }
                .verifyComplete()
    }

    def "GetExchangeById  should handle 4xx client error gracefully"() {
        given: "A mocked ExchangeVolumenDTO and expected ApiServeErrorException response"
        def filterDTO = Instancio.create(ExchangeVolumeDTO.class)
        def clientErrorExpected = new ApiServeErrorException("An error occurred on APIClient", "Unauthorized",
                ErrorTypeEnum.GECKO_CLIENT_ERROR, HttpStatus.UNAUTHORIZED)
        httpServiceCallMock.getMonoObject(_ as String, ExchangeById.class) >> Mono.error(clientErrorExpected)

        when: "GetExchangeVolumesById is called with the filter DTO"
        def actualExceptionObject = exchangeApiService.getExchangeVolumesById(filterDTO)

        then: "The correct error ApiServeErrorException is returned, and content is verified"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches {errorActual ->
                    errorActual instanceof ApiServeErrorException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_CLIENT_ERROR &&
                            errorActual.getHttpStatus().is4xxClientError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }

    def "GetExchangeById  should handle 5xx client error gracefully"() {
        given: "A mocked ExchangeVolumenDTO and expected ApiServeErrorException response"
        def filterDTO = Instancio.create(ExchangeVolumeDTO.class)
        def clientErrorExpected = new ApiServeErrorException("An error occurred on APIServer", "Loop Detected",
                ErrorTypeEnum.GECKO_SERVER_ERROR, HttpStatus.LOOP_DETECTED)
        httpServiceCallMock.getMonoObject(_ as String, ExchangeById.class) >> Mono.error(clientErrorExpected)

        when: "GetExchangeVolumesById is called with the filter DTO"
        def actualExceptionObject = exchangeApiService.getExchangeVolumesById(filterDTO)

        then: "The correct error ApiServeErrorException is returned, and content is verified"
        StepVerifier.create(actualExceptionObject)
                .expectErrorMatches {errorActual ->
                    errorActual instanceof ApiServeErrorException &&
                            errorActual.getErrorTypeEnum() == ErrorTypeEnum.GECKO_SERVER_ERROR &&
                            errorActual.getHttpStatus().is5xxServerError() &&
                            errorActual.getMessage() == clientErrorExpected.getMessage()
                }
                .verify()
    }
}
