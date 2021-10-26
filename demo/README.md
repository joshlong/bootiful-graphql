# Bootiful GraphQL

## Outline

* the tension at FB
* the Spring Boot starter
* building a `RuntimeWiringConfigurer` to handle a `query` called `hello` that
  takes an argument called `name`
* building a `@Controller` to handle a `@SchemaMapping` that returns the same
  thing but with a different language so we can see its working. Be sure to
  comment out the `RuntimeWiringConfigurer`.
* Then change the `@SchemaMapping` to be a `@QueryMapping`, as that's more concise.
* Then introduce the concept of a `Customer` whose data we can read from
  a `Map <Integer, Customer>`. Create the `Customer` with a `record`
* Create a `query` for `customers`.
* We need to add data, though, don't we? add a constructor that bootstraps 8 records. the logic we use to add a customer tho? We can reuse that for a client! 
* Create a mutation to `addCustomer(name:String ): Customer`
* Great, but let's suppose we wanted to add a field to explain when someone
  enrolled. We need a `Date`, but GraphQL doesn't support a `Date` out of the
  box. You can easily handle this with a customer `resolver` (controller handler
  method) that uses a `SimpleDataFormat` in a `ThreadLocal` to return
  a `String` (see below for the code).
* this ability, to mesh resolvers to form the whole object graph independent of the underlying representation, is one of the reasons graphql is so powerful. let's suppose we wanted to provide `Order` data for each customer, as well. Return `Mono<ArrayList<Order>>` that's `delayElement(Duration)`'d so that we can demonstrate concurrency 
* 	

## GraphQL

the following incantation is handy to construct a `SimpleDateFormat`:

```
new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"){{
	setTimeZone(TimeZone.getTimeZone("UTC"));
}};
```

the following incantation can be used to invoke an endpoint with security
applied

```
curl  -v -u jlong:pw http://localhost:8080/graphql \
-H 'Content-Type: application/json' \
--data-raw '{"query":"query { secureHello }" }'
```
 