# RestBuilder

This is a Groovy DSL for doing testing of HTTP services and REST APIs. I'm
using it from Spock Spec tests. There are examples in the src/test/groovy
directory. Under the covers it uses the Spring 3 RestTemplate.

## Usage

To use the RestBuilder, create a shared builder instance and optionally set
an `errorHandler`:

    def rest = new RestBuilder()
    rest.errorHandler = {response ->
      // Deal with errors here
    }

This instance can be re-used because each new block results in a new instance
of RestTemplate.

To send a request, you can set headers, URI parameters, set the `contentType`
and optionally the `responseType` inside a Groovy Closure:

    def location = {
      contentType "application/json"
      responseType URI
      body new Person(name: "John Doe")
      post "http://localhost:8098/riak/bucket"
    }

When POSTing data, if you set a `responseType` of `java.net.URI`, you'll get
back the Location header as a URI (underneath it uses the RestTemplate's
`postForLocation` method).

To GET an object for inspection:

    def response = {
      accept "application/json"
      responseType Map
      get "http://localhost:8098/riak/bucket/key"
    }
    // Deal with the body here (which will be a Map)
    def props = response.body.props

To set headers, use the `header` or `headers` style:

    def response = {
      header "X-Riak-Vclock", vclock
    }

or...

    def response = {
      headers ["X-Riak-Vclock": vclock]
    }

You can set URI parameters, which will be URL encoded and tacked onto the end of the URL:

    def response = {
      param "returnbody", "true"
      body new Person(name: "John Doe")
      post "http://localhost:8098/riak/bucket/johndoe"
    }

would result in a URL of: `http://localhost:8098/riak/bucket/johndoe?returnbody=true` being
passed to the RestTemplate.
