## SDKMAN! Secure Proxy

![Build status](https://github.com/sdkman/vendor-proxy/actions/workflows/release.yml/badge.svg)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/sdkman/vendor-proxy)

This microservice can be used to proxy and secure other microservices. At the moment, the service provides lightweight Authentication and Authorisation.

### Consumer Authentication

It acts as a proxy, searching for the presence of two request headers:

`Consmer-Key` : a unique identifier per Consumer.

`Consumer-Token` : a SHA-256 hash generated for the Consumer

### Setting up Services to Proxy

The application hinges on configuration to be set up in the `conf/application.conf` field. Here are some current examples as used by the SDKMAN API:

    services {
      "candidates" = {
        url = "http://somehost:8080/candidates"
        url = ${?CANDIDATE_ENDPOINT_API_URL}
        serviceToken = "default_token"
        serviceToken = ${?RELEASE_API_TOKEN}
      }
      "candidates/default" = {
        url = "http://somehost:8080/candidates/default"
        url = ${?DEFAULT_ENDPOINT_API_URL}
        serviceToken = "default_token"
        serviceToken = ${?RELEASE_API_TOKEN}
      }
      "versions" = {
        url = "http://somehost:8080/versions"
        url = ${?RELEASE_ENDPOINT_API_URL}
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
      ],
      "vendor": "vendor"
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


### Testing

#### Cucumber with SBT

To run the service tests, spin up postgres with docker as follows:

```
docker run --name postgres \
        -p 5432:5432 \
        -e POSTGRES_USER=postgres \
        -e POSTGRES_PASSWORD=postgres \
        -e POSTGRES_DB=vendors \
        -d postgres
```

Then run the tests with sbt:

```
sbt test
```

#### Running all vendor services in docker-compose

Publish docker images for all vendor services:

```
cd /path/to/vendor-release
sbt docker:publishLocal
cd /path/to/vendor-proxy
sbt docker:publishLocal
```

Start all datastores and services with docker-compose:

```
$ docker-compose up
```

Now interact with the vendor-proxy service through `localhost:9000`:

Create consumer:
```
curl --request PATCH \
  --url http://localhost:9000/consumers \
  --header 'Admin-Token: default_token' \
  --header 'Content-Type: application/json' \
  --data '{
	"consumer" : "marco@sdkman.io",
	"candidates": [
		"grails"
	]
}'
```

Create candidate:
```
curl --request POST \
  --url http://localhost:9000/candidates \
  --header 'Consumer-Key: 831a10da1a6808227d8ea75c30f1243f' \
  --header 'Consumer-Token: token' \
  --header 'Content-Type: application/json' \
  --data '{
	 "id" : "grails",
   "candidate" : "grails",
   "name" : "Grails",
   "description" : "It'\''s old and useless by now",
   "websiteUrl" : "https://github.com/grails/grails",
   "distribution" : "UNIVERSAL"
}'
```

Release version:
```
curl --request POST \
  --url http://localhost:9000/versions \
  --header 'Consumer-Key: 831a10da1a6808227d8ea75c30f1243f' \
  --header 'Consumer-Token: token' \
  --header 'Content-Type: application/json' \
  --data '{
	"candidate":"grails",
	"version":"2.1.1",
	"platform":"UNIVERSAL",
	"url" : "https://github.com/grails/grails-core/releases/download/v5.3.2/grails-5.3.2.zip"
}'
```

Set version as default
```
curl --request PUT \
  --url http://localhost:9000/candidates/default \
  --header 'Consumer-Key: 831a10da1a6808227d8ea75c30f1243f' \
  --header 'Consumer-Token: token' \
  --header 'Content-Type: application/json' \
  --data '{
	"candidate":"grails",
	"version":"2.1.1"
}'
```
