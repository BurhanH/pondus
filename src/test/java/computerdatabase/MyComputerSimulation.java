package computerdatabase;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class MyComputerSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://computer-database.gatling.io")
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-US,en;q=0.9,ru-KZ;q=0.8,ru;q=0.7")
            .contentTypeHeader("application/x-www-form-urlencoded")
            .originHeader("https://computer-database.gatling.io")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Mobile Safari/537.36");

    FeederBuilder.Batchable searchFeeder =
            csv("data/search.csv").random();
    ChainBuilder searchForComputer =
            exec(http("LoadingHomePage")
                    .get("/computers"))
                    .pause(2)
                    .feed(searchFeeder)
            .exec(http("SearchComputers_#{searchCriterion}")
                    .get("/computers?f=#{searchCriterion}")
                    .check(css("a:contains('#{searchComputerName}')", "href")
                            .saveAs("computerURL")))
                    .pause(2)
            .exec(http("LoadComputerDetails_#{searchComputerName}")
                    .get("#{computerURL}"))
                    .pause(2);

    ChainBuilder browse =
            repeat(5, "n").on(
                    exec(http("Page #{n}")
                            .get("/computers?p=1&#{n}=10&s=name&d=asc"))
                            .pause(2)
            );

    FeederBuilder.Batchable computerFeeder =
            csv("data/computers.csv").circular();
    ChainBuilder createComputer =
            exec(http("LoadCreateComputerPage")
                    .get("/computers/new"))
                    .pause(2)
                    .feed(computerFeeder)
            .exec(http("CreateNewComputer_#{computerName}")
                    .post("/computers")
                    .formParam("name", "#{computerName}")
                    .formParam("introduced", "#{introduced}")
                    .formParam("discontinued", "#{discontinued}")
                    .formParam("company", "#{companyId}")
                    .check(status().is(200))
            );
    private ScenarioBuilder admins = scenario("Admins")
            .exec(searchForComputer, browse, createComputer);

    private ScenarioBuilder users = scenario("Users")
            .exec(searchForComputer, browse);

    {
        setUp(
                admins.injectOpen(atOnceUsers(1)),
                users.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
