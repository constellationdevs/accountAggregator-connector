package coop.constellation.connectorservices.demoexternalaccounts.controller;

import coop.constellation.connectorservices.demoexternalaccounts.service.DemoExternalAccountImp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xtensifi.connectorservices.common.logging.ConnectorLogging;
import com.xtensifi.dspco.ConnectorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/externalConnector/devAcctAgg/1.0")
public class DemoExternalAccountsController {
    ConnectorLogging logger = new ConnectorLogging();
    DemoExternalAccountImp service;

    @Autowired
    public DemoExternalAccountsController(DemoExternalAccountImp service) {
        this.service = service;
    }

    @GetMapping("/awsping")
    public String getAWSPing() {
        return "{ping: 'pong'}";
    }

    @PostMapping(path = "/getAccounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConnectorMessage getExternalAccounts(@RequestBody ConnectorMessage connectorMessage)
            throws JsonProcessingException {
        String method = "DemoExternalAccountsController::getExternalAccounts";
        logger.info(connectorMessage, "Invoked " + method);

        try {
            service.init(connectorMessage);
            connectorMessage = service.getExternalAccounts(connectorMessage);
            logger.info(connectorMessage, method + " process is finished");
        } catch (Exception e) {
            logger.error(connectorMessage, method + " caught IOException: " + e.getMessage());
        }
        return connectorMessage;
    }

    @PostMapping(path = "/getTransactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ConnectorMessage getExternalTransactions(@RequestBody ConnectorMessage connectorMessage) {
        String method = "DemoExternalTransactionsController::getExternalTransactions";
        logger.info(connectorMessage, "Invoked " + method);

        try {
            service.init(connectorMessage);
            connectorMessage = service.getExternalTransactions(connectorMessage);
            logger.info(connectorMessage, method + " process is finished");
        } catch (Exception e) {
            logger.error(connectorMessage, method + " caught IOException: " + e.getMessage());
        }
        return connectorMessage;
    }

}