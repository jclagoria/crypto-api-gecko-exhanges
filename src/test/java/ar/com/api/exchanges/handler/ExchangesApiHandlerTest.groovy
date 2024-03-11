package ar.com.api.exchanges.handler

import ar.com.api.exchanges.dto.ExchangeDTO
import ar.com.api.exchanges.dto.ExchangeVolumeDTO
import ar.com.api.exchanges.dto.TickersByIdDTO
import ar.com.api.exchanges.dto.VolumeChartByIdDTO
import ar.com.api.exchanges.enums.ErrorTypeEnum
import ar.com.api.exchanges.exception.ApiClientErrorException
import ar.com.api.exchanges.model.Exchange
import ar.com.api.exchanges.model.ExchangeBase
import ar.com.api.exchanges.model.ExchangeById
import ar.com.api.exchanges.model.TickersById
import ar.com.api.exchanges.services.ExchangeApiService
import ar.com.api.exchanges.validators.ValidatorOfCTOComponent
import org.instancio.Instancio
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class ExchangesApiHandlerTest extends Specification {

    ExchangeApiService exchangeApiServiceMock
    ServerRequest serverRequestMock
    ValidatorOfCTOComponent validatorOfDTOComponentMock

    ExchangesApiHandler exchangesApiHandler

    def setup() {
        exchangeApiServiceMock = Mock(ExchangeApiService)
        serverRequestMock = Mock(ServerRequest)
        validatorOfDTOComponentMock = Mock(ValidatorOfCTOComponent)

        exchangesApiHandler = new ExchangesApiHandler(exchangeApiServiceMock, validatorOfDTOComponentMock)
    }

    def "GetAllExchangesCoinGecko return successfully a ServerResponse with HttpStatus Ok"() {
        given: "Mocked service with a list of Exchanges"
        def expectedListOfExchanges = Instancio.ofList(Exchange.class).
                size(3).create()
        serverRequestMock.queryParam(_ as String) >> Optional.of("150")
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
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
        serverRequestMock.queryParam(_ as String) >> Optional.of("100")
        serverRequestMock.queryParam(_ as String) >> Optional.of("2")
        exchangeApiServiceMock.getAllExchanges(_ as ExchangeDTO) >> Flux.empty()

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
        serverRequestMock.queryParam(_ as String) >> Optional.of("100")
        serverRequestMock.queryParam(_ as String) >> Optional.of("2")
        exchangeApiServiceMock.getAllExchanges(_ as ExchangeDTO) >>
                Flux.error(new RuntimeException("An error occurred"))

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

    def "GetAllExchangeMarketData return successfully a ServerResponse with HttpStatus Ok"() {
        given: "Mocked service with a list of Exchanges supported"
        def expectedListExchanges = Instancio.ofList(ExchangeBase.class).size(5).create()
        exchangeApiServiceMock.getAllSupportedMarkets() >> Flux.just(expectedListExchanges)

        when: "GetAllExchangeMarketData is called and return successfully ServerResponse"
        def actualResponseObject = exchangesApiHandler.getAllExchangeMarketData(serverRequestMock)

        then: "It returns a ServerResponse with the list of Exchanges"
        StepVerifier.create(actualResponseObject)
                .expectNextMatches { serverResponse ->
                    serverResponse.statusCode().is2xxSuccessful() &&
                            serverResponse.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "GetAllExchangeMarketData handles error gracefully"() {
        given: "A mock server request and an error"
        exchangeApiServiceMock.getAllSupportedMarkets() >> Flux.error(new RuntimeException("An error occurred"))

        when: "GetAllExchangeMarketData is called and return successfully and Error"
        def responseErrorActual = exchangesApiHandler.getAllExchangeMarketData(serverRequestMock)

        then: "It handles the error and returns an internal server error"
        StepVerifier.create(responseErrorActual)
                .expectErrorMatches { responseError ->
                    responseError instanceof ApiClientErrorException &&
                            responseError.getErrorTypeEnum() == ErrorTypeEnum.API_SERVER_ERROR &&
                            responseError.getMessage() == "An unexpected error occurred in getAllExchangeMarketData"
                }
                .verify()
    }

    def "GetExchangeVolumeDataById return successfully a ServerResponse with HttpStatus Ok"() {
        given: "Mocked service with ExchangeById"
        def expectedExchangeById = Instancio.create(ExchangeById.class)
        def filterDTO = Instancio.create(ExchangeVolumeDTO.class)
        serverRequestMock.pathVariable(_ as String) >> Instancio.create(String.class)
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getExchangeVolumesById(_ as ExchangeVolumeDTO) >> Mono.just(expectedExchangeById)

        when: "GetExchangeVolumeDataById is called and return successfully ServerResponse"
        def actualResponseObject = exchangesApiHandler
                .getExchangeVolumeDataById(serverRequestMock)

        then: "It returns a ServerResponse with a object ExchangeById"
        StepVerifier.create(actualResponseObject)
                .expectNextMatches { response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "GetExchangeVolumeDataById returns not found when no exchanges return form the API Service"() {
        given: "A mock server request and an empty GetExchangeVolumeDataById"
        def filterDTO = Instancio.create(ExchangeVolumeDTO.class)
        serverRequestMock.pathVariable(_ as String) >> Instancio.create(String.class)
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getExchangeVolumesById(_ as ExchangeVolumeDTO) >> Mono.empty()

        when: "getAllExchangesCoinGecko is called"
        def responseActualResponse = exchangesApiHandler
                .getExchangeVolumeDataById(serverRequestMock)

        then: "It return a not found response"
        StepVerifier.create(responseActualResponse)
                .expectNextMatches { responseMono ->
                    responseMono.statusCode() == HttpStatus.NOT_FOUND
                }
                .verifyComplete()
    }

    def "GetExchangeVolumeDataById handles error gracefully"() {
        given: "A mock server request and an empty GetExchangeVolumeDataById"
        def filterDTO = Instancio.create(ExchangeVolumeDTO.class)
        serverRequestMock.pathVariable(_ as String) >> Instancio.create(String.class)
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getExchangeVolumesById(_ as ExchangeVolumeDTO) >>
                Mono.error(new RuntimeException("An error occurred"))

        when: "getAllExchangesCoinGecko is called"
        def responseActualResponse = exchangesApiHandler
                .getExchangeVolumeDataById(serverRequestMock)

        then: "It handles the error and returns an internal server error"
        StepVerifier.create(responseActualResponse)
                .expectErrorMatches { responseError ->
                    responseError instanceof ApiClientErrorException &&
                            responseError.getErrorTypeEnum() == ErrorTypeEnum.API_SERVER_ERROR &&
                            responseError.getMessage() == "An unexpected error occurred in getExchangeVolumeDataById"
                }
                .verify()
    }

    def "GetTickerExchangeById return successfully a ServerResponse with HttpStatus Ok"() {
        given: "Mocked service with TickersById and FilterDTO and ServeRequest and ValidatorOfCTOComponent"
        def expectedTicketById = Instancio.create(TickersById.class)
        def filterDTO = Instancio.create(TickersByIdDTO.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
        serverRequestMock.queryParam(_ as String) >> Optional.of("bitcoin")
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getTicketExchangeById(_ as TickersByIdDTO) >> Mono.just(expectedTicketById)

        when: "GetTickerExchangeById is called and return successfully ServerResponse"
        def actualResponseObject = exchangesApiHandler.getTickerExchangeById(serverRequestMock)

        then: "It returns a ServerResponse with a object TickersById"
        StepVerifier.create(actualResponseObject)
                .expectNextMatches { response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "GetTickerExchangeById returns not found when no exchanges return form the API Service"() {
        given: "Mocked service with Empty and FilterDTO and ServeRequest and ValidatorOfCTOComponent"
        def filterDTO = Instancio.create(TickersByIdDTO.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
        serverRequestMock.queryParam(_ as String) >> Optional.of("bitcoin")
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getTicketExchangeById(_ as TickersByIdDTO) >> Mono.empty()

        when: "GetTickerExchangeById is called and return successfully ServerResponse of empty object"
        def responseActualResponse = exchangesApiHandler.getTickerExchangeById(serverRequestMock)

        then: "It return a not found response"
        StepVerifier.create(responseActualResponse)
                .expectNextMatches { responseMono ->
                    responseMono.statusCode() == HttpStatus.NOT_FOUND
                }
                .verifyComplete()
    }

    def "GetTickerExchangeById handles error gracefully"() {
        given: "A mock server request and an empty GetTickerExchangeById"
        def filterDTO = Instancio.create(TickersByIdDTO.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
        serverRequestMock.queryParam(_ as String) >> Optional.of("bitcoin")
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getExchangeVolumesById(_ as ExchangeVolumeDTO) >>
                Mono.error(new RuntimeException("An error occurred"))

        when: "getAllExchangesCoinGecko is called"
        def responseActualResponse = exchangesApiHandler
                .getTickerExchangeById(serverRequestMock)

        then: "It handles the error and returns an internal server error"
        StepVerifier.create(responseActualResponse)
                .expectErrorMatches { responseError ->
                    responseError instanceof ApiClientErrorException &&
                            responseError.getErrorTypeEnum() == ErrorTypeEnum.API_SERVER_ERROR &&
                            responseError.getMessage() == "An unexpected error occurred in getTickerExchangeById"
                }
                .verify()
    }

    def "GetVolumeChartById return successfully a ServerResponse with HttpStatus Ok"() {
        given: "Mocked service with List of String and FilterDTO and ServerRequest and ValidatorOgCToComponent"
        def expectedStringList = Instancio.ofList(String.class).size(5).create()
        def filterDTO = Instancio.create(VolumeChartByIdDTO.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getVolumeChartById(_ as VolumeChartByIdDTO) >> Flux.fromIterable(expectedStringList)

        when: "GetTickerExchangeById is called and return successfully ServerResponse"
        def actualResponseObject = exchangesApiHandler.getVolumeChartById(serverRequestMock)

        then: "It returns a ServerResponse with a object List of String"
        StepVerifier.create(actualResponseObject)
                .expectNextMatches { response ->
                    response.statusCode().is2xxSuccessful() &&
                            response.headers().getContentType() == MediaType.APPLICATION_JSON
                }
                .verifyComplete()
    }

    def "GetVolumeChartById return an error and handles error gracefully"() {
        given: "Mocked service with RuntimeException and FilterDTO and ServerRequest and ValidatorOgCToComponent"
        def filterDTO = Instancio.create(VolumeChartByIdDTO.class)
        serverRequestMock.queryParam(_ as String) >> Optional.of("1")
        validatorOfDTOComponentMock.validation(_) >> Mono.just(filterDTO)
        exchangeApiServiceMock.getVolumeChartById(_ as VolumeChartByIdDTO) >>
                Flux.error(new RuntimeException("An error occurred"))

        when: "GetTickerExchangeById is called and return successfully ServerResponse"
        def actualResponseObject = exchangesApiHandler.getVolumeChartById(serverRequestMock)

        then: "It returns a ServerResponse with a object List of String"
        StepVerifier.create(actualResponseObject)
                .expectErrorMatches { responseError ->
                    responseError instanceof ApiClientErrorException &&
                            responseError.getErrorTypeEnum() == ErrorTypeEnum.API_SERVER_ERROR &&
                            responseError.getMessage() == "An unexpected error occurred in getVolumeChartById"
                }
                .verify()
    }

}