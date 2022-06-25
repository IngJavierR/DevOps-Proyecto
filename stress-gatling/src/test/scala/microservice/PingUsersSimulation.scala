package microservice

import com.typesafe.config.ConfigFactory
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class PingUsersSimulation extends Simulation {

  val config = ConfigFactory.load("application")
  var baseUrl = config.getString("url.baseUrl")
  var baseAuthUrl = config.getString("url.baseAuthUrl")
  var username = config.getString("oauth2.username")
  var password = config.getString("oauth2.password")
  var client_id = config.getString("oauth2.client_id")
  var client_secret = config.getString("oauth2.client_secret")

  val httpProtocol = http
    .baseUrl(baseUrl)
    .inferHtmlResources(AllowList(), DenyList())
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("PostmanRuntime/7.28.4")

  val headersLogin = Map(
    "Cache-Control" -> "no-cache",
    "Postman-Token" -> "a435098d-aa46-4824-970f-756a972d44ce")

  object BusinessLogic {
    var headers_10 = Map("Content-Type" -> "application/json; charset=ISO-8859-1")

    val getPing =
      exec(http("GetPing")
        .get("/microservicio/ping")
        .headers(headers_10)
        .check(status.is(200))
      )
    val getUsers =
      exec(http("GetUsers")
        .get("/microservicio/users")
        .headers(headers_10)
        .check(status.is(200))
      )
  }

  val scn = scenario("GetPing")
    .exec(
      BusinessLogic.getPing,
      //BusinessLogic.getUsers
    )

  setUp(
    scn.inject(
      nothingFor(2 seconds),
      atOnceUsers(5),
      rampUsers(1) during (20)
  )).protocols(httpProtocol)
}
