package org.cip4.tools.alces.service.discovery;

import org.cip4.tools.alces.service.discovery.model.Connection;
import org.cip4.tools.alces.service.jmfmessage.JmfMessageService;
import org.cip4.tools.alces.service.testrunner.TestRunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the DiscoveryService interface.
 */
@Service
public class DiscoveryServiceImpl implements DiscoveryService {

    @Autowired
    private JmfMessageService jmfMessageService;

    @Autowired
    private TestRunnerService testRunnerService;

    @Override
    public Connection connect(String jmfEndpointUrl) {


        // JMF Handshake


        // testRunnerService.startTestSession(jmfMessageService.createQueryKnownDevices(), jmfEndpointUrl);


        return null;
    }
}
