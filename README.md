
## Build Your Own Google Client For Java

[gcloud-java](https://github.com/GoogleCloudPlatform/gcloud-java) is not supported compute-engine api, and [jcloud](http://jclouds.apache.org/) is too large for simple usage.

We just build a workable simple client from [google-api-java-client](https://github.com/google/google-api-java-client).

## How to use

### Create And Register HttpClient

```java
GoogleCredential googleCredential =
    GoogleCredential.fromStream(new FileInputStream(new File("json-key-file from service account")));

// grants scopes for http-client
googleCredential = googleCredential.createScoped(ComputeEnginsScopes.scopes());

// register the http-client to our GoogleComputeService object
GoogleComputeService api = new GoogleComputeService();
api.registerClient(ComputeEngine.class, new HttpClientBuilder(ComputeEngine.class, googleCredential).build());
```

```java
// create the Instances object
Instances instances = api.create(ComputeEngine.class, Instances.class);

String project = "your-project-id";
String zone = "asia-east1-b";

Param param = Param
        .create("name", "abce1")
        .add("machineType", "zones/asia-east1-b/machineTypes/n1-standard-1")
        .add("sourceImage",
                "https://www.googleapis.com/compute/v1/projects/ubuntu-os-cloud/global/images/ubuntu-1404-trusty-v20160314");

JsonObject result = instances.insert(project, zone, param);
```


## Add new features

An example for `ComputeEngine`. We group the resources of a service in following structure.

1. Service `ComputeEngine` annotated with `@RootUri`
1. Resource `Instances` is just an java inteface with operations
1. any primitive parameter in an operation should be annoted with `@Name`, becasue we cannot know the name of the parameter after complied
1. any extra parameter in an operation can set by `Param`
1. an operation without the request body should be annotated with `@Verb` `@PathParamsTemplate`
1. an operation with the request body should be annotated with `@Verb` `@PathParamsTemplate` `@RequestBodyTemplate`


```
@RootUri(rootUrl = "https://www.googleapis.com", servicePath = "/compute/v1/projects")
public interface ComputeEngine {

    public interface Instances {

        @Verb(HttpMethod.POST)
        @PathParamsTemplate("/:project/zones/:zone/instances")
        @RequestBodyTemplate("/instances.insert.json")
        public JsonObject insert(@Name("project") String project, @Name("zone") String zone, Param param);

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:project/zones/:zone/instances")
        public JsonObject list(@Name("project") String project, @Name("zone") String zone);

    }

}
```

Another example for `CloudStorage`:

1. `@OptionalQueryParameters` used by the optional URL parameters
1. We provide the `Param... param` for the optional parameters case


```
@RootUri(rootUrl = "https://www.googleapis.com", servicePath = "/storage/v1/b")
public interface CloudStorage {

    public interface Buckets {

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("?project=:project")
        @OptionalQueryParameters({ "maxResults", "pageToken", "prefix", "projection" })
        public JsonObject list(@Name("project") String project, Param... param);

    }

    public interface Objects {

        @Verb(HttpMethod.GET)
        @PathParamsTemplate("/:bucket/o")
        @OptionalQueryParameters({ "delimiter", "maxResults", "pageToken", "prefix", "projection" })
        public JsonObject list(@Name("bucket") String bucket, Param... param);

    }

}
```


### @PathParamsTemplate


```
@PathParamsTemplate("/:project/zones/:zone/instances")
```

In `@PathParamsTemplate` any string starts with `:` and following characters excludes `:`, `/` and `&` is a variable


### @RequestBodyTemplate

`@RequestBodyTemplate` will set a classpath to the json template, the following example is the template of the `Instances.insert` operation. We use the `${...}` format for variable notation.

```json
{
  "name": "${name}",
  "machineType": "${machineType}",
  "networkInterfaces": [
    {
      "accessConfigs": [
        {
          "type": "ONE_TO_ONE_NAT",
          "name": "External NAT"
        }
      ]
    }
  ],
  "tags": {
    "items": [
    ]
  },
  "disks": [
    {
      "initializeParams": {
        "sourceImage": "${sourceImage}"
      },
      "boot": true,
      "autoDelete": true
    }
  ],
  "metadata": {
    "items": []
  },
  "serviceAccounts": [
    {
      "email": "default",
      "scopes": [
        "https://www.googleapis.com/auth/devstorage.read_only"
      ]
    }
  ]
}
```
