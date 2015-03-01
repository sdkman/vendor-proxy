##Secure Proxy

This microservice can be used to front and secure multiple other microservices.

It acts as a proxy, searching for the presence of two request headers:

`consmer_key` : a unique identifier per consumer application

`consumer_token` : a SHA-256 hash generated for the consumer

The application hinges on configuration to be set up in the `conf/application.conf` field. Here are some current examples as used by the GVM api:

    services {
      "release" = {
        url = "http://localhost:8080/release"
        url = ${?RELEASE_ENDPOINT_API_URL}
        accessToken = "default_token"
        accessToken = ${?RELEASE_API_TOKEN}
      }
      "default" = {
        url = "http://localhost:8080/default"
        url = ${?DEFAULT_ENDPOINT_API_URL}
        accessToken = "default_token"
        accessToken = ${?RELEASE_API_TOKEN}
      }
      "announce/struct" = {
        url = "http://localhost:8081/announce/struct"
        url = ${?BROADCAST_STRUCT_API_URL}
        accessToken = "default_token"
        accessToken = ${?BROADCAST_API_TOKEN}
      }
      "announce/freeform" = {
        url = "http://localhost:8081/announce/freeform"
        url = ${?BROADCAST_FREEFORM_API_URL}
        accessToken = "default_token"
        accessToken = ${?BROADCAST_API_TOKEN}
      }
    }

In these configuration blocks per service, we have opted for using environment variables, although this is not a necessity. We have also provied default values for each environment variable.

And endpoint has also been provided for creating new consumers. This endpoint simply takes a JSON POST on '/consumer' of:

    {"consumer": "groovy"}

and returns a JSON response:

    {
      "consumerKey": "5f202e7ab75f00af194c61cc07ae6b0c",
      "consumerToken": "9d3d95435ace2906e3ba80c3dfcaf0ededb9084aabc205f6d1232121996185c2",
      "name": "groovy"
    }

Once this key and token have been obtained, they can be used to make subsequent calls to proxied endpoints. All these calls will require `consumer_key` and `consumer_token` headers to be set respectively for each call.

The service will automatically include an `access_token` header in calls that it makes to services that it proxies. 
