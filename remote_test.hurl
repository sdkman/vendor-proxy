POST https://vendors.sdkman.io/consumers
Admin-Token: 99404430353170033f63211acd3e4112ad8ec93a87e8e36faf0ecb9489f1e16d
{
	"consumer" : "info@sdkman.io",
	"candidates": [
		"java"
	]
}

HTTP 201
[Captures]
consumerKey: jsonpath "$.consumerKey"
consumerToken: jsonpath "$.consumerToken"
name: jsonpath "$.name"

POST https://vendors.sdkman.io/versions
Consumer-Key: {{consumerKey}}
Consumer-Token: {{consumerToken}}
{
	"candidate":"java",
	"version":"21.ea.xx",
	"vendor" : "open",
	"platform":"LINUX_ARM64",
	"url" : "https://download.java.net/java/early_access/jdk21/28/GPL/openjdk-21-ea+28_macos-aarch64_bin.tar.gz",
	"visible": false
}
HTTP 201

DELETE https://vendors.sdkman.io/versions
Consumer-Key: {{consumerKey}}
Consumer-Token: {{consumerToken}}
{
	"candidate":"java",
	"version":"21.ea.xx-open",
	"platform":"LINUX_ARM64"
}
HTTP 200

DELETE https://vendors.sdkman.io/consumers/{{consumerKey}}
Admin-Token: 99404430353170033f63211acd3e4112ad8ec93a87e8e36faf0ecb9489f1e16d

HTTP 200
