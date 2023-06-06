package com.github.bwar.samerrorhandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

/**
 * Handler for requests to Lambda function.
 */
public class ErrorHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Map<String, String> queryStringParameters = input.getQueryStringParameters();
        try {
            int dividend = Integer.parseInt(queryStringParameters.get("dividend"));
            int divisor = Integer.parseInt(queryStringParameters.get("divisor"));
            int result = dividend / divisor;

            response
                    .withStatusCode(200)
                    .withBody(
                            "{"
                                    + "\"dividend\":" + dividend + ","
                                    + "\"divisor\":" + divisor + ","
                                    + "\"result\":" + result +
                                    "}"
                    );
        }catch(NumberFormatException | ArithmeticException ex){
            response.setStatusCode(500);
            response.withBody("{ error : " + ex.getMessage() + " }");
            return response;
        }
        return response;
    }

    private String getPageContents(String address) throws IOException{
        URL url = new URL(address);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
