package microservice

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class SagaSimulation extends Simulation{

  val config = ConfigFactory.load("application")
  var baseUrl = "http://localhost:8100/order"

  val httpProtocol = http
    .baseUrl(baseUrl)
    .inferHtmlResources(AllowList(), DenyList())
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("PostmanRuntime/7.28.4")

  var token = ""
  val headersSaga = Map(
    "Cache-Control" -> "no-cache",
    "Postman-Token" -> "a435098d-aa46-4824-970f-756a972d44ce",
    "Content-Type" -> "application/json")

  object Saga {
    val order =
      exec(http("order")
        .post(baseUrl + "/orders")
        .headers(headersSaga)
        .body(StringBody("{ \"description\": \"Hamburguesa\", \"quantity\": 1 }"))
        .check(status.is(201))
      )
  }

  val scn = scenario("Saga")
    .exec(
      Saga.order
    )

  setUp(scn.inject(
    rampUsers(100) during (10)
  )).protocols(httpProtocol)
}
