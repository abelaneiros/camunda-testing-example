package example;

import example.bootstrap.CamundaConfiguration;
import java.io.InputStream;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CamundaConfiguration.class})
// I couldn't make this working, so I deploy both process manually
//@Deployment(resources = {
//    "CamundaExample2.bpmn", "CamundaSubProcess2.bpmn",
//})
public class CamundaFlowTest {

    private final static String MAIN_MODEL_NAME = "CamundaExample2.bpmn";
    private final static String SUB_PROCESS_MODEL_NAME = "CamundaSubProcess2.bpmn";
    private final ProcessScenario processScenario = mock(ProcessScenario.class);

    @Autowired
    public ProcessEngine processEngine;

    @Before
    public void deployProcess() {

        final InputStream is1 = CamundaFlowTest.class.getClassLoader().getResourceAsStream(MAIN_MODEL_NAME);
        final InputStream is2 = CamundaFlowTest.class.getClassLoader().getResourceAsStream(SUB_PROCESS_MODEL_NAME);
        final BpmnModelInstance flow1 = Bpmn.readModelFromStream(is1);
        final BpmnModelInstance flow2 = Bpmn.readModelFromStream(is2);

        final RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment()
                    .addModelInstance(MAIN_MODEL_NAME, flow1)
                    .addModelInstance(SUB_PROCESS_MODEL_NAME, flow2)
                    .deploy();

        when(processScenario.waitsAtMockedCallActivity("call-activity1"))
                .thenReturn(callActivity -> callActivity.complete(null));
    }

    @After
    public void resetMocks() {
        Mocks.reset();
    }

    private ProcessInstance startProcess() {

        final VariableMap variables = Variables.createVariables();

        final Scenario scenario = Scenario.run(this.processScenario)
                .withMockedProcess("CamundaSubProcess1")
                .startByKey("CamundaExample2", variables)
                .execute();

        return scenario.instance(this.processScenario);
    }

    @Test
    public void test() {

        final ProcessInstance processInstance = startProcess();

        assertThat(processInstance).isEnded();
        assertThat(processInstance).hasPassedInOrder(
                "request_event",
                "service-task1",
                "call-activity1",
                "service-task2",
                "call-activity2",
                "service-task3",
                "end_event");
    }
}
