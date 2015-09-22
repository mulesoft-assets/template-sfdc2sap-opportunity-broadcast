/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.construct.Flow;
import org.mule.context.notification.NotificationException;
import org.mule.tck.probe.PollingProber;
import org.mule.tck.probe.Prober;
import org.mule.templates.test.utils.ListenerProbe;
import org.mule.templates.test.utils.PipelineSynchronizeListener;

import com.mulesoft.module.batch.BatchTestHelper;
import com.sforce.soap.partner.SaveResult;

/**
 * The objective of this class is to validate the correct behavior of the Mule Template that make calls to external systems.
 * 
 * The test will invoke the batch process and afterwards check that the opportunities had been correctly created and that the ones that should be filtered are
 * not in the destination sand box.
 * 
 * The test validates that an account will get sync as result of the integration.
 */
public class BusinessLogicTestCreateOpportunityIT extends AbstractTemplateTestCase {

	private static final Logger LOGGER = LogManager.getLogger(BusinessLogicTestCreateOpportunityIT.class);
	private static final String POLL_FLOW_NAME = "triggerFlow";

	private final Prober pollProber = new PollingProber(10000, 1000);
	private final PipelineSynchronizeListener pipelineListener = new PipelineSynchronizeListener(POLL_FLOW_NAME);

	private BatchTestHelper helper;
	private List<Map<String, Object>> createdOpportunities = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> createdAccounts = new ArrayList<Map<String, Object>>();

	@BeforeClass
	public static void init() {

		System.setProperty("page.size", "1000");

		// Set the frequency between polls to 10 seconds
		System.setProperty("poll.frequencyMillis", "10000");

		// Set the poll starting delay to 20 seconds
		System.setProperty("poll.startDelayMillis", "20000");

		// Setting Default Watermark Expression to query SFDC with
		// LastModifiedDate greater than ten seconds before current time
		System.setProperty("watermark.default.expression", "#[groovy: new Date(System.currentTimeMillis() - 10000).format(\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\", TimeZone.getTimeZone('UTC'))]");

		System.setProperty("account.sync.policy", "syncAccount");

	}

	@AfterClass
	public static void shutDown() {
		System.clearProperty("account.sync.policy");
	}

	@Before
	public void setUp() throws Exception {

		stopFlowSchedulers(POLL_FLOW_NAME);
		registerListeners();

		helper = new BatchTestHelper(muleContext);

		retrieveSalesOrderFromSapFlow = lookupFlowConstruct("retrieveSalesOrderFromSapFlow");

		retrieveAccountFromSapFlow = lookupFlowConstruct("retrieveAccountFromSapFlow");

		createTestDataInSandBox();
	}

	private void registerListeners() throws NotificationException {
		muleContext.registerListener(pipelineListener);
	}

	private void waitForPollToRun() {
		pollProber.check(new ListenerProbe(pipelineListener));
	}

	@After
	public void tearDown() throws Exception {
		deleteTestDataFromSandBox();
	}

	@Test
	public void testMainFlow() throws Exception {

		// Run poll and wait for it to run
		runSchedulersOnce(POLL_FLOW_NAME);
		waitForPollToRun();

		// Wait for the batch job executed by the poll flow to finish
		helper.awaitJobTermination(TIMEOUT_SEC * 1000, 500);
		helper.assertJobWasSuccessful();
		
		Thread.sleep(20000);

		Assert.assertNull("The opportunity should not have been sync", invokeRetrieveFlow(retrieveSalesOrderFromSapFlow, createdOpportunities.get(0)).get("Id"));

		Assert.assertNull("The opportunity should not have been sync", invokeRetrieveFlow(retrieveSalesOrderFromSapFlow, createdOpportunities.get(1)).get("Id"));

		Map<String,Object> dummyAccount = new HashMap<String,Object>();
		dummyAccount.put("Name", "sfdc2sap_i0qpkqq3");
		
		Map<String, Object> accountPayload = invokeRetrieveFlow(retrieveAccountFromSapFlow, dummyAccount);
		Map<String, Object> opportunityPayload = invokeRetrieveFlow(retrieveSalesOrderFromSapFlow, createdOpportunities.get(2));
		
		Assert.assertNotNull("The opportunity should have been sync", opportunityPayload == null ? null : opportunityPayload.get("Id"));
		Assert.assertEquals("The opportunity should belong to a different account ", accountPayload == null ? null : accountPayload.get("CustomerNumber"), 
																					 opportunityPayload == null ? null : opportunityPayload.get("AccountId"));
	}

	private void createTestDataInSandBox() throws MuleException, Exception {
		createAccounts();
		createOpportunities();
	}

	@SuppressWarnings("unchecked")
	private void createAccounts() throws Exception {
		Flow flow = lookupFlowConstruct("createAccountInSalesforceFlow");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Name", "sfdc2sap_opp_bc_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX));
		map.put("BillingCity", "San Francisco");
		map.put("BillingCountry", "USA");
		map.put("Phone", "123456789");
		map.put("Industry", "Education");
		map.put("NumberOfEmployees", 9000);
		
		createdAccounts.add(map);

		MuleEvent event = flow.process(getTestEvent(createdAccounts, MessageExchangePattern.REQUEST_RESPONSE));
		List<SaveResult> results = (List<SaveResult>) event.getMessage().getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdAccounts.get(i).put("Id", results.get(i).getId());
		}

		LOGGER.info("Results of data creation in sandbox" + createdAccounts.toString());
	}

	@SuppressWarnings("unchecked")
	private void createOpportunities() throws Exception {
		Flow flow = lookupFlowConstruct("createOpportunityInSalesforceFlow");

		// This opportunity should not be sync
		Map<String, Object> opportunity = createOpportunity(0);
		opportunity.put("Amount", 300);
		createdOpportunities.add(opportunity);

		// This opportunity should not be sync
		opportunity = createOpportunity(1);
		opportunity.put("Amount", 1000);
		createdOpportunities.add(opportunity);

		// This opportunity should BE sync with it's account
		opportunity = createOpportunity(2);
		opportunity.put("Amount", 30000);
		opportunity.put("AccountId", createdAccounts.get(0).get("Id"));
		opportunity.put("StageName", "Closed Won");
		opportunity.put("Probability", "100");
		createdOpportunities.add(opportunity);

		MuleEvent event = flow.process(getTestEvent(createdOpportunities, MessageExchangePattern.REQUEST_RESPONSE));
		List<SaveResult> results = (List<SaveResult>) event.getMessage().getPayload();
		for (int i = 0; i < results.size(); i++) {
			createdOpportunities.get(i).put("Id", results.get(i).getId());
		}
	}

	private void deleteTestDataFromSandBox() throws MuleException, Exception {
		deleteTestOpportunityFromSandBox(createdOpportunities);
		deleteTestAccountFromSandBox(createdAccounts);
	}

}
