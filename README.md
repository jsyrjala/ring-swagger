# Ring-Swagger

[![Build Status](https://travis-ci.org/metosin/ring-swagger.png?branch=master)](https://travis-ci.org/metosin/ring-swagger)

[Swagger](...) implementation for Ring using Prismatic [Schema](https://github.com/Prismatic/schema) for data modelling and input data coercion.

- Provides handlers for both [Resource listing](https://github.com/wordnik/swagger-core/wiki/Resource-Listing) and [Api declaration](https://github.com/wordnik/swagger-core/wiki/API-Declaration).
- Does not cover how the routes and models are collected from web apps (and by so should be compatible with all Ring-based libraries)
   - Provides a Map-based interface for routing libs to create Swagger Spec out of the route definitions

For embedding a [Swagger-UI](https://github.com/wordnik/swagger-ui) into your Ring-app, check out the [ring-swagger-ui](https://github.com/metosin/ring-swagger-ui).

## Latest version

```clojure
[metosin/ring-swagger "0.8.3"]
```

## Client libraries

- [Compojure-Api](https://github.com/metosin/compojure-api) for Compojure

If your favourite web lib doesn't have an client adapter, you could write an it yourself. Here's howto:

1. Create routes for `api-docs` and `api-docs/:api`
2. Create route-collector to [collect the routes](https://github.com/metosin/ring-swagger/blob/master/test/ring/swagger/core_test.clj).
3. Publish it.
4. List it here.
5. Profit.

## Models

The building blocks for creating Web Schemas are found in package `ring.swagger.schema`. All schemas must be declared by `defmodel`, which set up the needed meta-data. Otherwise, it's just a normal [Schema](https://github.com/Prismatic/schema).

### Supported Schema elements

| Clojure | JSON Schema | Sample  |
| --------|-------|:------------:|
| `Long`, `schema/Int`        | integer, int64 | `1`|
| `Double`                    | number, double | `1.2`
| `String`, `schema/Str`, Keyword, `schema/Keyword`      | string | `"kikka"`
| `Boolean`                   | boolean | `true`
| `nil`                       | void |
| `java.util.Date`, `org.joda.time.DateTime`  | string, date-time | `"2014-02-18T18:25:37.456Z"`, consumes also without millis: `"2014-02-18T18:25:37Z"`
| `org.joda.time.LocalDate`   | string, date | `"2014-02-19"`
| `(schema/enum X Y Z)`       | *type of X*, enum(X,Y,Z)
| `(schema/maybe X)`          | *type of X*
| `(schema/both X Y Z)`       | *type of X*
| `(schema/recursive Var)`    | *Ref to (model) Var*
| `(schema/eq X)`    | *type of class of X*

- Vectors, Sets and Maps can be used as containers
  - Maps are presented as Complex Types and References. Model references are resolved automatically.
  - Nested maps are transformed automatically into flat maps with generated child references.

### Schema coersion

Ring-swagger utilizes [Schema coercions](http://blog.getprismatic.com/blog/2014/1/4/schema-020-back-with-clojurescript-data-coercion) for transforming the input data into vanilla Clojure and back. There are two modes for coersions: json and query.

#### Json-coercion

- numbers -> `Long` or `Double`
- string -> Keyword
- string -> `java.util.Date`, `org.joda.time.DateTime` or `org.joda.time.LocalDate`
- vectors -> Sets

#### Query-coersion:

extends the json-coersion with the following transformations:

- string -> Long
- string -> Double
- string -> Boolean

### A Sample Schema

```clojure
(require '[ring.swagger.schema :refer :all])
(require '[schema.core :as s])

(defmodel SubType  {:alive Boolean})
(defmodel AllTypes {:a Boolean
                    :b Double
                    :c Long
                    :d String
                    :e {:f [Keyword]
                        :g #{String}
                        :h #{(s/enum :kikka :kakka :kukka)}
                        :i Date
                        :j DateTime
                        :k LocalDate
                        :l (s/maybe String)
                        :m (s/both Long (s/pred odd? 'odd?))
                        :n SubType}})
```

- `AllTypesE` gets automatically generated (and referenced).

see models and coercion in action in [tests](https://github.com/metosin/ring-swagger/blob/master/test/ring/swagger/schema_test.clj).

## TODO

- produce path parameters on the client side (to support typed parameters)
- support for consumes
- support for auth
- non-json produces & consumes
- full spec

## Contributing

Pull Requests welcome. Please run the tests (`lein midje`) and make sure they pass before you submit one.

## License

Copyright © 2014 Metosin Oy

Distributed under the Eclipse Public License, the same as Clojure.
