## SDKMAN! Secure Proxy

This microservice can be used to proxy and secure other microservices. At the moment, the service provides lightweight Authentication and Authorisation.

### Consumer Authentication

It acts as a proxy, searching for the presence of two request headers:

`Consmer-Key` : a unique identifier per Consumer.

`Consumer-Token` : a SHA-256 hash generated for the Consumer

### Setting up Services to Proxy

The application hinges on configuration to be set up in the `conf/application.conf` field. Here are some current examples as used by the SDKMAN API:

    services {
      "release" = {
        url = "http://somehost:8080/release"
        url = ${?RELEASE_ENDPOINT_API_URL}
        serviceToken = "default_token"
        serviceToken = ${?RELEASE_API_TOKEN}
      }
      "default" = {
        url = "http://somehost:8080/default"
        url = ${?DEFAULT_ENDPOINT_API_URL}
        serviceToken = "default_token"
        serviceToken = ${?RELEASE_API_TOKEN}
      }
      "announce/struct" = {
        url = "http://somehost:8081/announce/struct"
        url = ${?BROADCAST_STRUCT_API_URL}
        serviceToken = "default_token"
        serviceToken = ${?BROADCAST_API_TOKEN}
      }
    }

In these configuration blocks per service, we have opted for using environment variables, although this is not a necessity. We have also provied default values for each environment variable. Each configuratoin block also specifies an `serviceToken` which will be propagated to the underlying microservice as an `Service-Token` request header. Provided your microservice communications use SSL, your microservices should be secure.


### Creating new Consumers

An endpoint has also been provided for creating new consumers. This endpoint simply takes a JSON `PATCH` on `/consumer` of:

    {
      "consumer": "person@example.org",
      "candidates": [
        "candidate1",
        "candidate2"
      ]
    }

and returns a JSON response:

    {
      "consumerKey": "5f202e7ab75f00af194c61cc07ae6b0c",
      "consumerToken": "9d3d95435ace2906e3ba80c3dfcaf0ededb9084aabc205f6d1232121996185c2",
      "name": "person@example.org"
    }

### Revoke existing Consumer

To revoke a consumer, a `DELETE` request can be made on the `/consumer/{consumer}` endpoint. This returns a JSON response:

    {
      "consumerKey": "5f202e7ab75f00af194c61cc07ae6b0c",
      "name": "groovy",
      "message": "consumer revoked"
    }

The endpoints themselves are secured, and looks for the presence of an `Admin-Token` request header. The value of this can be set by providing an `ADMIN_TOKEN` environment variable, which defaults to `default_token`.

Once the Consumer Key and Token have been obtained, they can be used to make subsequent calls to proxied endpoints. All these calls will _require `Consumer-Key` and `Consumer-Token` headers to be set respectively for each call_.


### Release

Start up supporting services:

```
$ ./compose up
```

Run up the service:

```
$ sbt run
$ http :9000/alive
```

Perform the release:

```
$ sbt release
```
