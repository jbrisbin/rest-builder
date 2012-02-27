package com.jbrisbin.rest.groovy

import org.springframework.http.HttpStatus
import spock.lang.Specification

/**
 * @author Jon Brisbin <jon@jbrisbin.com>
 */
class RestBuilderSpec extends Specification {

  def "handles GET"() {

    given:
    def rest = new RestBuilder()
    rest.errorHandler = {response ->
      println "error response: $response"
    }

    when:
    def r = rest {
      accept "application/json"
      header "X-Riak-ClientId", "RestBuilderSpec"
      date now()
      responseType Map
      get "http://localhost:8098/riak/status"
    }

    then:
    r.body.props.name == "status"

  }

  def "handles POST"() {

    given:
    def rest = new RestBuilder()
    rest.errorHandler = {response ->
      println "error response: $response"
    }

    when:
    def p = rest {
      contentType "application/json"
      header "X-Riak-ClientId", "RestBuilderSpec"
      param "returnbody", "true"
      responseType Person
      body new Person(name: "John Doe")
      post "http://localhost:8098/riak/test/post"
    }

    then:
    p.body.name == "John Doe"

  }

  def "handles PUT"() {

    given:
    def rest = new RestBuilder()
    rest.errorHandler = {response ->
      println "error response: $response"
    }

    when:
    def p1 = rest {
      responseType Person
      header "X-Riak-ClientId", "RestBuilderSpec"
      get "http://localhost:8098/riak/test/post"
    }
    def p2 = p1.body
    p2.name = "Jane Doe"
    def vclock = p1.headers["X-Riak-Vclock"]
    def p3 = rest {
      contentType "application/json"
      header "X-Riak-ClientId", "RestBuilderSpec"
      header "X-Riak-Vclock", vclock
      param "returnbody", "true"
      responseType Person
      body p1.body
      put "http://localhost:8098/riak/test/post"
    }

    then:
    p3.body.name == "Jane Doe"

  }

  def "handles DELETE"() {

    given:
    def rest = new RestBuilder()
    rest.errorHandler = {response ->
    }

    when:
    def p1 = rest {
      responseType Person
      header "X-Riak-ClientId", "RestBuilderSpec"
      get "http://localhost:8098/riak/test/post"
    }
    def vclock = p1.headers["X-Riak-Vclock"]
    rest {
      header "X-Riak-Vclock", vclock
      delete "http://localhost:8098/riak/test/post"
    }
    def p2 = rest {
      get "http://localhost:8098/riak/test/post"
    }

    then:
    p2.statusCode == HttpStatus.NOT_FOUND

  }

}

class Person {
  String name
}
