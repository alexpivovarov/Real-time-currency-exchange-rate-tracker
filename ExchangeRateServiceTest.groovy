package com.example.exchange.service // package must match the folder structure

import spock.lang.Specification

class ExchangeRateServiceTest extends Specification {

    // Test 1: Checks if convert() multiplies the input amount by the correct exchange rate
    def "convert should multiply amount by rate"() {
        given: // Set up test conditions
        def service = new ExchangeRateService() // Create an instance of a service
        service.redisClient.setRate("USD_EUR", "0.93") // Pretend Redis has a USD to EUR rate

        expect: // Assert the expected outcome
        service.convert("USD", "EUR", 100) = 93.0
    }

    // Test 2: If rate does not exist in Redis, the service returns -1
    def "convert should return -1 if rate is missing"() {
        given:
        def service = new ExchangeRateService() // No rate set in Redis

        expect:
        service.convert("USD", "ABC", 100) == -1 // Conversion should fail and return -1
    } 

    // Test 3: Verifies getRate() retrieves the correct rate string from Redis
    def "getRate should return correct string"(){
        given:
        def service = new ExchangeRateService()
        service.redisClient.setRate("USD_EUR", "0.93") // Manually insert a mock rate

        expect:
        service.getRate("USD", "EUR") = "0.93" // Should return that rate as a string
    }
}