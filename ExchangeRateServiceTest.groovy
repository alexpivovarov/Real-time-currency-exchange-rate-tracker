package com.example.exchange.service

import spock.lang.Specification

class ExchangeRateServiceTest extends Specification {

    def "fetchAndStore saves six currency pairs"() {
        given:
        def svc = new ExchangeRateService()

        when:
        svc.fetchAndStore()

        then:
        svc.getFromCache("USD_EUR") != null
        svc.getFromCache("USD_GBP") != null
        svc.getFromCache("EUR_USD") != null
        svc.getFromCache("GBP_USD") != null
        svc.getFromCache("EUR_GBP") != null
        svc.getFromCache("GBP_EUR") != null
    }
}
